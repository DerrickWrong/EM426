package com.controllers;

import org.springframework.stereotype.Component;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

@Component
public class RetailInvestorController {

	@FXML
	RadioButton dhRadio, phRadio, BHradio;

	@FXML
	TextField popPerK, balancePerK, gainPercent, lossPercent, biweeklyBox;

	@FXML
	CheckBox reinvestCheck;

	@FXML
	ComboBox cooldownDropBox;

	@FXML
	public void saveBtn() {

	}

	@FXML
	public void cancelBtn() {

	}

}
