package com.models;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
 
import com.models.demands.StockOrder;

import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Component
public class StockBroker {
   
	@Autowired
	@Qualifier("stockOrderStream")
	Sinks.Many<StockOrder> orderSink;
	
	@Autowired
	@Qualifier("stockOrderFlux")
	Flux<StockOrder> processedOrderStream;
	 
	@Autowired
	@Qualifier("tradingClock")
	Flux<Long> tradingClockFlux;	 
 
	@PostConstruct
	void init() {

	}

	public void submitOrder(StockOrder order) {
		this.orderSink.tryEmitNext(order);
	}

}
