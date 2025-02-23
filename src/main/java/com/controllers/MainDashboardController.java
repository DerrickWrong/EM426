package com.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.SpringFXManager;
import com.MIT.agents.Agent;
import com.models.SimulatedTradingMarket;
import com.models.interfaces.DemandTypeEnum;

import jakarta.annotation.PostConstruct;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.util.Pair;
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

	@Autowired
	SimulatedTradingMarket market;

	@PostConstruct
	void setUp() {

		this.messageSink.asFlux().publishOn(fxScheduler).subscribe(d -> {

			FXMLLoader loader = SpringFXManager.getInstance().loadFxml("views/NewsSymbol.fxml");
			try {
				stockHbox.getChildren().add(loader.load());
				NewsSymbolController nsc = loader.getController();
				nsc.setupController(d.getKey(), d.getValue());

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		});

	}

	@FXML
	private void addStockClicked() throws IOException {
		System.out.println("add stock button clickeed");
	}

	@FXML
	private void addPlayerClicked() throws IOException {
		System.out.println("add player button clickeed");
	}

	private void makePlayer(String name) throws IOException {

		Agent modelAgent = this.market.createOrFindAgent(name);

		FXMLLoader loader = SpringFXManager.getInstance().loadFxml("views/AgentAvatar.fxml");
		Pane p = (Pane) loader.load();

		PlayerController pc = loader.getController();
		pc.setAgent(modelAgent);

		// add to main dash board
		playerHBox.getChildren().add(p);

	}

	private void makeStock(String name) throws IOException {

		FXMLLoader loader = SpringFXManager.getInstance().loadFxml("views/StockSymbol.fxml");
		Pane p = (Pane) loader.load();

		
		
		this.stockHbox.getChildren().add(p);

	}

	// invoke at Main after everything has been setup
	public void preSeedEnvironment() throws IOException {

		// seed the board with news
		//this.messageSink.tryEmitNext(new Pair<>(DemandTypeEnum.NEWS, "wallstreetbets"));
		//this.messageSink.tryEmitNext(new Pair<>(DemandTypeEnum.NEWS, "stocks"));

		// seed the players
		this.makePlayer("LoneShark");
		this.makePlayer("2Faces");
		this.makePlayer("3rd Wheel");

	}

}
