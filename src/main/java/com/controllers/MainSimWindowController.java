package com.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.SpringFXManager;
import com.configurations.ReactorStreamConfig;
import com.models.StockBroker;
import com.models.demands.HedgeFund;
import com.models.demands.MarketAndLenders;

import jakarta.annotation.PostConstruct;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;

@Component
public class MainSimWindowController {

	@FXML
	Label dayLabel;

	@FXML
	AreaChart hedgieVsApesPlot;

	@FXML
	PieChart pieChart;

	@FXML
	LineChart<Long, Double> stockPricePlot;

	@Autowired
	MarketAndLenders marketLender;

	@Autowired
	StockBroker broker;

	@Autowired
	HedgeFund hedgie;

	@FXML
	Button simulatedBtn;

	@Autowired
	@Qualifier("fxScheduler")
	Scheduler fxScheduler;

	@Autowired
	@Qualifier("tradingClock")
	Flux<Long> tradingClockFlux;

	@Autowired
	ReactorStreamConfig reactorConfig;

	boolean simulationStarted = false;

	XYChart.Series supplySeries = new XYChart.Series<>();
	XYChart.Series demandSeries = new XYChart.Series<>();
	XYChart.Series agentSeries = new XYChart.Series<>();

	XYChart.Series<Long, Double> priceSeries = new XYChart.Series<>();

	@FXML
	public void initialize() {

		// setup pie chart
		this.pieChart.setData(marketLender.getStockDistribution());
		this.pieChart.setTitle("Share Distribution");

		supplySeries.setName("Market(Supply)");
		demandSeries.setName("Hedgies(Demand)");
		agentSeries.setName("Apes(Agents)");

		this.hedgieVsApesPlot.getData().addAll(supplySeries, demandSeries, agentSeries);

		this.priceSeries.setName("Price");
		this.stockPricePlot.getData().addAll(this.priceSeries);
	}

	@PostConstruct
	public void init() {

		// listen to the simulation
		tradingClockFlux.publishOn(fxScheduler).subscribe(tick -> {

			String timestamp = String.valueOf(tick);
			this.dayLabel.setText(timestamp);

			double floatVol = (this.marketLender.getFloatingShare() / 100) * this.marketLender.getVolume() / 1E6;
			double hedgedVol = (this.hedgie.getBorrow2Short() / 100) * (this.marketLender.getInstitutionShare() / 100)
					* this.marketLender.getVolume() / 1E6;

			// TODO Add all three (make sure they are symmetric)
			this.supplySeries.getData().add(new Data(timestamp, floatVol));
			this.demandSeries.getData().add(new Data(timestamp, hedgedVol));
			this.agentSeries.getData().add(new Data(timestamp, Math.random()));

			// System.out.println("Tick: " + tick + " float volume: " + floatVol);
			this.priceSeries.getData().add(new Data(timestamp, Math.random()));
		});

	}

	@FXML
	public void supplyClicked() throws IOException {

		FXMLLoader loader = SpringFXManager.getInstance().loadFxml("views/MarketLenderWindow.fxml");
		BorderPane bp = loader.load();
		Stage newStage = SpringFXManager.getInstance().getSubStage();
		newStage.setTitle("Configure Supply");
		newStage.setScene(new Scene(bp));
		newStage.show();
	}

	@FXML
	public void demandClicked() throws IOException {

		FXMLLoader loader = SpringFXManager.getInstance().loadFxml("views/HedgeFundWindow.fxml");
		BorderPane bp = loader.load();
		Stage newStage = SpringFXManager.getInstance().getSubStage();
		newStage.setTitle("Configure Demand");
		newStage.setScene(new Scene(bp));
		newStage.show();
	}

	@FXML
	public void agentClicked() throws IOException {
		FXMLLoader loader = SpringFXManager.getInstance().loadFxml("views/RetailInvestorsWindow.fxml");
		BorderPane bp = loader.load();
		Stage newStage = SpringFXManager.getInstance().getSubStage();
		newStage.setTitle("Configure Agent");
		newStage.setScene(new Scene(bp));
		newStage.show();
	}

	@FXML
	public void onSimulateClicked() {

		this.simulationStarted = !this.simulationStarted; // toggle the simulation status

		this.reactorConfig.toggleSimulationClock();

		if (this.simulationStarted) {
			this.simulatedBtn.setText("Pause");
		} else {
			this.simulatedBtn.setText("Simulate");
		}

	}

}
