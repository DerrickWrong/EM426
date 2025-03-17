package em426.api;

import em426.agents.*;
import javafx.beans.property.*;

import java.util.*;

public interface SupplyAPI {
	
	/*
	 *  Generated getters and setters, including properties 
	 */
	public UUID getId();
	public void setId(UUID id);
	
	public StringProperty nameProperty();
	public String getName();
	public void setName(String name) ;
	
	public IntegerProperty capacityProperty();
	public int getCapacity();
	public void setCapacity(int capacity);
	
	// TYPE -------------------------------------
	public ObjectProperty<ActType> typeProperty();
	public ActType getType();
	public void setType(ActType type);
	
	public DoubleProperty efficiencyProperty() ;
	public double getEfficiency();
	public void setEfficiency(double eff);
	
	public IntegerProperty startProperty();
	public int getStart() ;
	public void setStart(int start) ;
	
	public IntegerProperty stopProperty();
	public int getStop();
	public void setStop(int stop) ;
	
	public BooleanProperty recurProperty();
	public boolean isRecur();
	public void setRecur(boolean recur) ;
	
	public IntegerProperty everyProperty();
	public int getEvery() ;
	public void setEvery(int every);
	
	public IntegerProperty untilProperty();
	public int getUntil() ;
	public void setUntil(int until);
	
	public boolean isMatch(DemandAPI d);
	public ActState attempt(Act a);
	
	
}
