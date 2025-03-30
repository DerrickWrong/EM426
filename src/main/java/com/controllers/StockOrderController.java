package com.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.models.demands.StockOrder;
import com.utils.FxScheduler;

import jakarta.annotation.PostConstruct;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import reactor.core.publisher.Flux;

@Component
public class StockOrderController {

	@FXML
	TableView<StockOrder> orderDemandTable;
	
	@FXML
	TableColumn<StockOrder, String> orderTypeCol, uuidCol, bidPriceCol, volumeCol, statusCol;

	@Autowired
	Flux<StockOrder> stockOrderFlux;

	private final ObservableList<StockOrder> data = FXCollections.observableArrayList();
	
	@FXML
	public void initialize() {
		
		this.orderDemandTable.setItems(data);
		
		orderTypeCol.setCellValueFactory(new PropertyValueFactory<StockOrder, String>("type"));
		uuidCol.setCellValueFactory(new PropertyValueFactory<StockOrder, String>("orignatorUUID"));
		bidPriceCol.setCellValueFactory(new PropertyValueFactory<StockOrder, String>("bidPrice"));
		volumeCol.setCellValueFactory(new PropertyValueFactory<StockOrder, String>("numOfShares"));
		statusCol.setCellValueFactory(new PropertyValueFactory<StockOrder, String>("status"));
		
	}
	
	
	@PostConstruct
	void init() {
		 
		// map the data structure to tableview for display
		this.stockOrderFlux.publishOn(FxScheduler.fxThread()).subscribe(order -> {
			
			this.data.add(order);
			
		});
	}
	
	

}
