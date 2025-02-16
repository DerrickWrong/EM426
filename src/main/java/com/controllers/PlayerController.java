package com.controllers;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PlayerController {

	@FXML
	Label agentNameLabel;
	
	@FXML
	Label gainLossLabel;
	
	@FXML
	void checkPlayerClicked() {
		
		System.out.println("check player info");
		
	}
	
}
