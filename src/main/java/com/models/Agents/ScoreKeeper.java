package com.models.Agents;

import java.util.ArrayList;
import java.util.List; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.models.Agents.HedgeFund.HedgeFund;
import com.models.demands.Share;
import com.models.demands.ShareInfo;
import com.models.demands.StockOrder;
import com.utils.Simulatible;

import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Flux;

@Component
public class ScoreKeeper implements Simulatible{

	@Autowired
	Flux<StockOrder> completedOrderFlux;
	
	@Autowired
	Flux<ShareInfo> shareInfoFlux;
	
	ShareInfo info;
	
	final List<StockOrder> completedOrders = new ArrayList<>();
	
	@Autowired
	StockLender lender;
	
	@Autowired
	HedgeFund hedgie;
	
	
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
	 
	public double computeHedgieGainOrLoss() {
		
		Share borrowedShares = this.lender.getBorrowersTab().get(this.hedgie.getId());
		
		if(borrowedShares == null) {
			return 0;
		}
		
		double gainOrLoss = borrowedShares.getQuantity() * this.info.getCurrentPrice();
		
		this.completedOrders.clear();
		
		return this.hedgie.getTab() - gainOrLoss;
	}

	@Override
	public void resetAgent() {
		// TODO Auto-generated method stub
		this.completedOrders.clear();
	}
	
	
}
