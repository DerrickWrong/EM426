package com.controllers;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.MIT.agents.Agent;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

@Component
@Scope("prototype")
public class PlayerController {

	@FXML
	Label agentNameLabel;

	@FXML
	Label gainLossLabel;

	Agent myAgent;

	// Execute on FX Thread
	public void setAgent(Agent agentModel) {
		this.myAgent = agentModel;
		this.agentNameLabel.setText(agentModel.getName());
		this.gainLossLabel.setText(String.valueOf(agentModel.getScore()));
	}

	@FXML
	void checkPlayerClicked() {

		System.out.println("check player info " + myAgent.getName() + " with score of " + myAgent.getScore());

	}

}
