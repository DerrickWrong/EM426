package em426.agents;

import em426.api.*;
import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.beans.value.*;

import java.util.UUID;

/**
 *  Demand class represents a single demand (task, activity, job) for attention
 *  Agents may observe, select, and attempt to act in response to a demand
 *  This implementation will be useful for demands that require effort and capabilities
 * @author Bryan.Moser
 * @since 2025 02
 */
public class Demand implements DemandAPI {

    private UUID id; // no need for property -- won't change

    // A property, from JavaFX, allows the attributes of the class to be observable (and bindable)
    // if only working with a text UI, these attributes could be their plain type (e.g. int, String)
    private StringProperty name; // a human readable short name for use UI labels
    private ObjectProperty<ActType> type;
    private ObjectProperty<DemandState> state;

    private IntegerProperty effort; // effort in seconds (remaining)
    private IntegerProperty effortInitial; // effort in seconds at start
    private IntegerProperty start;
    private IntegerProperty stop;
    private IntegerProperty priority;

    private BooleanProperty recur;
    private IntegerProperty every;
    private IntegerProperty until;

    // DEFAULT VALUES
    private static final ActType DEFAULT_TYPE = ActType.WORK;
    private static final DemandState DEFAULT_STATE = DemandState.INACTIVE;
    private static int DEFAULT_START = 9;
    private static int DEFAULT_STOP = 17;
    private static int DEFAULT_PRIORITY = 50;
    private static int DEFAULT_EFFORT = 3600*4; // 4 hours
    private static int DEFAULT_EVERY = 24;
    private static int DEFAULT_UNTIL = 24*14;
    private static boolean DEFAULT_RECUR = false;

    /**
     * Constructs a demand with default state INACTIVE with default values
     */
    public Demand() {
        this(DEFAULT_TYPE, DEFAULT_EFFORT);
    }

    /**
     * Constructs a demand with default state INACTIVE with _type and nominal _effortSecs
     * @param _type the type of demand effort from set of TYPE_ constants in this class
     * @param _effortSecs in seconds
     */
    public Demand(ActType _type, int _effortSecs) {
        this(_type, _effortSecs, DEFAULT_START, DEFAULT_STOP);
    }

    /**
     * Constructs a demand object with default name and the following input values
     * @param _type  the type of demand effort from set of TYPE_ constants in this class
     * @param _effortSecs  nominal effort in seconds
     * @param _start  the start time as int of this demand
     * @param _stop   the stop time as int of this demand
     */
    public Demand(ActType _type, int _effortSecs, int _start, int _stop) {
        // default name is type and effort in hours
        this(_type, _effortSecs, _start, _stop, _type + ": "+ _effortSecs/3600.0);
    }

    /**
     * Constructs a demand object with defaults and the following input values
     * @param _type  the type of demand effort from set of TYPE_ constants in this class
     * @param _effortSecs  nominal effort in seconds
     * @param _start  the start time as int of this demand
     * @param _stop   the stop time as int of this demand
     * @param _name	  a short text name for human readible tag of this demand
     */
    public Demand(ActType _type, int _effortSecs, int _start, int _stop, String _name) {
        id = UUID.randomUUID();
        type = new SimpleObjectProperty<>(_type);
        state = new SimpleObjectProperty<>(DEFAULT_STATE);

        name = new SimpleStringProperty(_name);
        effort = new SimpleIntegerProperty(_effortSecs);
        effortInitial = new SimpleIntegerProperty(_effortSecs);
        start = new SimpleIntegerProperty(_start);

        // lets prevent stop from being before start
        if (_stop < _start) _stop = _start;

        stop = new SimpleIntegerProperty(_stop);

        // add change listeners so that later we retain start >= stop at all times
        stop.addListener((obs, oldV, newV) -> {
           if (newV.intValue() < start.get()) {
               start.set(newV.intValue());
           }
        });
        start.addListener((obs, oldV, newV) -> {
            if (newV.intValue() > stop.get()) {
                stop.set(newV.intValue());
            }
        });

        priority = new SimpleIntegerProperty(DEFAULT_PRIORITY);

        recur = new SimpleBooleanProperty(DEFAULT_RECUR);
        every = new SimpleIntegerProperty(DEFAULT_EVERY);
        until = new SimpleIntegerProperty(DEFAULT_UNTIL);
    }


