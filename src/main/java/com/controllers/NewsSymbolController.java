package com.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.models.demands.RedditDataFactory;
import com.models.interfaces.DemandTypeEnum;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class NewsSymbolController {

	@FXML
	Label demandLabel;
	
	@FXML
	Label newsSource; 
	
	@Autowired
	RedditDataFactory reddit;
	 
	
	public void setupController(DemandTypeEnum demandEnum, String sourceText) {
		 
		demandLabel.setText(demandEnum.name());
		newsSource.setText(sourceText);
		
		reddit.pollingPost(sourceText);
	}
	
	
	@FXML
	void checkNewsClicked() {
		
		System.out.println("News clicked and popup a news windows");
		
		
	}
	
}
