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
import com.models.Agents.StockBroker;
import com.models.Agents.StockLender;
import com.models.demands.Share;
import com.models.demands.StockOrder;
import com.models.demands.StockOrder.type;
import com.utils.SimAgentTypeEnum;

import em426.agents.Agent;
import jakarta.annotation.PostConstruct; 
import reactor.core.publisher.Flux;

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
	Flux<Long> simulationClock;

	boolean simRunOnce = false;

	@Autowired
	SimAgentFactory agentFactory;
	
	private List<Agent> agentList = new ArrayList<>();
	
	int counter = 0;
	int stopAt = 100;
	
	int numOfMonteCarlo = 10000; // TODO - TBD
	
	@PostConstruct
	void init() {

		
		this.simulationClock.subscribe(t -> {

			if (!simRunOnce) {
				this.setupSim(); // run once
				simRunOnce = true;
			}
			
			this.counter += 1;
			
			if(counter == stopAt) {
				streamConfig.stopSim();
			}
		});
		 
	}

	void setupSim() {

		// TODO put this in a factory and kick it out at the simulate command

		int shortInterestShares = (int) ((this.shortedRatio / 100.0) * this.stockVolume);
		int marketNumShares = (int) ((1.0 - ((this.insiderR + this.instituteR) / 100.0)) * this.stockVolume);

		Market marketAgent = this.agentFactory.createMarketAgent();
		this.agentList.add(marketAgent);
		
		// register the market
		Share marketShares = new Share(marketAgent.getId(), this.stockPrice, marketNumShares + shortInterestShares, SimAgentTypeEnum.Market);
		this.exchange.getStockListing().registerShares2Pool(marketShares);
		
		StockOrder firstSell = new StockOrder(marketAgent.getId(), type.SELL, this.stockPrice, shortInterestShares, SimAgentTypeEnum.Market, 0L);
		this.exchange.submitOrder(firstSell, 0L);
		
		// create lender agent
		StockLender lender = this.agentFactory.createLender();
		lender.setMargineCall(0.5); //50% price increase 
		this.agentList.add(lender);
		
		// set reporter
		this.report.setLender(lender);
		/*
		
		
		// create broker agent
		StockBroker broker = this.agentFactory.createStockBroker();
		broker.setLender(lender);
		this.agentList.add(broker);
		
		// create hedgie
		HedgeFund hedgie = this.agentFactory.createHedgie();
		hedgie.setParameter(lender, 7.5E9, 0.95, 0.5, 20, 0.5);
		this.agentList.add(hedgie);
		
		// add short interest to the market
		StockOrder shortOrder = new StockOrder(hedgie.getId(), type.SHORT, this.stockPrice, shortInterestShares, SimAgentTypeEnum.Hedgie, 0L);  
		lender.borrowStock(shortOrder);
	
		
		
		// create retail investors
		List<Ape> retailInvestorAgents = new ArrayList<>();
		int numOf100kApes = 10;
		
		for(int i = 0; i < numOf100kApes; i++) {
			Ape ape = this.agentFactory.createApe();
			retailInvestorAgents.add(ape);
			this.agentList.add(ape);
		}
		*/
	}

	public void reRunSimulation() {
		this.agentList.clear();
		this.exchange.reset();
		this.counter = 0;
		this.simRunOnce = false;
		this.streamConfig.resetSim();
	}
	
}
