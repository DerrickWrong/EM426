package com.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.SpringFXManager;

import jakarta.annotation.PostConstruct;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.util.converter.NumberStringConverter;

@Component
public class HedgiesController {

	@FXML
	TextField startingBalance, currentBalance, percent2Short, sellBelowPrice, decayPercent, cashout;


 

	@FXML
	void initialize() {


	}

	@FXML
	public void closeBtn() {
		SpringFXManager.getInstance().getSubStage().close();
	}

}
