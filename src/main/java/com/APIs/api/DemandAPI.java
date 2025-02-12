package em426.api;

import javafx.beans.property.*;

import java.util.*;

/**
 * A demand is a described need for attention, for an agent to apply abilities and effort to satisfy the demand.
 * Demands have a unique ID, a name, priority, the type of activity expected, nominal effort required, and current state.
 * The timing of this demand (request) can be characterized with start, stop, and recurrence.
 * @author Bryan R. Moser
 *
 */
public interface DemandAPI {
		
		// A property, from JavaFX, allows the attributes to be observable (and bindable)
		// if only working with a text UI and not using JavaFX, no need for property; these attributes could be their plain POJO type (e.g. int, String)
		
		// BELOW is a BUNCH of boilerplate code for getting, setting, and accessing properties
		// Yes, it is verbose, one of the challenges of Java. In most cases the code can be automatically generated.
		// While it would be MUCH simpler to just have a public property which is then .get() and .set(x),
		// It is best practice and convention in Java to encapsulate internal variables and to conform to
		// convention and backward compatibility by have the get and set calls.  Take a look at the alternative code which is much shorter,
		// but which would lead to less flexibility and compatibility in later development
	
		public UUID getId();
		public void setId(UUID id);
		
		// NAME -------------------------------------
		public StringProperty nameProperty();
		public String getName();
		public void setName(String name);
		
		// PRIORITY -------------------------------------
		public IntegerProperty priorityProperty();
		public int getPriority();
		public void setPriority(int priority);

		// TYPE -------------------------------------
		public ObjectProperty<ActType> typeProperty();
		public ActType getType();
		public void setType(ActType type);
		
		// STATE -------------------------------------
		public ObjectProperty<DemandState> stateProperty();
		public DemandState getState();
		public void setState(DemandState state);

		// EFFORT -------------------------------------
		public IntegerProperty effortProperty(); // remaining effort
		public int getEffort();
		public void setEffort(int nominalEffort_seconds);
		
		public void reset();
		public IntegerProperty effortInitialProperty();
		public int getEffortInitial();
		public void setEffortInitial(int nominalEffort_seconds);
		
		/*
		 * Returns the effort of this demand in hours (convenience method)
		 */
		public double getEffortHrs();
		public void setEffortHrs(double _effort);
		
		// START -------------------------------------
		public IntegerProperty startProperty();
		public int getStart();
		public void setStart(int start);
		
		// STOP -------------------------------------
		public IntegerProperty stopProperty();
		public int getStop();
		public void setStop(int stop);
		
		// RECUR -------------------------------------
		public BooleanProperty recurProperty();
		public boolean getRecur();
		public void setRecur(boolean isRecurring);
		public boolean isRecur();
		
		// EVERY -------------------------------------
		public IntegerProperty everyProperty();
		public int getEvery();
		public void setEvery(int every);
		
		// UNTIL -------------------------------------
		public IntegerProperty untilProperty();
		public int getUntil();
		public void setUntil(int until);
}
