package com.models;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.models.demands.RedditDataFactory; 

import jakarta.annotation.PostConstruct;


@Component
public class SimulatedTradingMarket {
 
	
	@Autowired
	RedditDataFactory dataFactory;
	
	
	@PostConstruct
	void setup() {
		
		
		
	}
	
	
	
	
	
}
