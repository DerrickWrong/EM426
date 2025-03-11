package com.models.demands;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.MIT.agents.Agent;
import com.MIT.agents.AgentActable;
import com.models.StockBroker;

import jakarta.annotation.PostConstruct;

@Component
public class HedgeFund extends Agent implements AgentActable {

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
	MarketAndLenders marketLender;

	@Autowired
	StockBroker broker;

	AtomicLong dateTracker = new AtomicLong();

	@PostConstruct
	public void init() {

		// listen to trading clock
		this.broker.listen2TradingClock().subscribe(d -> {

			this.dateTracker.set(d); 
			this.select();
			this.observe();
			this.act();
		});

		this.broker.listen2ProcessedOrder().filter(d -> {

			return d.getOrginator() == this.getId();

		}).subscribe(processedOrder -> {
			
			double cost = processedOrder.getBidPrice() * processedOrder.getNumSharePerMil() * 1000;
			
			// update the balances
			this.currentBalance = (this.balanaceInMil * 1000);
			

		});

	}

	@Override
	public void act() {
		// TODO Auto-generated method stub

	}

	@Override
	public void select() {
		// TODO Auto-generated method stub
		if (this.currentState == State.IDLE) {

			Double bidPrice = this.marketLender.getInitalPrice() * (100 - this.decay2Sell) / 100;
			Double vol = (this.borrow2Short / 100) * this.marketLender.getInstitutionShare() * this.marketLender.getVolume();

			StockOrder order = new StockOrder(this.getId(), StockOrder.type.SHORT, bidPrice, vol,
					this.dateTracker.get());

			this.broker.submitOrder(order);

			// set to next state
			this.currentState = State.SELLNWAIT;
		}
	}

	@Override
	public void observe() {
		// TODO Auto-generated method stub

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
