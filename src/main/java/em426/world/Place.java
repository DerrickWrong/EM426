package em426.world;

import em426.api.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.scene.shape.*;

import java.util.*;

/**
 * The Place class implements entity and sensor.
 * A place has a shape (2D), is part of a tree so has parent and child places. Contains demands.
 */
public class Place implements EntityAPI, SensorAPI {
    // leaving these attributes public for now, but getter and setter wrappers are defined below. Will be private
    UUID id;
    StringProperty name;
    ObjectProperty<Shape> shape ;
    ObjectProperty<Place> parent;
    ListProperty<Place> children;
    ListProperty<DemandAPI> signals;

    // constructor
    public Place(String name){

        this.id = UUID.randomUUID();

        this.name = new SimpleStringProperty(name);
        this.shape = new SimpleObjectProperty<>(new Rectangle(10, 10));
        this.parent = new SimpleObjectProperty<>();
        this.children = new SimpleListProperty<>(FXCollections.observableArrayList());
        this.signals = new SimpleListProperty<>(FXCollections.observableArrayList());

    }

    // Getter and Setters

    @Override   public UUID getId() { return id; }
    @Override   public Class<?> getType() {return this.getClass(); }

    @Override   public String getName() {return name.get(); }
    @Override   public void setName(String name) {this.name.set(name); }
    @Override   public StringProperty nameProperty() { return name;  }

    @Override   public boolean equals(EntityAPI obj) {
                    return this == obj || (obj instanceof Place && id.equals(((Place)obj).id)); }

    @Override   public Shape getShape() {return shape.get(); }
    @Override   public void setShape(Shape shape) {this.shape.set(shape);}
    @Override   public ObjectProperty<Shape> shapeProperty() {return shape; }

    @Override   public ListProperty<DemandAPI> demandsProperty() {return signals; }
    @Override   public void add(DemandAPI... demands) {
        for (DemandAPI demand : demands) {
            signals.add(demand);
        }
    }
    @Override   public void remove(DemandAPI... demands) {this.signals.removeAll(Arrays.asList(demands)); }

    @Override  public Place getParent() {return parent.get(); }
    public ObjectProperty<Place> parentProperty() {return parent;}
    public void setParent(Place parent) {this.parent.set(parent);}

    public ObservableList<Place> getChildren() {return children.get(); }
    @Override    public ListProperty<Place> childrenProperty() {return children;}
    public void add(EntityAPI... children) {
         for (EntityAPI child : children) {
             if (child instanceof Place) {
                 this.children.add((Place) child);
             }
         }
     }


    public void setXY(int x, int y){
        shape.get().setLayoutX(x);
        shape.get().setLayoutY(y);
    }


// State and calculations
    /**
     * If all signals are COMPLETE or IGNORED, return true
     * @return
     */
    public boolean isComplete() {
        for (DemandAPI d : signals) {
            if (!d.getState().equals(DemandState.COMPLETE) &&
                    !d.getState().equals(DemandState.IGNORED))
                return false;
        }
        return true;
    }

    public boolean hasRemainingEffort(){
        for (DemandAPI demand : signals.get()){
            if (demand.getEffort() > 0) return true;
        }
        return false;
    }

    /**
     *
     * @return remaining effort in seconds of all demands in place
     */
    public int getRemainingEffort(){
        int effort = 0;
        for (DemandAPI demand : signals.get()){
            effort += demand.getEffort();
        }
        return effort;
    }


    // SensorAPI
    /**
     * A place cna have sensors for counting the #of visits (by agents) and # of acts in the place
     */
    public final static String METRIC_Visits = "Visits";
    public final static String METRIC_Acts = "Acts";
    private final List<String> metrics = List.of(METRIC_Visits, METRIC_Acts);

    private int visitsCumulative = 0, actsCumulative = 0;

    @Override    public List<String> getMetricNames() {return metrics;}

    @Override    public boolean hasMetric(String metricName) {return metrics.contains(metricName); }

    @Override    public double sample(String metricName) {
        return switch (metricName) {
            case METRIC_Visits -> visitsCumulative;
            case METRIC_Acts -> actsCumulative;
            default -> 0.0;
        };
    }

    public void resetMetrics(){
        visitsCumulative = 0;
        actsCumulative = 0;
    }

}
