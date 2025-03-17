package com.models.demands;
 
import com.configurations.StockConfigurator;
import com.configurations.StockConfigurator.Category;

import em426.agents.Agent;

public class Share {

	final StockConfigurator stock;
	Double price, quantity;
	Agent owner;
	Category cat;

	public Share(StockConfigurator stock) {
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

	public Agent getOwner() {
		return owner;
	}

	public void setOwner(Agent owner) {
		this.owner = owner;
	}

	public Category getCat() {
		return cat;
	}

	public void setCat(Category cat) {
		this.cat = cat;
	}

	public StockConfigurator getStock() {
		return stock;
	}

}