    public String toString() {
        String retVal = name.get() + " [" + start.get() + "-" + stop.get() + "] " + type.get() + ": " + getEffortHrs() + " hrs";
        retVal += " priority=" + priority.get();
        if (recur.get())
            retVal += " every " + every.get() + " until " + stop.get();
        return retVal;
    }


    /**
     * Returns true of the demand is active during the period from start to top
     * @param now  an int representing time
     * @return  true if the now time is during an active period for this demand
     */
    public boolean isActive(int now) {
        return (now >= start.get() && now <= stop.get());
    }

    // BELOW is a BUNCH of boilerplate code for getting, setting, and accessing properties
    // Yes, it is verbose, one of the challenges of Java. In most cases the code can be automatically generated.
    // While it would be MUCH simpler to just have a public property which is then .get() and .set(x),
    // It is best practice and convention in Java to encapsulate internal variables instead as privte, and to conform to
    // convention and backward compatibility by have the get and set calls.

    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }

    // NAME -------------------------------------
    public StringProperty nameProperty() {
        return name;
    }
    public String getName() {
        return name.get();
    }
    /**
     * @param name A human readable short name as tag or label in the UI
     */
    public void setName(String name) {
        this.name.set(name);
    }

    // PRIORITY -------------------------------------
    public IntegerProperty priorityProperty() {
        return priority;
    }
    public int getPriority() {
        return priority.get();
    }
    public void setPriority(int priority) {
        this.priority.set(priority);
    }

    // TYPE -------------------------------------
    public ObjectProperty<ActType> typeProperty() {
        return type;
    }
    public ActType getType() {
        return type.get();
    }
    public void setType(ActType type) {
        this.type.set(type);
    }

    // STATE -------------------------------------
    public ObjectProperty<DemandState> stateProperty() {
        return state;
    }
    public DemandState getState() {
        return state.get();
    }
    public void setState(DemandState state) {
        this.state.set(state);
    }

    // EFFORT INITIAL -------------------------------
    public IntegerProperty effortInitialProperty() {
        return this.effortInitial;
    }
    public int getEffortInitial() {
        return this.effortInitialProperty().get();
    }
    public void setEffortInitial(final int effortInitial) {
        this.effortInitialProperty().set(effortInitial);
    }

    // EFFORT -------------------------------------
    public IntegerProperty effortProperty() {
        return effort;
    }
    /*
     * Returns the effort of this demand in seconds
     */
    public int getEffort() {
        return effort.get();
    }
    public void setEffort(int nominalEffort_seconds) {
        this.effort.set(nominalEffort_seconds);
    }

    /*
     * Returns the effort of this demand in hours (convenience method)
     */
    public double getEffortHrs() {
        return effort.get()/3600.0;
    }
    public void setEffortHrs(double _effort) {
        this.effort.set((int) (_effort*3600));
    }

    // START -------------------------------------
    public IntegerProperty startProperty() {
        return start;
    }
    public int getStart() {
        return start.get();
    }
    public void setStart(int start) {
        this.start.set(start);
    }

    // STOP -------------------------------------
    public IntegerProperty stopProperty() {
        return stop;
    }
    public int getStop() {
        return stop.get();
    }
    public void setStop(int stop) {
        this.stop.set(stop);
    }


    // TODO - dont allow stop to be before start.

    // RECUR -------------------------------------
    public BooleanProperty recurProperty() {
        return recur;
    }
    public boolean getRecur() {
        return recur.get();
    }
    public void setRecur(boolean isRecurring) {
        this.recur.set(isRecurring);
    }
    public boolean isRecur() {
        return recur.get();
    }

    // EVERY -------------------------------------
    public IntegerProperty everyProperty() {
        return every;
    }
    public int getEvery() {
        return every.get();
    }
    public void setEvery(int every) {
        this.every.set(every);
    }

    // UNTIL -------------------------------------
    public IntegerProperty untilProperty() {
        return until;
    }
    public int getUntil() {
        return until.get();
    }
    public void setUntil(int until) {
        this.until.set(until);
    }



    // CONVENIENCE METHODS
    /**
     * Set the active period of this demand from start to stop
     * does now allow stop before start
     * @param start
     * @param stop
     */
    public void setPeriod(int start, int stop) {
        if (start > stop) return;
        this.start.set(start);
        this.stop.set(stop);
    }


    /**
     * resets the demands effort to it's original value
     */
    public void reset() {
        effort.set(effortInitial.get());
        state.set(DEFAULT_STATE);

    }

}
