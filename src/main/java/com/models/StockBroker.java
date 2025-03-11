package com.models;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.models.demands.MarketAndLenders;
import com.models.demands.StockOrder;

import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Component
public class StockBroker {
  
	AtomicBoolean tradeClockFlag = new AtomicBoolean(false);

	Duration speedOfClock = Duration.ofSeconds(1);
	private Sinks.Many<StockOrder> orderSink = Sinks.many().multicast().directBestEffort();
	private Flux<StockOrder> processedOrderStream;

	private Sinks.Many<Long> tradeClock = Sinks.many().multicast().directBestEffort();
	private AtomicLong counter = new AtomicLong(0L);

	@Autowired
	MarketAndLenders marketBoard;

	@PostConstruct
	void init() {

		// Trading Clock
		Flux.interval(speedOfClock).filter(f -> this.tradeClockFlag.get()).subscribe(tick -> {
			this.tradeClock.tryEmitNext(counter.getAndIncrement());
		});

		// Trading Order Stream
		this.processedOrderStream = this.orderSink.asFlux().takeUntil(p -> tradeClockFlag.get()).map(order -> {

			if (order.getOrderType() == StockOrder.type.SHORT) {

				// borrow from institutions
				double ins_shares_mil = (this.marketBoard.getInstitutionShare() / 100) * (this.marketBoard.getVolume() / 1E6);
				double borrowRatio = (order.getNumSharePerMil() / 1E6) / ins_shares_mil / 100;
				
				double newShortPercent = this.marketBoard.getInstitutionShare() * borrowRatio;
				
				double new_instu_percent = this.marketBoard.getInstitutionShare() - newShortPercent;
				double new_float_percent = this.marketBoard.getFloatingShare() + newShortPercent;
				
				// update the volume
				this.marketBoard.updateChart(this.marketBoard.getInsiderShare(),new_instu_percent, new_float_percent);

				// update the price
				this.marketBoard.setPrice(order.getBidPrice());

				// logic of processing the stock
				order.setTrue2ProcessedOrder();
			}

			return order;
		});

	}

	public void toggleBroker() {
		// toggle the trade clock flag
		boolean old = this.tradeClockFlag.get();
		this.tradeClockFlag.set(!old);
	}

	public Flux<Long> listen2TradingClock() {
		return this.tradeClock.asFlux();
	}

	public Flux<StockOrder> listen2ProcessedOrder() {
		return this.processedOrderStream;
	}

	public void submitOrder(StockOrder order) {
		this.orderSink.tryEmitNext(order);
	}

}
