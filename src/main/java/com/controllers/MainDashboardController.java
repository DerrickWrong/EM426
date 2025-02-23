package com.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.SpringFXLoader;
import com.models.interfaces.DemandTypeEnum;

import jakarta.annotation.PostConstruct;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.util.Pair;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.Many;
import reactor.core.scheduler.Scheduler;

@Component
public class MainDashboardController {

	@FXML
	private Label dataLable;

	@FXML
	private HBox stockHbox;

	@FXML
	private HBox playerHBox;

	@Autowired
	Many<Pair<DemandTypeEnum, String>> messageSink;

	@Autowired
	@Qualifier("fxScheduler")
	private Scheduler fxScheduler;

	@PostConstruct
	void setUp() {

		this.messageSink.asFlux().publishOn(fxScheduler).subscribe(d -> {

			FXMLLoader loader = SpringFXLoader.getInstance().loadFxml("views/NewsSymbol.fxml");
			try {
				stockHbox.getChildren().add(loader.load());
				NewsSymbolController nsc = loader.getController();
				nsc.setupController(d.getKey(), d.getValue());

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		});

		// seed the board
		this.messageSink.tryEmitNext(new Pair<>(DemandTypeEnum.NEWS, "wallstreetbets"));
	}

	@FXML
	private void addStockClicked() throws IOException {
		System.out.println("add stock button clickeed");

		FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/StockSymbol.fxml"));
		Pane p = (Pane) loader.load();
		stockHbox.getChildren().add(p);

		FXMLLoader loader2 = new FXMLLoader(getClass().getResource("../views/NewsSymbol.fxml"));
		Pane p2 = (Pane) loader2.load();
		stockHbox.getChildren().add(p2);

	}

	@FXML
	private void addPlayerClicked() throws IOException {
		System.out.println("add player button clickeed");

		FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/AgentAvatar.fxml"));
		Pane p = (Pane) loader.load();
		playerHBox.getChildren().add(p);

	}

}
