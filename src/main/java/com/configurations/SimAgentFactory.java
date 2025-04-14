package com.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.models.Agents.Ape;
import com.models.Agents.HedgeFund;
import com.models.Agents.Market;
import com.models.Agents.PaperHand;
import com.models.Agents.StockBroker;
import com.models.Agents.StockLender;

@Component
public class SimAgentFactory {

	private final ConfigurableApplicationContext springContext;

	@Autowired
	public SimAgentFactory(ConfigurableApplicationContext appContext) {
		this.springContext = appContext;
	}
	
	public Market createMarketAgent() {
		
		Market market = (Market) this.springContext.getBean(Market.class);
		return market;
	}

	public StockLender createLender() {
		StockLender lender = (StockLender) this.springContext.getBean(StockLender.class);
		return lender;
	}
	
	/*
	 * @Bean public StockLender createLender() {
	 * 
	 * return new StockLender(); }
	 * 
	 * @Bean public HedgeFund createHedgie() {
	 * 
	 * return new HedgeFund(); }
	 * 
	 * @Bean public PaperHand creatPaperHand() { return new PaperHand(); }
	 * 
	 * @Bean public StockBroker createStockBroker() { return new StockBroker(); }
	 * 
	 * @Bean public Market createMarketAgent() { return new Market(); }
	 * 
	 * @Bean Ape createApe() { return new Ape(); }
	 */

}
