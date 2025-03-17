package com.models.demands;
 
import com.models.demands.Stock.Category;

import em426.agents.Agent;

public class Share {

	final Stock stock;
	Double price, quantity;
	Agent owner;
	Category cat;

	public Share(Stock stock) {
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

	public Stock getStock() {
		return stock;
	}

}
