package com.models.demands;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import em426.agents.Agent;
import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Component
public class HedgeFund extends Agent {

	public enum State {
		IDLE, SELLNWAIT, BUY2COVER, BANKRUPT;
	}

	private State currentState = State.IDLE;

	private double balanaceInMil = 100;
	private double currentBalance = 0d;

	private double borrow2Short = 50;
	private double triggerCover = 10;
	private double decay2Sell = 5;
	private double cashoutAt = 10; // stock dropped to 10% of its purchase value
 

	@Autowired
	@Qualifier("tradingClock")
	Flux<Long> tradingClockFlux;

	@Autowired
	@Qualifier("stockOrderStream")
	Sinks.Many<StockOrder> stockOrderStream;

	AtomicLong dateTracker = new AtomicLong();

	@PostConstruct
	public void init() {

		// listen to trading clock
		tradingClockFlux.subscribe(d -> {

			this.dateTracker.set(d);
		
		});
	}

	public double getBalanaceInMil() {
		return balanaceInMil;
	}

	public void setBalanaceInMil(double balanaceInMil) {
		this.balanaceInMil = balanaceInMil;
	}

	public double getCurrentBalance() {
		return currentBalance;
	}

	public void setCurrentBalance(double currentBalance) {
		this.currentBalance = currentBalance;
	}

	public double getBorrow2Short() {
		return borrow2Short;
	}

	public void setBorrow2Short(double borrow2Short) {
		this.borrow2Short = borrow2Short;
	}

	public double getTriggerCover() {
		return triggerCover;
	}

	public void setTriggerCover(double triggerCover) {
		this.triggerCover = triggerCover;
	}

	public double getDecay2Sell() {
		return decay2Sell;
	}

	public void setDecay2Sell(double decay2Sell) {
		this.decay2Sell = decay2Sell;
	}

	public double getCashoutAt() {
		return cashoutAt;
	}

	public void setCashoutAt(double cashoutAt) {
		this.cashoutAt = cashoutAt;
	}

}
