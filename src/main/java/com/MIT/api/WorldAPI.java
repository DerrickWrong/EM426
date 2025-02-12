package com.MIT.api;

import java.util.*;

/**
 * The WorldAPI interface provides methods for a total ecosystem in context.
 * Within this world boundaries are the interactions of agents and model entities.
 */
public interface WorldAPI {

    public List<EntityAPI> getEntities();
    public List<AgentAPI> getAgents();
}
