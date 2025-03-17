package em426.api;

import javafx.beans.property.*;
import javafx.collections.*;

import java.util.*;

/**
 * The WorldAPI interface provides methods for a total ecosystem in context.
 * Within this world boundaries are the interactions of agents and model entities.
 */
public interface WorldAPI {

    public StringProperty nameProperty();

    public ListProperty<AgentAPI> agentsProperty();

    public ListProperty<EntityAPI> placesProperty();
    public ObjectProperty<EntityAPI> placeRootProperty();

}
