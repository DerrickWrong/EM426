package com.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.SpringFXManager;
import com.configurations.ReactorStreamConfig;
import com.configurations.StockConfigurator; 
import com.models.StockBroker;
import com.models.demands.HedgeFund;
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
	LineChart<Long, Double> stockPricePlot;

	@Autowired
	StockBroker broker;

	@Autowired
	HedgeFund hedgie;

	@FXML
	Button simulatedBtn, stepButton;

	@Autowired
	@Qualifier("fxScheduler")
	Scheduler fxScheduler;

	@Autowired
	@Qualifier("tradingClock")
	Flux<Long> tradingClockFlux;

	@Autowired
	ReactorStreamConfig reactorConfig;

	boolean simulationStarted = false;

	// XYChart
	@FXML
	AreaChart hedgieVsApesPlot;
	XYChart.Series supplySeries = new XYChart.Series<>();
	XYChart.Series demandSeries = new XYChart.Series<>();
	XYChart.Series agentSeries = new XYChart.Series<>();
	XYChart.Series<Long, Double> priceSeries = new XYChart.Series<>();
	
	// PieChart
	@FXML
	PieChart pieChart; 
	
	@Autowired
	StockConfigurator stockConfig;
	
	@FXML
	public void initialize() {

		// setup pie chart
		this.pieChart.setData(stockConfig.getStockHoldingDistribution());
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

			// Update Volume Graph
			this.supplySeries.getData().add(new Data(timestamp, Math.random()));
			this.demandSeries.getData().add(new Data(timestamp, Math.random()));
			this.agentSeries.getData().add(new Data(timestamp, Math.random()));

			// update Price Graph
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

	@FXML
	public void onStepClicked() {

		reactorConfig.stepToggleClock();
	}

}
