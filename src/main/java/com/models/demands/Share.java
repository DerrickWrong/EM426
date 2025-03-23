package com.models.demands;
 
import java.util.UUID;

import com.configurations.StockExchangeConfigurator; 
 

// This is the supply?
public class Share {

	final StockExchangeConfigurator stock;
	Double price, quantity;
	UUID owner; 

	public Share(StockExchangeConfigurator stock) {
		this.stock = stock;
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
