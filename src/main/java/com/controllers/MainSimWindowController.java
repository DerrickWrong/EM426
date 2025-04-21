package com.controllers;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.SpringFXManager;
import com.configurations.ReactorStreamConfig;
import com.configurations.WorldSimulator; 
import com.models.demands.ShareInfo;
import com.models.demands.StockOrder;
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
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Scheduler;

@Component
public class MainSimWindowController {

	@FXML
	Label dayLabel, currStockPriceLabel;

	@FXML
	LineChart<Long, Double> stockPricePlot;

	@FXML
	Button simulatedBtn, stepButton;

	@Autowired
	@Qualifier("fxScheduler")
	Scheduler fxScheduler;

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
	WorldSimulator simulation;

	@FXML
	public void initialize() {

		// setup pie chart
		//this.pieChart.setData(stockConfig.getStockHoldingDistribution());
		this.pieChart.setTitle("Share Distribution");

		supplySeries.setName("Market(Supply)");
		demandSeries.setName("Hedgies(Demand)");
		agentSeries.setName("Apes(Agents)");
		
		this.hedgieVsApesPlot.getData().addAll(supplySeries, demandSeries, agentSeries);

		this.stockPricePlot.getData().addAll(this.priceSeries);
		this.stockPricePlot.legendVisibleProperty().set(false);
	}

	@Autowired
	Flux<ShareInfo> shareInfoFlux;

	Long dateCounter = 0L;

	@PostConstruct
	public void init() {

		// update the price board
		this.shareInfoFlux.publishOn(fxScheduler).subscribe(info -> {

			String timestamp = String.valueOf(dateCounter);
			this.dayLabel.setText(timestamp);

			// Update Volume Graph
			this.supplySeries.getData().add(new Data(timestamp, info.getFloatingShares()));
			this.demandSeries.getData().add(new Data(timestamp, info.getShortedShares()));
			this.agentSeries.getData().add(new Data(timestamp, info.getApeShares())); // how fast is people buying

			// update Price Graph
			this.priceSeries.getData().add(new Data(timestamp, info.getCurrentPrice()));

			String stockPriceTag = String.format("%.2f", info.getCurrentPrice());
			this.currStockPriceLabel.setText("Current Price @ $" + stockPriceTag);

			dateCounter++;

		});
	}
	
	private void cleanGraphData() {
		
		// clean up volume graph
		this.supplySeries.getData().clear();
		this.demandSeries.getData().clear();
		this.agentSeries.getData().clear();
		
		// clean up price graph
		this.priceSeries.getData().clear();
		
		this.dateCounter = 0L; // reset the x-axis
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

		if (this.simulationStarted) {
			this.simulatedBtn.setText("Re-Run");
			
			this.reactorConfig.toggleSimulationClock();
		} else {
			
			//clean up the plots 
			this.cleanGraphData();
			
			this.simulation.reRunSimulation();
			this.simulatedBtn.setText("Simulate");
		}
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
	
	@FXML
	public void clickConfig() throws IOException {
		
		FXMLLoader loader = SpringFXManager.getInstance().loadFxml("views/ConfigurationWindow.fxml");
		Pane p = loader.load();
		Stage ns = SpringFXManager.getInstance().getSubStage();
		ns.setTitle("Configuration");
		ns.setScene(new Scene(p));
		ns.show();
		
	}
	
	@FXML
	public void runMonteCarlo() throws IOException {
		
		// TODO - trigger monte carlo sim
		
		FXMLLoader loader = SpringFXManager.getInstance().loadFxml("views/HedgeGainWindow.fxml");
		Pane p = loader.load();
		Stage ns = SpringFXManager.getInstance().getSubStage();
		ns.setTitle("Configuration");
		ns.setScene(new Scene(p));
		ns.show();
		 
	}
	
}
