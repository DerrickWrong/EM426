package com.models.demands;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;

@Component
public class MarketAndLenders {

	// TODO - Need to fix this to support multi-stock
	private String stockName = "GME";
	private double initalPrice = 22.95;
	private double volume = 446.51 * 1000000;

	private double marginCallPercent; // at what % rise will trigger margin call

	private final ObservableList<PieChart.Data> stockDistribution = FXCollections.observableArrayList();

	public ObservableList<PieChart.Data> getStockDistribution() {
		return stockDistribution;
	}

	private PieChart.Data insider = new PieChart.Data("Insider", 8.49);
	private PieChart.Data institution = new PieChart.Data("Institute", 32.87);
	private PieChart.Data floating = new PieChart.Data("Float", 58.64);

	@PostConstruct
	public void init() {

		this.stockDistribution.add(this.insider);
		this.stockDistribution.add(this.institution);
		this.stockDistribution.add(this.floating);

		// listen to trading orders from the other agents

		// issue out processed orders

		// issue not processed orders

	}

	public double getInsiderShare() {
		return this.insider.getPieValue();
	}

	public double getInstitutionShare() {
		return this.institution.getPieValue();
	}

	public double getFloatingShare() {
		return this.floating.getPieValue();
	}

	public void updateChart(double insiderShare, double institutionShare, double floatingShare) {
		this.insider.setPieValue(insiderShare);
		this.institution.setPieValue(institutionShare);
		this.floating.setPieValue(floatingShare);
	}

	public String getStockName() {
		return stockName;
	}

	public void setStockName(String stockName) {
		this.stockName = stockName;
	}

	public double getInitalPrice() {
		return initalPrice;
	}

	public void setPrice(double initalPrice) {
		this.initalPrice = initalPrice;
	}

	public double getMarginCallPercent() {
		return marginCallPercent;
	}

	public void setMarginCallPercent(double marginCallPercent) {
		this.marginCallPercent = marginCallPercent;
	}

	public double getVolume() {
		return volume;
	}

	public void setVolume(double volume) {
		this.volume = volume;
	}

}
