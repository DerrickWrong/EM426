package com.models;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.models.demands.Share;
import com.models.demands.StockOrder;

import em426.api.ActState;
import em426.api.SupplyState;

public class ListingStock {

	public ListingStock() {
	}

	// This is the pool of the stock
	public final ConcurrentHashMap<UUID, Share> sharesRegistry = new ConcurrentHashMap<>();

	// Floating Share Priority Queue
	public final PriorityQueue<Share> sellingSharesQueue = new PriorityQueue<Share>(Comparator.comparingDouble(o->o.getPrice()));

	public final PriorityQueue<StockOrder> sellOrderQueue = new PriorityQueue<StockOrder>(Comparator.comparingDouble(o->o.getBidPrice()));

	public void registerShareAndSellOrder(StockOrder sellOrder) {
		
		//get the shares needed
		Share existing = this.sharesRegistry.get(sellOrder.getUUID());
		
		// figure out the remainder
		int remainder = existing.getQuantity() - sellOrder.getNumOfShares();
		
		if(remainder > 1) {
			Share remainingShares = new Share(existing, remainder);
			this.sharesRegistry.put(sellOrder.getUUID(), remainingShares);
		}else {
			this.sharesRegistry.remove(sellOrder.getUUID());
		}
		
		// publish the shares and order
		Share toBeSold = new Share(existing, sellOrder.getNumOfShares());
		
		this.sellingSharesQueue.add(toBeSold);
		this.sellOrderQueue.add(sellOrder);
	}
	
	public void registerShares2Pool(Share s) {
		
		if(this.sharesRegistry.containsKey(s.getOwner())) {
			
			Share oldShares = this.sharesRegistry.get(s.getOwner());
			Share comb = oldShares.combineShare(s);
			this.sharesRegistry.put(s.getOwner(), comb);
		}else { 
			this.sharesRegistry.put(s.getOwner(), s);
		}
	}
	 
	public void checkExpirations(Long currTimestamp) {
		
		//TODO write code to verify the content is still valid
		
		
	}

	// Helper function to create buyer shares
	public Share computeBuyersShares(StockOrder sellerOrder, StockOrder buyersOrder) {

		int remainingShares = sellerOrder.getNumOfShares() - buyersOrder.getNumOfShares();

		if (remainingShares >= 0) {

			if (buyersOrder.getBidPrice() >= sellerOrder.getBidPrice()) {

				// buyer's order fully executed
				return new Share(buyersOrder.getUUID(), buyersOrder.getBidPrice(), buyersOrder.getNumOfShares(),
						buyersOrder.getAgentType());
			} else {

				return Share.EMPTY;
			}

		} else {

			if (buyersOrder.getBidPrice() >= sellerOrder.getBidPrice()) {
				// buyer's order partially execute b/c not enough stock
				return new Share(buyersOrder.getUUID(), buyersOrder.getBidPrice(), sellerOrder.getNumOfShares(),
						buyersOrder.getAgentType());
			} else {

				return Share.EMPTY;
			}

		}
	}

	public StockOrder updateBuyersOrder(StockOrder sellerOrder, StockOrder buyersOrder, long currTimestamp) {

		int remainingShares = sellerOrder.getNumOfShares() - buyersOrder.getNumOfShares();

		if (remainingShares >= 0) {

			if (buyersOrder.getBidPrice() >= sellerOrder.getBidPrice()) {
				// fully executed
				return new StockOrder(buyersOrder, ActState.COMPLETE, currTimestamp);

			} else {
				return new StockOrder(buyersOrder, ActState.INCOMPLETE, currTimestamp);
			}

		} else {
			// partially executed
			if (buyersOrder.getBidPrice() >= sellerOrder.getBidPrice()) {
				// buyer's order fully executed
				return new StockOrder(buyersOrder, buyersOrder.getBidPrice(), sellerOrder.getNumOfShares(),
						ActState.PARTIAL, currTimestamp);
			} else {

				return new StockOrder(buyersOrder, ActState.INCOMPLETE, currTimestamp);
			}

		}
	}

	public Share computeSellerShares(StockOrder sellersOrder, StockOrder buyersOrder, Share sellersShare) {

		int remainingShares = sellersOrder.getNumOfShares() - buyersOrder.getNumOfShares();

		if (remainingShares >= 0) {

			if (buyersOrder.getBidPrice() >= sellersOrder.getBidPrice()) {

				// buyer's order fully executed
				return new Share(sellersShare, remainingShares);
			} else {

				return Share.EMPTY;
			}

		} else {

			if (buyersOrder.getBidPrice() >= sellersOrder.getBidPrice()) {
				// buyer's order partially execute b/c not enough stock
				Share outOfShares = new Share(sellersShare, 0);
				outOfShares.setState(SupplyState.UNAVAILABLE);
				return outOfShares;
			} else {

				return Share.EMPTY;
			}
		}

	}

	public StockOrder updateSellersOrder(StockOrder sellersOrder, StockOrder buyersOrder, long currTimestamp) {

		int remainingShares = sellersOrder.getNumOfShares() - buyersOrder.getNumOfShares();

		if (remainingShares > 0) {
			// buyer orders get processed but seller still have shares left
			if (buyersOrder.getBidPrice() >= sellersOrder.getBidPrice()) {

				// buyer's order fully executed
				return new StockOrder(sellersOrder, sellersOrder.getBidPrice(), remainingShares, ActState.PARTIAL,
						currTimestamp);
			} else {

				return StockOrder.INVALID;
			}

		} else {
			// everything has been sold

			if (buyersOrder.getBidPrice() >= sellersOrder.getBidPrice()) {

				return new StockOrder(sellersOrder, buyersOrder.getBidPrice(), 0, ActState.COMPLETE, currTimestamp);

			} else {
				return StockOrder.INVALID;
			}

		}

	}

	
	public void withdrawSellOrder(StockOrder sellOrder) {
		
		Share sellingShares = this.sharesRegistry.get(sellOrder.getUUID());
		this.sellOrderQueue.remove(sellOrder);
		this.sellingSharesQueue.remove(sellingShares);
		
	}
	
	public void purge() {
		
		this.sharesRegistry.clear();
		this.sellingSharesQueue.clear();
		this.sellOrderQueue.clear();
	}
}
