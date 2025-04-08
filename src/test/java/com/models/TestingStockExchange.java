package com.models;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.models.demands.Share;
import com.models.demands.StockOrder;
import com.models.demands.StockOrder.type;
import com.utils.SimAgentTypeEnum;

import javafx.application.Platform;

@SpringBootTest
class TestingStockExchange {

	@Autowired
	StockExchange exchange;

	@BeforeAll
	static void initJfxRuntime() {
		// kick off javafx stuff
		Platform.startup(() -> {
		});
	}

	@Test
	void test() {

		// add some sell orders
		UUID testUUID = UUID.randomUUID();
		double stockPrice = 10.0;
		int volume = 100;
		Share shortedShares = new Share(testUUID, stockPrice, volume, SimAgentTypeEnum.Hedgie);
		StockOrder shortOrder = new StockOrder(testUUID, type.SHORT, stockPrice, volume, SimAgentTypeEnum.Hedgie, 0L);
		
		this.exchange.registerSellorShortOrder(shortedShares, shortOrder);
		
		// buy order
		StockOrder buyOrder = new StockOrder(UUID.randomUUID(), type.BUY, 11, 49, SimAgentTypeEnum.Retail, 1L);
		this.exchange.processImmediateBuyOrder(2L, buyOrder);
		
		int sharesLeft = this.exchange.getStockListing().sellingSharesQueue.peek().getQuantity();
		assertEquals(51, sharesLeft);
		
 
	}

}
