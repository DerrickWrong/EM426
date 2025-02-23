package com.controllers;
   
import java.util.List;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.models.demands.News;

import javafx.fxml.FXML; 
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
 
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class NewsDashboardController {
 
	@FXML
	TableView<News> tableViewTable;
	
	@FXML
	TableColumn<News, String> titleColumn, postidColumn, numCommColumn, scoreColumn;
	
	@FXML
    public void initialize() {
		
		titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
		postidColumn.setCellValueFactory(new PropertyValueFactory<>("postId"));
		numCommColumn.setCellValueFactory(new PropertyValueFactory<>("numOfComments"));
		scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
	}
	
	public void renderData(List<News> newsData) {
		
		tableViewTable.getItems().addAll(newsData);
		  
	}
	
	
}
