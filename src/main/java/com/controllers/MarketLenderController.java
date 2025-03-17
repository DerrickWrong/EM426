package com.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.SpringFXManager;
import com.configurations.StockConfigurator;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.util.converter.NumberStringConverter;

@Component
public class MarketLenderController {

	@FXML
	TextField symbolName, stockPrice, volumeBox, insiderBox, institutionBox;

	@Autowired
	StockConfigurator stockOfInterest;

	@FXML
	public void initialize() {
		this.symbolName.textProperty().bindBidirectional(this.stockOfInterest.getSymbol());

		this.stockPrice.textProperty().bindBidirectional(this.stockOfInterest.getCurrentPrice(),
				new NumberStringConverter());

		this.volumeBox.textProperty().bindBidirectional(this.stockOfInterest.getCurrVolume(),
				new NumberStringConverter());

		this.insiderBox.textProperty().bindBidirectional(this.stockOfInterest.getInsiderProperty(),
				new NumberStringConverter());
		
		this.institutionBox.textProperty().bindBidirectional(this.stockOfInterest.getInstProperty(),
				new NumberStringConverter());
	}

	@FXML
	public void cancelBtnClicked() {
		SpringFXManager.getInstance().getSubStage().close();
	}

}
