package em426.agents;

import em426.api.*;

public class Act {
	public AgentAPI 	agent;
	public SupplyAPI	supply;
	public DemandAPI	demand;
	public int			start;
;	public int			end;
	public int			effort;	// the actual effort of this act in secs
	public boolean		success;
	public ActState 	state;

	public AgentAPI getAgent() {
		return agent;
	}
	public void setAgent(AgentAPI agent) {
		this.agent = agent;
	}

	public SupplyAPI getSupply() {
		return supply;
	}
	public void setSupply(SupplyAPI supply) {
		this.supply = supply;
	}

	public DemandAPI getDemand() {
		return demand;
	}
	public void setDemand(DemandAPI demand) {
		this.demand = demand;
	}

	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}
	public void setEnd(int end) {
		this.end = end;
	}

	public int getEffort() {
		return effort;
	}
	public void setEffort(int effort) {
		this.effort = effort;
	}

	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}

	public ActState getState() {
		return state;
	}
	public void setState(ActState state) {
		this.state = state;
	}


	/**
	 * Convenience method to record the acting start attributes of an act
	 * @param timeStart
	 * @param a
	 * @param s
	 * @param d
	 */
	public void GetSet(int timeStart, AgentAPI a, SupplyAPI s, DemandAPI d ) {
		agent = a;
		supply = s;
		demand = d;
		start = timeStart;
		state = ActState.START;
	}
	
	public void attempt() {
		ActState after = supply.attempt(this);
		//TODO judge result of attempt and change state
		this.state = after;
	}
	
	/**
	 * Takes an act that is not yet committed
	 * Returns it to its start state, and sets agent time back to start of the act
	 * Also returns the selected demand and supply back
	 */
	public void rollback() {
		// TODO 
	}

	/**
	 * Convenience method to record the finishing step attributes of an act
	 * @param timeStop
	 * @param effort
	 * @param success
	 */
	public void WrapUp(int timeStop, int effort, boolean success) {
		this.end 	= timeStop;
		this.effort 	= effort;
		this.success 	=  success;
	}

	// TODO: Note that all acts must be reversible, and may depend on other acts that are not yet committed
}
