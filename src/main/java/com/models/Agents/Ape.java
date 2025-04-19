package com.models.Agents;

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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Component
@Scope("prototype")
public class Ape extends Agent {

	private long payDay = 10; // every biweekly
	private double buyBidPercent = 1.05; // default 5% higher than the sell price
	private double balance = 500; // initial balanace
	private int agentScaleFactor = 1; // number of agents to scale
	private double holdingshares = 0;
	private long shortDisclosureDelay = 30; // time for hedge fund to report short interest

	@Autowired
	@Qualifier("ApeStateMachine")
	private StateMachine apeState;

	@Autowired
	MarkovModel model;

	@Autowired
	Flux<ShareInfo> shareInfoFlux;

	@Autowired
	Flux<Long> simulationClock;

	@Autowired
	@Qualifier("stockOrderStream")
	Sinks.Many<StockOrder> stockOrderStream;

	@Autowired
	@Qualifier("completedOrderFlux")
	Flux<StockOrder> completedOrderFlux;

	public Ape(double initialBalance, int numAgent, long disclosureDelay, double bidAbovePercent,
			long payInvestFrequency) {

		this.agentScaleFactor = numAgent;
		this.shortDisclosureDelay = disclosureDelay;
		this.buyBidPercent = bidAbovePercent;
		this.payDay = payInvestFrequency;

		this.balance = initialBalance * this.agentScaleFactor;
	}

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
				this.balance = 0; // clean out the balance
			}

			apeState.send(Messages.EMPTY);// move to hold state
		});

		// combining two flux
		Flux.zip(this.simulationClock, this.shareInfoFlux).filter(d -> {

			return d.getT1() >= this.shortDisclosureDelay;

		}).subscribe(data -> {

			if (apeState.getCurrent() == ApeState.OBSERVE) {
 
				double bidPrice = this.buyBidPercent * data.getT2().getCurrentPrice();
				int numShares = (int) (this.balance / bidPrice);

				StockOrder order = new StockOrder(this.getId(), type.BUY, bidPrice, numShares, SimAgentTypeEnum.Retail,
						data.getT1());
				
				this.stockOrderStream.tryEmitNext(order);
				
				System.out.println("Agent " + this.getId() + " buying at $" + data.getT2().getCurrentPrice() + " on "
						+ data.getT1() + "day - number of shares " + numShares);

				apeState.send(ApeState.BUYMOREMESSAGE);
			}

			if (apeState.getCurrent() == ApeState.BUY) {

				long elapsedTime = (data.getT1() - this.shortDisclosureDelay);

				if (elapsedTime % this.payDay == 0) {

					double bidPrice = this.buyBidPercent * data.getT2().getCurrentPrice();
					int numShares = (int) (this.balance / bidPrice);

					StockOrder order = new StockOrder(this.getId(), type.BUY, bidPrice, numShares, SimAgentTypeEnum.Retail,
							data.getT1());
					
					this.stockOrderStream.tryEmitNext(order);
					
					System.out.println("Agent " + this.getId() + " buying at $" + data.getT2().getCurrentPrice()
							+ " on " + data.getT1() + "day (payday) - number of shares " + numShares);

				}

			}

		});

		// listen and watch for stock volume change (additional float shares from
		// Hedgie)
		/*
		 * shareInfoFlux.subscribe(shareInfo -> {
		 * 
		 * // getting paid this.balance.set(this.balance.get() + this.salary4Invest);
		 * 
		 * if (apeState.getCurrent() == ApeState.BUY) {
		 * 
		 * double buyPrice = shareInfo.getCurrentPrice() * buyBidPercent.get(); // this
		 * should be a bid price int numShare = (int) (this.balance.get() / buyPrice);
		 * 
		 * StockOrder order = new StockOrder(this.getId(), type.BUY, buyPrice, numShare,
		 * SimAgentTypeEnum.Retail, shareInfo.getTimestamp());
		 * this.stockOrderStream.tryEmitNext(order);
		 * 
		 * System.out.println("Ape buying " + numShare + " @ $" + buyPrice + " UUID - "
		 * + this.getId()); }
		 * 
		 * if (apeState.getCurrent() == ApeState.HOLD) {
		 * 
		 * if (this.balance.get() >= initialBalance) {
		 * System.out.println("Ape Buy more " + this.getId());
		 * apeState.send(ApeState.BUYMOREMESSAGE); // go back to buy state } }
		 * 
		 * // add randomness to the actions this.apeState =
		 * this.model.ApeHandW2BuyorSell(this.apeState);
		 * 
		 * });
		 */
	}

}
