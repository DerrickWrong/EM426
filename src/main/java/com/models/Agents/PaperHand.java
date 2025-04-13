package com.models.Agents;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.github.pnavais.machine.StateMachine;
import com.models.MarkovModel;
import com.models.demands.ShareInfo;
import com.models.demands.StockOrder;

import em426.agents.Agent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Component
@Scope("prototype")
public class PaperHand extends Agent {

	@Autowired
	@Qualifier("ApeStateMachine")
	private StateMachine apeState;

	@Autowired
	MarkovModel model;

	@Autowired
	Flux<ShareInfo> shareInfoFlux;

	@Autowired
	@Qualifier("stockOrderStream")
	Sinks.Many<StockOrder> stockOrderStream;

	@Autowired
	@Qualifier("completedOrderFlux")
	Flux<StockOrder> completedOrderFlux;
	 

	public PaperHand() {
	}
	 
	
}
