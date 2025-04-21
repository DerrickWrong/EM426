package com.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.configurations.SimConfiguration;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

@Component
public class HedgiesController {

	@FXML
	TextField borrow2short, dicountPercent;

	@Autowired
	SimConfiguration simConfg;

	@FXML
	void initialize() {
		
		
		this.borrow2short.setText(String.valueOf(this.simConfg.shortRatio));
		this.dicountPercent.setText(String.valueOf(this.simConfg.dicountPercent)); 
		

		this.borrow2short.textProperty().addListener((obs, oldVal, newVal) -> {

			if (newVal.isBlank()) {
				return;
			}

			this.simConfg.shortRatio = Double.valueOf(newVal);
		});

		this.dicountPercent.textProperty().addListener((obs, oldVal, newVal) -> {

			if (newVal.isBlank()) {
				return;
			}

			this.simConfg.dicountPercent = Double.valueOf(newVal);
		});
 
	}

}
