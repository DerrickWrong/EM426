package com.models.demands;

import java.util.UUID;

import em426.api.ActState;

public class StockOrder {

	public enum type {
		BUY, SELL, SHORT, COVER
	}

	private double bidPrice; 
	private double numOfShares; 
	private final UUID orginator;
	private final type orderType;
	private final long timeRequested;
	private ActState orderStatus;

	public StockOrder(UUID originator, type orderType, double bidPrice, double numShare, long simTimeRequested) {

		this.orginator = originator;
		this.orderType = orderType;
		this.bidPrice = bidPrice;
		this.numOfShares = numShare;
		this.timeRequested = simTimeRequested;
		this.orderStatus = ActState.START; 
	}

	public void changeStatus(ActState s) {
		this.orderStatus = s;
	}

	public ActState getStatus() {
		return this.orderStatus;
	}
 

	public long getTimeRequested() {
		return timeRequested;
	}

	public double getBidPrice() {
		return bidPrice;
	} 

	public double getNumShare() {
		return numOfShares;
	}

	public UUID getOrginator() {
		return orginator;
	}

	public type getOrderType() {
		return orderType;
	}
	
	public double getNumOfShares() {
		return numOfShares;
	}

	public ActState getOrderStatus() {
		return orderStatus;
	}
	
	public void setBidPrice(double bidPrice) {
		this.bidPrice = bidPrice;
	}
	
 
 

}
