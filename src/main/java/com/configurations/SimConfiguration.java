package com.configurations;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Component;

@Component
public class SimConfiguration {

	// Simulation Level
	public int MonteCarloTrials = 1;
	public int simulationDuration = 100;
	public AtomicInteger monteCarloCounter = new AtomicInteger(0);
	
	// Market
	public double buyRatio = 1.05;
	
	// Mutual Fund 
	public double mutualBuyRatio = 1.05;
	
	// Hedgie parameters
	public double shortRatio = .1;
	public double shortSellDisountRate = .95;
	public long brokerDelay = 0L; 
	
	// Agent Parameters
	public int numOfAgents = 1;
	public double initialBalance = 500;
	public int multiplier = 100000;
	public long disclosureDelay = 30;
	public double bidAbovePercent = 1.05;
	public long payFrequency = 10;
	public double diamondHandRatio = 0.5;

}
