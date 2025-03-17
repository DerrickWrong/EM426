package em426.agents;

/**
 * AN enum with set values for the travel mode name and speed, in average miles per hour
 */
public enum TravelMode {
    Walk ("Walk", 2),
    Bicycle ("Bike", 10),
    Taxi ("Taxi", 25),
    Car ("Car", 30),
    Bus ("Public Bus", 20),
    Plane ("Airplane", 300);

    private String name;
    private int speed;
    TravelMode(String modeName, int modeSpeed) {
        this.name = modeName;
        this.speed = modeSpeed;
    }

    public String getName() {
        return name;
    }

    public int getSpeed() {
        return speed;
    }
}
