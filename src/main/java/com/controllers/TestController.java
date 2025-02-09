package com.controllers;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class TestController {

	@FXML
	private Label testLabel;

	@FXML
	protected void clickTestButton() {
		
		int x = 10;
		int y = 6;
		
		int t = x + y;
		
		testLabel.setText("Welcome to JavaFX! X + Y = " + t);
		
	}
	
}
