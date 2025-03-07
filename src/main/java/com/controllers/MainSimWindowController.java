package com.controllers;

import java.io.IOException;

import org.springframework.stereotype.Component;

import com.SpringFXManager;

import jakarta.annotation.PostConstruct;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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

	@PostConstruct
	public void init() {

	}

	@FXML
	public void supplyClicked() throws IOException {

		FXMLLoader loader = SpringFXManager.getInstance().loadFxml("views/MarketLenderWindow.fxml");
		BorderPane bp = loader.load();
		Stage newStage = new Stage();
		newStage.setTitle("Configure Supply");
		newStage.setScene(new Scene(bp));
		newStage.show();
	}

	@FXML
	public void demandClicked() throws IOException {

		FXMLLoader loader = SpringFXManager.getInstance().loadFxml("views/HedgeFundWindow.fxml");
		BorderPane bp = loader.load();
		Stage newStage = new Stage();
		newStage.setTitle("Configure Demand");
		newStage.setScene(new Scene(bp));
		newStage.show();
	}

	@FXML
	public void agentClicked() throws IOException {
		FXMLLoader loader = SpringFXManager.getInstance().loadFxml("views/RetailInvestorsWindow.fxml");
		BorderPane bp = loader.load();
		Stage newStage = new Stage();
		newStage.setTitle("Configure Agent");
		newStage.setScene(new Scene(bp));
		newStage.show();
	}

	@FXML
	public void onDragDone() {
		System.out.println("Slider: " + timelineSlider.getValue());
	}

}
