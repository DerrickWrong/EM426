package com.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.models.StockExchange;
import com.models.Agents.Hedgie;
import com.models.Agents.Lender;
import com.models.Agents.Market;
import com.models.Agents.StockBroker;
import com.models.demands.Share;
import com.models.demands.StockOrder;
import com.models.demands.StockOrder.type;
import com.utils.SimAgentTypeEnum;

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
	Hedgie hedgie;

	@Autowired
	Market market;
   
	@Autowired
	StockBroker broker;
	
	@Autowired
	StockExchange exchange;

	@Autowired
	Flux<Long> simulationClock;
	
	@Autowired
	Lender lender;

	boolean onceFlag = false;

	@PostConstruct
	void init() {

		this.simulationClock.subscribe(t -> {

			if (!onceFlag) {
				this.setupSim(); // run once
				onceFlag = true;
			}

		});

	}

	void setupSim() {

		// TODO put this in a factory and kick it out at the simulate command

		int companyShares = (int) ((this.insiderR / 100.0) * this.stockVolume);
		int mutualFundsShares = (int) ((this.instituteR / 100.0) * this.stockVolume);
		int shortInterestShares = (int) ((this.shortedRatio / 100.0) * this.stockVolume);
		int marketNumShares = (int) ((1.0 - ((this.insiderR + this.instituteR) / 100.0)) * this.stockVolume);

		// register
		Share marketShares = new Share(market.getId(), this.stockPrice, marketNumShares, SimAgentTypeEnum.Market);
		this.exchange.getStockListing().registerShares2Pool(marketShares);

		// add short interest to the market
		StockOrder shortOrder = new StockOrder(hedgie.getId(), type.SHORT, this.stockPrice, shortInterestShares,
				SimAgentTypeEnum.Hedgie, 0L);  
		
		this.lender.borrowStock(shortOrder);
		
	}

}
