package com.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.SpringFXManager;
import com.models.Agents.HedgeFund.Hedgie;

import jakarta.annotation.PostConstruct;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.util.converter.NumberStringConverter;

@Component
public class HedgiesController {

	@FXML
	TextField startingBalance, currentBalance, percent2Short, sellBelowPrice, decayPercent, cashout;

	@Autowired
	private Hedgie hedgie;
 

	@FXML
	void initialize() {

		this.startingBalance.textProperty().bindBidirectional(this.hedgie.getBalance(), new NumberStringConverter());
		this.currentBalance.textProperty().bindBidirectional(this.hedgie.getCurrBalance(), new NumberStringConverter());
		this.percent2Short.textProperty().bindBidirectional(this.hedgie.getBorrow2ShortRatio(),
				new NumberStringConverter());
		this.sellBelowPrice.textProperty().bindBidirectional(this.hedgie.getTrigger2Cover(),
				new NumberStringConverter());
		this.decayPercent.textProperty().bindBidirectional(this.hedgie.getGetMarginCall(), new NumberStringConverter());
		this.cashout.textProperty().bindBidirectional(this.hedgie.getCashoutProfitAt(), new NumberStringConverter());
	}

	@FXML
	public void closeBtn() {
		SpringFXManager.getInstance().getSubStage().close();
	}

}
