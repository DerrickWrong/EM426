package com.configurations;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.models.StockExchange;
import com.models.Agents.Ape;
import com.models.Agents.HedgeFund;
import com.models.Agents.Market;
import com.models.Agents.NewsReporter;
import com.models.Agents.ScoreKeeper;
import com.models.Agents.StockBroker;
import com.models.Agents.StockLender;
import com.models.demands.Share;
import com.models.demands.StockOrder;
import com.models.demands.StockOrder.type;
import com.utils.SimAgentTypeEnum;

import em426.agents.Agent;
import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Configuration
@PropertySource("classpath:app.properties")
@PropertySource("classpath:stock.properties")
public class WorldSimulator {

	@Value("${stockName}")
	private String stockName;

	@Value("${stockPrice}")
	private double stockPrice;

	@Value("${stockVolume}")
	private int stockVolume;

	@Value("${insiderRatio}")
	private double insiderR;

	@Value("${insititueRatio}")
	private double instituteR;

	@Value("${shortedRatio}")
	private double shortedRatio;

	@Autowired
	ReactorStreamConfig streamConfig;

	@Autowired
	StockExchange exchange;

	@Autowired
	NewsReporter report;

	@Autowired
	ScoreKeeper scoreKeeper;

	@Autowired
	Flux<Long> simulationClock;

	@Autowired
	SimConfiguration simConfig;

	@Autowired
	SimAgentFactory agentFactory;
	
	@Autowired
	Sinks.Many<Double> resultSink;

	@Autowired
	Sinks.Many<Integer> simulationCompletion;
	
	private List<Agent> agentList = new ArrayList<>();

	int counter = 0;
	
	@PostConstruct
	void init() {

		this.simulationClock.subscribe(t -> {

			if (counter == 0) {
				this.setupSim(); // run once 
			}

			if (counter == simConfig.simulationDuration) {   
				streamConfig.stopSim();
				this.wrapUp();
				this.simulationCompletion.tryEmitNext(1);
			}
			
			this.counter += 1;
		});
		
	}

	void setupSim() {

		// TODO put this in a factory and kick it out at the simulate command

		int shortInterestShares = (int) ((this.shortedRatio / 100.0) * this.stockVolume);
		int marketNumShares = (int) ((1.0 - ((this.insiderR + this.instituteR) / 100.0)) * this.stockVolume);

		Market marketAgent = this.agentFactory.createMarketAgent();
		this.agentList.add(marketAgent);

		// register the market
		Share marketShares = new Share(marketAgent.getId(), this.stockPrice, marketNumShares + shortInterestShares,
				SimAgentTypeEnum.Market);
		this.exchange.getStockListing().registerShares2Pool(marketShares);

		StockOrder firstSell = new StockOrder(marketAgent.getId(), type.SELL, this.stockPrice, shortInterestShares,
				SimAgentTypeEnum.Market, 0L);
		this.exchange.submitOrder(firstSell, 0L);

		// create lender agent
		StockLender lender = this.agentFactory.createLender();
		lender.setMargineCall(0.5); // 50% price increase
		this.agentList.add(lender);

		// set reporter
		this.report.setLender(lender);

		// create broker agent
		StockBroker broker = this.agentFactory.createStockBroker();
		broker.setLender(lender);
		this.agentList.add(broker); 
		broker.setBuyOrderDelay(simConfig.brokerDelay);

		// create hedgie
		HedgeFund hedgie = this.agentFactory.createHedgie();
		this.agentList.add(hedgie);
		
		double shortPrice = simConfig.shortSellDisountRate * this.stockPrice;
		int shares2Short = (int) (simConfig.shortRatio * this.stockVolume);
		
		// add short interest to the market
		StockOrder shortOrder = new StockOrder(hedgie.getId(), type.SHORT, shortPrice, shares2Short,
				SimAgentTypeEnum.Hedgie, 0L);
		lender.borrowStock(shortOrder);
 
		this.scoreKeeper.setKeeper(lender, shortPrice * shares2Short, hedgie.getId());

		// create retail investors
		List<Ape> retailInvestorAgents = new ArrayList<>();

		for (int i = 0; i < simConfig.numOfAgents; i++) {
			Ape ape = this.agentFactory.createApe(simConfig.initialBalance, simConfig.multiplier,
					simConfig.disclosureDelay, simConfig.bidAbovePercent, simConfig.payFrequency);
			retailInvestorAgents.add(ape);
			this.agentList.add(ape);
		}

	}

	public void wrapUp() {

		double gainOrLoss = this.scoreKeeper.computeHedgieGainOrLoss() / 10E6;

		if (gainOrLoss > 0) {
			System.out.println("**********************************");
			System.out.format("Profit: %.2f mil \n", gainOrLoss);
			System.out.println("**********************************");
		} else {
			System.out.println("**********************************");
			System.out.format("Loss: %.2f mil \n", gainOrLoss);
			System.out.println("**********************************");
		}
		
		this.resultSink.tryEmitNext(gainOrLoss);
		
	}

	public void resetSimulation() {

		this.agentList.forEach(agent -> {

			this.agentFactory.destroyAgent(agent);
		});

		this.agentList.clear();

		this.exchange.reset();
		this.counter = 0;
		this.streamConfig.resetSim();
	}
}
