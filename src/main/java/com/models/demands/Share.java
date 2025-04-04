package com.models.demands;
  

import java.util.UUID;

import com.configurations.StockExchangeConfigurator;

import em426.api.SupplyState; 
 
 
public class Share {

	StockExchangeConfigurator stock;
	Double price, quantity;
	UUID owner; 
	SupplyState state;
 
	public Share(StockExchangeConfigurator stock) {
		this.stock = stock;
	}
	
	public Share(UUID owner, double price, double quantity) {
		this.owner = owner;
		this.price = price;
		this.quantity = quantity;
		this.state = SupplyState.ACTIVE;
	}
	
	public SupplyState getState() {
		return state;
	}

	public void setState(SupplyState state) {
		this.state = state;
	}
	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	public UUID getOwner() {
		return owner;
	}

	public void setOwner(UUID owner) {
		this.owner = owner;
	}
 
	public StockExchangeConfigurator getStock() {
		return stock;
	}

}
