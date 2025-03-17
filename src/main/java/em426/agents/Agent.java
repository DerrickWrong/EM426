
package em426.agents;

import em426.api.*;
import em426.world.*;
import javafx.beans.property.*;
import javafx.collections.*;

import java.util.*;
/**
 *  Agent class represents a person or team who can move, apply effort to act in response to demands, and coordinate with others
 * @author Bryan.Moser
 * @since 2025 02
 */
public class Agent implements AgentAPI, SensorAPI {
    UUID id;
    StringProperty name;

    ListProperty<SupplyAPI> supplies;     // this agent's abilities
    ListProperty<DemandAPI> demands;     // internal set of demands for selection towards completion
    ListProperty<Act> acts;        // simply for archive, or possible learning, of past acts

    ListProperty<DemandAPI> signals;    // external -- bound to outside entities (pointer only)

    ObjectProperty<AgentMode> mode;  // the mode of the agent in the attention cycle
    ObjectProperty<EntityAPI> place;  // the place (location) of the agent, if any

    IntegerProperty agentTime; // the time as understood by the agent

    ObjectProperty<DemandAPI> selectedDemand; // the currently selected demand
    ObjectProperty<SupplyAPI> selectedSupply; // the current selected supply
    ObjectProperty<Act> inProcessAct; // the act that is underway

    BooleanProperty randomSelect; // if true, the selection of pending will be in random order
    BooleanProperty randomAct; // if true, the selection of active  will be in random order
    BooleanProperty randomObserve; // if true, the observation of external signals will be in random order

    IntegerProperty pendingLimit;  // how many demands can be observed and pending at same time
    IntegerProperty activeLimit;  // how many demands can be selected and active at same time


    //private int timeLimit;  // TODO are these external?
   // private int timePausePeriod; // when to make a next pause as duration from current time
    //private int agentLoopDelay;  // a pause added to the agent loop for simulation observation

    final static int WaitDuration = 300;

    public Agent() {
        id = UUID.randomUUID();
        name = new SimpleStringProperty("agent");

        supplies = new SimpleListProperty<>(FXCollections.observableArrayList());
        demands = new SimpleListProperty<>(FXCollections.observableArrayList());
        signals = new SimpleListProperty<>(FXCollections.observableArrayList());
        acts = new SimpleListProperty<>(FXCollections.observableArrayList());

        var emptyList = new SimpleListProperty<DemandAPI>(FXCollections.observableArrayList());

        mode = new SimpleObjectProperty<>(AgentMode.INACTIVE);
        place = new SimpleObjectProperty<EntityAPI>();
        
        

        // time related properties
        agentTime = new SimpleIntegerProperty(0);

//        timeLimit = 3600 * 24 * 14; // 2 weeks
//        timePausePeriod = 3600 * 4;// each 4 hours
//        agentLoopDelay = 0;  // for future use

        selectedDemand = new SimpleObjectProperty<DemandAPI>();
        selectedSupply = new SimpleObjectProperty<SupplyAPI>();
        inProcessAct = new SimpleObjectProperty<Act>();

        // behavior attributes
        pendingLimit = new SimpleIntegerProperty(1);
        activeLimit = new SimpleIntegerProperty(3);

        randomObserve = new SimpleBooleanProperty(false); // if true, the observation of external signals will be in random order
        randomSelect = new SimpleBooleanProperty(false); // if true, the selection of pending will be in random order
        randomAct = new SimpleBooleanProperty(false); // if true, the attention to active demands will be in random order
    }

    @Override   public UUID getId() {
        return id;
    }

// Getter and Setter implementations
    @Override   public String getName() {return name.get();   }
    @Override   public StringProperty nameProperty() {return name; }

    @Override   public Class<?> getType() {return Agent.class; }

    @Override   public void setName(String name) { this.name.set(name); }

    @Override   public ObjectProperty<AgentMode> modeProperty() {
        return mode;
    }
    @Override   public AgentMode getMode() {
        return mode.get();
    }
    @Override   public void setMode(AgentMode state) { mode.set(state); }

    @Override   public EntityAPI getPlace() { return place.get(); }
    @Override   public ObjectProperty<EntityAPI> placeProperty() {return place; }
    @Override   public void setPlace(EntityAPI place) {this.place.set(place); }

    @Override   public ListProperty<SupplyAPI> suppliesProperty() {return supplies; }
    @Override   public ObservableList<SupplyAPI> getSupplies() {return supplies.get();}
    @Override   public void setSupplies(ObservableList<SupplyAPI> supplies) {this.supplies.set(supplies);}

