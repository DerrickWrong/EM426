package em426.api;

import em426.agents.*;
import javafx.beans.property.*;
import javafx.collections.*;

import java.util.*;

/**
 * Agent for EM.426 2025;
 * @author Bryan R. Moser
 *
 */
public interface AgentAPI {
	
	public UUID getId();

	public StringProperty nameProperty() ;
	public String getName() ;
	public void setName(final String name);

	public ListProperty<DemandAPI> demandsProperty() ;
	public ObservableList<DemandAPI> getDemands() ;
	public void setDemands(final ObservableList<DemandAPI> demandsPending) ;

	public ObjectProperty<AgentMode> modeProperty() ;
	public AgentMode getMode() ;
	public void setMode(final AgentMode state) ;

	public ListProperty<SupplyAPI> suppliesProperty();
	public ObservableList<SupplyAPI> getSupplies();
	public void setSupplies(final ObservableList<SupplyAPI> supplies) ;

	public IntegerProperty agentTimeProperty();
	public int getAgentTime();
	public void setAgentTime(final int agentTime) ;

	public ListProperty<Act> actsProperty() ;
	public ObservableList<Act> getActs() ;
	public void setActs(final ObservableList<Act> acts) ;

	public ObjectProperty<EntityAPI> placeProperty() ;
	public EntityAPI getPlace() ;
	public void setPlace(final EntityAPI place) ;

	// AGENT LOOP METHODS -----------------------------------------------------

//	/**
//	 *  Loops until pending demands are exhausted or time limit, whichever comes first
//	 *
//	 * @param timeLimit  time (in seconds) to stop the loop no matter the completion
//	 * @param pauseEvery time (in seconds) to pause the agent. Loop is exited but not completed
//	 * if pause. If pauseEvery is 0, then no pause
//	 */
//	public void loop(int timeLimit, int pauseEvery) ;

	/**
	 * a single loop step
	 */
	public void step() ;

	/**
	 * resets this agent to time = 0 with no pending, active, or moot demands
	 */
	public void reset() ;

	// AGENT STEP METHODS -----------------------------------------------------

	/***
	 * Agent will attempt to observe signals and adjust internal pending demands before selecting
	 * Those observed are added to an agent internal list of demands
	 * @return the state of the agent at the end of this step
	 */
	public AgentMode observe() ;
	/**
	 *  The agent selected amongst a set of demands (it has observed)
	 *  @return the state of the agent at the end of this step
	 */
	public AgentMode select() ;

	/**
	 *  A a selected demand is matched to supply and started
	 *  @return the state of the agent at the end of this step
	 */
	public AgentMode act() ;

	/**
	 *  The current act is completed through this method
	 *  @return the state of the agent at the end of this step
	 */
	public AgentMode complete() ;

	/**
	 *  The agent will move forward in time with no activity this step
	 *  @return the state of the agent at the end of this step
	 */
	public AgentMode doNothing() ;

	// some move up into simulator
//	/*
//	 * set agent to time = 0 and begin loop (beyond a single step, until finish or pause
//	 */
//	public void start() ;
//
//	/*
//	 * continue an agent loop that has been paused (without resetting nor returning to time = 0)
//	 */
//	public void continueSim() ;



	public ObjectProperty<DemandAPI> selectedDemandProperty();
	public DemandAPI getSelectedDemand() ;
	public void setSelectedDemand(final DemandAPI selectedDemand) ;

	public ObjectProperty<SupplyAPI> selectedSupplyProperty() ;
	public SupplyAPI getSelectedSupply() ;
	public void setSelectedSupply(final SupplyAPI selectedSupply);

	public int getPendingLimit();
	public IntegerProperty pendingLimitProperty();
	public void setPendingLimit(int pendingLimit);

	public int getActiveLimit();
	public IntegerProperty activeLimitProperty();
	public void setActiveLimit(int activeLimit);

	public boolean isRandomObserve() ;
	public BooleanProperty randomObserveProperty();
	public void setRandomObserve(boolean randomObserve);

	public boolean isRandomSelect();
	public BooleanProperty randomSelectProperty();
	public void setRandomSelect(boolean randomSelect);

	public boolean isRandomAct();
	public BooleanProperty randomActProperty();
	public void setRandomAct(boolean randomSelect);
}
