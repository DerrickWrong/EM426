package com.controllers;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.models.demands.Stock;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class StockSymbolController {

	@FXML
	Label symbolLabel;

	@FXML
	Label symbolPrice;

	private Stock myStock;

	@FXML
	void checkStockClicked() {

		System.out.println("Check Stock: " + myStock.getName() + " price: $" + myStock.getCurrentPrice()
				+ " with volume(share in M): " + myStock.getCurrVolume());
	}

	// invoke in FXThread
	public void setStock(Stock stock) {
		
		this.myStock = stock;
		this.symbolLabel.setText(stock.getSymbol());
		this.symbolPrice.setText("$" + String.valueOf(myStock.getCurrentPrice()));
		
	}

}
