package com.models;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.configurations.SimAgentFactory;
import javafx.application.Platform;

@SpringBootTest
class TestDynamicAgent {

	@Autowired
	SimAgentFactory agentFactory;
	
	@BeforeAll
	static void initJfxRuntime() {
		// kick off javafx stuff
		Platform.startup(() -> {
		});
	}
	
	@Test
	void test() {
		
		
		
		
		fail("Not yet implemented");
	}

}
