package com.models;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.models.Agents.Company;
import com.models.Agents.Hedgie;
import com.models.Agents.Market;
import com.models.demands.Share;
import com.models.demands.StockOrder;
import com.models.demands.StockOrder.type;
import com.utils.SimAgentTypeEnum;

import jakarta.annotation.PostConstruct;
import javafx.util.Pair;
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
	Sinks.Many<Pair<Share, StockOrder>> sellOrShortOrderSink;

	@Autowired
	StockExchange exchange;

	@Autowired
	Flux<Long> simulationClock;

	boolean onceFlag = false;

	@PostConstruct
	void init() {

		this.simulationClock.subscribe(t -> {

			System.out.println("Simulation time t = " + t);

			if (!onceFlag) {
				this.setupSim(); // run once
				onceFlag = true;
			}

		});

	}

	void setupSim() {

		int companyShares = (int) ((this.insiderR / 100.0) * this.stockVolume);
		int mutualFundsShares = (int) ((this.instituteR / 100.0) * this.stockVolume);
		int shortInterestShares = (int) ((this.shortedRatio / 100.0) * this.stockVolume);
		int marketNumShares = (int) ((100 - (this.insiderR + this.instituteR) / 100.0) * this.stockVolume);

		// register
		Share marketShares = new Share(market.getId(), this.stockPrice, marketNumShares, SimAgentTypeEnum.Market);
		this.exchange.getStockListing().registerShares2Pool(marketShares);

		// add short interest to the market
		Share shortedShares = new Share(hedgie.getId(), this.stockPrice, shortInterestShares, SimAgentTypeEnum.Hedgie);
		StockOrder shortOrder = new StockOrder(hedgie.getId(), type.SHORT, this.stockPrice, shortInterestShares,
				SimAgentTypeEnum.Hedgie, 0L);
		this.sellOrShortOrderSink.tryEmitNext(new Pair<>(shortedShares, shortOrder));

	}

}
