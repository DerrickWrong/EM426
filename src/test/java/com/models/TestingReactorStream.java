package com.models;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

class TestingReactorStream {

	Flux<String> listeningTextStream = Flux.empty();
	
	FluxSink<String> publishingTextStream;
	
	//@BeforeAll
	private void before() {
		
		this.listeningTextStream = Flux.create(e->{
			this.publishingTextStream = e;	
		});
		
		this.listeningTextStream.subscribe(c->{
			
			System.out.println("Recevied " + c);
			
		});
		
		
	}
	
	
	@Test
	void test() {
		 
		this.before();
		
		this.publishingTextStream.next("Hello");
		this.publishingTextStream.next("world");
		
		
		fail("Not yet implemented");
	}

}
