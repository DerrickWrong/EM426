package com.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.models.StockExchange;
import com.models.Agents.HedgeFund; 
import com.models.Agents.Market;
import com.models.Agents.StockBroker;
import com.models.Agents.StockLender;
import com.models.demands.Share;
import com.models.demands.StockOrder;
import com.models.demands.StockOrder.type;
import com.utils.SimAgentTypeEnum;

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
	Market market;
   
	@Autowired
	StockExchange exchange;

	@Autowired
	Flux<Long> simulationClock;

	boolean simEndFlag = false;

	@Autowired
	SimAgentFactory agentFactory;
	
	int counter = 0;
	int stopAt = 100;
	@PostConstruct
	void init() {

		
		this.simulationClock.subscribe(t -> {

			if (!simEndFlag) {
				this.setupSim(); // run once
				simEndFlag = true;
			}
			
			this.counter += 1;
			
			if(counter == stopAt) {
				simEndFlag = true;
				streamConfig.stopSim();
			}
		});
		 
	}

	void setupSim() {

		// TODO put this in a factory and kick it out at the simulate command

		int shortInterestShares = (int) ((this.shortedRatio / 100.0) * this.stockVolume);
		int marketNumShares = (int) ((1.0 - ((this.insiderR + this.instituteR) / 100.0)) * this.stockVolume);

		// register
		Share marketShares = new Share(market.getId(), this.stockPrice, marketNumShares, SimAgentTypeEnum.Market);
		this.exchange.getStockListing().registerShares2Pool(marketShares);

		StockLender lender = this.agentFactory.createLender();
		lender.setMargineCall(0.3);
		
		StockBroker broker = this.agentFactory.createStockBroker();
		broker.setLender(lender);
		
		HedgeFund hedgie = this.agentFactory.createHedgie();
		hedgie.setParameter(lender, 7.5E9, 0.95, 0.5, 10, 0.5);
		
		// add short interest to the market
		StockOrder shortOrder = new StockOrder(hedgie.getId(), type.SHORT, this.stockPrice, shortInterestShares, SimAgentTypeEnum.Hedgie, 0L);  
		
		lender.borrowStock(shortOrder);
		
	}

}
