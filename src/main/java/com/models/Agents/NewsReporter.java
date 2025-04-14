package com.models.Agents;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.models.StockExchange;
import com.models.demands.Share;
import com.models.demands.ShareInfo;
import com.models.demands.StockOrder;
import com.utils.SimAgentTypeEnum;

import em426.agents.Agent;
import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

/**
 * This class is to report the current market status to all agents. In a sense,
 * this could drive the behavior and action of agents.
 */

@Component
@PropertySource("classpath:stock.properties")
public class NewsReporter extends Agent {

	@Autowired
	Sinks.Many<ShareInfo> shareInfoStream;

	@Autowired
	StockExchange wallStreet;

	@Autowired
	Flux<Long> simulationClock;
	
	StockLender lender;

	@Value("${stockVolume}")
	private int stockVolume;
	 
	@PostConstruct
	void init() {

		this.simulationClock.subscribe(t -> {

			StockOrder so = this.wallStreet.getStockListing().sellOrderQueue.peek();

			if (so == null) {
				return;
			}

			ShareInfo info = new ShareInfo(so.getBidPrice(), so.getNumOfShares(), t);
			info = this.computeRatio(info);

			this.shareInfoStream.tryEmitNext(info);
		});

	}

	public void setLender(StockLender lender) {
		this.lender = lender;
	}
	
	// Review the ratio
	public ShareInfo computeRatio(ShareInfo info) {

		ConcurrentHashMap<UUID, Share> sharesRegistry = this.wallStreet.getStockListing().sharesRegistry;
		ConcurrentHashMap<UUID, Share> pendingSales = this.wallStreet.getStockListing().pendingSalesRegistry;
		Map<UUID, Share> borrowsTab = this.lender.getBorrowersTab();
		
		
		int marketFloat = 0;
		int shorted = 0;
		int institute = 0;
		int company = 0;
		int apes = 0;

		for (Map.Entry<UUID, Share> k : sharesRegistry.entrySet()) {
			Share share = k.getValue();

			if (share.getType() == SimAgentTypeEnum.Retail) {
				apes += share.getQuantity();
			}

			if (share.getType() == SimAgentTypeEnum.Market) {
				marketFloat += share.getQuantity();
			}

			if (share.getType() == SimAgentTypeEnum.MutualFund) {
				institute += share.getQuantity();
			}

			if (share.getType() == SimAgentTypeEnum.Company) {
				company += share.getQuantity();
			}
		}

		for (Map.Entry<UUID, Share> k : pendingSales.entrySet()) {
			Share share = k.getValue();

			if (share.getType() == SimAgentTypeEnum.Retail) {
				apes += share.getQuantity();
			}

			if (share.getType() == SimAgentTypeEnum.Market) {
				marketFloat += share.getQuantity();
			}

			if (share.getType() == SimAgentTypeEnum.MutualFund) {
				institute += share.getQuantity();
			}

			if (share.getType() == SimAgentTypeEnum.Company) {
				company += share.getQuantity();
			}
		}
		
		for(Map.Entry<UUID, Share> k : borrowsTab.entrySet()) {
			
			Share share = k.getValue();
			shorted += share.getQuantity();
		}

		double marketRatio = marketFloat * 100.0 / this.stockVolume;
		double shortRatio = shorted * 100.0 / this.stockVolume;
		double instituteRatio = institute * 100.0 / this.stockVolume;
		double companyRatio = company * 100.0 / this.stockVolume;
		double apeRatio = apes * 100.0 / this.stockVolume;

		info.setFloatingShares(marketRatio);
		info.setShortedShares(shortRatio);
		info.setInstituShares(instituteRatio);
		info.setInsiderShares(companyRatio);
		info.setApeShares(apeRatio);

		return info;
	}

}
