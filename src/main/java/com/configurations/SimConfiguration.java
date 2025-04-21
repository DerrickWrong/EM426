package com.configurations;

import org.springframework.stereotype.Component;

@Component
public class SimConfiguration {

	// Simulation Level
	public int MonteCarloTrials = 1;
	public int simulationDuration = 100;
	
	// Market
	public double buyRatio = 1.05;
	
	// Mutual Fund 
	public double mutualBuyRatio = 1.05;
	
	// Hedgie parameters
	public double shortRatio = 10;
	public double dicountPercent =95;
	public long brokerDelay = 0L; 
	
	// Agent Parameters
	public int numOfAgents = 10;
	public double initialBalance = 500;
	public int multiplier = 1000000;
	public long disclosureDelay = 15;
	public double bidAbovePercent = 1.05;
	public long payFrequency = 10;
	public double agentMix = 0.0;

}
