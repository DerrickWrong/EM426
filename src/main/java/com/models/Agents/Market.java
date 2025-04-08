package com.models.Agents;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.configurations.AgentStateConfig.MarketState; 
import com.github.pnavais.machine.StateMachine;
import com.github.pnavais.machine.model.State;
import com.models.StockExchange;
import com.models.demands.ShareInfo;
import com.models.demands.StockOrder;
import com.models.demands.StockOrder.type;
import com.utils.HelperFn;
import com.utils.SimAgentTypeEnum;

import em426.agents.Agent;
import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Component
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

	private double triggerSellPercentage = -5;
	private double triggerBuyPercentage = 5;
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

	@PostConstruct
	void init() {

		this.stockHolding = this.stockVolume * (this.floatingRatio / 100);
 
 
		this.shareInfoFlux.buffer(this.numTicksToObserve).subscribe(shareInfo -> {

			double latestPrice = shareInfo.get(numTicksToObserve - 1).getCurrentPrice();
			double der = HelperFn.getDerivative(this.initialHoldingPrice, latestPrice, 1);

			double percent = (der / this.initialHoldingPrice) * 100.0;

			if (percent > 1 && percent > this.triggerBuyPercentage) {
				System.out.println("Market buy ");

				if (this.MarketStateMachine.getCurrent() == MarketState.IDLE) {

					this.MarketStateMachine.send(MarketState.BUYNOW);

				} else {
					this.MarketStateMachine.send(MarketState.NEXTSTATE);
				}

				double numShares = this.computeSharesToProcess();
				double price = this.buyAbovePricePercentage * latestPrice;
				//StockOrder order = new StockOrder(this.getId(), type.BUY, price, numShares, "Market");
				//this.stockOrderStream.tryEmitNext(order);
				
				System.out.println("Market buying " + numShares + " @ $" + price);
			}

			if (percent < 0 && percent < this.triggerSellPercentage) {
				
				this.MarketStateMachine.send(MarketState.SELLNOW);

				if (this.MarketStateMachine.getCurrent() == MarketState.IDLE) {

					this.MarketStateMachine.send(MarketState.SELLNOW);

				} else {
					this.MarketStateMachine.send(MarketState.NEXTSTATE);
				}

				int numShares = (int) this.computeSharesToProcess();
				double price = this.sellBelowPricePercentage * latestPrice;
				//StockOrder order = new StockOrder(this.getId(), type.SELL, price, numShares, SimAgentTypeEnum.Market);
				//this.stockOrderStream.tryEmitNext(order);
				
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
