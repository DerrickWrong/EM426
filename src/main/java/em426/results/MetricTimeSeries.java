package em426.results;

import em426.api.*;

import javafx.beans.property.*;

import java.io.*;
import java.util.*;

// MetricTimeSeries -- a metric is a specific dimension of performance -- such as cost, occupancy, etc -- that is an output of the analysis engine
// Each of the metrics contains results data, which can be queried based on a period of sampling.
// Note -- the actual sampling may be more or less granular than the buckets (total duration/period)


/**
 * For a single metric and system object, a time series
 */
public class MetricTimeSeries implements Serializable{

	private static final long serialVersionUID = -3951828599399764644L;
	private static final int CompressVersion = 2;  // V1.0 2021-03-14, V2 21-05-06

	private String metricName;  // this is redundant with Metric, but useful? for flat persistence?
	private UUID 	objID;
	private String 	objName;
	private Class<?> 	objClass;
	private long	period;  // the periodicity (duration) of a sample bucket
	private int 	numRuns;  // the number of runs (monte carlo), or series sampled.
	private double 	numRunsSqrt;
	private long 	start=0;  //the start time.  default is 0
	private long 	end=1;  //the stop time.  default is 1

	private BooleanProperty emptyAsZero; // if true, a bucket time slot that is empty will count as ZERO
//	private enum bucketPos { begin(0.0), mid (0.5), end (1.0);
//		private double value;
//		private bucketPos(double value) {
//			this.value = value;
//		}
//	};
//
//	public  bucketPos position; // determines where sample stat within bucket is placed

	private long currentBucket;
	private int currentBucketCount;
	private double currentBucketSum;
	private int currentRunID ;

	// TODO consider refactor to array for speed
	private HashMap<Long, Integer> count; // the sample buckets as bucket start time and value
	private HashMap<Long, Double> mu;
	private HashMap<Long, Double> sq;
	private HashMap<Long, Double> min;
	private HashMap<Long, Double> max;

	/**
	 *
	 * @param metricName - the name of this metric
	 * @param ModelObj - the model object (e.g. site, person, space) this metric is sensing
	 * @param period
	 * @param numRuns
	 */
	public MetricTimeSeries(String metricName, SensorAPI ModelObj, long period, int numRuns) {
		this.metricName = metricName;
		this.objID = ModelObj.getId();
		this.period = period;
		this.numRuns = numRuns;
		this.numRunsSqrt = Math.sqrt(numRuns);

		count = new HashMap<Long, Integer>();
		mu = new HashMap<Long, Double>();
		sq = new HashMap<Long, Double>();
		min = new HashMap<Long, Double>();
		max = new HashMap<Long, Double>();

		emptyAsZero = new SimpleBooleanProperty(false);

		//position = bucketPos.mid; // determines where sample stat within bucket is placed

		currentBucket = (long) (start);
		currentBucketCount = 0;
		currentBucketSum = 0;
		currentRunID = -1;
	}

	//TODO to deprecate ;  shortcut for compatibility with V1a4
	public ArrayList<Double> getSeriesValuesAvg(){
		return new ArrayList<Double>(mu.values());
	}
	public String getMetricName() {
		return metricName;
	}

	public void setMetricName(String metricName) {
		this.metricName = metricName;
	}

	public long getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public long getEnd() {
		return end;
	}

	public long getPeriod() {
		return period;
	}

	public void setPeriod(int period) {
		this.period = period;
	}

	public UUID getObjectID() {
		return objID;
	}
	public String getObjName() {
		return objName;
	}
	public Class getObjClass() {
		return objClass;
	}

	public int getNumRuns() {
		return numRuns;
	}

    public int getRunCount() {
    	return 1;
    }

	/**
	 * if true, the entry for a run which does not add a sample to the bucket is counted as a zero
	 * for example, when a run stops shorter than others
	 * @return
	 */
	public boolean isEmptyAsZero() {
		return emptyAsZero.get();
	}

	public BooleanProperty emptyAsZeroProperty() {
		return emptyAsZero;
	}


    /**
     * assumes that time increases in order, and samples submitting during a run for same bucket in sequence
     * runs start at 0 or 1
     * and runs increase sequentially
     * @param runID
     * @param time
     * @param val
     */
    public void addSample(int runID, long time, double val) {

    	// find the bucket's start -- the value a multiple of period from start (indicate end of bucket)
    	long timeFromStart = time-start;
    	long bucket = (long) (start + timeFromStart - timeFromStart%period);// + period*position.value);

    	if (runID > currentRunID) { // a new run, so reset current values
    		// reset for next bucket
        	currentBucketCount = 1;
        	currentBucketSum = val;
        	currentBucket = bucket;
        	currentRunID = runID;
        	return;
    	}

    	// TODO consider convolution of value based on position in bucket
    	if (bucket == currentBucket) { // adding into current bucket on this run
        	currentBucketCount += 1;
        	currentBucketSum += val;
    	}
    	else {//a new bucket on this run; consolidate prior bucket value into timeline
    		if(mu.containsKey(currentBucket)) {  // a prior run has already created this bucket
    			addBucketSummary(currentBucket, currentBucketSum / currentBucketCount) ; 	// average of multiple in bucket		
    			count.put(currentBucket, count.get(currentBucket) + 1); // this increments AFTER updates

    		}
    		else {  // a new bucket, not yet created in a prior run
    			count.put(currentBucket, 1); 
    			mu.put(currentBucket,  val);
    			sq.put(currentBucket, 0.0);     
    			min.put(currentBucket,  val);
    			max.put(currentBucket,  val);
    		}

    		if (end < time)  // update to capture the largest end across runs
    			end = time;

    		// reset current for next bucket
    		currentBucketCount = 1;
    		currentBucketSum = val;
    		currentBucket = bucket;
    	}
    }

