package com.models.Agents;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.configurations.AgentStateConfig.ApeState;
import com.github.pnavais.machine.StateMachine;
import com.github.pnavais.machine.api.message.Messages;
import com.models.MarkovModel;
import com.models.demands.ShareInfo;
import com.models.demands.StockOrder;
import com.models.demands.StockOrder.type;
import com.utils.SimAgentTypeEnum;

import em426.agents.Agent;
import em426.api.ActState;
import jakarta.annotation.PostConstruct;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Component
@Scope("prototype")
public class Ape extends Agent {

	private final double initialBalance = 2000 * 100000; // scale it to 100k apes per agent
	private double salary4Invest = 1000 * 100000;
	private long payDay = 10; // every biweekly
	private DoubleProperty buyBidPercent = new SimpleDoubleProperty(1.05); // default 5% higher than the sell price
	private DoubleProperty balance = new SimpleDoubleProperty(initialBalance);
	private double holdingshares = 0;

	@Autowired
	@Qualifier("ApeStateMachine")
	private StateMachine apeState;

	@Autowired
	MarkovModel model;

	@Autowired
	Flux<ShareInfo> shareInfoFlux;

	@Autowired
	@Qualifier("stockOrderStream")
	Sinks.Many<StockOrder> stockOrderStream;

	@Autowired
	@Qualifier("completedOrderFlux")
	Flux<StockOrder> completedOrderFlux;

	@PostConstruct
	void init() {

		apeState.send(Messages.EMPTY); // move to observe state

		this.completedOrderFlux.filter(o -> {

			return (o.getOrderType() == type.SHORT && o.getOrderState() == ActState.COMMITTED);

		}).subscribe(o -> {

			// change the state to buy move due to seeing the demand (short)!
			apeState.send(ApeState.BUYMOREMESSAGE); // move to buy state

		});

		this.completedOrderFlux.filter(o -> {

			return o.getUUID() == this.getId() && o.getOrderState() == ActState.COMMITTED;

		}).subscribe(o -> {

			if (o.getOrderType() == type.BUY) {

				this.holdingshares = this.holdingshares + o.getNumOfShares();
				this.balance.set(0); // clean out the balance
			}

			if (o.getOrderType() == type.SELL) {

				this.balance.set(o.getBidPrice() * this.holdingshares);
				this.holdingshares = 0;
			}
			apeState.send(Messages.EMPTY);// move to hold state
		});

		// listen and watch for stock volume change (additional float shares from
		// Hedgie)
		shareInfoFlux.sample(Duration.ofSeconds(payDay)).subscribe(shareInfo -> {

			// getting paid
			this.balance.set(this.balance.get() + this.salary4Invest);

			if (apeState.getCurrent() == ApeState.BUY) {

				double buyPrice = shareInfo.getCurrentPrice() * buyBidPercent.get(); // this should be a bid price
				int numShare = (int) (this.balance.get() / buyPrice);

				StockOrder order = new StockOrder(this.getId(), type.BUY, buyPrice, numShare, SimAgentTypeEnum.Retail, shareInfo.getTimestamp());
				this.stockOrderStream.tryEmitNext(order);

				System.out.println("Ape buying " + numShare + " @ $" + buyPrice + " UUID - " + this.getId());
			}

			if (apeState.getCurrent() == ApeState.HOLD) {

				if (this.balance.get() >= initialBalance) {
					System.out.println("Ape Buy more " + this.getId());
					apeState.send(ApeState.BUYMOREMESSAGE); // go back to buy state
				}
			}

			if (apeState.getCurrent() == ApeState.SELL && this.holdingshares > 0) {
				
				double sellPrice = shareInfo.getCurrentPrice() * (1.0 - (buyBidPercent.get() / 100));
				int numShare = (int) this.holdingshares;
				
				StockOrder order = new StockOrder(this.getId(), type.SELL, sellPrice, numShare, SimAgentTypeEnum.Retail, shareInfo.getTimestamp());
				this.stockOrderStream.tryEmitNext(order);

				System.out.println("Ape selling " + numShare + " @ $" + sellPrice + " UUID - " + this.getId());
			}

			// add randomness to the actions
			this.apeState = this.model.ApeHandW2BuyorSell(this.apeState);

		});
	}

	public DoubleProperty getBalance() {
		return balance;
	}

	public DoubleProperty getBuyBidPercent() {
		return buyBidPercent;
	}

}
