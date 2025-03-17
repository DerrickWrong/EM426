package em426.api;

import em426.results.*;
import javafx.beans.property.*;

import java.util.*;

public interface SensorAPI {
    public UUID getId(); // the related object (agent or entity) ID
    public List<String> getMetricNames();
    public boolean hasMetric(String metricName);
    public double sample(String metricName);
    public void resetMetrics();

    public String getName();
    public void setName(String name);
    public StringProperty nameProperty();

    public Class<?> getType() ; // allows call to a specific class constructor
}
