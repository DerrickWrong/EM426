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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

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

	private StockLender lender;

	public StockBroker() {
	}

	public void setLender(StockLender lender) {
		this.lender = lender;
	}

	public void setBuyOrderDelay(long milliseconds) {
		this.delay = Duration.ofMillis(milliseconds);
	}

	@PostConstruct
	void init() {

		// listen to stock orders
		// listen for all traditional sell orders
		this.stockOrderStream.filter(order -> {

			return order.getActState() == ActState.START
					&& (order.getOrderType() == type.SELL || order.getOrderType() == type.COVER);

		}).subscribe(order -> {

			// look up the share owned by the seller
			this.wallStreet.submitOrder(order, order.getOrderRequestedAtTime());

		});

		this.stockOrderStream.filter(order -> {

			return order.getActState() == ActState.START && order.getOrderType() == type.BUY;

		}).delayElements(this.delay).subscribe(order -> {

			this.wallStreet.submitOrder(order, order.getOrderRequestedAtTime());
		});

		this.stockOrderStream.filter(order -> {

			return order.getActState() == ActState.START && order.getOrderType() == type.SHORT;

		}).subscribe(order -> {

			this.lender.borrowStock(order);
			this.wallStreet.submitOrder(order, order.getOrderRequestedAtTime());
		});

	}

}
