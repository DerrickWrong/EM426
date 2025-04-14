package com.models.Agents;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.models.StockExchange;
import com.models.demands.Share;
import com.models.demands.ShareInfo;
import com.models.demands.StockOrder;

import em426.agents.Agent;
import jakarta.annotation.PostConstruct;
import javafx.util.Pair;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Component
@Scope("prototype")
public class StockLender extends Agent{

	@Autowired
	Flux<ShareInfo> shareInfoFlux;
 
	private double triggerMarginPercentage;

	AtomicReference<ShareInfo> cache = new AtomicReference<ShareInfo>();
	// lend stocks and margin call

	final Map<UUID, Share> borrowersTab = new HashMap<>();

	

	@Autowired
	Sinks.Many<Pair<UUID, Share>> marginCallSink;
	
	@Autowired
	StockExchange exchange;

	public StockLender() {}
	
	public void setMargineCall(double triggerMarginCall) {
		
		this.triggerMarginPercentage = triggerMarginCall;
		
	}
	
	
	@PostConstruct
	void init() {

		this.shareInfoFlux.subscribe(info -> {

			this.cache.set(info);

			// check tab and see if margin call is needed
			this.checkTab(info);

		});

	}

	// The magic method of creating new shares. :) 
	public void borrowStock(StockOrder borrowOrder) {

		// create shares to be borrowed
		Share borrowShares = new Share(borrowOrder.getUUID(), borrowOrder.getBidPrice(), borrowOrder.getNumOfShares(),
				borrowOrder.getAgentType());

		// see if the borrower existed
		if (borrowersTab.containsKey(borrowOrder.getUUID())) {

			// updates the borrower's shares
			borrowersTab.get(borrowShares.getOwner()).combineShare(borrowShares);

		} else {

			Share newBorrowShares = new Share(borrowOrder.getUUID(), borrowOrder.getBidPrice(),
					borrowOrder.getNumOfShares(), borrowOrder.getAgentType());

			this.borrowersTab.put(borrowOrder.getUUID(), newBorrowShares);
		}

		this.exchange.submitShortOrder(borrowOrder, borrowShares);
		
	}

	public void checkTab(ShareInfo info) {

		for (Map.Entry<UUID, Share> entry : this.borrowersTab.entrySet()) {

			double deltaPercent = 100.0
					* (info.getCurrentPrice() - entry.getValue().getPrice()) / info.getCurrentPrice();

			if (deltaPercent > this.triggerMarginPercentage) {

				this.marginCallSink.tryEmitNext(new Pair<>(entry.getKey(), entry.getValue()));
			}
		}

	}
	
	public Map<UUID, Share> getBorrowersTab() {
		return borrowersTab;
	}

	
}
