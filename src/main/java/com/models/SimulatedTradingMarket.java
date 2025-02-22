package com.models;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.models.demands.RedditDataFactory;
import com.models.interfaces.DemandStream; 

import jakarta.annotation.PostConstruct;


@Component
public class SimulatedTradingMarket {

	@Autowired
	private DemandStream demandStream;
	
	@Autowired
	RedditDataFactory dataFactory;
	
	
	@PostConstruct
	void setup() {
		
		
		
	}
	
	
	
	
	
}
