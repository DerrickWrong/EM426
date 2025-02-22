package com.models.interfaces;

import org.springframework.stereotype.Component;

import com.MIT.agents.Demand;

import jakarta.annotation.PostConstruct;
import javafx.util.Pair;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

@Component
public class DemandStream {

	Flux<Pair<DemandTypeEnum, Demand>> listeningDemandStream;

	FluxSink<Pair<DemandTypeEnum, Demand>> publishingDemandStream;

	@PostConstruct
	void setup() {

		this.listeningDemandStream = Flux.create(sink -> {

			this.publishingDemandStream = sink;

		});
		
		
	}

	public final Flux<Pair<DemandTypeEnum, Demand>> listen() {

		return this.listeningDemandStream;
	}

	public void publish(final DemandTypeEnum type, final Demand demand) {
		
		Pair<DemandTypeEnum, Demand> p = new Pair<>(type, demand);
		
		this.publishingDemandStream.next(p);
	};

}
