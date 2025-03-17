package em426.api;

/**
 * A set of possible states for the demand, describing the stage across a life of the demand until it is complete or ignored.
 * @author Bryan R. Moser
 *
 */
public enum DemandState {
	INACTIVE, PENDING, STARTING, ACTIVE, COMPLETE, IGNORED
}
