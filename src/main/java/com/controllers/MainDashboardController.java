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
	public void initialize() throws IOException {
		
		System.out.println("Constructor are made");
		
		//create reddit news icon
		FXMLLoader loader2 = new FXMLLoader(getClass().getResource("../views/NewsSymbol.fxml"));
		Pane p2 = (Pane) loader2.load();
		stockHbox.getChildren().add(p2);
		
		
	}
	
	
	@FXML
	private void addStockClicked() throws IOException {
		System.out.println("add stock button clickeed");
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/StockSymbol.fxml"));
		Pane p = (Pane) loader.load();
		stockHbox.getChildren().add(p);
		
		FXMLLoader loader2 = new FXMLLoader(getClass().getResource("../views/NewsSymbol.fxml"));
		Pane p2 = (Pane) loader2.load();
		stockHbox.getChildren().add(p2);
		
	}

	@FXML
	private void addPlayerClicked() throws IOException {
		System.out.println("add player button clickeed");
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/AgentAvatar.fxml"));
		Pane p = (Pane) loader.load();
		playerHBox.getChildren().add(p);
		
	}

}
