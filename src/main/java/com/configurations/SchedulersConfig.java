package com.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.utils.FxScheduler;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Configuration
public class SchedulersConfig {

    @Bean
    Scheduler fxScheduler() {
		// To render things on JavaFX
		return FxScheduler.fxThread();
	}
	 
    @Bean
    Scheduler sellScheduler() {
    	return Schedulers.newSingle("SellingScheduler");
    }
    
	@Bean
	Scheduler ioScheduler() {
		// ideal for IO, database and 3rd party accesses
		return Schedulers.boundedElastic();
	} 
	
	@Bean
	Scheduler workhorsesScheduler() {
		// ideal for non-blocking and high bandwidth tasks
		return Schedulers.newParallel("Workhorses");
	}
	
}
