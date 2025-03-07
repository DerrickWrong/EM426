package com.models;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.SpringFXManager;
import com.MIT.agents.Agent;

import jakarta.annotation.PostConstruct;
import javafx.util.Duration;
import javafx.util.Pair;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Component
public class SimulatedTradingMarket {

	private final Map<String, Agent> agentMap = new HashMap<>();

	private final Sinks.Many<Integer> turnSink = Sinks.many().multicast().directBestEffort();;

	private Integer counter = 0;

	@Autowired
	Flux<Pair<String, Float>> guessSink; // passively listen to all the guesses
	
	@PostConstruct
	private void setUp() {
		counter = 0;
		
		this.guessSink.subscribe(guess->{
			
			// store all the guess ("User Name" & "Guess Price")
			
			// Find Ecluidean Distance
			
			// Update Scores (Winner gets +1 and Losers get -1)
			
		});
		
	}

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

	public void moveTurn() {

		this.counter = this.counter + 1;
		this.turnSink.tryEmitNext(counter);
	}

	// listen to turn
	public Flux<Integer> listen() {
  
		return this.turnSink.asFlux();
	}

}
