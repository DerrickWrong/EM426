package com.configurations;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
 
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration; 

import com.models.demands.StockOrder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Configuration
public class ReactorStreamConfig {

	// periodicity of the simulation clock
	private Duration simPeriodicity = Duration.ofSeconds(1);

	private final AtomicLong counter = new AtomicLong(0L);
	AtomicBoolean tradeClockFlag = new AtomicBoolean(false);

	@Bean
	Flux<Long> tradingClock() {

		// create a trading clock from a cold flux
		Sinks.Many<Long> clockSink = Sinks.many().multicast().directBestEffort();

		// Trading Clock
		Flux.interval(simPeriodicity).filter(f -> tradeClockFlag.get()).subscribe(tick -> {
			clockSink.tryEmitNext(counter.getAndIncrement());
		});

		return clockSink.asFlux();
	}

	public void toggleSimulationClock() {
		boolean flag = this.tradeClockFlag.get();
		this.tradeClockFlag.set(!flag);
	}

	// This stream is used to pass stock order (requesting)
	@Bean
	Sinks.Many<StockOrder> stockOrderStream() {
		return Sinks.many().multicast().directBestEffort();
	}

	// This is the listening stream of the stock order
	@Bean
	Flux<StockOrder> stockOrderFlux() {
		return this.stockOrderStream().asFlux();
	}

}
