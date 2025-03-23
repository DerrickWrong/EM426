package com.models.Agents;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.github.pnavais.machine.StateMachine;

import em426.agents.Agent;
import jakarta.annotation.PostConstruct;

@Component
@Scope("prototype")
public class Ape extends Agent{
 
	@Autowired
	@Qualifier("ApeState")
	private StateMachine apeState;
	
	@PostConstruct
	void init() {
		
		// listen and watch for stock volume change (additional float shares from Hedgie)
		
	}
	
}
