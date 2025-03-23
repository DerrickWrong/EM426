package com.models;
 

import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import com.github.pnavais.machine.StateMachine; 

class TestStateMachine {

	@Test
	void test() {
		
		StateMachine stateMachine = StateMachine.newBuilder()
                .from("A").to("B").on("1")
                .from("B").to("C").on("2")                
                .build();
		
		Assert.isTrue(stateMachine.getCurrent().getName().equals("A"), "Is A");
		
		stateMachine.send("1");
		Assert.isTrue(stateMachine.getCurrent().getName().equals("B"), "Is B");
		
		stateMachine.send("2");
		Assert.isTrue(stateMachine.getCurrent().getName().equals("C"), "Is C");
		 
	}

}
