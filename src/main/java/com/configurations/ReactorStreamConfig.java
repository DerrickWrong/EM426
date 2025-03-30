package com.configurations;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.models.demands.ShareInfo;
import com.models.demands.StockOrder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Configuration
public class ReactorStreamConfig {

	// periodicity of the simulation clock
	private Duration simPeriodicity = Duration.ofSeconds(1);

	private final AtomicLong simClockCounter = new AtomicLong(0L);
	AtomicBoolean simClockToggleFlag = new AtomicBoolean(false);

	private Sinks.Many<Long> simulationClockStream = Sinks.many().multicast().directBestEffort();
	
	public void stepToggleClock() {
		// Turn off the tradeClockFlag
		simClockToggleFlag.set(false);
		simulationClockStream.tryEmitNext(simClockCounter.getAndIncrement());
	}
	
	@Bean
	Flux<Long> simulationClock() {

		// create a Simulation clock from a cold flux

		// Trading Clock using fixed Flux
		Flux.interval(simPeriodicity).filter(f -> simClockToggleFlag.get()).subscribe(tick -> {
			simulationClockStream.tryEmitNext(simClockCounter.getAndIncrement());
		});
		
		return simulationClockStream.asFlux();
	}

	public void toggleSimulationClock() {
		boolean flag = this.simClockToggleFlag.get();
		this.simClockToggleFlag.set(!flag);
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
	Sinks.Many<ShareInfo> shareInfoStream(){
		return Sinks.many().multicast().directBestEffort();
	}
	
	@Bean
	Flux<ShareInfo> shareInfoFlux(){
		return this.shareInfoStream().asFlux();
	}

}
