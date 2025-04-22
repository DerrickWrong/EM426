package com.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.configurations.SimConfiguration;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

@Component
public class HedgiesController {

	@FXML
	TextField borrow2short, dicountPercent, brokerDelayBox, shortDisclosureDelayBox;

	@Autowired
	SimConfiguration simConfig;

	@FXML
	void initialize() {
		
		this.borrow2short.setText(String.valueOf(this.simConfig.shortRatio));
		this.dicountPercent.setText(String.valueOf(this.simConfig.shortSellDisountRate)); 
		this.brokerDelayBox.setText(String.valueOf(this.simConfig.brokerDelay));
		this.shortDisclosureDelayBox.setText(String.valueOf(simConfig.disclosureDelay));

		this.borrow2short.textProperty().addListener((obs, oldVal, newVal) -> {

			if (newVal.isBlank()) {
				return;
			}

			this.simConfig.shortRatio = Double.valueOf(newVal);
		});

		this.dicountPercent.textProperty().addListener((obs, oldVal, newVal) -> {

			if (newVal.isBlank()) {
				return;
			}

			this.simConfig.shortSellDisountRate = Double.valueOf(newVal);
		});
		
		this.brokerDelayBox.textProperty().addListener((obs, oldVal, newVal)->{
			
			if(newVal.isBlank()) {
				return;
			}
			
			this.simConfig.brokerDelay = Long.valueOf(newVal);
			
		});
		
		this.shortDisclosureDelayBox.textProperty().addListener((obs, old, newVal) -> {

			if (newVal.isBlank()) {
				return;
			}
			simConfig.disclosureDelay = Long.valueOf(newVal);

		});
 
	}

}
