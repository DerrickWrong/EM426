package com.models;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.models.demands.Share;
import com.models.demands.StockOrder;
import com.models.demands.StockOrder.type;
import com.utils.SimAgentTypeEnum;

import em426.api.ActState;
import em426.api.SupplyState;

class ListingStockTest {

	private ListingStock listing = new ListingStock();

	private UUID owner = UUID.randomUUID();
	private double shareBoughtPrice = 20.0;
	private int numShare = 1000;

	private Share myShare = new Share(this.owner, this.shareBoughtPrice, this.numShare, SimAgentTypeEnum.Retail);

	// testing order;
	private double sellPrice = 30.0;
	private int sellNum = 500;

	@BeforeEach
	void setup() {
		this.listing.registerShares2Pool(myShare);
		StockOrder sellOrder = new StockOrder(this.owner, type.SELL, this.sellPrice, this.sellNum,
				SimAgentTypeEnum.Retail, 1L);
		this.listing.registerShareAndSellOrder(sellOrder);
	}

	@Test
	void testCheaperShares() {

		UUID newOwner = UUID.randomUUID();
		Share cheapShares = new Share(newOwner, 10, 100, SimAgentTypeEnum.Retail);
		StockOrder cheapSellOrder = new StockOrder(newOwner, type.SELL, 29, 100, SimAgentTypeEnum.Retail, 10L);
		this.listing.registerShares2Pool(cheapShares);
		this.listing.registerShareAndSellOrder(cheapSellOrder);

		double cheapestNum = this.listing.sellOrderQueue.peek().getNumOfShares();
		double cheapestPrice = this.listing.sellOrderQueue.peek().getBidPrice();

		assertEquals(cheapestPrice, cheapSellOrder.getBidPrice());
		assertEquals(cheapestNum, cheapSellOrder.getNumOfShares());
	}

	@Test
	void testProcessingBuyerShare() {

		StockOrder buyOrder = new StockOrder(UUID.randomUUID(), type.BUY, 31, 500, SimAgentTypeEnum.Retail, 1L);
		StockOrder sellerOrder = this.listing.sellOrderQueue.peek();

		// just right
		Share buyerShares = this.listing.computeBuyersShares(sellerOrder, buyOrder);
		assertEquals(buyerShares.getPrice(), buyOrder.getBidPrice());
		assertEquals(buyerShares.getQuantity(), buyOrder.getNumOfShares());

		// just right but not enough shares
		StockOrder notEnough = new StockOrder(UUID.randomUUID(), type.BUY, 31, 1000, SimAgentTypeEnum.Retail, 1L);
		buyerShares = this.listing.computeBuyersShares(sellerOrder, notEnough);
		assertEquals(buyerShares.getPrice(), buyOrder.getBidPrice());
		assertEquals(buyerShares.getQuantity(), sellerOrder.getNumOfShares());

		// too cheap
		StockOrder cheapBuyOrder = new StockOrder(UUID.randomUUID(), type.BUY, 29, 100, SimAgentTypeEnum.Retail, 1L);
		buyerShares = this.listing.computeBuyersShares(sellerOrder, cheapBuyOrder);

		assertEquals(buyerShares, Share.EMPTY);

		// too cheap and too many
		StockOrder tooManytooCheapOrder = new StockOrder(UUID.randomUUID(), type.BUY, 29, 1000, SimAgentTypeEnum.Retail,
				1L);
		buyerShares = this.listing.computeBuyersShares(sellerOrder, tooManytooCheapOrder);

		assertEquals(buyerShares, Share.EMPTY);
	}

	@Test
	void testProcessingBuyersOrder() {

		StockOrder buyOrder = new StockOrder(UUID.randomUUID(), type.BUY, 31, 500, SimAgentTypeEnum.Retail, 1L);
		StockOrder sellerOrder = this.listing.sellOrderQueue.peek();

		// just right
		StockOrder rst = this.listing.updateBuyersOrder(sellerOrder, buyOrder, 1L);
		assertEquals(rst.getBidPrice(), buyOrder.getBidPrice());
		assertEquals(rst.getNumOfShares(), buyOrder.getNumOfShares());

		// just right but not enough shares
		StockOrder notEnough = new StockOrder(UUID.randomUUID(), type.BUY, 31, 1000, SimAgentTypeEnum.Retail, 1L);
		rst = this.listing.updateBuyersOrder(sellerOrder, notEnough, 1L);
		assertEquals(rst.getBidPrice(), buyOrder.getBidPrice());
		assertEquals(rst.getNumOfShares(), sellerOrder.getNumOfShares());
		assertEquals(rst.getActState(), ActState.PARTIAL);

		// too cheap
		StockOrder cheapBuyOrder = new StockOrder(UUID.randomUUID(), type.BUY, 29, 100, SimAgentTypeEnum.Retail, 1L);
		rst = this.listing.updateBuyersOrder(sellerOrder, cheapBuyOrder, 1L);
		assertEquals(ActState.INCOMPLETE, rst.getActState());

		// too cheap and too many
		StockOrder tooManytooCheapOrder = new StockOrder(UUID.randomUUID(), type.BUY, 29, 1000, SimAgentTypeEnum.Retail,
				1L);
		rst = this.listing.updateBuyersOrder(sellerOrder, tooManytooCheapOrder, 1L);
		assertEquals(ActState.INCOMPLETE, rst.getActState());
	}

