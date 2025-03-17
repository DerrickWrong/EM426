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

	private Sinks.Many<Long> centralClock = Sinks.many().multicast().directBestEffort();
	
	public void stepToggleClock() {
		// Turn off the tradeClockFlag
		tradeClockFlag.set(false);
		centralClock.tryEmitNext(counter.getAndIncrement());
	}
	
	@Bean
	Flux<Long> tradingClock() {

		// create a trading clock from a cold flux

		// Trading Clock using fixed Flux
		Flux.interval(simPeriodicity).filter(f -> tradeClockFlag.get()).subscribe(tick -> {
			centralClock.tryEmitNext(counter.getAndIncrement());
		});
		
		return centralClock.asFlux();
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

	@Bean
	Sinks.Many<StockConfigurator> stockStream() {
		return Sinks.many().multicast().directBestEffort();
	}

	@Bean
	Flux<StockConfigurator> stockFlux() {
		return this.stockStream().asFlux();
	}

}
