package com.models;

import java.util.HashMap;
import java.util.Map; 
import org.springframework.stereotype.Component;

import com.SpringFXManager;
import com.MIT.agents.Agent;
 

@Component
public class SimulatedTradingMarket {

	private final Map<String, Agent> agentMap = new HashMap<>();

 

	public Agent createOrFindAgent(String name) {

		if (!agentMap.containsKey(name)) {

			Agent tempAgent = SpringFXManager.getInstance().getSpringContext().getBean(Agent.class);
			tempAgent.setName(name);
			tempAgent.setScore(10); // default starting score
			agentMap.put(tempAgent.getName(), tempAgent);

			return tempAgent;
		} else {

			return agentMap.get(name);
		}
	}

}
