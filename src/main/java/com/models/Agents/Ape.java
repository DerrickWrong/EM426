package com.models.Agents;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.github.pnavais.machine.StateMachine;
import com.models.MarkovModel;

import em426.agents.Agent;
import jakarta.annotation.PostConstruct;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

@Component
@Scope("prototype")
public class Ape extends Agent{
 
	private DoubleProperty balance = new SimpleDoubleProperty(2000);
	
	@Autowired
	@Qualifier("ApeState")
	private StateMachine apeState;
	
	@Autowired
	MarkovModel model;
	
	@PostConstruct
	void init() {
		
		// listen and watch for stock volume change (additional float shares from Hedgie)
		
		
		
		
		
	}
	
}
