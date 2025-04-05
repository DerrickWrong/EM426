package com.models;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.models.demands.Share;
import com.models.demands.StockOrder;

import em426.api.ActState;
import jakarta.annotation.PostConstruct;
import javafx.util.Pair;
import reactor.core.publisher.Sinks;

@Component
public class StockExchange {

	private Long delayForBuyOrder = 5L; // to be changed

	private final ListingStock stockListing = new ListingStock();

	Sinks.Many<Pair<Long, StockOrder>> internalBuySink = Sinks.many().multicast().directBestEffort();

	@Autowired
	@Qualifier("completedOrder")
	Sinks.Many<StockOrder> completedOrderSink;

	@PostConstruct
	void init() {

		this.internalBuySink.asFlux().delayElements(Duration.ofSeconds(this.delayForBuyOrder)).subscribe(p -> {

			this.processImmediateBuyOrder(p.getKey(), p.getValue());

		});

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
					this.stockListing.sellingSharesQueue.poll(); // remove from the queue
					this.stockListing.registerShareAndSellOrder(sellerShare, sellerNO);
					break; // break the while loop and done
				}

				if (buyerNO.getActState() == ActState.PARTIAL) {
					this.stockListing.sellOrderQueue.poll();
					this.stockListing.sellingSharesQueue.poll();
					continue; // continue to the next available
				}

			} else {

				// order is incomplete (bid price too low?)
				completedOrderSink.tryEmitNext(buyerNO);
				break;
			}

		} // end while
	}

	// this is what StockBroker use to call under normal circumstance
	public void submitOrder(StockOrder order, long timestamp) {
		this.internalBuySink.tryEmitNext(new Pair<>(timestamp, order));
	}

	// method to process short, cover and sell orders
	public void registerSellorShortOrder(Share shares, StockOrder sellOrder) {
		this.stockListing.registerShareAndSellOrder(shares, sellOrder);
	}

}
