package com.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.configurations.SimConfiguration;
 
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField; 
@Component
public class RetailInvestorController {

	@FXML
	TextField numAgentBox, multiplierBox, balanceBox, buybidPercentBox, payFreqBox, shortDisclosureDelayBox;

	@FXML
	Slider agentStability;

	@Autowired
	SimConfiguration simConfig;

	@FXML
	public void initialize() {
		
		this.numAgentBox.setText(String.valueOf(simConfig.numOfAgents));
		this.multiplierBox.setText(String.valueOf(simConfig.multiplier));
		this.balanceBox.setText(String.valueOf(simConfig.initialBalance));
		this.buybidPercentBox.setText(String.valueOf(simConfig.bidAbovePercent));
		this.payFreqBox.setText(String.valueOf(simConfig.payFrequency));
		this.shortDisclosureDelayBox.setText(String.valueOf(simConfig.disclosureDelay));
		this.agentStability.setValue(simConfig.agentMix);
		
		this.numAgentBox.textProperty().addListener((observable, oldValue, newValue) -> {

			if (newValue.isBlank()) {
				return;
			}
			simConfig.numOfAgents = Integer.valueOf(newValue);

		});

		this.multiplierBox.textProperty().addListener((observable, old, newVal) -> {

			if (newVal.isBlank()) {
				return;
			}
			simConfig.multiplier = Integer.valueOf(newVal);

		});

		this.balanceBox.textProperty().addListener((obs, old, newVal) -> {
			if (newVal.isBlank()) {
				return;
			}
			simConfig.initialBalance = Double.valueOf(newVal);
		});

		this.buybidPercentBox.textProperty().addListener((obs, old, newVal) -> {

			if (newVal.isBlank()) {
				return;
			}
			simConfig.bidAbovePercent = Double.valueOf(newVal);

		});

		this.payFreqBox.textProperty().addListener((obs, old, newVal) -> {

			if (newVal.isBlank()) {
				return;
			}
			simConfig.payFrequency = Long.valueOf(newVal);

		});

		this.shortDisclosureDelayBox.textProperty().addListener((obs, old, newVal) -> {

			if (newVal.isBlank()) {
				return;
			}
			simConfig.disclosureDelay = Long.valueOf(newVal);

		});

		this.agentStability.valueProperty().addListener((obs, old, newVal) -> {

			simConfig.agentMix = newVal.doubleValue();
		});
	}

	@FXML
	void agentDraggedAction() {

		System.out.println("Dragged " + this.agentStability.getValue());

	}

}
