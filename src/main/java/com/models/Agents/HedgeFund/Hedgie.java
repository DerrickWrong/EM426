package com.models.Agents.HedgeFund;
 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.models.demands.StockOrder;

import em426.agents.Agent; 
import jakarta.annotation.PostConstruct;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Component
public class Hedgie extends Agent {
 
	private DoubleProperty balance = new SimpleDoubleProperty(100000000);
	private DoubleProperty currBalance = new SimpleDoubleProperty(0);
	private DoubleProperty borrow2ShortRatio = new SimpleDoubleProperty(50); // % to borrow from institutional investors
	private DoubleProperty trigger2Cover= new SimpleDoubleProperty(50); // need to sell more or pay a higher premium    
	private DoubleProperty getMarginCall = new SimpleDoubleProperty(80); // % of loss to trigger margin call 
	private DoubleProperty cashoutProfitAt = new SimpleDoubleProperty(30); // % of profit to trigger a cashout 
	
	@Autowired
	@Qualifier("tradingClock")
	Flux<Long> tradingClockFlux;


	@Autowired
	@Qualifier("stockOrderStream")
	Sinks.Many<StockOrder> orderSink;
	
	@PostConstruct
	public void init() {

		// listen to trading clock
		tradingClockFlux.subscribe(d -> {
			
			// observe
			// select
			// act
		
		});
	}
	
	 

	public DoubleProperty getBalance() {
		return balance;
	}

	public DoubleProperty getCurrBalance() {
		return currBalance;
	}

	public DoubleProperty getBorrow2ShortRatio() {
		return borrow2ShortRatio;
	}

	public DoubleProperty getTrigger2Cover() {
		return trigger2Cover;
	}

	public DoubleProperty getGetMarginCall() {
		return getMarginCall;
	}

	public DoubleProperty getCashoutProfitAt() {
		return cashoutProfitAt;
	}

}