    @Override    public ObservableList<DemandAPI> getDemands() {return demands.get();}
    @Override    public ListProperty<DemandAPI> demandsProperty() {return demands; }
    @Override    public void setDemands(ObservableList<DemandAPI> demands) {this.demands.set(demands);    }

    @Override    public int getAgentTime() {     return agentTime.get();    }
    @Override    public IntegerProperty agentTimeProperty() {return agentTime; }
    @Override    public void setAgentTime(int agentTime) { this.agentTime.set(agentTime);    }


    public Act getInProcessAct() {
        return inProcessAct.get();
    }
    public ObjectProperty<Act> inProcessActProperty() {
        return inProcessAct;
    }
    public void setInProcessAct(Act inProcessAct) {
        this.inProcessAct.set(inProcessAct);
    }

    @Override   public DemandAPI getSelectedDemand() {return selectedDemand.get(); }
    @Override   public ObjectProperty<DemandAPI> selectedDemandProperty() { return selectedDemand; }
    public void setSelectedDemand(DemandAPI selectedDemand) {this.selectedDemand.set(selectedDemand); }

    @Override   public SupplyAPI getSelectedSupply() {  return selectedSupply.get(); }
    @Override   public ObjectProperty<SupplyAPI> selectedSupplyProperty() {return selectedSupply; }
    public void setSelectedSupply(SupplyAPI selectedSupply) {this.selectedSupply.set(selectedSupply); }

    public ObservableList<DemandAPI> getSignals() {return signals.get(); }
    public ListProperty<DemandAPI> signalsProperty() {return signals; }
    public void setSignals(ObservableList<DemandAPI> signals) {this.signals.set(signals);}


    // Agent Loop methods

    /*
     * takes a single step in the loop
     */
    @Override   public void step() {
        switch (mode.get()) {
            case INACTIVE:
                mode.set(AgentMode.OBSERVING);
                break;
            case WAITING: // the final step for this agent
                mode.set(doNothing());
                return;
            case OBSERVING:
                mode.set(observe());
                //mode.set(observe());
                break;
            case SELECTING:
                mode.set(select());
                break;
            case ACTING: // an act has started and is not yet complete
                mode.set(act());
                break;
            case COMPLETING: // wrapping up an act
                mode.set(complete());
                break;
            case TERMINATING: // the final step for this agent
                terminate();
                return;
            case PAUSED: // pause and exit the loop
                return;
            default:
        }
    }

    /**
     * resets this agent to time = 0 with no pending, active, or moot demands
     */
    @Override   public void reset() {
        agentTime.set(0);
        demands.clear();
        acts.clear();

        selectedDemand.set(null);
        selectedSupply.set(null);
        inProcessAct.set(null);

       resetMetrics();

        setMode(AgentMode.INACTIVE);
    }

    /***
     * Agent will attempt to observe signals and adjust internal pending demands before selecting
     * Those observed are added to an agent internal list of demands
     * @return the state of the agent at the end of this step
     */
    @Override
    public AgentMode observe() {
        // Note the management of the signals is handled externally
        // if random observe, then iterate on signals from a random start point.
        int size = signals.size();
        int offset = randomObserve.get() ? getRandomDemand(DemandState.INACTIVE): 0;

        int pendingCount = countDemands(DemandState.PENDING);
        for (int n = 0; n < size; n++) {
            if (pendingCount >= pendingLimit.get()){
                break;
            }
            DemandAPI d = signals.get((n + offset) % size); // TODO - only select unhandled demands
            if (!demands.contains(d)) { // check status -- if observable add
                demands.add(d);
                d.setState(DemandState.PENDING); // TODO - have this tag be specific to agent, and not in external signal
                pendingCount += 1;
            }
        }
        // the signals are external demands visible to this agent
        // agent will look and (under conditions) respond to signal and add to internal demands
        if (countDemands(DemandState.PENDING, DemandState.ACTIVE) == 0) {
            return AgentMode.WAITING;
        }

        agentTime.set(agentTime.get() + WaitDuration);// TODO shall we set a default observation time?  save as act?
        return AgentMode.SELECTING;

    }

    /**
     * internal convenience to count occurrence of 1 or more states in the demands list
     * @param state
     * @return the count of demands with these states
     */
    private int countDemands(DemandState ... state){
        int count = 0;
        for (DemandState s : state) {
            count += (int) demands.get().stream().filter(d -> d.getState() == s).count();
        }
        return count;
    }

