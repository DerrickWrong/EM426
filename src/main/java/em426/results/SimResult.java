package em426.results;

import em426.api.*;

import em426.results.MetricSummary.*;
import em426.sim.*;

import java.io.*;
import java.time.*;
import java.time.format.*;
import java.util.*;


public class SimResult implements ResultsAPI, Serializable {

	private static final long serialVersionUID = 784266442597253743L;
	private static final int CompressVersion = 1;  // original version for export, specific to smart city decisions

	private UUID resultID;

	private String name;
	private String shortID;

	private SimSettings engineSettings;
	private SimSignature engineSignature;

	private int runsCount;
	private Instant createdAt;

	private ArrayList<Metric> metrics;

	public SimResult() {
		resultID = UUID.randomUUID();
		createdAt = Instant.now();
		runsCount = 1; // default value;
		metrics = new ArrayList<Metric>();
	}

	// New result with a single ModelObject input
	public SimResult(SimSignature signature, SimSettings mySettings, WorldAPI model, int numRuns) {
		this();
		engineSettings = mySettings;
		engineSignature = signature;
		runsCount = numRuns;

	}

	/***
	 * returns the maximum of the maximum values for the metric type across all
	 * object types TODO - assumes a metric Summary for Max has been collected
	 *
	 * @param metricType - the string name of the metric, e.g SPACE_OCCUPANCY
	 * @return
	 */
	public double getMaxOfMax(String metricType) {
		double val = Double.NEGATIVE_INFINITY;
		// step through all metrics, checking summary to compare max
		for (Metric m : metrics) {
			if (m.getName().contentEquals(metricType)) {
				MetricSummary s = m.getSummary(SummaryType.MAX);
				if (s != null)
					val = Math.max(val, s.getMax());
			}
		}

		return val;
	}

	/***
	 * returns the maximum of the maximum values for the metric on model object
	 * types TODO - assumes a metric Summary for Max has been collected
	 *
	 * @param metricType      - the string name of the metric, e.g SPACE_OCCUPANCY
	 * @param modelObjectType - the type of model object, e.g. Space
	 * @return
	 */
	public double getMaxOfMax(String metricType, Class modelObjectType) {
		double val = Double.NEGATIVE_INFINITY;
		// step through all metrics, checking summary to compare max
		for (Metric m : metrics) {
			if (modelObjectType.isAssignableFrom(m.getModelObjectType())) {
				if (m.getName().contentEquals(metricType)) {
					MetricSummary s = m.getSummary(SummaryType.MAX);
					if (s != null)
						val = Math.max(val, s.getMax());
				}
			}
		}

		return val;
	}

	// METHODS from the interface class

	public boolean hasNext() {
		// TODO what else must we do to implement iterator on metrics?
		return metrics.iterator().hasNext();
	}

	public Metric next() {
		return metrics.iterator().next();
	}

	public Set<SensorAPI> getModelObjects() {
		Set<SensorAPI> moSet = new HashSet<>();
		for (Metric x : metrics) {
			moSet.add(x.getModelObject());
		}
		return moSet;
	}

	@Override
	public List<Metric> getMetrics(SensorAPI obj) {
		var mList = new ArrayList<Metric>();
		for (Metric x : metrics) {
			if (x.getModelObjectID().equals(obj.getId()))
				mList.add(x);
		}
		return mList;
	}


	public List<Metric> getMetrics(UUID objID) {
		var mList = new ArrayList<Metric>();
		for (Metric x : metrics) {
			if (x.getModelObjectID() == objID)
				mList.add(x);
		}
		return mList;
	}

	/**
	 * returns a set of Metric Names for the object in this result
	 *
	 * @param obj
	 * @return
	 */
	public List<String> getMetricNames(SensorAPI obj) {
		var mnSet = new ArrayList<String>();
		for (Metric x : metrics) {
			if (x.getModelObject().getId() == obj.getId()
					&& !mnSet.contains(x.name))
				mnSet.add(x.getName());
		}
		return mnSet;
	}

	/**
	 * returns a set of Metric Names for the object type in this result
	 */
	public List<String> getMetricNames(Class<?> type) {
		var mnSet = new ArrayList<String>();
		for (Metric x : metrics) {
			if (x.getModelObject().getType() == type
					&& !mnSet.contains(x.name))
				mnSet.add(x.getName());
		}
		return mnSet;
	}

	/**
	 * returns a set of all Metric Names in this result
	 */
	public List<String> getMetricNames() {
		var mnSet = new ArrayList<String>();
		for (Metric x : metrics) {
			if (!mnSet.contains(x.name))
				mnSet.add(x.getName());
		}
		return mnSet;
	}

	public Iterator<Metric> iterator() {
		return metrics.iterator();
	}

	public SimSettings getSettings() {
		return engineSettings;
	}

	public void setSettings(SimSettings engineSettings) {
		this.engineSettings = engineSettings;
	}

	public SimSignature getSignature() {
		return engineSignature;
	}

	public void setSignature(SimSignature engineSignature) {
		this.engineSignature = engineSignature;
	}

	public UUID getResultID() {
		return resultID;
	}

	public void addMetric(Metric metric_) {
		metrics.add(metric_);
	}

