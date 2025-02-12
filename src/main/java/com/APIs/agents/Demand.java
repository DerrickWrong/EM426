package em426.agents;

import em426.api.*;
import javafx.beans.property.*;

import java.util.*;

public class Demand implements DemandAPI {

    UUID myID = UUID.randomUUID();
    String myName;

    @Override
    public UUID getId() {
        return myID;
    }

    @Override
    public void setId(UUID id) {
        myID = id;
    }

    @Override
    public StringProperty nameProperty() {
        return null;
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public void setName(String name) {
        myName = name;
    }

    @Override
    public IntegerProperty priorityProperty() {
        return null;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public void setPriority(int priority) {

    }

    @Override
    public ObjectProperty<ActType> typeProperty() {
        return null;
    }

    @Override
    public ActType getType() {
        return null;
    }

    @Override
    public void setType(ActType type) {

    }

    @Override
    public ObjectProperty<DemandState> stateProperty() {
        return null;
    }

    @Override
    public DemandState getState() {
        return null;
    }

    @Override
    public void setState(DemandState state) {

    }

    @Override
    public IntegerProperty effortProperty() {
        return null;
    }

    @Override
    public int getEffort() {
        return 0;
    }

    @Override
    public void setEffort(int nominalEffort_seconds) {

    }

    @Override
    public void reset() {

    }

    @Override
    public IntegerProperty effortInitialProperty() {
        return null;
    }

    @Override
    public int getEffortInitial() {
        return 0;
    }

    @Override
    public void setEffortInitial(int nominalEffort_seconds) {

    }

    @Override
    public double getEffortHrs() {
        return 0;
    }

    @Override
    public void setEffortHrs(double _effort) {

    }

    @Override
    public IntegerProperty startProperty() {
        return null;
    }

    @Override
    public int getStart() {
        return 0;
    }

    @Override
    public void setStart(int start) {

    }

    @Override
    public IntegerProperty stopProperty() {
        return null;
    }

    @Override
    public int getStop() {
        return 0;
    }

    @Override
    public void setStop(int stop) {

    }

    @Override
    public BooleanProperty recurProperty() {
        return null;
    }

    @Override
    public boolean getRecur() {
        return false;
    }

    @Override
    public void setRecur(boolean isRecurring) {

    }

    @Override
    public boolean isRecur() {
        return false;
    }

    @Override
    public IntegerProperty everyProperty() {
        return null;
    }

    @Override
    public int getEvery() {
        return 0;
    }

    @Override
    public void setEvery(int every) {

    }

    @Override
    public IntegerProperty untilProperty() {
        return null;
    }

    @Override
    public int getUntil() {
        return 0;
    }

    @Override
    public void setUntil(int until) {

    }
}
