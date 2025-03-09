package com.models.demands;

import com.MIT.agents.Demand;

public class Stock extends Demand{
	
	private float currentPrice;
	private int currVolume;
	private float shortInterestRatio; 
	private final String symbol; 
	
	
	public float getCurrentPrice() {
		return currentPrice;
	}

	public void setCurrentPrice(float currentPrice) {
		this.currentPrice = currentPrice;
	}

	public int getCurrVolume() {
		return currVolume;
	}

	public void setCurrVolume(int currVolume) {
		this.currVolume = currVolume;
	}

	public float getShortInterestRatio() {
		return shortInterestRatio;
	}

	public void setShortInterestRatio(float shortInterestRatio) {
		this.shortInterestRatio = shortInterestRatio;
	}

	

	
	
	public String getSymbol() {
		return symbol;
	}

	public Stock(String name) {
		this.symbol = name;
	}
	
	
	
	
}
