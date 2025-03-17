package em426.api;


import em426.sim.*;
import javafx.beans.value.*;

public interface SimulatorAPI {

    public WorldAPI getModel(); // return top level model object
   
    public void setModel(WorldAPI model);// set top level model object
    
    public String getDescription();

    public SimSettings getSettings();

    public void setDescription(String description); //Katie: updated setDescription to take a String as input
    
    /**
     * Settings used by the analysis engine to generate a result
     * e.g. how many runs, timelimits, physics used to forecast etc
     * These are switches
     * @param settings
     */
    public void setSettings(SimSettings settings);

    /**
     * Establish any sensors, prepare a result, and start the initial run of the simulator
     * @return true if the simulator is complete
     */
    public boolean run();

    /**
     * Start, or restarts, the simulator and runs until complete or break caused by step pause
     * @return true if the simulation is complete
     */
    public boolean continueRun();
    
    public ResultsAPI getResults();

    /**
     * A flag which shows if the simulator is active
     * @return true if a simulation is in process
     */
    ObservableValue<Boolean> runningProperty();
}
