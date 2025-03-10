package com.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.SpringFXManager;
import com.models.demands.MarketAndLenders;

import jakarta.annotation.PostConstruct; 
import javafx.fxml.FXML; 
import javafx.scene.control.TextField;

@Component
public class MarketLenderController {

	@FXML
	TextField symbolName, stockPrice, volumeBox, insiderBox, institutionBox, floatingBox, marginBox;
 
	@Autowired
	MarketAndLenders marketLender;

	@FXML
	public void initialize() {

		// pre-populate the content
		this.symbolName.setText(marketLender.getStockName());
		this.stockPrice.setText(String.valueOf(marketLender.getInitalPrice()));
		this.volumeBox.setText(String.valueOf(marketLender.getVolume()));
		this.insiderBox.setText(String.valueOf(marketLender.getInsiderShare()));
		this.institutionBox.setText(String.valueOf(marketLender.getInstitutionShare()));
		this.floatingBox.setText(String.valueOf(marketLender.getFloatingShare()));
		this.marginBox.setText(String.valueOf(marketLender.getMarginCallPercent()));
	}

	@PostConstruct
	public void init() {

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
