package com.controllers;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.SpringFXManager;
import com.configurations.ReactorStreamConfig;
import com.configurations.StockExchangeConfigurator;
import com.google.common.util.concurrent.AtomicDouble;
import com.models.Agents.HedgeFund.Hedgie;
import com.models.Agents.StockBroker.StockBroker;
import com.models.demands.StockOrder;

import em426.api.ActState;
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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
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
	Hedgie hedgie;

	@FXML
	Button simulatedBtn, stepButton;

	@Autowired
	@Qualifier("fxScheduler")
	Scheduler fxScheduler;

	@Autowired
	Flux<Long> tradingClockFlux;

	@Autowired
	@Qualifier("stockOrderStream")
	Sinks.Many<StockOrder> orderSink;

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
	StockExchangeConfigurator stockConfig;

	@FXML
	public void initialize() {

		// setup pie chart
		this.pieChart.setData(stockConfig.getStockHoldingDistribution());
		this.pieChart.setTitle("Share Distribution");

		supplySeries.setName("Market(Supply)");
		demandSeries.setName("Hedgies(Demand)");
		agentSeries.setName("Apes(Agents)");

		this.hedgieVsApesPlot.getData().addAll(supplySeries, demandSeries, agentSeries);

		this.stockPricePlot.getData().addAll(this.priceSeries);
		this.stockPricePlot.legendVisibleProperty().set(false);
	}

	AtomicDouble stockBoardPrice = new AtomicDouble(0.0);

	@PostConstruct
	public void init() {

		this.orderSink.asFlux().filter(o -> {

			return o.getActState() == ActState.COMPLETE;

		}).subscribe(order -> {

			// update with the latest price
			this.stockBoardPrice.set(order.getBidPrice());

		});

		// listen to the simulation
		tradingClockFlux.publishOn(fxScheduler).subscribe(tick -> {

			String timestamp = String.valueOf(tick);
			this.dayLabel.setText(timestamp);

			// Update Volume Graph
			this.supplySeries.getData().add(new Data(timestamp, Math.random()));
			this.demandSeries.getData().add(new Data(timestamp, Math.random()));
			this.agentSeries.getData().add(new Data(timestamp, Math.random()));

			// update Price Graph
			this.priceSeries.getData().add(new Data(timestamp, this.stockBoardPrice.get()));
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
	public void startSimulationClicked() {

		this.simulationStarted = true;

		this.reactorConfig.toggleSimulationClock();

		this.simulatedBtn.setText("Pause");
	}

	@FXML
	public void onPauseClicked() {

		this.simulationStarted = false;

		this.reactorConfig.toggleSimulationClock();

		this.simulatedBtn.setText("Simulate");
	}

	@FXML
	public void onStepClicked() {

		reactorConfig.stepToggleClock();
	}

	@FXML
	public void clickViewDemands() throws IOException {
  
		FXMLLoader loader = SpringFXManager.getInstance().loadFxml("views/StockOrderWindow.fxml");
		BorderPane bp = loader.load();
		Stage newStage = SpringFXManager.getInstance().getSubStage();
		newStage.setTitle("Transactions - Stock Orders");
		newStage.setScene(new Scene(bp));
		newStage.show();
		
	}
}
