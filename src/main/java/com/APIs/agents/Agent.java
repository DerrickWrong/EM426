
package em426.agents;

import em426.api.*;
import javafx.beans.property.*;
import javafx.collections.*;

import java.util.*;

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
