package com.controllers;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.SpringFXManager;
import com.models.demands.News;
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
	}

	@FXML
	void checkNewsClicked() throws IOException {

		FXMLLoader loader = SpringFXManager.getInstance().loadFxml("views/NewsDashboard.fxml");
		Pane p2 = (Pane) loader.load();

		NewsDashboardController ndc = loader.getController();
		List<News> newsPosts = reddit.getPosts(newsSource.getText());
		ndc.renderData(newsPosts);

		Stage s = new Stage();
		s.setTitle(newsSource.getText());
		s.initModality(Modality.WINDOW_MODAL);
		s.setScene(new Scene(p2));
		s.show();

	}

}
