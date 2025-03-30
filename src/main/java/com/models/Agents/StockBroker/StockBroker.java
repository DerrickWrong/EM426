package com.models.Agents.StockBroker;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.configurations.StockExchangeConfigurator;
import com.models.demands.StockOrder;

import em426.api.ActState;
import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Component
public class StockBroker {

	@Autowired
	Flux<StockOrder> stockOrderStream;

	@Autowired
	Flux<Long> tradingClockFlux;

	@Autowired
	StockExchangeConfigurator stonk;

	@Autowired
	Sinks.Many<StockOrder> completedOrderStream;
	
	Queue<StockOrder> ordersQueue = new ConcurrentLinkedQueue<>();

	@PostConstruct
	void init() {

		this.stockOrderStream.filter(order -> {
		
			// only accept unprocessed orders
			return order.getActState() == ActState.PENDING;
		
		}).subscribe(order -> {

			this.ordersQueue.add(order);
		});

		this.tradingClockFlux.subscribe(t -> {

			// this is where stock broker execute its work
			for (int i = 0; i < this.ordersQueue.size(); i++) {

				// fetch the order
				StockOrder ord = this.ordersQueue.poll();

				// meet the demand
				ord = stonk.processStockOrder(ord);

				// notify the buyer/seller
				completedOrderStream.tryEmitNext(ord);
			}

		});
	}
}
