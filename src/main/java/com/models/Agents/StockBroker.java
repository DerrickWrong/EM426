package com.models.Agents;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.models.StockExchange;
import com.models.demands.StockOrder;
import com.models.demands.StockOrder.type;

import em426.agents.Agent;
import em426.api.ActState;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Scheduler;

@Component
@Scope("prototype")
public class StockBroker extends Agent {

	// processing delay for all buy orders
	private Duration delay = Duration.ZERO;

	@Autowired
	@Qualifier("stockOpenOrderFlux")
	Flux<StockOrder> stockOrderStream;

	@Autowired
	Flux<Long> tradingClockFlux;

	@Autowired
	StockExchange wallStreet;

	@Autowired
	@Qualifier("completedOrder")
	Sinks.Many<StockOrder> completedOrderStream;
	
	@Autowired
	@Qualifier("sellScheduler")
	Scheduler sellerScheduler;

	private StockLender lender;
	
	Disposable a1, a2, a3;

	public StockBroker() {
	}

	public void setLender(StockLender lender) {
		this.lender = lender;
	}

	public void setBuyOrderDelay(long milliseconds) {
		
		if(milliseconds == 0L) {
			this.delay = Duration.ZERO;
		}
		
		this.delay = Duration.ofMillis(milliseconds);
	}

	@PreDestroy
	void destroy() {
		this.a1.dispose();
		this.a2.dispose();
		this.a3.dispose();
	}
	
	@PostConstruct
	void init() {

		// listen to stock orders
		// listen for all traditional sell orders
		a1 = this.stockOrderStream.filter(order -> {

			return order.getActState() == ActState.START
					&& (order.getOrderType() == type.SELL || order.getOrderType() == type.COVER);

		}).publishOn(sellerScheduler).subscribe(order -> {

			// look up the share owned by the seller
			this.wallStreet.submitOrder(order, order.getOrderRequestedAtTime());

		});

		a2 = this.stockOrderStream.filter(order -> {

			return order.getActState() == ActState.START && order.getOrderType() == type.BUY;

		}).delayElements(this.delay).publishOn(sellerScheduler).subscribe(order -> {

			this.wallStreet.submitOrder(order, order.getOrderRequestedAtTime());
		});

		a3 = this.stockOrderStream.filter(order -> {

			return order.getActState() == ActState.START && order.getOrderType() == type.SHORT;

		}).publishOn(sellerScheduler).subscribe(order -> {

			this.lender.borrowStock(order);
			this.wallStreet.submitOrder(order, order.getOrderRequestedAtTime());
		});

	}

}
