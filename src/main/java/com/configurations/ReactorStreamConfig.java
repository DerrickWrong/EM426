package com.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.MIT.agents.Demand;
import com.models.interfaces.DemandTypeEnum;

import javafx.util.Pair;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.Many;

@Configuration
public class ReactorStreamConfig {

	@Bean
	Sinks.Many<Pair<DemandTypeEnum, String>> demandMessageSink() {
		Many<Pair<DemandTypeEnum, String>> sink = Sinks.many().multicast().directBestEffort();
		return sink;
	}

	@Bean
	Sinks.Many<Pair<DemandTypeEnum, Demand>> demandSink() {
		Many<Pair<DemandTypeEnum, Demand>> sink = Sinks.many().multicast().directBestEffort();
		return sink;
	}
	
	

}
