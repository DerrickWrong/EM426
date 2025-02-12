
package com.MIT.agents;

import javafx.beans.property.*;
import javafx.collections.*;

import java.util.*;

import com.MIT.api.AgentAPI;
import com.MIT.api.AgentMode;
import com.MIT.api.DemandAPI;

public class Agent implements AgentAPI {

    private UUID myID = UUID.randomUUID();
    @Override
    public UUID getId() {
        return myID;
    }

    @Override
    public ObjectProperty<AgentMode> modeProperty() {
        return null;
    }

    @Override
    public AgentMode getMode() {
        return null;
    }

    @Override
    public void setMode(AgentMode state) {

    }

    @Override
    public ListProperty<DemandAPI> signalsProperty() {
        return null;
    }

    @Override
    public ObservableList<DemandAPI> getSignals() {
        return null;
    }

    @Override
    public void setSignals(ObservableList<DemandAPI> signals) {

    }
}
