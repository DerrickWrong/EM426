package com.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.SpringFXManager;
import com.models.demands.MarketAndLenders;
import com.models.demands.Stock;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.util.converter.NumberStringConverter;

@Component
public class MarketLenderController {

	@FXML
	TextField symbolName, stockPrice, volumeBox, insiderBox, institutionBox, floatingBox, marginBox;

	@Autowired
	MarketAndLenders marketLender;

	private Stock stockOfInterest = new Stock();

	@FXML
	public void initialize() {
		this.symbolName.textProperty().bindBidirectional(this.stockOfInterest.getSymbol());

		this.stockPrice.textProperty().bindBidirectional(this.stockOfInterest.getCurrentPrice(),
				new NumberStringConverter());
		
		this.volumeBox.textProperty().bindBidirectional(this.stockOfInterest.getCurrVolume(),
				new NumberStringConverter());

	}

	@FXML
	public void saveBtnClicked() {

		this.marketLender.setStockName(symbolName.getText());
		this.marketLender.setPrice(Double.valueOf(stockPrice.getText()));

		double insiderShares = Double.valueOf(this.insiderBox.getText());
		double institutionShare = Double.valueOf(this.institutionBox.getText());

		double floatingShares = 100.0 - (insiderShares + institutionShare);

		this.marketLender.updateChart(insiderShares, institutionShare, floatingShares);
		this.marketLender.setMarginCallPercent(Double.valueOf(marginBox.getText()));

		SpringFXManager.getInstance().getSubStage().close();
	}

	@FXML
	public void cancelBtnClicked() {
		SpringFXManager.getInstance().getSubStage().close();
	}

}
