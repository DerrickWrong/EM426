package com.models;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class TestingDynamicStreams {

	@Test
	void test() {
		
		List<Integer> ticks = new ArrayList<>();
		
		for(int i = 0; i < 10; i++) {
			ticks.add(i);
		}
		
		IntStream st = IntStream.range(0, 10);
		
		Flux.fromStream(st.boxed()).subscribe(t->{
			
			System.out.println("TextFlux 1: " + t);
		});
		
	
		
		fail("Not yet implemented");
	}
	
	


}
