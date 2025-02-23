package com.models;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest; 
import com.models.interfaces.DemandTypeEnum;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.util.Pair; 
import reactor.core.publisher.Sinks.Many;

@SpringBootTest
class TestingReactorStream extends Application {

	@Autowired
	Many<Pair<DemandTypeEnum, String>> messageSink;

	@Test
	void test() {

		String msg = "wallstreetbets";
		Pair<DemandTypeEnum, String> testMessage = new Pair<DemandTypeEnum, String>(DemandTypeEnum.NEWS, msg);

		messageSink.asFlux().subscribe(c -> {

			System.out.println("data " + c.getKey().name() + " " + c.getValue());

		});

		messageSink.tryEmitNext(testMessage);
		messageSink.tryEmitComplete();

		/*
		 * Sinks.Many<String> hotSource =
		 * Sinks.unsafe().many().multicast().directBestEffort(); Flux<String> hotFlux =
		 * hotSource.asFlux();
		 * 
		 * hotFlux.subscribe(data -> System.out.println("Subscriber 1: " + data));
		 * 
		 * hotSource.tryEmitNext("bbabababab"); hotSource.emitNext("blue",
		 * Sinks.EmitFailureHandler.FAIL_FAST); hotSource.emitNext("green",
		 * Sinks.EmitFailureHandler.FAIL_FAST);
		 * 
		 * hotFlux.subscribe(data -> System.out.println("Subscriber 2: " + data));
		 * 
		 * hotSource.emitNext("orange", Sinks.EmitFailureHandler.FAIL_FAST);
		 * hotSource.emitNext("purple", Sinks.EmitFailureHandler.FAIL_FAST);
		 * hotSource.emitComplete(Sinks.EmitFailureHandler.FAIL_FAST);
		 */
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub

	}

}