	/**
	 * This method to be called after a sim result is generated, to finalize
	 */
	public void postProcess() {
		metrics.forEach(m-> m.postProcess());
	}

	public String toString() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYY-MM-dd hh:mm:ss").withZone(ZoneOffset.UTC);
		return formatter.format(createdAt);
	}

	/**
	 * Creates a new Metric, adds it to this result, and returns it
	 *
	 * @param modelObj
	 * @param metricName
	 * @return
	 */
	public Metric addMetric(SensorAPI modelObj, String metricName) {

		Metric newMetric = new Metric(modelObj, metricName, runsCount);
		this.addMetric(newMetric);
		return newMetric;
	}

	public void removeMetric(Metric metric_) {
		metrics.remove(metric_);
	}

	public int getMetricsCount() {
		return metrics.size();
	}

	public ArrayList<Metric> getMetrics() {
		return metrics;
	}
	public Metric getMetric(int metricIndex) {
		if (metricIndex >= 0 && metricIndex < metrics.size())
			return metrics.get(metricIndex);
		else
			return null;
	}

	public Metric getMetric(SensorAPI modelObj, String metricName) {
		UUID targetID = modelObj.getId();
		for (Metric x : metrics) {
			if (x.modelObj.getId() == targetID)
				if (x.getName().equalsIgnoreCase(metricName))
					return x;
		}
		return null;
	}

	public Metric getMetric(UUID modelObjID, String metricName) {
		for (Metric x : metrics) {
			if (x.getModelObjectID() == modelObjID)
				if (x.getName().equalsIgnoreCase(metricName))
					return x;
		}
		return null;
	}

	/**
	 * Adds a sample to the metric for the sensored object (SensorAPI) of name metricName
	 * @param run - the run occurance in a Monte Carlo loop
	 * @param time - the time to sample - should be of consistent units (e.g. hrs) of all other samples
	 * @param modelObj - the sensored object
	 * @param metricName - the name of the metric, typically defined in the domain class (e.g  Place, Agent)
	 */
	public void addSample(int run, long time, SensorAPI modelObj, String metricName) {
		var m = getMetric(modelObj, metricName);
		m.addSample(run, time, modelObj.sample( metricName ));
	}

	/**
	 * Returns a new list of metrics of class type which match the metric name, ignoring case
	 * An empty list  if no metrics are found which match.
	 * @param metricName
	 * @return an ArrayList of Metrics
	 */
	public ArrayList<Metric> getMetrics(Class<?> type,String metricName) {
		var ret = new ArrayList<Metric>();
		for (Metric x : metrics) {
			if (	x.getModelObjectType().isInstance(type)
					&& x.getName().equalsIgnoreCase(metricName)
			)
				ret.add(x);
		}
		return ret;
	}
	/**
	 * Returns a new list of metrics which match the metric name, ignoring case
	 * An empty list  if no metrics are found which match.
	 * @param metricName
	 * @return an ArrayList of Metrics
	 */
	public ArrayList<Metric> getMetrics(String metricName) {
		var ret = new ArrayList<Metric>();
		for (Metric x : metrics) {
			if (x.getName().equalsIgnoreCase(metricName))
				ret.add(x);
		}
		return ret;
	}

	/**
	 * Returns the ending (max) time series time assumes all tiem series in result
	 * are same length If no time series in result set, then returns 0
	 *
	 * @return
	 */
	public long getTimeSeriesEnd() {
		long n = 0;
		for (Metric x : metrics) {
			if (x.hasTimeSeries()) {
				return x.getSeries().getEnd();
			}
		}
		return n;
	}

	/**
	 * Returns the sgtarting (min) time series time assumes all tiem series in
	 * result are same length If no time series in result set, then returns 0
	 *
	 * @return
	 */
	public long getTimeSeriesStart() {
		long n = 0;
		for (Metric x : metrics) {
			if (x.hasTimeSeries()) {
				return x.getSeries().getStart();
			}
		}
		return n;
	}

	public int getRunsCount() {
		return runsCount;
	}

	public void setRunsCount(int numRuns) {
		this.runsCount = numRuns;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	@Override
	public String setName(String resultName) {
		return this.name = resultName;
	}

	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * Creates a new result with the same signature, settings, objects, and metric
	 * but clears the results data
	 *
	 * @return
	 */
	public SimResult copyClearResults() {
		SimResult retVal = new SimResult();

		retVal.engineSettings = engineSettings;
		retVal.engineSignature = engineSignature;
		retVal.runsCount = runsCount;

		retVal.metrics = new ArrayList<Metric>();
		for (Metric m : metrics) {
			Metric mNew = new Metric(m.modelObj, m.name, runsCount);
			if (m.hasTimeSeries())
				mNew.setTimeSeries(m.getSeries().getPeriod());

			for (MetricSummary s : m) {
				mNew.addSummary(s.getType());
			}

			retVal.addMetric(mNew);
		}
		return retVal;
	}

	/**
	 * A short ID, readable by humans, meant for interface use including as chart
	 * nodes. NOT a UUID.
	 */
	public void setShortID(String id) {
		this.shortID = id;
	}

	@Override
	public String getShortID() {
		return shortID;
	}

}
