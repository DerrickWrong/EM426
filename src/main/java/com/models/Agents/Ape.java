package com.models.Agents;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.configurations.AgentStateFactory;
import com.configurations.AgentStateFactory.ApeState;
import com.configurations.SimConfiguration;
import com.github.pnavais.machine.StateMachine;
import com.github.pnavais.machine.api.message.Messages;
import com.models.MarkovModel;
import com.models.demands.ShareInfo;
import com.models.demands.StockOrder;
import com.models.demands.StockOrder.type;
import com.utils.SimAgentTypeEnum;
import com.utils.Simulatible;

import em426.agents.Agent;
import em426.api.ActState;
import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Component
@Scope("prototype")
public class Ape extends Agent implements Simulatible {

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
	
	@Autowired
	SimConfiguration simConfig;

	@Override
	public void resetAgent() {
		// TODO Auto-generated method stub
		this.payDay = simConfig.payFrequency;
		this.buyBidPercent = simConfig.bidAbovePercent;
		this.initialBalance = simConfig.initialBalance;
		this.agentScaleFactor = simConfig.multiplier;
		this.shortDisclosureDelay = simConfig.disclosureDelay;
		
		this.balance = this.initialBalance * this.agentScaleFactor;
		 
		this.holdingshares = 0;
	}
	
	@PostConstruct
	void init() {
		
		this.apeState = this.agentFactory.ApeStateMachine();

		apeState.send(Messages.EMPTY); // move to observe state

		this.completedOrderFlux.filter(o -> {

			return o.getUUID() == this.getId() && o.getOrderState() == ActState.COMMITTED;

		}).subscribe(o -> {
			
			if(o.getOrderType() == type.BUY) {
				this.holdingshares = this.holdingshares + o.getNumOfShares();
				this.balance = 0; // clean out the balance 
				this.apeState.setCurrent(ApeState.HOLD);
			}
			
			if(o.getOrderType() == type.SELL) {
				
				this.balance = this.holdingshares * o.getBidPrice();
				this.holdingshares = 0; 
				this.apeState.setCurrent(ApeState.OBSERVE);
			}
			
		});
		
		// getting paid
		this.simulationClock.subscribe(d->{
			
			if(d % this.payDay == 0) {
				this.balance += this.initialBalance * this.agentScaleFactor;
			}
			
		});
		

		// combining two flux
		Flux.zip(this.simulationClock, this.shareInfoFlux).filter(d -> {

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
 
	}

	

}
