package em426.results;

import java.io.*;
import java.util.*;


/**
 * A Metric Summary collects a single value for each run of samples
 * FOr example, a Monte Carlo run of 30 would lead this summary to have 30 values, one for each run
 * @author Bryan R. Moser
 *
 */
public class MetricSummary implements Serializable  {
	

	private static final long serialVersionUID = 984378883225776208L;
	private static final int CompressVersion = 1;  // V2.0 2021-03-14
	
	private String metricName;  // this is redundant with Metric, but useful?
	private UUID objectID;
	private SummaryType type;
	public static enum SummaryType {MIN, MAX, SUM, AVG, COUNT, STARTVALUE, ENDVALUE};
	
	// TODO if the # of runs is known, could be an array for efficiencies
	// TODO: all values are doubles for now;  allow various sample types in future
	private double[] values = null;
	private double[] valuesSorted = null;
	private double[] count = null;
	
	public MetricSummary(UUID modelObjectID, String metricName, SummaryType type, int runsCount) {
		this.objectID = modelObjectID;
		this.type = type;
		this.metricName = metricName;
		
		if (runsCount >0) {
			values = new double[runsCount];	
			if (this.type == SummaryType.AVG)
				count = new double[runsCount];	// TODO only if AVG
		}
	}
	public String getMetricName() {
		return metricName;
	}
	public void setMetricName(String metricName) {
		this.metricName = metricName;
	}
	public UUID getModelObject() {
		return objectID;
	}
	public void setModelObject(UUID modelObject) {
		this.objectID = modelObject;
	}
	public SummaryType getType() {
		return this.type;
	}
	public void setType(SummaryType type) {
		this.type = type;
	}
	public double[] getValues() {
		return values;
	}
	
	public int getRunsCount() {
		return values.length;
	}
	
	/**
	 * 
	 * @param run - the number of the run, from 0 to 1 less than the count of runs
	 * @return a double
	 */
	public double getValue(int run) {	
		return values[run];
	}
	
	/**
	 * A result to this data set, one per run
	 * If set multiple times within a run, the type will be applied to combine with 
	 * previous settings of this same run.
	 * If the type obviously only needs to be set one time (e.g. ENDVALUE), then for efficiency sake it
	 * is is recommended to only set once at the end of each run.
	 * @param run - the number of the analysis run, from 0 to 1 less than the count of runs
	 * @param value
	 */
	public void setValue(int run, double value) {
		if (this.type == SummaryType.AVG) {
			count[run] += 1;
		}
		values[run] = 
				switch (this.type){
				case STARTVALUE, ENDVALUE -> value;
				case MIN -> Math.min(values[run],value);
				case MAX -> Math.max(values[run],value); 
				case SUM  -> values[run] + value;
				case COUNT -> values[run] + 1;	
				case AVG -> values[run] + (value - values[run])/count[run];
				};
		
	    
	}
	
	public double get(int index) {
		return values[index];
	}
	
	// Analytics of the summary result set
	// e.g. the average of runs, the minimum of all runs
	// therefore, if the metric is itself a min, this cold be the average of minimums, and so on
	public double getAvg() {
		double sum = 0;
		for(double x : values) {
			sum += x;
		}
		return sum/values.length;
	}
	
	public double getMin() {
		if (values.length == 0) return 0;
		double min = values[0];
		for(double x : values) {
			min = Math.min(x,  min);
		}
		return min;
	}
	
	public double getMax() {
		if (values.length == 0) return 0;
		double max = values[0];
		for(double x : values) {
			max = Math.max(x,  max);
		}
		
		return max;
	}
	// TODO change to better algorithm (see timeseries)
	public double getStdDev() {
		if (values.length == 0) return 0;
		double sum = 0;
		double sumSquare = 0;
		double variance;

		for(double x : values) {
			sum += x;
			sumSquare += x*x;
		}
		variance =  (sumSquare-sum*sum/values.length)/(values.length-1);
		
		return Math.sqrt(variance);
	}
	
	/**
	 * The nth percentile is the lowest score that is greater than a certain percentage (�n�) of the scores
	 * @param percentile from 0 to 1.0
	 * @return
	 */
	public double getMeanPct(double percentile) {
		int n = (int) (percentile*values.length);
		valuesSorted = values.clone();
		Arrays.parallelSort(valuesSorted);
		
		return valuesSorted[n];
	}
	
	public double getMeanQ1() {
		return getMeanPct(.25);
	}
	public double getMeanQ2() {
		return getMeanPct(.5);
	}
	public double getMeanQ3() {
		return getMeanPct(.75);
	}
	

}
	
	
