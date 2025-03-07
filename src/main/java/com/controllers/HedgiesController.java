package com.controllers;

import org.springframework.stereotype.Component;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

@Component
public class HedgiesController {

	@FXML
	TextField startingBalance, currentBalance, percent2Short, sellBelowPrice, decayPercent, cashout;

	@FXML
	public void saveBtn() {
	}

	@FXML
	public void closeBtn() {

	}

}
