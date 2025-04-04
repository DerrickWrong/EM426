package com.models.Agents;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import em426.agents.Agent;

@Component
@PropertySource("classpath:stock.properties")
public class StockExchange extends Agent {

	@Value("${stockName}")
	private String stockName;

	@Value("${stockPrice}")
	private double stockInitPrice;

	@Value("${stockVolume}")
	private double stockTotalVolume;
	
	@Value("${insiderRatio}")
	private double insiderR;

	@Value("${insititueRatio}")
	private double instituteR;

	@Value("${shortedRatio}")
	private double shortedRatio;
	
	
	
	
	
	
}
