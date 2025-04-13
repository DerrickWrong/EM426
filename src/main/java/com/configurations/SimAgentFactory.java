package com.configurations;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;

import com.models.Agents.Ape;
import com.models.Agents.HedgeFund; 
import com.models.Agents.PaperHand;
import com.models.Agents.StockBroker;
import com.models.Agents.StockLender;
 

@Configuration
@PropertySource("classpath:app.properties")
public class SimAgentFactory {

	
	@Bean
	public StockLender createLender() {
		
		return new StockLender();
	}
	
	@Bean
	public HedgeFund createHedgie() {

		return new HedgeFund();
	}

	@Bean
	public PaperHand creatPaperHand() {
		return new PaperHand();
	}

	@Bean
	public StockBroker createStockBroker() {
		return new StockBroker();
	}
	
	
	@Value("${numberOfApeAgents}")
	int numberOfApes;
	
	@Bean
	@Scope("prototype")
	Ape createApe() {
		return new Ape();
	}
	
	
	@Bean
	List<Ape> createApesAgents() {
		
		List<Ape> apes = new ArrayList<>();
		
		for(int i = 0; i < this.numberOfApes; i++) {
			 
			apes.add(this.createApe());
		}
		
		return apes;
	} 
	
}
