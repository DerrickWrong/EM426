package com.models;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.MIT.agents.Demand;
import com.models.demands.News;
import com.models.demands.RedditDataFactory;  
import com.models.interfaces.DemandTypeEnum;

import javafx.util.Pair;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@SpringBootTest
class TestRedditDataFactory {

	@Autowired
	RedditDataFactory dataFactory;

	@Autowired
	Flux<Pair<DemandTypeEnum, Demand>> dataStream;

	@Test
	void test() {

		Flux<Pair<DemandTypeEnum, Demand>> source = dataStream.filter(null)
				.filter(p -> (p.getKey().compareTo(DemandTypeEnum.NEWS) == 0));

		StepVerifier.create(source).consumeNextWith(d -> {

			News n = (News) d.getValue();


		});


	}

}
