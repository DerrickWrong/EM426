package com.models.demands;

import java.util.UUID;

import em426.api.ActType;
import em426.api.DemandAPI;
import em426.api.DemandState;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;

public class ShareInfo implements DemandAPI{
	
	private double currentPrice, currVolume;
	private String symbol;
	
	private double insiderShares, instituShares, shortedShares, floatingShares;
	
	public ShareInfo(String symbol, double price, double vol) {
		this.symbol = symbol;
		this.currentPrice = price;
		this.currVolume = vol;
	}

	public void setCurrPrice(double price) {
		this.currentPrice = price;
	}
	
	public double getInsiderShares() {
		return insiderShares;
	}

	public void setInsiderShares(double insiderShares) {
		this.insiderShares = insiderShares;
	}

	public double getInstituShares() {
		return instituShares;
	}

	public void setInstituShares(double instituShares) {
		this.instituShares = instituShares;
	}

	public double getShortedShares() {
		return shortedShares;
	}

	public void setShortedShares(double shortedShares) {
		this.shortedShares = shortedShares;
	}

	public double getFloatingShares() {
		return floatingShares;
	}

	public void setFloatingShares(double floatingShares) {
		this.floatingShares = floatingShares;
	}

	public double getCurrentPrice() {
		return currentPrice;
	}

	public double getCurrVolume() {
		return currVolume;
	}

	public String getSymbol() {
		return symbol;
	}

	@Override
	public UUID getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setId(UUID id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public StringProperty nameProperty() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return this.symbol;
	}

	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IntegerProperty priorityProperty() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getPriority() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setPriority(int priority) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ObjectProperty<ActType> typeProperty() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ActType getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setType(ActType type) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ObjectProperty<DemandState> stateProperty() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DemandState getState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setState(DemandState state) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IntegerProperty effortProperty() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getEffort() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setEffort(int nominalEffort_seconds) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IntegerProperty effortInitialProperty() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getEffortInitial() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setEffortInitial(int nominalEffort_seconds) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double getEffortHrs() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setEffortHrs(double _effort) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IntegerProperty startProperty() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getStart() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setStart(int start) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IntegerProperty stopProperty() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getStop() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setStop(int stop) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public BooleanProperty recurProperty() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean getRecur() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setRecur(boolean isRecurring) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isRecur() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IntegerProperty everyProperty() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getEvery() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setEvery(int every) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IntegerProperty untilProperty() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getUntil() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setUntil(int until) {
		// TODO Auto-generated method stub
		
	} 
	
	
	
}
