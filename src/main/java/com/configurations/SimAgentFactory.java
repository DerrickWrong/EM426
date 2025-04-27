package com.configurations;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration; 

import com.models.Agents.Ape; 

import em426.agents.Agent;

@Configuration
public class SimAgentFactory {

	private final ConfigurableApplicationContext springContext;
	
	@Autowired
	public SimAgentFactory(ConfigurableApplicationContext appContext) {
		this.springContext = appContext;
	}
	
	public List<Ape> createApe(int numAgent) {

		List<Ape> apeList = new ArrayList<>();
		
		for(int i = 0; i < numAgent; i++) {
			Ape ape = (Ape) this.springContext.getBean(Ape.class);
			apeList.add(ape);
		}
	
		return apeList;
	}

	public void destroyAgent(Agent agent) {
		this.springContext.getBeanFactory().destroyBean(agent);
	}
}
