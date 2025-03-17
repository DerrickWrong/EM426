package com.configurations;

import java.util.Comparator;
import java.util.PriorityQueue;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.models.demands.Share;

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

@Configuration
@PropertySource("classpath:stock.properties")
public class StockConfigurator {

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

	public enum Category {
		INSIDER, INSTITUTE, FLOAT;
	}

	private final DoubleProperty currentPrice = new SimpleDoubleProperty(0.0);
	private final DoubleProperty currVolume = new SimpleDoubleProperty(0.0);
	private final StringProperty symbol = new SimpleStringProperty("");

	private final DoubleProperty insiderProperty = new SimpleDoubleProperty(0.0);
	private final DoubleProperty instProperty = new SimpleDoubleProperty(0.0);

	private final ObservableList<PieChart.Data> stockHoldingDistribution = FXCollections.observableArrayList();
	private PieChart.Data insiderRatio = new PieChart.Data(Category.INSIDER.toString().toLowerCase(), 0);
	private PieChart.Data institutionRatio = new PieChart.Data(Category.INSTITUTE.toString().toLowerCase(), 0);
	private PieChart.Data floatingRatio = new PieChart.Data(Category.FLOAT.toString().toLowerCase(), 0);

	// Floating Share Priority Queue
	private PriorityQueue<Share> floatingShareQueue;

	@PostConstruct
	void init() {

		this.symbol.setValue(stockName);
		this.currentPrice.setValue(this.stockPrice);
		this.currVolume.setValue(this.stockVolume);
		

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

		this.insiderProperty.setValue(insiderR);
		this.instProperty.setValue(instituteR);
		this.floatingRatio.setPieValue(100.0 - (insiderR + instituteR));		
	}

	public void addFloatingShare(Share s) {

		this.floatingShareQueue.add(s);
		Share cheap = this.floatingShareQueue.peek();
		this.currentPrice.set(cheap.getPrice());
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
