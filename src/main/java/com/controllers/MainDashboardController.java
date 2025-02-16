package com.controllers;

import java.io.IOException;

import org.springframework.stereotype.Component;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

@Component
public class MainDashboardController {

	@FXML
	private Label dataLable;

	@FXML
	private HBox stockHbox;

	@FXML
	private HBox playerHBox;

	@FXML
	private void addStockClicked() throws IOException {
		System.out.println("add stock button clickeed");
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/StockSymbol.fxml"));
		Pane p = (Pane) loader.load();
		stockHbox.getChildren().add(p);
		
	}

	@FXML
	private void addPlayerClicked() throws IOException {
		System.out.println("add player button clickeed");
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/AgentAvatar.fxml"));
		Pane p = (Pane) loader.load();
		playerHBox.getChildren().add(p);
		
	}

}
