package com.models.demands;

import java.util.UUID;

public class StockOrder {

	public enum type {
		BUY, SELL, SHORT, COVER
	}

	private final double bidPrice;
	private boolean OrderProcessed = false; // flag toggled by Stock Broker class
	private final double numOfShares;
	private final UUID orginator;
	private final type orderType;
	private final long timeRequested;

	public StockOrder(UUID originator, type orderType, double bidPrice, double volPerMil, long simTimeRequested) {

		this.orginator = originator;
		this.orderType = orderType;
		this.bidPrice = bidPrice;
		this.numOfShares = volPerMil;
		this.timeRequested = simTimeRequested;
	}

	public void setTrue2ProcessedOrder() {
		this.OrderProcessed = true;
	}

	public long getTimeRequested() {
		return timeRequested;
	}

	public double getBidPrice() {
		return bidPrice;
	}

	public boolean isOrderProcessed() {
		return OrderProcessed;
	}

	public double getNumSharePerMil() {
		return numOfShares;
	}

	public UUID getOrginator() {
		return orginator;
	}

	public type getOrderType() {
		return orderType;
	}

}
