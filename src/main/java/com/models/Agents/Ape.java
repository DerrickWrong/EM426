package com.models.Agents;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.configurations.AgentStateFactory;
import com.configurations.AgentStateFactory.ApeState;
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
import jakarta.annotation.PreDestroy;
import reactor.core.Disposable;
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
	private double initialBalance;

	@Autowired
	AgentStateFactory agentFactory;

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

	// disposables
	Disposable completeStreamDisposable, clockDisposable, zipDisposable;

	public Ape(double initialBalance, int numAgent, long disclosureDelay, double bidAbovePercent,
			long payInvestFrequency) {

		this.agentScaleFactor = numAgent;
		this.shortDisclosureDelay = disclosureDelay;
		this.buyBidPercent = bidAbovePercent;
		this.payDay = payInvestFrequency;
		this.initialBalance = initialBalance;
		this.balance = initialBalance * this.agentScaleFactor;
	}

	@PreDestroy
	void destroy() {
  
		this.completeStreamDisposable.dispose();
		this.zipDisposable.dispose();
		this.clockDisposable.dispose();
		
	}

	@PostConstruct
	void init() {

		this.apeState = this.agentFactory.ApeStateMachine();

		apeState.send(Messages.EMPTY); // move to observe state

		this.completeStreamDisposable = completedOrderFlux.filter(o -> {

			return o.getUUID() == this.getId() && o.getOrderState() == ActState.COMMITTED;

		}).subscribe(o -> {

			if (o.getOrderType() == type.BUY) {
				this.holdingshares = this.holdingshares + o.getNumOfShares();
				this.balance = 0; // clean out the balance
				this.apeState.setCurrent(ApeState.HOLD);
			}

			if (o.getOrderType() == type.SELL) {

				this.balance = this.holdingshares * o.getBidPrice();
				this.holdingshares = 0;
				this.apeState.setCurrent(ApeState.OBSERVE);
			}

		});

		// getting paid
		this.clockDisposable = simulationClock.subscribe(d -> {

			if (d % this.payDay == 0) {
				this.balance += this.initialBalance * this.agentScaleFactor;
			}

		});

		// combining two flux
		zipDisposable = Flux.zip(this.simulationClock, this.shareInfoFlux).filter(d -> {

			return d.getT1() >= this.shortDisclosureDelay && (this.balance > 0);

		}).subscribe(data -> {

			this.model.ApeHandW2BuyorSell(this.apeState);

			if (apeState.getCurrent() == ApeState.OBSERVE) {

				apeState.send(ApeState.BUYMOREMESSAGE);
			}

			if (apeState.getCurrent() == ApeState.BUY) {

				long elapsedTime = (data.getT1() - this.shortDisclosureDelay);

				if (elapsedTime % this.payDay == 0) {

					double bidPrice = this.buyBidPercent * data.getT2().getCurrentPrice();
					int numShares = (int) (this.balance / bidPrice);

					StockOrder order = new StockOrder(this.getId(), type.BUY, bidPrice, numShares,
							SimAgentTypeEnum.Retail, data.getT1());

					this.stockOrderStream.tryEmitNext(order);

				}
			}

			if (apeState.getCurrent() == ApeState.SELL) {

				double bidPrice = data.getT2().getCurrentPrice() * (2.0 - this.buyBidPercent);

				StockOrder order = new StockOrder(this.getId(), type.SELL, bidPrice, (int) this.holdingshares,
						SimAgentTypeEnum.Retail, data.getT1());

				this.stockOrderStream.tryEmitNext(order);

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
