package com.models.Agents;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.configurations.AgentStateConfig.MarketState;
import com.github.pnavais.machine.StateMachine;
import com.github.pnavais.machine.model.State;
import com.models.StockExchange;
import com.models.demands.ShareInfo;
import com.models.demands.StockOrder;
import com.models.demands.StockOrder.type; 
import com.utils.SimAgentTypeEnum;

import em426.agents.Agent;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Component
@Scope("prototype")
@PropertySource("classpath:stock.properties")
public class Market extends Agent {

	@Value("${stockPrice}")
	private double initialHoldingPrice;

	@Value("${stockVolume}")
	private double stockVolume;

	@Value("${floatingRatio}")
	private double floatingRatio;

	private double balance = 0;

	private double stockHolding; // num of shares holding

	private int numTicksToObserve = 5; // use for computing the average performance of the stock

	private double sellBelowPricePercentage = 0.95;
	private double buyAbovePricePercentage = 1.05;

	@Autowired
	@Qualifier("MarketStateMachine")
	StateMachine MarketStateMachine;

	@Autowired
	StockExchange wallStreet;

	@Autowired
	Flux<ShareInfo> shareInfoFlux;

	@Autowired
	Sinks.Many<StockOrder> stockOrderStream;

	public Market(){}
	
	@PreDestroy
	void tearDown() {
		System.out.println("Destroying Market object");
	}
	
	@PostConstruct
	void init() {

		this.stockHolding = this.stockVolume * (this.floatingRatio / 100);
		
		this.shareInfoFlux.buffer(this.numTicksToObserve).subscribe(shareInfo -> {

			ShareInfo latest = shareInfo.get(numTicksToObserve - 1);
			double latestPrice = latest.getCurrentPrice();

			double probability = Math.random(); // completely random

			if (probability >= 0.5) {

				if (this.MarketStateMachine.getCurrent() == MarketState.IDLE) {

					this.MarketStateMachine.send(MarketState.BUYNOW);

				} else {
					this.MarketStateMachine.send(MarketState.NEXTSTATE);
				}

				int numShares = (int) this.computeSharesToProcess();
				double price = this.buyAbovePricePercentage * latestPrice;
				StockOrder order = new StockOrder(this.getId(), type.BUY, price, numShares, SimAgentTypeEnum.Market,
						latest.getTimestamp());
				this.stockOrderStream.tryEmitNext(order);

				System.out.println("Market buying " + numShares + " @ $" + price);

			} else {

				this.MarketStateMachine.send(MarketState.SELLNOW);

				if (this.MarketStateMachine.getCurrent() == MarketState.IDLE) {

					this.MarketStateMachine.send(MarketState.SELLNOW);

				} else {
					this.MarketStateMachine.send(MarketState.NEXTSTATE);
				}

				int numShares = (int) this.computeSharesToProcess();
				double price = this.sellBelowPricePercentage * latestPrice;
				StockOrder order = new StockOrder(this.getId(), type.SELL, price, numShares, SimAgentTypeEnum.Market,
						latest.getTimestamp());
				this.wallStreet.submitOrder(order, order.getOrderRequestedAtTime());

				System.out.println("Market selling " + numShares + " @ $" + price);

			}

			this.MarketStateMachine.send(MarketState.BACK2IDLE);

		});

	}

	public double computeSharesToProcess() {

		double shares = 0;

		State currState = this.MarketStateMachine.getCurrent();

		if (currState == MarketState.SELL1P || currState == MarketState.BUY1P) {
			shares = this.stockHolding * 0.01;
		}

		if (currState == MarketState.SELL5P || currState == MarketState.BUY5P) {

			shares = this.stockHolding * 0.05;
		}

		if (currState == MarketState.SELL10P || currState == MarketState.BUY10P) {
			shares = this.stockHolding * 0.1;
		}

		return shares;
	}

	public double getBalance() {
		return balance;
	}

}
