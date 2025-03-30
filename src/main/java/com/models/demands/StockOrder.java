package com.models.demands;

import java.util.UUID;

import em426.api.ActState;

public class StockOrder {

	public enum type {
		BUY, SELL, SHORT, COVER
	}

	private final type ot;
	private ActState orderState;

	private final double bidPrice, numOfShares;
	private final String orignatorUUID, type;
	private String status;

	public StockOrder(UUID originator, type orderType, double bidPrice, double numShare) {

		this.orignatorUUID = "Hedgie_" + originator.toString();

		this.bidPrice = bidPrice;
		this.numOfShares = numShare;

		this.type = orderType.toString();

		this.ot = orderType;
		this.status = ActState.START.toString();
		this.orderState = ActState.START;

	}
	
	public void changeStatus(ActState s) {
		this.orderState = s;
		this.status = s.toString();
	}

	public type getOrderType() {
		return this.ot;
	}
	
	public ActState getActState() {
		return this.orderState;
	}

	public ActState getOrderState() {
		return orderState;
	}


	public double getBidPrice() {
		return bidPrice;
	}


	public double getNumOfShares() {
		return numOfShares;
	}


	public String getOrignatorUUID() {
		return orignatorUUID;
	}


	public String getType() {
		return type;
	}


	public String getStatus() {
		return status;
	}

}
