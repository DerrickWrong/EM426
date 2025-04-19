package com.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import com.models.Agents.Ape;
import com.models.Agents.HedgeFund;
import com.models.Agents.Market;
import com.models.Agents.PaperHand;
import com.models.Agents.StockBroker;
import com.models.Agents.StockLender;

import em426.agents.Agent;

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

	public StockBroker createStockBroker() {
		StockBroker broker = (StockBroker) this.springContext.getBean(StockBroker.class);
		return broker;
	}

	public HedgeFund createHedgie() {

		HedgeFund hedgie = (HedgeFund) this.springContext.getBean(HedgeFund.class);
		return hedgie;
	}

	public Ape createApe(double initialBalance, int numAgent, long disclosureDelay, double bidAbovePercent,
			long payFreq) {

		Ape ape = (Ape) this.springContext.getBean(Ape.class, initialBalance, numAgent, disclosureDelay,
				bidAbovePercent, payFreq);
		return ape;
	}

	public PaperHand createPaperhand() {
		PaperHand ph = this.springContext.getBean(PaperHand.class);
		return ph;
	}

	public void destroyAgent(Agent agent) {
		this.springContext.getBeanFactory().destroyBean(agent);
	}
}
