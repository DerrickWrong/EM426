package em426.world;

import em426.api.*;
import javafx.beans.property.*;
import javafx.collections.*;

import java.util.*;

/**
 * An implementation for EM.426 in 2025 of a simple world of spaces and connections
 * Ntoe this is a specific domain implementation, with places and agents
 */
public class World implements WorldAPI, SensorAPI {

    private StringProperty nameProperty;

    private ListProperty<EntityAPI> places;
    private ObjectProperty<EntityAPI> placeRoot;
    public ObjectProperty<EntityAPI> placeSelected;

    private ListProperty<AgentAPI> agents;
    public ObjectProperty<AgentAPI> agentSelected;


    public World() {
        nameProperty = new SimpleStringProperty();

        places = new SimpleListProperty<>(FXCollections.observableArrayList());
        placeRoot = new SimpleObjectProperty<>();
        placeSelected = new SimpleObjectProperty<>();

        agents = new SimpleListProperty<>(FXCollections.observableArrayList());
        agentSelected = new SimpleObjectProperty<>();
    }

    public World(String name){
        this();
        this.nameProperty.set(name);
    }

    // Getters and Setters, with JavaFX property

    @Override   public Class<?> getType() {return World.class; } // necessary as is in interface; needed?

    @Override   public String getName() {return nameProperty.get();}
    @Override   public void setName(String name) {this.nameProperty.set(name);}
    @Override   public StringProperty nameProperty() {return nameProperty; }

    // ENTITIES
    @Override public ListProperty<EntityAPI> placesProperty() { return places; }
    public ObservableList<EntityAPI> getPlaces() {return places.get(); }
    public void setPlaces(ObservableList<EntityAPI> places) { this.places.set(places); }

    @Override  public ObjectProperty<EntityAPI> placeRootProperty() { return placeRoot; }
    public EntityAPI getPlaceRoot() { return placeRoot.get();}
    public void setPlaceRoot(EntityAPI placeRoot) {
        if(!places.contains(placeRoot))
            places.add(placeRoot);
        this.placeRoot.set(placeRoot); }

    public EntityAPI getEntitySelected() { return placeSelected.get();  }
    public ObjectProperty<EntityAPI> entitySelectedProperty() { return placeSelected;  }
    public void setEntitySelected(EntityAPI entitySelected) { this.placeSelected.set(entitySelected); }

    // AGENTS
    @Override  public ListProperty<AgentAPI> agentsProperty() { return agents; }
    public ObservableList<AgentAPI> getAgents() { return agents.get();   }
    public void setAgents(ObservableList<AgentAPI> agents) { this.agents.set(agents); }

    public AgentAPI getAgentSelected() { return agentSelected.get(); }
    public ObjectProperty<AgentAPI> agentSelectedProperty() { return agentSelected; }
    public void setAgentSelected(AgentAPI agentSelected) { this.agentSelected.set(agentSelected);  }

    // State

    /**
     * A world is "complete" if all demands have no remaining effort
     * @return true if no remainign effort
     */
    public boolean isComplete(){
        for (EntityAPI entity : places.get()){
            if (((Place) entity).hasRemainingEffort()) return false;
        }
        return true;
    }

    // TODO complete if ignored or complete (could be remainign effort?)

    /**
     * resets teh demands in all places
     */
    public void resetDemands(){
        places.forEach(place -> {
            place.demandsProperty().get().forEach(DemandAPI::reset);
        });

    }

    public void resetAgents() {
        // TODO -- where  do we reset agents in place?
        agents.forEach(AgentAPI::reset);
    }

    // Sensor API methods

    private UUID id = UUID.randomUUID();
    @Override     public UUID getId() { return id;}

    public final static String METRIC_AgentsActive = "Agents Active";
    public final static String METRIC_PlacesActive = "Places Active";
    public final static String METRIC_EffortRemaining = "Effort Remaining Hrs";

    List<String> metrics = Arrays.asList(METRIC_AgentsActive, METRIC_PlacesActive);

    private int agentsActive = 0;
    private int placesActive = 0;

    @Override
    public List<String> getMetricNames() {return metrics;  }

    @Override
    public boolean hasMetric(String metricName) {
        return metrics.contains(metricName);
    }

    @Override
    public double sample(String metricName) {
        return switch (metricName) {
            case METRIC_AgentsActive -> agentsActive;
            case METRIC_PlacesActive -> placesActive;
            case METRIC_EffortRemaining -> getEffortRemainingHrs();
            default -> 0.0;
        };
    }

    @Override
    public void resetMetrics() {
        agentsActive = 0;
        placesActive = 0;
    }



    public double getEffortRemainingHrs() {
        double effortRemaining = 0;
        for (EntityAPI entity : places.get()){
            effortRemaining += ((Place) entity).getRemainingEffort()/3600.0;
        }
        return effortRemaining;
    }
}
