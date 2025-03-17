package com.models.demands;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;

public class Stock {

	public enum Category {
		INSIDER, INSTITUTE, FLOAT;
	}

	private final DoubleProperty currentPrice = new SimpleDoubleProperty(0.0);
	private final DoubleProperty currVolume = new SimpleDoubleProperty(0.0);
	private final StringProperty symbol = new SimpleStringProperty("");
	private final ObservableList<PieChart.Data> stockHoldingDistribution = FXCollections.observableArrayList();

	private PieChart.Data insiderRatio = new PieChart.Data(Category.INSIDER.toString(), 0);
	private PieChart.Data institutionRatio = new PieChart.Data(Category.INSTITUTE.toString(), 0);
	private PieChart.Data floatingRatio = new PieChart.Data(Category.FLOAT.toString(), 0);

	// Floating Share Priority Queue
	PriorityQueue<Share> floatingShareQueue; 
	List<Share> instituteShareList, insiderShareList;

	public Stock() {
		this.stockHoldingDistribution.addAll(insiderRatio, institutionRatio, floatingRatio);

		this.floatingShareQueue = new PriorityQueue<Share>(new Comparator<Share>() {

			@Override
			public int compare(Share o1, Share o2) {
				return Double.compare(o1.getPrice(), o2.getPrice());
			}
		});
		
		this.instituteShareList = new ArrayList<>();
		this.insiderShareList = new ArrayList<>();
		
	}

	public void addInsiderShare(Share s) {
		
		double oldVal = insiderRatio.getPieValue(); 
		oldVal += s.getQuantity();
		
		
		
	}

	public void addInstituteShare(Share s) {
 
	}

	public void addFloatingShare(Share s) {

		this.currVolume.add(s.getQuantity());
		this.floatingShareQueue.add(s);
		Share cheap = this.floatingShareQueue.peek();
		this.currentPrice.set(cheap.getPrice());
	}

	public void updatePieChart() {

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

}
