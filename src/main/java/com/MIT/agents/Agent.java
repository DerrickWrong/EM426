
package com.MIT.agents;

import javafx.beans.property.*;
import javafx.collections.*;
import javafx.util.Pair;
import reactor.core.publisher.Sinks;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.MIT.api.AgentAPI;
import com.MIT.api.AgentMode;
import com.MIT.api.DemandAPI;
import com.models.SimulatedTradingMarket;
import com.models.interfaces.DemandTypeEnum;

import jakarta.annotation.PostConstruct;

@Component
@Scope("prototype")
public class Agent implements AgentAPI {

	private UUID myID = UUID.randomUUID();
	private String name;
	private int score;

	@Autowired
	Sinks.Many<Pair<DemandTypeEnum, Demand>> demandSink;

	@Autowired
	SimulatedTradingMarket market;

	@PostConstruct
	void setUp() {

		demandSink.asFlux().subscribe(c -> {

			// TODO - Listen to demands / Observe() / bascially training the Neural Nets

		});

		this.market.listen().subscribe(i -> {

			// invoke select()??? TODO Need to add some sort of policy

			this.act();

		});

	}

	protected void act() {

		// guess number
	}

	protected void select() {
	}

	protected void observe() {
	}

	public Agent() {
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public UUID getId() {
		return myID;
	}

	@Override
	public ObjectProperty<AgentMode> modeProperty() {
		return null;
	}

	@Override
	public AgentMode getMode() {
		return null;
	}

	@Override
	public void setMode(AgentMode state) {

	}

	@Override
	public ListProperty<DemandAPI> signalsProperty() {
		return null;
	}

	@Override
	public ObservableList<DemandAPI> getSignals() {
		return null;
	}

	@Override
	public void setSignals(ObservableList<DemandAPI> signals) {

	}
}
