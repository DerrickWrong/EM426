package com.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.configurations.SimConfiguration;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

@Component
public class ConfigurationController {

	@FXML
	TextField monteCarloTrials, simTime;

	@Autowired
	SimConfiguration simConfg;

	@FXML
	public void initialize() {

		this.simTime.setText(String.valueOf(this.simConfg.simulationDuration));
		this.monteCarloTrials.setText(String.valueOf(this.simConfg.MonteCarloTrials));
		
		
		this.monteCarloTrials.textProperty().addListener((observable, oldValue, newValue) -> {

			if(newValue.isBlank()) {
				return;
			}
			this.monteCarloTrials.setText(newValue);
			this.simConfg.MonteCarloTrials = Integer.valueOf(newValue);
		});

		this.simTime.textProperty().addListener((observable, oldValue, newValue) -> {

			if(newValue.isBlank()) {
				return;
			}
			this.simTime.setText(newValue);
			this.simConfg.simulationDuration = Integer.valueOf(newValue);

		});

	}

}
