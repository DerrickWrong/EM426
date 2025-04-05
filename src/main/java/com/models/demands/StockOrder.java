package com.models.demands;

import java.util.UUID;

import com.utils.SimAgentTypeEnum;

import em426.api.ActState;

public class StockOrder {

	public enum type {
		BUY, SELL, SHORT, COVER
	}

	private final type ordertype;
	private ActState orderState;

	private final double price;
	private final int numOfShares;
	private final UUID orginator;
	private final SimAgentTypeEnum agentType;

	private final long orderRequestedAtTime;

	private long sellOrderExpirationTime = 160L; // 5 days listing with 160 ticks per 15 min for sell order

	public final static StockOrder INVALID = new StockOrder(null, null, 0.0, 0, null, 0L);

	public StockOrder(UUID originator, type orderType, double bidPrice, int numShare, SimAgentTypeEnum agentType,
			long orderRequestedAtTime) {

		this.orginator = originator;
		this.agentType = agentType;

		this.price = bidPrice;
		this.numOfShares = numShare;

		this.ordertype = orderType;
		this.orderState = ActState.START;
		this.orderRequestedAtTime = orderRequestedAtTime;
	}

	public StockOrder(StockOrder order, double soldPrice, int remaining, ActState state, long orderRequestedAtTime) {
		this.orginator = order.orginator;
		this.price = soldPrice;
		this.numOfShares = remaining;
		this.agentType = order.agentType;
		this.ordertype = order.ordertype;
		this.orderState = state;
		this.orderRequestedAtTime = orderRequestedAtTime;
	}

	public StockOrder(StockOrder order, ActState status, long currTime) {

		this.orginator = order.orginator;
		this.agentType = order.agentType;
		this.price = order.price;
		this.numOfShares = order.numOfShares;
		this.ordertype = order.ordertype;
		this.orderState = status;
		this.orderRequestedAtTime = currTime;

	} 

	public type getOrderType() {
		return this.ordertype;
	}

	public ActState getActState() {
		return this.orderState;
	}

	public ActState getOrderState() {
		return orderState;
	}

	public double getBidPrice() {
		return price;
	}

	public int getNumOfShares() {
		return numOfShares;
	}

	public UUID getUUID() {

		return orginator;
	}

	public SimAgentTypeEnum getAgentType() {
		return agentType;
	}

	public long getOrderRequestedAtTime() {
		return orderRequestedAtTime;
	}

	public long getSellOrderExpirationTime() {
		return sellOrderExpirationTime;
	}

	public void setSellOrderExpirationTime(long sellOrderExpirationTime) {
		this.sellOrderExpirationTime = sellOrderExpirationTime;
	}

}
