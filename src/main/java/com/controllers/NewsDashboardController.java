package com.controllers;

import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.stereotype.Component;

import com.models.demands.RedditDataFactory;

import jakarta.annotation.PostConstruct;
import javafx.fxml.FXML; 
import javafx.scene.control.TableColumn;

@Component 
public class NewsDashboardController {
 
	@FXML
	TableColumn titleColumn;
	
	@FXML
	TableColumn postidColumn;
	
	@FXML
	TableColumn numCommColumn;
	
	@FXML
	TableColumn scoreColumn;
	
	@Autowired
	RedditDataFactory redditFac;
	
	
	
	@PostConstruct
	void setup() {
		
		
	}
	
	
}