	/**
	 * if true, the entry for a run which does not add a sample to the bucket is counted as a zero
	 * for example, when a run stops shorter than others
	 * @param _emptyAsZero
	 */
	public void setEmptyAsZero(boolean _emptyAsZero) {
		this.emptyAsZero.set(_emptyAsZero);
	}

	// TODO consider if min and max should be for ALL samples, or just average per run?
    /**
     * Uses Weldford's Algorithm to add a single (summarized) value into a bucket
     * Should only be called once per run
     * updates the values incrementally (mu, min, max, sq)
     * @param bucket
     * @param val
     */
    private void addBucketSummary(long bucket, double val) {
    	double oldCount = count.get(bucket);
    	double oldMu = mu.get(bucket);
    	double newMu = oldMu + (val - oldMu)/oldCount;
    	double newSq = sq.get(bucket) + (val - oldMu)*(val - newMu);
    	mu.put(bucket,  newMu);
    	sq.put(bucket, newSq);   		
    	if (min.get(bucket) > val)
    		min.put(bucket,  val);
    	if (max.get(bucket) < val)
    		max.put(bucket,  val);
    }
    
    /**
     * This method should be run at the conclusion all runs (one last time);
     * ensures the final value of the final run is included in the time series
     * NOT YET IMPLEMENTED
     */
    // TODO add also to summary level results set, find mins and maxes for axes across results set
    public void processFinalSample() {
		if(mu.containsKey(currentBucket)) {  // a prior run has already created this bucket
			addBucketSummary(currentBucket, currentBucketSum / currentBucketCount) ; 	// average of multiple in bucket		
			count.put(currentBucket, count.get(currentBucket) + 1); // this increments AFTER updates

		}
		else {  // a new bucket, not yet created in a prior run
			count.put(currentBucket, 1); 
			mu.put(currentBucket,  currentBucketSum);
			sq.put(currentBucket, 0.0); 
			min.put(currentBucket,  currentBucketSum);
			max.put(currentBucket,  currentBucketSum);
		}
    }

	/**
	 * returns the average of samples at this time bucket
	 * if there is no sample, returns null or if emptyAsZero then 0
	 * @param time
	 * @return
	 */
	public Double getAvg(long time) {
    	Double v =  mu.get(time);
    	if (v == null) {
			return emptyAsZero.get() ? 0.0 : null;
		}
		return v * count.get(time)/ (double)numRuns;
    }

	/**
	 * returns variance for the time bucket
	 * if no samples, returns null unless emptyAsZero which returns 0
	 * @param time - signifying the time of a sample bucket
	 * @return  variance for the time bucket if it exists
	 */
	public Double getVar(long time) {
    	Double v =  sq.get(time);
		if (v == null) {
			return emptyAsZero.get() ? 0.0 : null;
		}
    	double ss =  v;

    	Integer n = count.get(time);
    	if (n == null) return 0.0;  // Unlikely, but just in case
    	if (emptyAsZero.get()) {
        	double mOrig =  mu.get(time);
        	double mn=mOrig;
    		for (int x = n+1; x<=numRuns; x++) { // go through each to calculate missing zero samples
    			ss += mn* mOrig*n/x;
    			mn = mOrig*n/x;
    		}
    		n = numRuns;
    	}
    	 return ss/n;
    }
    
    public Double getStdDev(long time) {
		Double v = getVar(time);
		if (v == null) {
			return emptyAsZero.get() ? 0.0 : null;
		}
		return Math.sqrt(v);
    }
    
    // TODO divide by the sqrt of number of data points
    public double getStdErr(long time) {
    	return Math.sqrt(getStdDev(time))/numRunsSqrt;
    }
    
    public Double getMin(long time) {
    	Double v =  min.get(time);
		if (v == null) {
			return emptyAsZero.get() ? 0.0 : null;
		}
    	//previous... why?
		// if (emptyAsZero.get() && (count.get(time) < (double)numRuns))
    		//v = Math.min(0, v);
    	return v;
    }
    public Double getMax(long time) {
    	Double v =  max.get(time);
		if (v == null) {
			return emptyAsZero.get() ? 0.0 : null;
		}
    	return v;
    }
    

}


