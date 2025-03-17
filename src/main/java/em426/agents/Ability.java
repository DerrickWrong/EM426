package em426.agents;

import em426.api.*;
import javafx.beans.property.*;

import java.util.*;

/**
 * A Class to represent Supplies in an agent based model
 * @author Bryan Moser
 *
 */
public class Ability implements SupplyAPI {
	private UUID id;

	private StringProperty name;
	private ObjectProperty<ActType> type;
	private ObjectProperty<SupplyState>	state;
	private IntegerProperty	capacity; // effort in seconds
	
	// Productivity: multiplier on (demand nominal effort/ supplied effort) 
	// Can this supply satisfy demand with less or more effort compared to nominal? 
	private DoubleProperty efficiency;  
	
	// Attributes of timing could become their own class (availability or calendar)
	private IntegerProperty start;
	private IntegerProperty stop;
	private BooleanProperty recur;
	private IntegerProperty every;
	private IntegerProperty until;
	private DoubleProperty errorRate; // from 0 to 1, with 1 being always an error, 0 being perfect
	
	// DEFAULT VALUES
	private static ActType DEFAULT_TYPE = ActType.WORK;
	private static int DEFAULT_START = 9;
	private static int DEFAULT_STOP = 17;
	private static int DEFAULT_CAPACITY = 3600*8;
	private static double DEFAULT_EFFICIENCY = 1.0;
	private static int DEFAULT_EVERY = 24;
	private static int DEFAULT_UNTIL = 24*14;
	private static boolean DEFAULT_RECUR = false;
	private static double DEFAULT_ERRORRATE = 0;

	public Ability() {
		this(DEFAULT_TYPE, DEFAULT_CAPACITY);
	}
/**
 * Constructs a supply with default state pending with _type and nominal capacity
 * @param _type the type of demand effort from set of TYPE_ constants in this class
 * @param _capacitySecs a capacity for effort (for the timing)in seconds
 */
	public Ability(ActType _type, int _capacitySecs) {
		this(_type, _capacitySecs, DEFAULT_START, DEFAULT_STOP, _type + ":" + _capacitySecs/3600);
	}
	
	/**
	 * Constructs a supply object with default and the following input values
	 * @param _type  the type of demand effort from set of TYPE_ constants in this class
	 * @param _capacitySecs  nominal effort in seconds
	 * @param _start  the start time as int of this demand
	 * @param _stop   the stop time as int of this demand
	 * @param _name		a human readable tag to display in UX 
	 */
	public Ability(ActType _type, int _capacitySecs, int _start, int _stop, String _name) {
		id = UUID.randomUUID();
		type = new SimpleObjectProperty<ActType>(_type);
		capacity = new SimpleIntegerProperty(_capacitySecs);
		efficiency = new SimpleDoubleProperty(DEFAULT_EFFICIENCY);	
		start = new SimpleIntegerProperty(_start);
		stop = new SimpleIntegerProperty(_stop);
		name = new SimpleStringProperty(_name);
		recur = new SimpleBooleanProperty(DEFAULT_RECUR);
		every = new SimpleIntegerProperty(DEFAULT_EVERY);
		until = new SimpleIntegerProperty(DEFAULT_UNTIL);
		errorRate = new SimpleDoubleProperty(DEFAULT_ERRORRATE);
	}
	
	
/*
 *  Several convenience methods
 */
	/**
	 * Sets a recurring pattern of supply capacity (total availability). 
	 * This implies that in each period the supply of nominal effort is available
	 * This does not indicate how the supply is replenished, just that it exists
	 * @param _every
	 * @param _completion
	 */
	public void setRecurrance(int _every, int _completion) {
		this.setRecur(true);
		every.set(_every);
		until.set(_completion);
	}
	
	
	public String toString() {
		String retVal = "Supply- "+ name.get() + " [" + start.get() + "-" + stop.get() + "] " + type.get() + ": " + capacity.get()/3600.0 + " hrs";

		if (recur.get()) retVal += " every " + every.get() + " until "+ until.get();
		return retVal;
	}
	
	/**
	 * Returns true of the supply is active during the period from start to top
	 * @param _from  an int representing the start time of the period
	 * @param _until an int representing the stop time of the period
	 * @return
	 */
	public boolean isAvailable(int _from, int _until) {
		return false;
	}

	
	/**
	 * Set the active period of this demand from start to stop
	 * does now allow stop before start
	 * @param _start
	 * @param _stop
	 */
	public void setPeriod(int _start, int _stop) {
		if (_start > _stop) return;
		start.set(_start);
		stop.set(_stop);
	}

	public double getCapacityHrs() {
		return capacity.get()/3600.0;
	}

	public void setCapacityHrs(double _capacityHrs) {
		capacity.set((int)(_capacityHrs*3600.0));
	}
	
	
	public boolean isMatch(DemandAPI d) {
		//TODO just checking name matching, but could be whole type hierarchical  match
		return (d.getType() == type.get());
	}
	
