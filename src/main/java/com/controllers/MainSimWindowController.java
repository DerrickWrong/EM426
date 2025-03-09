package com.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.SpringFXManager;
import com.models.demands.MarketAndLenders;

import jakarta.annotation.PostConstruct;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.StackedAreaChart;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

@Component
public class MainSimWindowController {

	@FXML
	Label dayLabel;

	@FXML
	StackedAreaChart hedgieVsApesPlot;

	@FXML
	PieChart pieChart;

	@FXML
	Slider timelineSlider;

	@Autowired
	MarketAndLenders marketLender;

	@FXML
	public void initialize() {

		// setup pie chart
		this.pieChart.setData(marketLender.getStockDistribution());
		this.pieChart.setTitle("Share Distribution");

	}

	@PostConstruct
	public void init() {

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

}