	@Test
	void testProcessingSellerShares() {

		StockOrder buyOrder = new StockOrder(UUID.randomUUID(), type.BUY, 31, 300, SimAgentTypeEnum.Retail, 1L);
		StockOrder sellerOrder = this.listing.sellOrderQueue.peek();
		Share sellerShares = this.listing.sellingSharesQueue.peek();

		int remainingShares = sellerOrder.getNumOfShares() - buyOrder.getNumOfShares();

		// Just right
		Share rst_sellshares = this.listing.computeSellerShares(sellerOrder, buyOrder, sellerShares);
		assertEquals(sellerShares.getPrice(), rst_sellshares.getPrice());
		assertEquals(remainingShares, rst_sellshares.getQuantity());

		// all seller's share sold
		StockOrder notEnoughBuyOrdere = new StockOrder(UUID.randomUUID(), type.BUY, 31, 1000, SimAgentTypeEnum.Retail,
				1L);
		rst_sellshares = this.listing.computeSellerShares(sellerOrder, notEnoughBuyOrdere, sellerShares);
		assertEquals(0, rst_sellshares.getQuantity());
		assertEquals(SupplyState.UNAVAILABLE, rst_sellshares.getState());

		// cheap buy order
		StockOrder cheapBuyOrder = new StockOrder(UUID.randomUUID(), type.BUY, 29, 100, SimAgentTypeEnum.Retail, 1L);
		rst_sellshares = this.listing.computeSellerShares(sellerOrder, cheapBuyOrder, sellerShares);
		assertEquals(Share.EMPTY, rst_sellshares);

		StockOrder manyNCheapOrder = new StockOrder(UUID.randomUUID(), type.BUY, 29, 1000, SimAgentTypeEnum.Retail, 1L);
		rst_sellshares = this.listing.computeSellerShares(sellerOrder, manyNCheapOrder, sellerShares);
		assertEquals(Share.EMPTY, rst_sellshares);
	}

	@Test
	void testProcessingSellersOrders() {

		StockOrder buyOrder = new StockOrder(UUID.randomUUID(), type.BUY, 31, 300, SimAgentTypeEnum.Retail, 1L);
		StockOrder sellerOrder = this.listing.sellOrderQueue.peek();

		int remainingShares = sellerOrder.getNumOfShares() - buyOrder.getNumOfShares();

		// Just right
		StockOrder rst_sellOrder = this.listing.updateSellersOrder(sellerOrder, buyOrder, 1L);
		assertEquals(remainingShares, rst_sellOrder.getNumOfShares());
		assertEquals(ActState.PARTIAL, rst_sellOrder.getActState());

		// just right but not enough shares
		StockOrder notEnough = new StockOrder(UUID.randomUUID(), type.BUY, 31, 1000, SimAgentTypeEnum.Retail, 1L);
		rst_sellOrder = this.listing.updateSellersOrder(sellerOrder, notEnough, 1L);
		assertEquals(0, rst_sellOrder.getNumOfShares());
		assertEquals(notEnough.getBidPrice(), rst_sellOrder.getBidPrice());
		assertEquals(ActState.COMPLETE, rst_sellOrder.getActState());

		// too cheap
		StockOrder cheapBuyOrder = new StockOrder(UUID.randomUUID(), type.BUY, 29, 100, SimAgentTypeEnum.Retail, 1L);
		rst_sellOrder = this.listing.updateSellersOrder(sellerOrder, cheapBuyOrder, 1L);
		assertEquals(StockOrder.INVALID, rst_sellOrder);

		// too cheap and too many
		StockOrder many2CheapOrder = new StockOrder(UUID.randomUUID(), type.BUY, 29, 1000, SimAgentTypeEnum.Retail, 1L);
		rst_sellOrder = this.listing.updateSellersOrder(sellerOrder, many2CheapOrder, 1L);
		assertEquals(StockOrder.INVALID, rst_sellOrder);
	}

}
