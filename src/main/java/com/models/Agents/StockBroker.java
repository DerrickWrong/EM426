package com.models.Agents;

import java.time.Duration;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.models.StockExchange;
import com.models.demands.StockOrder;
import com.models.demands.StockOrder.type;

import em426.agents.Agent;
import em426.api.ActState;
import jakarta.annotation.PostConstruct;
import javafx.beans.property.SimpleIntegerProperty;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Component
public class StockBroker extends Agent {

	// processing delay for all buy orders
	SimpleIntegerProperty buyOrderDelay = new SimpleIntegerProperty(1);

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

	Queue<StockOrder> ordersQueue = new ConcurrentLinkedQueue<>();

	private PriorityQueue<StockOrder> sellOrderQueue, buyOrderQueue;

	@PostConstruct
	void init() {

		// create the two queues
		this.sellOrderQueue = new PriorityQueue<StockOrder>(new Comparator<StockOrder>() {

			@Override
			public int compare(StockOrder o1, StockOrder o2) {
				return Double.compare(o1.getBidPrice(), o2.getBidPrice());
			}
		});

		this.buyOrderQueue = new PriorityQueue<StockOrder>(new Comparator<StockOrder>() {

			@Override
			public int compare(StockOrder o1, StockOrder o2) {
				return Double.compare(o1.getBidPrice(), o2.getBidPrice());
			}
		});

		// listen to stock orders
		this.stockOrderStream.filter(order -> {

			// only accept unprocessed orders
			return order.getActState() == ActState.START;

		}).subscribe(order -> {

			this.ordersQueue.add(order);
		});

		// listen for all traditional sell orders
		this.stockOrderStream.filter(order -> {
			
			return order.getActState() == ActState.START && order.getOrderType() == type.SELL;
		
		}).subscribe(order -> {
			
			this.sellOrderQueue.add(order);
			
		});

		// listen for all traditional buy orders
		this.stockOrderStream.filter(order -> {

			return order.getActState() == ActState.START && order.getOrderType() == type.BUY;

		}).delayElements(Duration.ofSeconds(this.buyOrderDelay.get())).subscribe(order -> {

			this.buyOrderQueue.add(order);

		});

		// buffer delay processing by 5 seconds
		this.tradingClockFlux.buffer(this.buyOrderDelay.get()).subscribe(t -> {

			// this is where stock broker execute its work
			for (int i = 0; i < this.ordersQueue.size(); i++) {

				// fetch the order
				StockOrder ord = this.ordersQueue.poll();

				// meet the demand
				//ord = stonk.processStockOrder(ord);

				// notify the buyer/seller
				//completedOrderStream.tryEmitNext(ord);
			}

		});
	}

	public void processShortOrder(StockOrder order) {
		// no delay process immediately
		this.sellOrderQueue.add(order);
	}

	public void processCoverOrder(StockOrder order) {
		// no delay process immediately
		this.buyOrderQueue.add(order);
	}

	public SimpleIntegerProperty getBuyOrderDelay() {
		return buyOrderDelay;
	}
}
