package com.configurations;

import java.util.Comparator;
import java.util.PriorityQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.models.demands.Share;
import com.models.demands.ShareInfo;
import com.models.demands.StockOrder;
import com.models.demands.StockOrder.type;

import em426.api.ActState;
import jakarta.annotation.PostConstruct;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

// In the future, this class will be @Prototype to support multiple stocks
@Configuration
@PropertySource("classpath:stock.properties")
public class StockExchangeConfigurator {

	@Value("${stockName}")
	private String stockName;

	@Value("${stockPrice}")
	private double stockPrice;

	@Value("${stockVolume}")
	private double stockVolume;

	@Value("${insiderRatio}")
	private double insiderR;

	@Value("${insititueRatio}")
	private double instituteR;

	@Value("${shortedRatio}")
	private double shortedRatio;

	@Autowired
	private Sinks.Many<ShareInfo> shareInfoStream;

	@Autowired
	private Flux<Long> simulationClock;

	public enum Category {
		INSIDER, INSTITUTE, FLOAT, SHORTED;
	}

	private final DoubleProperty currentPrice = new SimpleDoubleProperty(0.0);
	private final DoubleProperty currVolume = new SimpleDoubleProperty(0.0);
	private final StringProperty symbol = new SimpleStringProperty("");

	private final DoubleProperty insiderProperty = new SimpleDoubleProperty(0.0);
	private final DoubleProperty instProperty = new SimpleDoubleProperty(0.0);
	private final DoubleProperty shortProperty = new SimpleDoubleProperty(0.0);

	private final ObservableList<PieChart.Data> stockHoldingDistribution = FXCollections.observableArrayList();
	private PieChart.Data insiderRatio = new PieChart.Data(Category.INSIDER.toString().toLowerCase(), 0);
	private PieChart.Data institutionRatio = new PieChart.Data(Category.INSTITUTE.toString().toLowerCase(), 0);
	private PieChart.Data shortedInterestRatio = new PieChart.Data(Category.SHORTED.toString().toLowerCase(), 0);
	private PieChart.Data floatingRatio = new PieChart.Data(Category.FLOAT.toString().toLowerCase(), 0);

	// Floating Share Priority Queue
	private PriorityQueue<Share> floatingShareQueue;

	@PostConstruct
	void init() {

		this.symbol.setValue(stockName);
		this.currentPrice.setValue(this.stockPrice);
		this.currVolume.setValue(this.stockVolume);
		
		ShareInfo info = new ShareInfo(this.symbol.get(), this.currentPrice.get(), this.currVolume.get());

		// emit stock price
		this.simulationClock.subscribe(t -> {

			info.setCurrPrice(this.currentPrice.get());
			info.setInsiderShares(this.insiderProperty.get());
			info.setInstituShares(this.instProperty.get());
			info.setShortedShares(this.shortProperty.get()); 
			this.shareInfoStream.tryEmitNext(info);

		}); 

		this.stockHoldingDistribution.addAll(insiderRatio, institutionRatio, floatingRatio);

		this.floatingShareQueue = new PriorityQueue<Share>(new Comparator<Share>() {

			@Override
			public int compare(Share o1, Share o2) {
				return Double.compare(o1.getPrice(), o2.getPrice());
			}
		});

		this.insiderProperty.addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {

				insiderRatio.setPieValue(newValue.doubleValue());
			}
		});

		this.instProperty.addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				institutionRatio.setPieValue(newValue.doubleValue());
			}
		});

		this.shortProperty.addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				shortedInterestRatio.setPieValue(newValue.doubleValue());

			}

		});

		this.insiderProperty.setValue(insiderR);
		this.instProperty.setValue(instituteR);
		this.shortProperty.setValue(shortedRatio);
		this.floatingRatio.setPieValue(100.0 - (insiderR + instituteR + shortedRatio));
	}

	// All stock trading happens here*
	public StockOrder processStockOrder(StockOrder order) {

		if (order.getOrderType() == StockOrder.type.BUY || order.getOrderType() == StockOrder.type.COVER) {

			// buy or cover - reduce the floating shares (This is where supply meets demand)

			// check if there is any floating queue at or below this price
			while (!this.floatingShareQueue.isEmpty()) {

				Share currShare = this.floatingShareQueue.poll();

				if (currShare.getPrice() <= order.getBidPrice()) {

					// only when the bidding price higher than or equal to the current price will be
					// processed
					double purchasingNumOfShare = order.getNumOfShares();

					if (currShare.getQuantity() < purchasingNumOfShare) {

						// partially executed b/c not enough share  
						order.changeStatus(ActState.PARTIAL);

						// TODO - BUGGGG Need to figure out how to move to next iteration and execute
						// the remaining order.
						continue;
					} else {
						// fully executed
						double newCurrShareNum = currShare.getQuantity() - purchasingNumOfShare;
						currShare.setQuantity(newCurrShareNum);
						this.floatingShareQueue.add(currShare); 
						order.changeStatus(ActState.COMPLETE);
					}

					// update the pie chart if the order is fully/partial executed
					double float2Reduce = purchasingNumOfShare / this.currVolume.get();
					double newFloRatio = this.floatingRatio.getPieValue() - float2Reduce;
					this.floatingRatio.setPieValue(newFloRatio);
					break;

				} else {

					// bidding price too low
					this.floatingShareQueue.add(currShare); // put it back to the pQueue
					order.changeStatus(ActState.INCOMPLETE);
					break;
				}

			} // end of while loop

		} 
		
		
		// TODO - put this in a testable function
		if(order.getOrderType() == type.SHORT ) {
			
			// This is where the magic happens :) 
			Share s = new Share(this);
			s.setOwner(order.getOrignatorUUID());
			s.setPrice(order.getBidPrice());
			s.setQuantity(order.getNumOfShares());
			this.floatingShareQueue.add(s);
			
			// update current price
			this.currentPrice.set(order.getBidPrice());
			
			// update volume distribution
			double new_si = 100 * order.getNumOfShares() / this.getCurrVolume().get();
			instProperty.set(instProperty.get() - new_si);
			shortProperty.set(shortProperty.get() + new_si);
			
			// update the status
			order.changeStatus(ActState.COMPLETE); 
			
			StockOrder processedOrder = StockOrder.copyConstructor(order);
			processedOrder.changeStatus(ActState.COMMITTED);
			order = processedOrder;
			
		}
		
		return order;
	}

	public StringProperty getSymbol() {
		return this.symbol;
	}

	public DoubleProperty getCurrentPrice() {
		return currentPrice;
	}

	public ObservableList<PieChart.Data> getStockHoldingDistribution() {
		return stockHoldingDistribution;
	}

	public DoubleProperty getCurrVolume() {
		return currVolume;
	}

	public DoubleProperty getInsiderProperty() {
		return insiderProperty;
	}

	public DoubleProperty getInstProperty() {
		return instProperty;
	}

}
