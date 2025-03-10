package com.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.SpringFXManager;
import com.models.demands.HedgeFund;

import jakarta.annotation.PostConstruct;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

@Component
public class HedgiesController {

	@FXML
	TextField startingBalance, currentBalance, percent2Short, sellBelowPrice, decayPercent, cashout;

	@Autowired
	private HedgeFund hedgie;

	@PostConstruct
	public void init() {
		// listen to the market news
	}

	@FXML
	public void initialize() {

		this.startingBalance.setText(String.valueOf(hedgie.getBalanaceInMil()));
		this.currentBalance.setText(String.valueOf(hedgie.getCurrentBalance()));
		this.percent2Short.setText(String.valueOf(hedgie.getBorrow2Short()));
		this.sellBelowPrice.setText(String.valueOf(hedgie.getTriggerCover()));
		this.decayPercent.setText(String.valueOf(hedgie.getDecay2Sell()));
		this.cashout.setText(String.valueOf(hedgie.getCashoutAt()));
	}

	@FXML
	public void saveBtn() {

		hedgie.setBalanaceInMil(Double.valueOf(startingBalance.getText()));

		hedgie.setBorrow2Short(Double.valueOf(percent2Short.getText()));
		hedgie.setTriggerCover(Double.valueOf(sellBelowPrice.getText()));
		hedgie.setDecay2Sell(Double.valueOf(decayPercent.getText()));
		hedgie.setCashoutAt(Double.valueOf(cashout.getText()));
		
		SpringFXManager.getInstance().getSubStage().close();
	}

	@FXML
	public void closeBtn() {
		SpringFXManager.getInstance().getSubStage().close();
	}

}
