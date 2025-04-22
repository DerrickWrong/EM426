package com.models;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.models.demands.Share;
import com.models.demands.StockOrder;
import com.models.demands.StockOrder.type;
import com.utils.SimAgentTypeEnum;

import em426.api.ActState;
import jakarta.annotation.PostConstruct;
import javafx.util.Pair;
import reactor.core.publisher.Sinks;

@Component
public class StockExchange {

	private final ListingStock stockListing = new ListingStock();

	Sinks.Many<Pair<Long, StockOrder>> internalBuySink = Sinks.many().multicast().directBestEffort();

	@Autowired
	@Qualifier("completedOrder")
	Sinks.Many<StockOrder> completedOrderSink;

	@PostConstruct
	void init() {

		// buy & cover orders only
		this.internalBuySink.asFlux().filter(p -> {

			StockOrder order = p.getValue();

			return order.getOrderType() == type.BUY || order.getOrderType() == type.COVER;

		}).subscribe(p -> {

			// check expiration date TODO
			// long timestamp = p.getKey();

			this.processImmediateBuyOrder(p.getKey(), p.getValue());

		});

	}

	public void reset() {
		this.stockListing.purge();
	}

	// This method is only called by external when using Cover order
	public void processImmediateBuyOrder(Long timestamp, StockOrder BuyerOrder) {
		while (!this.stockListing.sellOrderQueue.isEmpty()) {

			StockOrder currSellOrder = this.stockListing.sellOrderQueue.peek();
			Share currSellingShares = this.stockListing.sellingSharesQueue.peek();

			// compute the updates
			Share buyerShare = this.stockListing.computeBuyersShares(currSellOrder, BuyerOrder);
			Share sellerShare = this.stockListing.computeSellerShares(currSellOrder, BuyerOrder, currSellingShares);
			StockOrder buyerNO = this.stockListing.updateBuyersOrder(currSellOrder, currSellOrder, timestamp);
			StockOrder sellerNO = this.stockListing.updateSellersOrder(currSellOrder, BuyerOrder, timestamp);

			if (buyerShare != Share.EMPTY) {

				// add the buyer's new shares to the pool
				this.stockListing.registerShares2Pool(buyerShare);

				completedOrderSink.tryEmitNext(buyerNO);
				completedOrderSink.tryEmitNext(sellerNO);

				if (buyerNO.getActState() == ActState.COMPLETE) {

					if (!this.stockListing.sellingSharesQueue.isEmpty()) {
						this.stockListing.sellingSharesQueue.poll(); // remove from the queue 
					}
					
					if(!this.stockListing.sellOrderQueue.isEmpty()) {
						this.stockListing.sellOrderQueue.poll();
					}

					// re-register the selling stocks
					this.stockListing.registerShares2Pool(sellerShare);
					this.stockListing.registerShareAndSellOrder(sellerNO);
					return; // break the while loop and done
				}

				if (buyerNO.getActState() == ActState.PARTIAL) {
					this.stockListing.sellOrderQueue.poll();
					this.stockListing.sellingSharesQueue.poll();
					this.stockListing.sharesRegistry.remove(currSellOrder.getUUID());
					continue; // continue to the next available
				}

			} else {

				// order is incomplete (bid price too low?)
				completedOrderSink.tryEmitNext(buyerNO);
				break;
			}

		} // end while

		StockOrder UnprocessedOrder = new StockOrder(BuyerOrder, ActState.INCOMPLETE,
				BuyerOrder.getOrderRequestedAtTime());
		completedOrderSink.tryEmitNext(UnprocessedOrder);

	}

	// this is what StockBroker use to call under normal circumstance
	public void submitOrder(StockOrder order, long timestamp) {

		if (order.getOrderType() == type.SELL) {
			this.stockListing.registerShareAndSellOrder(order);
		} else {
			this.internalBuySink.tryEmitNext(new Pair<>(timestamp, order));
		}
	}

	public void submitShortOrder(StockOrder shortOrder, Share borrowedshares) {

		ConcurrentHashMap<UUID, Share> sharesRegistry = stockListing.sharesRegistry;

		// Find the market repo
		for (Map.Entry<UUID, Share> k : sharesRegistry.entrySet()) {
			Share share = k.getValue();

			if (share.getType() == SimAgentTypeEnum.Market) {

				// add the short to the market repo - simulate order being absorbed by the
				// market
				Share comb = share.combineShare(borrowedshares);
				stockListing.sharesRegistry.replace(k.getKey(), comb);

				StockOrder completedShort = new StockOrder(shortOrder, ActState.COMPLETE,
						shortOrder.getOrderRequestedAtTime() + 1);
				completedOrderSink.tryEmitNext(completedShort);
				break;
			}

		}

	}

	public ListingStock getStockListing() {
		return stockListing;
	}

}