    private int getRandomDemand(DemandState state) {
        // if randomAct is true, then iterate on current active demands from a random start point.
        int size = countDemands(DemandState.ACTIVE);
        int offset = randomAct.get() ? (int) (Math.random() * size) : 0;
        return getDemandNthIndex(offset, state);

    }
    // returns the index of nth demand of type, or 0 if not present
    // we use this so we can start at an arbitrary index
    private int getDemandNthIndex(int n, DemandState state) {
        int count = 0;
        for (int i=0; i<demands.size(); i++) {
            if (demands.get(i).getState() == state) {
                count++;
                if(count == n){
                    return i;
                }
            }
            count += 1;
        }
        return 0;
    }

    /**
     * The agent selected amongst a set of demands (it has observed)
     *
     * @return the state of the agent at the end of this step
     */
    @Override
    public AgentMode select() {

        AgentMode retV;
        int offset=0;
        selectedDemand.set(null);

        // Let's start with our currently active demands

        // if randomAct is true, then iterate on current active demands from a random start point.
        // iterate across active demands
        int size = countDemands(DemandState.ACTIVE);
        if (size  > 0) {
            if (randomAct.get()) offset = getRandomDemand(DemandState.ACTIVE);

            for (int n = 0; n < demands.size(); n++) {
                DemandAPI d = demands.get((n + offset) % demands.size());
                if (d.getState() != DemandState.ACTIVE) {
                    continue;
                }
                SupplyAPI s = match(d);
                if (s != null) {
                    selectedDemand.set(d);
                    selectedSupply.set(s);

                    return AgentMode.ACTING;
                } else {
                    // for this active demand, no supplies fit.  Should we ditch it? Or ignore to come back?
                    // note that if supplies are static and don't evolve, this situation might not change
                    d.setState(DemandState.IGNORED);
                }
            }
        }
        // if we got this far, none of our existing supplies and demands match
        // Move on to PENDING demands and find one or more
        retV =  AgentMode.OBSERVING;
        offset = 0;
        // if random select, then iterate on current active demands from a random start point.
        size = countDemands(DemandState.PENDING);
        if (size  > 0) {
            if (randomSelect.get()) offset = getRandomDemand(DemandState.PENDING);

            for (int n = 0; n < demands.size(); n++) {
                // don't proceed if the active limit already reached.
                if (this.countDemands(DemandState.ACTIVE) >= activeLimit.get())
                    break;

                DemandAPI d = demands.get((n + offset) % demands.size());
                if (d.getState() != DemandState.PENDING) {
                    continue;
                }

                // look for a supply to match (again.. could be periodic?)
                // TODO should we wait identify selected vs active separately?
                SupplyAPI s = match(d);
                if (s != null) {
                    selectedDemand.set(d); // we found a matching supply
                    selectedSupply.set(s);
                    d.setState(DemandState.ACTIVE);
                    retV = AgentMode.ACTING;
                } else {
                    // for this pending demand, no supplies fit.  Should we ditch it? Or ignore to come back?
                    d.setState(DemandState.IGNORED);
                }
            }
        }
        // in this case, no pending or active demands match.  Shall we wait?  observe? compete?
        return retV;
    }

    /**
     * returns the first matching supply or null if none present
     *
     * @param d
     * @return
     */
    private SupplyAPI match(DemandAPI d) {
        for (SupplyAPI s : supplies) {
            if (s.isMatch(d)) { // we found a matching supply
                return s;
            }
            // we could handle the matching with horizons... a way to look at matching within rules and time
            // we would consider will I EVER able to match,even in future. If not, then ignore,otherwise wait or defer for now
        }
        return null;
    }

    /**
     * The agent will move forward in time with no activity this step
     *
     * @return the state of the agent at the end of this step
     */
    @Override
    public AgentMode doNothing() {
        agentTime.set(agentTime.get() + 5 * 60); // 5 minutes
        return AgentMode.OBSERVING;
    }



