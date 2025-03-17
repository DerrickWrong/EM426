package em426.api;

import em426.results.*;
import em426.sim.*;

import java.time.*;
import java.util.*;


/**
 * A holder of AnalyzerAPI results (as Metrics) and the information related to the engine,
 * its settings, and the models(s) analyzed.
 *
 * TODO need to generalize from domain site/ population to a multi domain ready framework
 *
 * The type iterates on the metrics list it contains
 * @author Bryan R. Moser
 *
 */
public interface ResultsAPI extends Iterable<Metric>{

	public UUID getResultID();

	public String setName(String resultName);
	public String getName();

	public void setShortID(String shortID); // a human readable short ID, not unique
	public String getShortID();

	public Instant getCreatedAt();

	// Signature -- defines the type, name, version #,  description, and ID of the AnalyzerAPI that generated this result.
	public void setSignature(SimSignature signature);
	public SimSignature getSignature();

	// Settings -- defines settings of the AnalyzerAPI when a specific result was generated
	public void setSettings(SimSettings engineSettings);
	public SimSettings getSettings();

	// Each metric can have just a summary, which stores aggregate performance and/or timeseries data
	public void addMetric(Metric metric_);
	public void removeMetric(Metric metric_);
	public int getMetricsCount();

	public Metric getMetric(int metricIndex);
	public Metric getMetric(SensorAPI modelObj, String metricName);

	public int getRunsCount();
	public long getTimeSeriesStart();
	public long getTimeSeriesEnd();

	public double getMaxOfMax(String metricType);
	public double getMaxOfMax(String metricType, Class modelObjectType);

	public String toString();

	// returns an immutable set of model objects referenced in this result with metrics
	public Set<SensorAPI> getModelObjects();
	public List<Metric> getMetrics(SensorAPI obj);
	public List<String> getMetricNames(SensorAPI obj);

}
