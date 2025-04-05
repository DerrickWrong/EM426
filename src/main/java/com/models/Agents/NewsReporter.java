package com.models.Agents;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.models.StockExchange;
import com.models.demands.Share;
import com.models.demands.ShareInfo;

import em426.agents.Agent;
import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;


/**
 * This class is to report the current market status to all agents. 
 * In a sense, this could drive the behavior and action of agents.
 * */

@Component
public class NewsReporter extends Agent {

	@Autowired
	Sinks.Many<ShareInfo> shareInfoStream;
	
	@Autowired
	StockExchange wallStreet;
	
	@Autowired
	Flux<Long> simulationClock;
	
	@PostConstruct
	void init() {
		
		this.simulationClock.subscribe(t->{
			
			Share currShare = this.wallStreet.getStockListing().sellingSharesQueue.peek();
			
			if(currShare == null) {
				return;
			}
			
			ShareInfo info = new ShareInfo(currShare.getPrice(), currShare.getQuantity(), t);
			
			this.shareInfoStream.tryEmitNext(info);
		});

	}
	
	// TODO - add compute pie ratio here
	
	
	
}
