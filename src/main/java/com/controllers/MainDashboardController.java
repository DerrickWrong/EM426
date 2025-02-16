package com.controllers;

import org.springframework.stereotype.Component;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

@Component
public class MainDashboardController {

	@FXML
	private Label dataLable;

	@FXML
	private HBox stockHbox;

	@FXML
	private HBox playerHBox;

	@FXML
	private void addStockClicked() {
		System.out.println("add stock button clickeed");
	}

	@FXML
	private void addPlayerClicked() {
		System.out.println("add player button clickeed");
	}

}