	/**
	 * return the overlap of hours for this supply with Demand for whole expressed life of the supply and demand
	 * does not check for availability but assumes not allocated to other demands 
	 * @param d
	 * @return the hours of overlap
	 */
	public double getOverlapHrs(Demand d) {
		double total = 0.0;
		for (int n = start.get(); n < this.stop.get(); n++) {
			if (n >= d.getStart() && n <= d.getStop())
				total++;
		}
		if (this.recur.get()) {
			
		}
		
		return total;
	}
	
	/**
	 * return the overlap of hours for this supply with Demand d during the period from _start to _stop
	 * @param d
	 * @param _start
	 * @param _stop
	 * @return the hours of overlap
	 */
	public double getHoursOverlapInPeriod(Demand d, int _start, int _stop) {
		return 0.0; // TBD!
	}
	
    public static ArrayList<Ability> createTestSupplySet() {
    	// Set up some supplies
    	
    	Ability s1 = new Ability(ActType.WORK, 3600*8, 9, 17, "worker" );
    	Ability s2 = new Ability(ActType.COMM, 3600*2, 9, 13, "talker" );

    	// Set up a demand table
    	ArrayList<Ability> oList = new ArrayList<Ability>();

    	oList.add(s1);
    	oList.add(s2);      

    	// do various things to show and test the list

    	// print the Supplies in the list
    	oList.forEach(System.out::println);   
    	
    	return oList;
    }
    
    /*
     *  Generated getters and setters, including properties 
     */
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}

	// NAME

	public StringProperty nameProperty() {
		return this.name;
	}
	
	public String getName() {
		return this.nameProperty().get();
	}
	
	public void setName(String name) {
		this.nameProperty().set(name);
	}

	// CAPACITY

	public IntegerProperty capacityProperty() {
		return this.capacity;
	}
	
	public int getCapacity() {
		return this.capacityProperty().get();
	}

	public void setCapacity(int capacity) {
		this.capacityProperty().set(capacity);
	}

	// PRODUCTIVITY

	public DoubleProperty productivityProperty() {
		return this.efficiency;
	}
	public double getProductivity() {
		return this.productivityProperty().get();
	}
	public void setProductivity(double productivity) {
		this.productivityProperty().set(productivity);
	}

	// START

	public IntegerProperty startProperty() {
		return this.start;
	}
	
	public int getStart() {
		return this.startProperty().get();
	}
	
	public void setStart(int start) {
		this.startProperty().set(start);
	}

	// STOP
	
	public IntegerProperty stopProperty() {
		return this.stop;
	}
	
	public int getStop() {
		return this.stopProperty().get();
	}
	
	public void setStop(int stop) {
		this.stopProperty().set(stop);
	}

	// RECUR
	
	public BooleanProperty recurProperty() {
		return this.recur;
	}
	
	public boolean isRecur() {
		return this.recurProperty().get();
	}
	
	public void setRecur(boolean recur) {
		this.recurProperty().set(recur);
	}

	// EVERY
	
	public IntegerProperty everyProperty() {
		return this.every;
	}
	
	public int getEvery() {
		return this.everyProperty().get();
	}
	
	public void setEvery(int every) {
		this.everyProperty().set(every);
	}

	// UNTIL
	
	public IntegerProperty untilProperty() {
		return this.until;
	}
	
	public int getUntil() {
		return this.untilProperty().get();
	}
	
	public void setUntil(int until) {
		this.untilProperty().set(until);
	}

	// TYPE

	public ObjectProperty<ActType> typeProperty() {
		return this.type;
	}
	
	public ActType getType() {
		return this.typeProperty().get();
	}
	
	public void setType(final ActType type) {
		this.typeProperty().set(type);
	}

	// STATE
	
	public ObjectProperty<SupplyState> stateProperty() {
		return this.state;
	}
	
	public SupplyState getState() {
		return this.stateProperty().get();
	}
	
	public void setState(final SupplyState state) {
		this.stateProperty().set(state);
	}

	// EFFICIENCY

	public DoubleProperty efficiencyProperty() {
		return efficiency;
	}

	public double getEfficiency() {
		return efficiency.get();
	}

	public void setEfficiency(double eff) {
		efficiency.set(eff);
		
	}
	
/**
 * In this simulator, this is the lowest level where supply meets demand
 * Mutates the act with effort, duration, and success
 */
	public ActState attempt(Act a) {

		DemandAPI d = a.demand;

		// assumed this fit was checked during selection, but just in case...
		if (!isMatch(d)) {
			a.success = false;
			return ActState.START;
		}

		// TODO we will assume that the capacity of supply is for one attempt
		// and instantly replenished. If a calendar or horizon is considered, could
		// be capacity per period.

		 var actualEffortRemaining = (int) (d.getEffort()/efficiency.get());

		 // only allow up to the capacity
		 if (actualEffortRemaining > capacity.get()) {
			 a.effort = capacity.get();
			 // what is the portion of nominal effort treated
			 d.setEffort( d.getEffort() -  (int) (a.effort * efficiency.get()));
			 }
		 else
		 {
			 a.effort = actualEffortRemaining;
			 d.setEffort(0);
			 // TODO we could have the demand remember its original nominal effort too
		 }

		a.end = a.start + a.effort; // assumes actual effort is time
		a.success = true;
		return ActState.COMPLETE;

	}

}
