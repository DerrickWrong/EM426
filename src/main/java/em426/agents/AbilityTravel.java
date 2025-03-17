package em426.agents;

import em426.api.*;

/**
 * In Java, extends will create a class that starts with the methods and attributes of the parent class,
 * then allows addition and overwrite
 * If the SupplyAPI happens to be travel, we also want to node the travel mode
 */
public class AbilityTravel extends Ability {

    /**
     *  a specific kind of supply that is for travel
     *  In addition to a time and effort capacity, the supply is specific to a mode
     */

    private TravelMode mode;

    public AbilityTravel(TravelMode mode, int capacity) {
        super(ActType.TRAVEL,capacity);
        this.mode = mode;

    }

    public TravelMode getMode() {
        return mode;
    }

    public void setMode(TravelMode mode) {
        this.mode = mode;
    }
}
