package em426.sim;

import em426.agents.*;
import em426.api.*;
import em426.results.*;
import em426.sim.SimSignature.*;
import em426.world.*;
import javafx.beans.property.*;
import javafx.collections.*;

import java.util.*;

public class Simulator implements SimulatorAPI {
    private String desc = "Simulator for EM426 2025";
    private SimSettings settings;
    private SimSignature signature;
    private World world;

    private int runs, sample;
    private boolean hasSteps;

    IntegerProperty globalTime; // time in secs
    IntegerProperty timeLimit; // time Limit in secs
    IntegerProperty run; // time in secs
    BooleanProperty running;
    private SimResult latestResult;

    public Simulator() {
        settings = new SimSettings();
        settings.add(Double.class, "Time Limit", "at what duration from start should the sim stop, in hrs", 168.0);
        settings.add(Integer.class, "Runs", "how many runs of Monte Carlo attempted", 1);
        settings.add(Integer.class, "Sample Interval", "how many hours for each sample bucket", 1);
        settings.add(String.class, "Description", "what is the design intent of this sim", "default description");
        settings.add(Boolean.class, "Verbose", "the simulator writes to system out during analysis (useful for debugging)", Boolean.FALSE);
        settings.add(Boolean.class, "Allow Steps", "the simulator stops after a step to allow observation then continue by hand", Boolean.TRUE);

        signature = new SimSignature(Type.AGENTBASED_SIMULATOR, "SimEM426", "V2.0", "Bryan EM426 Simulator");

        latestResult = null;
        run = new SimpleIntegerProperty(0);
        globalTime = new SimpleIntegerProperty(0);
        timeLimit = new SimpleIntegerProperty(168 * 3600);  // when the simulator stops if not yet finished
        running = new SimpleBooleanProperty(false);
    }

    /**
     * The model contains the environment in the world where the agents are acting. In this case, places.
     */
    @Override    public WorldAPI getModel() {
        return  world;
    }
    @Override    public void setModel(WorldAPI model) {this.world = (World) model; }

    @Override    public String getDescription() {
        return desc;
    }
    @Override    public void setDescription(String description) {desc = description;}

    @Override    public SimSettings getSettings() {return settings; }
    @Override    public void setSettings(SimSettings settings) {
        this.settings = settings;
    }

    public int getRuns(){
        Object r = settings.getValue("Runs");
        return r==null ? 1 : (int) r;
    }

    /**
     * Establish any sensors, prepare a result, and start the initial run of the simulator
     * @return true if the simulator is complete
     */
    @Override    public boolean run(){

        runs = getRuns();
        sample = (int) settings.getValue("Sample Interval"); // TODO make clear if this is hours
        hasSteps = (boolean) settings.getValue("Allow Steps");

        latestResult = new SimResult(signature, settings, world, runs);

        //  By hand placement of metric sensors on agents and other model objects in the environment

        Place root = (Place) world.getPlaceRoot(); // down casting to get full entity and sensor attributes
        latestResult.addMetric(root, Place.METRIC_Visits).addSummariesAndTimeSeries(sample);

        for (AgentAPI x : world.getAgents()) {
            var agentX = (Agent) x; // down casting to get full entity and sensor attributes
            latestResult.addMetric(agentX, Agent.METRIC_WorkEffortHrs).addSummariesAndTimeSeries(sample);
            latestResult.addMetric(agentX, Agent.METRIC_CommEffortHrs).addSummariesAndTimeSeries(sample);
        }

        reset();

        timeLimit.set(getTimeLimitSecs());
        run.set(0);
        running.set(true);
        return continueRun();
    }

    @Override
    public boolean continueRun() {
        // Monte Carlo loop
        Place root = (Place) world.getPlaceRoot();
        while (run.get() < runs) {
            Agent recentAgent;
            long timeHrs;

            // Global Loop!
            while (globalTime.get() < timeLimit.get()) {

                recentAgent = (Agent) globalStep();
                // TODO if the agent has no supplies then skip
                // sample is added with run, time and value
                timeHrs = getGlobalTime() / 3600;

                latestResult.addSample(run.get(), timeHrs, root, Place.METRIC_Visits);
                latestResult.addSample(run.get(), timeHrs, recentAgent, Agent.METRIC_WorkEffortHrs); // TODO - allow addition of metric of not exist, and thus no need to initiate above
                latestResult.addSample(run.get(), timeHrs, recentAgent, Agent.METRIC_CommEffortHrs);

                if (world.isComplete()) {
                    break;
                }
                // check here for pause between steps
                if (hasSteps) {
                    return false;
                }

                // allow for commits
                //			for(AgentAPI x : agents){
                //
                //			}
            }
            // The monte carlo run ends here, so reset in cae of additional loop
            run.set(run.get() + 1);
            world.resetAgents();
            world.resetDemands();
            setGlobalTime(0);

        }
        latestResult.postProcess();// TODO check if still needed
        running.set(false);
        return true;
}

    public IntegerProperty runProperty() {
        return run;
    }

    @Override    public ResultsAPI getResults() {
        return latestResult;
    }

    @Override    public BooleanProperty runningProperty() {
        return running;
    }

    public int getTimeLimitSecs() {
        return (int) (getTimeLimitHrs() * 3600.0);
    }

    public double getTimeLimitHrs() {
        Object tl = settings.getValue("Time Limit");
        return tl instanceof Double ? (Double) tl : 168.0;
}

    /**
     * finds the next agent (which has lowest agetn time) and calls this agent to step
     * @return the agent that is selected and steps
     */
    private AgentAPI globalStep() {
    // find an agent with the earliest agent time
    AgentAPI nextAgent = Collections.min(world.getAgents(), Comparator.comparing(AgentAPI::getAgentTime));

    // set global time to this earliest of the agent times TODO check for commits
    globalTime.set(nextAgent.getAgentTime());
    nextAgent.step();

    return nextAgent;
}

    public void reset() {
        running.set(false);
        globalTime.set(0);
        run.set(0);
        world.resetDemands();
        world.resetAgents();
    }


public ListProperty<AgentAPI> agentsProperty() {return world.agentsProperty();}
public ObservableList<AgentAPI> getAgents() { return agentsProperty().get();}
public void setAgents(final ObservableList<AgentAPI> agents) { agentsProperty().set(agents);}

public void setWorld(World aSite) {
    this.world = aSite;
}

public IntegerProperty globalTimeProperty() {
    return this.globalTime;
}
public int getGlobalTime() {
    return this.globalTimeProperty().get();
}
public void setGlobalTime(final int globalTime) {
    this.globalTimeProperty().set(globalTime);
}

public IntegerProperty timeLimitProperty() {
    return this.timeLimit;
}
public int getTimeLimit() {
    return this.timeLimitProperty().get();
}
public void setTimeLimit(final int timeLimit) {
    this.timeLimitProperty().set(timeLimit);
}

}
