
package com.MIT.agents;

import javafx.beans.property.*;
import javafx.collections.*; 

import java.util.*;
 
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.MIT.api.AgentAPI;
import com.MIT.api.AgentMode;
import com.MIT.api.DemandAPI;  

import jakarta.annotation.PostConstruct;

@Component
@Scope("prototype")
public class Agent implements AgentAPI {

	private UUID myID = UUID.randomUUID();
	private String name;
	private int score;
 
 
	@PostConstruct
	void setUp() {
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
