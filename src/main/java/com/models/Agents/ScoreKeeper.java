package com.models.Agents;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.models.demands.Share;
import com.models.demands.ShareInfo;
import com.models.demands.StockOrder;

import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Flux;

@Component
public class ScoreKeeper {

	@Autowired
	Flux<StockOrder> completedOrderFlux;
	
	@Autowired
	Flux<ShareInfo> shareInfoFlux;
	
	ShareInfo info;
	
	final List<StockOrder> completedOrders = new ArrayList<>();
	
	StockLender lender;
	double hedgieInvestment;
	UUID hedgieUUID;
	
	
	// This role of this class if to compute the result 
	@PostConstruct
	void init() {
		
		this.completedOrderFlux.subscribe(order->{
			
			this.completedOrders.add(order);
			
		});
		
		this.shareInfoFlux.subscribe(i->{
			
			this.info = i;
		});
	}
	
	public void setKeeper(StockLender lender, double hedgieInvestment, UUID hedgieID) {
		
		this.lender = lender;
		this.hedgieInvestment = hedgieInvestment;
		this.hedgieUUID = hedgieID;
	}
	
	public double computeHedgieGainOrLoss() {
		
		Share borrowedShares = this.lender.getBorrowersTab().get(this.hedgieUUID);
		
		double gainOrLoss = borrowedShares.getQuantity() * this.info.getCurrentPrice();
		
		this.completedOrders.clear();
		
		return this.hedgieInvestment - gainOrLoss;
	}
	
	
}
