package com.models.demands;

import java.util.UUID;

import com.utils.SimAgentTypeEnum;

import em426.api.SupplyState;

public class Share  {

	final Double purchasedPrice;
	final int quantity;
	final UUID owner;
	SupplyState state;
	final SimAgentTypeEnum type;



	public final static Share EMPTY = new Share();

	private Share() {
		this.purchasedPrice = 0.0;
		this.quantity = 0;
		this.owner = UUID.randomUUID();
		this.state = SupplyState.INACTIVE;
		this.type = null;
	}

	public Share(UUID owner, double price, int quantity, SimAgentTypeEnum t) {
		this.owner = owner;
		this.purchasedPrice = price;
		this.quantity = quantity;
		this.state = SupplyState.ACTIVE;
		this.type = t;
	}

	public Share(Share s, int remainingShares) {
		this.owner = s.owner;
		this.purchasedPrice = s.purchasedPrice;
		this.quantity = remainingShares;
		this.state = s.state;
		this.type = s.type;
	}

	public Share combineShare(Share s) {

		int newQuantity = this.quantity + s.quantity;
		double newPrice = ((this.purchasedPrice * this.quantity) + (s.purchasedPrice * s.quantity)) / newQuantity;

		Share combinedShares = new Share(this.owner, newPrice, newQuantity, this.type);

		return combinedShares;
	}

	public SupplyState getState() {
		return state;
	}

	public void setState(SupplyState state) {
		this.state = state;
	}

	public Double getPrice() {
		return purchasedPrice;
	}

	public int getQuantity() {
		return quantity;
	}

	public UUID getOwner() {
		return owner;
	}
	
	public SimAgentTypeEnum getType() {
		return type;
	}

}
