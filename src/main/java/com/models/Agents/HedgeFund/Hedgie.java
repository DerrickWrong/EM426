package com.models.Agents.HedgeFund;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.configurations.AgentStateConfig.HedgieState;
import com.configurations.StockExchangeConfigurator;
import com.github.pnavais.machine.StateMachine;
import com.github.pnavais.machine.api.message.Messages;
import com.models.demands.ShareInfo;
import com.models.demands.StockOrder;
import com.models.demands.StockOrder.type;

import em426.agents.Agent;
import em426.api.ActState;
import jakarta.annotation.PostConstruct;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Component
public class Hedgie extends Agent {

	private double originalPrinciple = 7.5E9; // 7 billion dollar
	private double shortBidPercent = 0.95;
	private double marginReqPercent = 0.5;
	private int numberOfDump = 10; // default is 10 (1 is to short everythign at once)
	private int counter = numberOfDump;
	
	private double currSellingPrice;

	private DoubleProperty balance = new SimpleDoubleProperty(originalPrinciple);
	private DoubleProperty currBalance = new SimpleDoubleProperty(0);
	private DoubleProperty borrow2ShortRatio = new SimpleDoubleProperty(50); // % to borrow from institutional investors
	private DoubleProperty trigger2Cover = new SimpleDoubleProperty(50); // need to sell more or pay a higher premium
	private DoubleProperty getMarginCall = new SimpleDoubleProperty(80); // % of loss to trigger margin call
	private DoubleProperty cashoutProfitAt = new SimpleDoubleProperty(30); // % of profit to trigger a cashout

	@Autowired
	Sinks.Many<StockOrder> orderSink;

	@Autowired
	Flux<ShareInfo> shareInfoFlux;

	@Autowired
	Flux<StockOrder> processedOrderFlux;

	@Autowired
	@Qualifier("HedgieStateMachine")
	StateMachine stateMachine;
	
	@Autowired
	StockExchangeConfigurator stockExchange;

	@PostConstruct
	public void init() {

		double fund = this.originalPrinciple / this.numberOfDump;

		this.shareInfoFlux.sample(Duration.ofSeconds(10)).subscribe(s -> {

			if (stateMachine.getCurrent() == HedgieState.IDLE) {

				this.shortingStock(fund, s);
			}

		});

		// Processed Orders
		this.processedOrderFlux.filter(f -> {

			return (f.getUUID() == this.getId()) && (f.getActState() == ActState.COMMITTED);

		}).subscribe(po -> {

			if(this.stockExchange.peekStockShare().getPrice() == this.currSellingPrice) {
				return; // can't short more stock until supply has been consumed
			}
			 
			this.counter = this.counter - 1;
			
			if (this.counter > 0) {
				stateMachine.send(HedgieState.IDLEMSG);
			}
		});

	}

	private void shortingStock(double fund, ShareInfo s) {

		// kick off the simulation by sending shorting order
		currSellingPrice = shortBidPercent * s.getCurrentPrice();
		double share2Short = Math.round(fund / (marginReqPercent * s.getCurrentPrice()));
		balance.set(fund - share2Short * s.getCurrentPrice());
		StockOrder order = new StockOrder(this.getId(), type.SHORT, currSellingPrice, share2Short, "Hedgie");

		this.orderSink.tryEmitNext(order);
		this.balance.set(this.balance.get() - fund);

		System.out.println("Hedgie: selling " + share2Short + " which is " + (100.0 * share2Short / s.getCurrVolume())
				+ " % of all shares @ $" + currSellingPrice + ".");

		// move to BNS state
		stateMachine.send(Messages.EMPTY);
	}

	public DoubleProperty getBalance() {
		return balance;
	}

	public DoubleProperty getCurrBalance() {
		return currBalance;
	}

	public DoubleProperty getBorrow2ShortRatio() {
		return borrow2ShortRatio;
	}

	public DoubleProperty getTrigger2Cover() {
		return trigger2Cover;
	}

	public DoubleProperty getGetMarginCall() {
		return getMarginCall;
	}

	public DoubleProperty getCashoutProfitAt() {
		return cashoutProfitAt;
	}

}
