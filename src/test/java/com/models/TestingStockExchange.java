package com.models;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
 
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import com.models.Agents.StockBroker;
import com.models.demands.Share;
import com.models.demands.StockOrder;
import com.models.demands.StockOrder.type;
import com.utils.SimAgentTypeEnum;

import javafx.application.Platform;
import reactor.core.publisher.Sinks;

@SpringBootTest
class TestingStockExchange {

	@Autowired
	StockExchange exchange;

	@Autowired
	@Qualifier("stockOrderStream")
	Sinks.Many<StockOrder> stockOrderStream;
	
	@Autowired
	StockBroker broker;
	
	@BeforeAll
	static void initJfxRuntime() {
		// kick off javafx stuff
		Platform.startup(() -> {
		});
	}

	@BeforeEach
	void beforeTest() {
		this.exchange.getStockListing().purge();
	}

	@Test
	void testBuyingShort() {

		// add some sell orders
		UUID testUUID = UUID.randomUUID();
		double stockPrice = 10.0;
		int volume = 100;
		Share shortedShares = new Share(testUUID, stockPrice, volume, SimAgentTypeEnum.Hedgie);
		this.exchange.getStockListing().registerShares2Pool(shortedShares);

		StockOrder shortOrder = new StockOrder(testUUID, type.SHORT, stockPrice, volume, SimAgentTypeEnum.Hedgie, 0L);

		this.stockOrderStream.tryEmitNext(shortOrder);

		// buy order
		StockOrder buyOrder = new StockOrder(UUID.randomUUID(), type.BUY, 11, 49, SimAgentTypeEnum.Retail, 1L);
		this.exchange.submitOrder(buyOrder, 2L);

		int sharesLeft = this.exchange.getStockListing().sellingSharesQueue.peek().getQuantity();
		assertEquals(51, sharesLeft);

	}

	@Test
	void testSellingOwnStock() {

		UUID Market = UUID.randomUUID();
		Share marketShares = new Share(Market, 10, 100, SimAgentTypeEnum.Market);
		this.exchange.getStockListing().registerShares2Pool(marketShares);

		StockOrder sellOrder = new StockOrder(Market, type.SELL, 11, 50, SimAgentTypeEnum.Market, 1L);
		this.exchange.submitOrder(sellOrder, sellOrder.getOrderRequestedAtTime());

		// buy order
		StockOrder buyOrder = new StockOrder(UUID.randomUUID(), type.BUY, 11, 49, SimAgentTypeEnum.Retail, 1L);
		this.exchange.submitOrder(buyOrder, buyOrder.getOrderRequestedAtTime());

		int sharesLeft = this.exchange.getStockListing().sellingSharesQueue.peek().getQuantity();
		assertEquals(1, sharesLeft);
	}
	
	@Test
	void testWithdrawingSellOrder() {
		
		UUID Market = UUID.randomUUID();
		Share marketShares = new Share(Market, 10, 100, SimAgentTypeEnum.Market);
		this.exchange.getStockListing().registerShares2Pool(marketShares);

		StockOrder sellOrderExpensive = new StockOrder(Market, type.SELL, 11, 50, SimAgentTypeEnum.Market, 1L);
		StockOrder sellOrderCheap = new StockOrder(Market, type.SELL, 9, 50, SimAgentTypeEnum.Market, 1L);
		
		this.exchange.submitOrder(sellOrderExpensive, 1L);
		this.exchange.submitOrder(sellOrderCheap, 1L);
		
		this.exchange.getStockListing().withdrawSellOrder(sellOrderExpensive);
		
		StockOrder order = this.exchange.getStockListing().sellOrderQueue.poll();
		
		assertEquals(sellOrderCheap, order);
		assertTrue(this.exchange.getStockListing().sellOrderQueue.isEmpty());
		
	}

}
