package com.models;

import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.stereotype.Component;

import com.configurations.AgentStateFactory.ApeState;
import com.configurations.AgentStateFactory.HedgieState;
import com.configurations.SimConfiguration;
import com.github.pnavais.machine.StateMachine; 

// This class defines the probabilities of agent state transition in AgentStateConfig. This agent class will define the triggers.
 
@Component 
public class MarkovModel { 
	
	@Autowired
	SimConfiguration simConfig;
	
	// For Apes
	public StateMachine ApeHandW2BuyorSell(StateMachine mach) {
		
		// Transition from hold and wait to either buy or sell
		if(Math.random() <= simConfig.diamondHandRatio) {
			
			return mach.send(ApeState.BUYMOREMESSAGE);
		}else {
			return mach.send(ApeState.SELLMESSAGE);
		}
	}
	
	// For Hedgies
	public StateMachine HedgieW2ShortOrCover(StateMachine mach) {
		
		if(Math.random() >= 0.5) {
			// 0.3 to double down...
			return mach.send(HedgieState.SMMESSAGE);
		}else {
			// 0.7 to cover and exit
			return mach.send(HedgieState.CMMESSAGE);
		}
		
	}
	
	
}