    /**
     * A a selected demand is matched to supply and started
     *
     * @return the state of the agent at the end of this step
     */
    @Override
    public AgentMode act () {
        // TODO note that the number of active demands could be >1 up tp active limit. Which is selected?
        Act a = new Act(); // TODO where is agent set for act?
        a.GetSet(agentTime.get(), this, selectedSupply.get(), selectedDemand.get());
        inProcessAct.setValue(a); // to allow acts across many steps
        a.setAgent(this);
        acts.add(a);

        a.attempt();

        agentTime.set(a.end);

        switch (a.demand.getType()) {
            case WORK:
                effortWorkCumulative += a.getEffort()/3600.0;
                break;
            case COMM:
                effortCommCumulative += a.getEffort()/3600.0;
            default:
                break;
        }
        // this approach has the agent keep teh same demand until it is chewed down
        if (selectedDemand.get().getEffort() == 0)
            return AgentMode.COMPLETING;
        else
            return AgentMode.ACTING;
    }

    /**
     * The current act is completed through this method
     *
     * @return the state of the agent at the end of this step
     */
    @Override
    public AgentMode complete() {

        // What effort was required?  How much time has passed?
        // agentTime.set(inProcessAct.get().end);
        // TODO - partial completion of demand

        selectedDemand.get().setState(DemandState.COMPLETE);
        selectedDemand.set(null);
        selectedSupply.set(null);
        inProcessAct.set(null);

        return AgentMode.OBSERVING;
    }

    /**
     * for any completed acts that are before the global time, commits them
     *
     * @param globalTime
     */

    public void commit ( int globalTime){
        inProcessAct.get().setState(ActState.COMMITTED);

    }

    /**
     * terminate is only called as the last step of an agent loop
     *
     * @return
     */

    public AgentMode terminate() {
        return null;
    }

    // ACTS

    public ListProperty<Act> actsProperty () {
        return this.acts;
    }
    public ObservableList<Act> getActs () {
        return this.actsProperty().get();
    }
    public void setActs ( final ObservableList<Act> acts){
        this.actsProperty().set(acts);
    }


    // Limits for agent internal attention -- how many at one time?
    public int getPendingLimit() {
        return pendingLimit.get();
    }
    public IntegerProperty pendingLimitProperty() {
        return pendingLimit;
    }
    public void setPendingLimit(int pendingLimit) {
        this.pendingLimit.set(pendingLimit);
    }

    public int getActiveLimit() {
        return activeLimit.get();
    }
    public IntegerProperty activeLimitProperty() {
        return activeLimit;
    }
    public void setActiveLimit(int activeLimit) {
        this.activeLimit.set(activeLimit);
    }


    // Behavioral attributes
    public boolean isRandomSelect() {
        return randomSelect.get();
    }
    public BooleanProperty randomSelectProperty() {
        return randomSelect;
    }
    public void setRandomSelect(boolean randomSelect) {
        this.randomSelect.set(randomSelect);
    }

    public boolean isRandomAct() {
        return randomAct.get();
    }
    public BooleanProperty randomActProperty() {
        return randomAct;
    }
    public void setRandomAct(boolean isRandom) {
        randomAct.set(isRandom);
    }

    public boolean isRandomObserve() {
        return randomObserve.get();
    }
    public BooleanProperty randomObserveProperty() {
        return randomObserve;
    }
    public void setRandomObserve(boolean randomObserve) {
        this.randomObserve.set(randomObserve);
    }

    // Sensor API

    public final static String METRIC_WorkEffortHrs = "Work Effort Hrs";
    public final static String METRIC_CommEffortHrs = "Comm Effort Hrs";

    private final List<String> metrics = List.of(METRIC_WorkEffortHrs, METRIC_CommEffortHrs);

    @Override
    public List<String> getMetricNames() {return metrics;}

    @Override
    public boolean hasMetric(String metricName) {return metrics.contains(metricName); }

    @Override
    public double sample(String metricName) {
        return switch (metricName) {
            case METRIC_WorkEffortHrs -> effortWorkCumulative;
            case METRIC_CommEffortHrs -> effortCommCumulative;
            default -> 0.0;
        };
    }

    public void resetMetrics(){
        effortWorkCumulative = 0;
        effortCommCumulative = 0;
    }

    double effortWorkCumulative = 0;
    double effortCommCumulative = 0;

    // Effort accumulators (convenience-- later sampling approaches can replace these)
    public double getEffortWorkCumulative() {
        return effortWorkCumulative;
    }
    public void setEffortWorkCumulative(double effortWorkCumulative) {
        this.effortWorkCumulative = effortWorkCumulative;
    }

    public double getEffortCommCumulative() {
        return effortCommCumulative;
    }
    public void setEffortCommCumulative(double effortCommCumulative) {
        this.effortCommCumulative = effortCommCumulative;
    }
}
