package com.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.models.demands.RedditDataFactory; 
import com.models.interfaces.DemandTypeEnum;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

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
	void checkNewsClicked() throws IOException {
		
		/*
		this.stream.publishMessage(newsSource.getText());

		// Load the news dashboard 
		FXMLLoader loader2 = new FXMLLoader(getClass().getResource("../views/NewsDashboard.fxml"));
		Pane p2 = (Pane) loader2.load(); 
		
		Stage s = new Stage();
		s.setTitle(newsSource.getText());
		s.initModality(Modality.WINDOW_MODAL);
		s.setScene(new Scene(p2));
		s.show();
		*/ 
	}

}
