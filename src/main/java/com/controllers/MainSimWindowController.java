package com.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.SpringFXManager;
import com.models.StockBroker;
import com.models.demands.MarketAndLenders;

import jakarta.annotation.PostConstruct;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.StackedAreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import reactor.core.scheduler.Scheduler;

@Component
public class MainSimWindowController {

	@FXML
	Label dayLabel;

	@FXML
	StackedAreaChart<Long, Double> hedgieVsApesPlot;

	@FXML
	PieChart pieChart;

	@FXML
	Slider timelineSlider;

	@Autowired
	MarketAndLenders marketLender;
	
	@Autowired
	StockBroker broker;

	@FXML
	Button simulatedBtn;

	@Autowired
	@Qualifier("fxScheduler")
	Scheduler fxScheduler;

	

	boolean simulationStarted = false;

	XYChart.Series<Long, Double> supplySeries = new XYChart.Series<>();
	XYChart.Series<Long, Double> demandSeries = new XYChart.Series<>();
	XYChart.Series<Long, Double> agentSeries = new XYChart.Series<>();

	@FXML
	public void initialize() {

		// setup pie chart
		this.pieChart.setData(marketLender.getStockDistribution());
		this.pieChart.setTitle("Share Distribution");
		
		supplySeries.setName("Market(Supply)"); 
		demandSeries.setName("Hedgies(Demand)");
		agentSeries.setName("Apes(Agents)");
		
		hedgieVsApesPlot.getData().addAll(supplySeries);
		
	}

	@PostConstruct
	public void init() {

		//this.supplySeries.getData().add(new XYChart.Data<Integer, Double>(0, 10d));
		// supplySeries.getData().add(new XYChart.Data<Integer, Double>(1, 10d));
		// supplySeries.getData().add(new XYChart.Data<Integer, Double>(2, 9d));

		// listen to the simulation
		this.broker.listen2TradingClock().publishOn(fxScheduler).subscribe(tick -> {

			this.dayLabel.setText(String.valueOf(tick));

			double floatVol = this.marketLender.getFloatingShare() / 100 * this.marketLender.getVolume() / 1000000;
			
			
			
			// TODO Add all three (make sure they are symmetric)
			this.supplySeries.getData().add(new Data<Long, Double>(tick, floatVol));

			// System.out.println("Tick: " + tick + " float volume: " + floatVol);

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
	public void onDragDone() {
		System.out.println("Slider: " + timelineSlider.getValue());
	}

	@FXML
	public void onSimulateClicked() {

		this.simulationStarted = !this.simulationStarted; // toggle the simulation status
		this.broker.toggleBroker();

		if (this.simulationStarted) {
			this.simulatedBtn.setText("Stop");
		} else {
			this.simulatedBtn.setText("Simulate");
		}

	}

}
