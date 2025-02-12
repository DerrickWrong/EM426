package com.MIT.api;

import javafx.beans.property.*;
import javafx.collections.*;

import java.util.*;

/**
 * Agent for EM.426 2024;
 * @author Bryan R. Moser
 *
 */
public interface AgentAPI {
	
	public UUID getId();

	public ObjectProperty<AgentMode> modeProperty() ;
	public AgentMode getMode() ;
	public void setMode(final AgentMode state) ;

	public ListProperty<DemandAPI> signalsProperty() ;
	public ObservableList<DemandAPI> getSignals() ;
	public void setSignals(final ObservableList<DemandAPI> signals) ;

}
