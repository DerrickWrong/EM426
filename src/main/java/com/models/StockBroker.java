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
				double borrowRatio = (order.getNumSharePer1000() / 1E6) / ins_shares_mil;

				//double new_ins_sharesRatio = (ins_shares_mil - shares2BorrowInMil) * 1000000
				//		/ this.marketBoard.getVolume();

				// update float shares
				double float_shares_mil = (this.marketBoard.getFloatingShare() / 100) * this.marketBoard.getVolume();
				double new_float_shares_mil = order.getNumSharePer1000() + float_shares_mil;
				double new_float_ratio = new_float_shares_mil / this.marketBoard.getVolume();

				// update the volume
				// this.marketBoard.updateChart(this.marketBoard.getInsiderShare(),
				// new_ins_sharesRatio, new_float_ratio);

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
