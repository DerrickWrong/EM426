package em426.fx;

import em426.api.*;
import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.chart.*;
import javafx.scene.chart.XYChart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.io.*;
import java.util.*;

public class SupplyChart extends BorderPane
{
    @FXML
    private ButtonBar topButtonBar;
	@FXML
	private StackedAreaChart<Integer, Number> suppliesSAChart;
    @FXML
    private NumberAxis xAxis;
	
	private ObjectProperty<SupplyAPI> selectedSupply;
	private ListProperty<SupplyAPI> supplies;
	private MapProperty<UUID, String> colors;
	ListChangeListener<SupplyAPI> dListen;
	
	
	private static final int XAXIS_TICKUNIT = 24;
	private static final String[] SeriesColors = {
			"#e41a1c","#377eb8","#4daf4a","#984ea3","#ff7f00","#ffff33","#a65628","#f781bf","#999999"
			};

	/**
	 * A UI control that shows a graph based on a set of Supplys
	 * The control listens to the Supplys property for added, removed Supplys
	 * Each Supply is listened for changes to Supply attributes
	 * A colors map is generated and applied to each Supply that is added.
	 * The series shown for each Supply is selectable.
	 * @author Bryan R. Moser
	 * @since February 2021
	 */
	public SupplyChart() {
		// attach FXML to this control instance
		FXMLLoader loader = new FXMLLoader(getClass().getResource("SupplyChart.fxml"));
		loader.setRoot(this);
		loader.setController(this);
		try {
			loader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	    supplies = new SimpleListProperty<SupplyAPI>(FXCollections.observableArrayList());
	    colors = new SimpleMapProperty<UUID, String>(FXCollections.observableHashMap());
	    xAxis.setTickUnit(XAXIS_TICKUNIT);
	    
	    //supplyBarChart.setCreateSymbols(false);
	    
	    // trigger response of a Supply is added or removed

	    ListChangeListener<SupplyAPI> dListen= new ListChangeListener<SupplyAPI>(){
    		@Override
    		public void onChanged(Change<? extends SupplyAPI> c) {

    			while (c.next()) {
    				if (c.wasAdded()) {
    					List<? extends SupplyAPI> addedSubList = c.getAddedSubList();
    					for (SupplyAPI x : addedSubList) {
    						add(x);
    					}
    				}
    				if (c.wasRemoved()) {
    					List<? extends SupplyAPI> removedList = c.getRemoved();
    					for (SupplyAPI x : removedList) {
    						delete(x);
    					}
    				}
    			}
    		}
    		};
    		
    	    supplies.addListener(dListen);
    	    
    	    selectedSupply = new SimpleObjectProperty<>();
    	    selectedSupply.addListener((obs, oldvalue, newvalue) -> {
    	    		setSupplyHighlighted(oldvalue, false);
    	    		setSupplyHighlighted(newvalue, true);
    			});
    	    
	}
	
	public ListProperty<SupplyAPI> suppliesProperty() {
		return supplies;
	}
	public MapProperty<UUID, String> colorsProperty(){
		return colors;
	}
	public ObjectProperty<SupplyAPI> selectedSupplyProperty(){
		return selectedSupply;
	}

	// TODO tie to selectedSupply property
	private void setSupplyHighlighted(SupplyAPI d, boolean highlight) {
		// TODO iterate and change previous or all to less opaque fill
		for (Series<Integer, Number> x : suppliesSAChart.getData()) {
			Node xn = x.getNode();
			if (xn.getUserData().equals(d)) {
				Node fill = xn.lookup(".chart-series-area-fill");
				Node line = xn.lookup(".chart-series-area-line");
				
				String colTxt = colors.get(d.getId());
				String lineStyle = "-fx-stroke-width: "+ (highlight ? "5" : "1")
						+ "; -fx-stroke: " + (highlight ? colTxt+"ff" : colTxt+"99");
				fill.setStyle("-fx-fill: " + colTxt + (highlight ? "dd" : "99"));
				line.setStyle(lineStyle);
			}
		}
	}

	@FXML
	public void initialize()
	{        
	}
	
	private Series<Integer, Number> getSeriesForSupply(SupplyAPI d) {
		for( Series x : suppliesSAChart.getData()) {
			if (x.getNode().getUserData().equals(d))
				return x;
		}
		return null;
	}

	
	private void createSupplyChartSeries(SupplyAPI d) {
		// Create a series for each Supply

		var newSeries = new Series<Integer, Number>();
		newSeries.nameProperty().bind(d.nameProperty());
		
		addCapacityPoints(newSeries, d);
		
		// Check for recurrence
		if (d.isRecur()) {
			addRecurPoints(newSeries, d);
		}

		// add listeners to underlying data model

		ChangeListener updateListener = (obs, oldval, newval) -> {
			newSeries.getData().clear();// TODO change just the difference
			addCapacityPoints(newSeries, d);
			if (d.isRecur()) {
				removeRecurPoints(newSeries, d);	
				addRecurPoints(newSeries, d);
			}};
			
		d.startProperty().addListener(updateListener);
		d.stopProperty().addListener(updateListener);
		
		ChangeListener recurListener = (obs, oldval, newval) -> {
			if ((boolean) newval) 
				addRecurPoints(newSeries, d);
			else 
				removeRecurPoints(newSeries, d);	
		};
		
		d.recurProperty().addListener(recurListener);
		
		ChangeListener recurDetailsListener = (obs, oldval, newval) -> {
			if (d.isRecur()) {
				removeRecurPoints(newSeries, d);	
				addRecurPoints(newSeries, d);
			}};
		
		d.everyProperty().addListener(recurDetailsListener);
		d.untilProperty().addListener(recurDetailsListener);
		
		//TODO store listeners for removal
		
		newSeries.nameProperty().bind(d.nameProperty());
		
		// find the color with the least frequency and use it for this new Series.
		int minColor = 0;
		int minCount = Integer.MAX_VALUE;
		for (int m = 0; m< SeriesColors.length; m++){
			int count = Collections.frequency(colors.values(), SeriesColors[m]);
			if (count < minCount) {
				minColor = m;
				minCount = count;
			}	
		}
		
		colors.put(d.getId(), SeriesColors[minColor]);
		suppliesSAChart.getData().add(newSeries);
		
		newSeries.getNode().setUserData(d); // store Supply reference in node for lookup later
		
		Tooltip t = new Tooltip();
		t.textProperty().bind(d.nameProperty());
		Tooltip.install(newSeries.getNode(), t);
		
		Node fill = newSeries.getNode().lookup(".chart-series-area-fill");
		fill.setOnMouseClicked(event -> {
			selectedSupply.set(d);
		});

		setSupplyHighlighted(d, false); // establishes the colors
	}
	
	private void addCapacityPoints(Series<Integer, Number> series, SupplyAPI d) {
		Data<Integer, Number> xy;
		double y = d.getCapacity()/3600.0; // in hours
		for (int n = d.getStart(); n <= d.getStop(); n++) {
			xy = new Data<Integer, Number>(n, y);
			xy.YValueProperty().bind(Bindings.divide(d.capacityProperty(), 3600.0));
			series.getData().add(xy);
		}
		sortSupplySAChart();
		if (d.getStop() > xAxis.getUpperBound()) 
			xAxis.setUpperBound(d.getStop()+XAXIS_TICKUNIT-d.getStop()%XAXIS_TICKUNIT);
	}
	
	private void removeEffortPoints(Series<Integer, Number> series, SupplyAPI d) {
		//remove all point great than stop.
		series.getData().removeIf(item -> (item.getXValue() >= d.getStart() && item.getXValue() <= d.getStop()));
		sortSupplySAChart();
		xAxis.setUpperBound(getUpperBound());
	}
	
	private void addRecurPoints(Series<Integer, Number> series, SupplyAPI d) {
			Data<Integer, Number> xy;
			int every = d.getEvery();
			for (int n = d.getStop()+1; n <= d.getUntil(); n++) { // add to the right in recurring pattern
				int x = n % every;
				
				xy = new Data<Integer, Number>(n, 0);
				if ((x >= d.getStart() && x <= d.getStop())) // if within range, bind to effort in hours
					xy.YValueProperty().bind(Bindings.divide(d.capacityProperty(), 3600.0));

				series.getData().add(xy);
			}
			
			sortSupplySAChart();
			if (d.getUntil() > xAxis.getUpperBound()) 
				xAxis.setUpperBound(d.getUntil()+ XAXIS_TICKUNIT-d.getUntil()%XAXIS_TICKUNIT);
		}
	
	private void removeRecurPoints(Series<Integer, Number> series, SupplyAPI d) {
		//remove all points greater than stop.
		int stop = d.getStop();
		series.getData().removeIf(item -> (item.getXValue() > stop));
		sortSupplySAChart();
		xAxis.setUpperBound(getUpperBound());
	}
	
	private double getUpperBound() {
		double max = 0;
		for (SupplyAPI d : supplies) {
			max = Math.max(max, (d.isRecur() ? d.getUntil() : d.getStop()));
		}
		return max+ XAXIS_TICKUNIT-max%XAXIS_TICKUNIT;
	}

	private void add(SupplyAPI d) {
		// in case it has already been added, remove it first
		delete(d);
		
		// Create a new series for new Supply
		createSupplyChartSeries(d);
		sortSupplySAChart();
	}

	private void delete(SupplyAPI d) {
		// Only a single Supply needs to be updated
		for (Series<Integer, Number> x : suppliesSAChart.getData()) {
			if (x.getNode().getUserData().equals(d)) {	
				// we found the series corresponding to the Supply already existing
				suppliesSAChart.getData().remove(x);
				colors.remove(d.getId());
				return;
			}
		}
		// TODO remove listeners from data model
	}

	private void sortSupplySAChart() {
		suppliesSAChart.getData().sort((o1,o2)->{
			ObservableList<Data<Integer, Number>> l1 = o1.getData();
			ObservableList<Data<Integer, Number>> l2 = o2.getData();
			int start1 = l1.get(0).getXValue();
			int start2 = l2.get(0).getXValue();
			if(start1 > start2) 
				return 1; 
			if(start1 == start2) {
				int end1 = l1.get(l1.size()-1).getXValue();
				int end2 = l2.get(l2.size()-1).getXValue();	
				if (end1 < end2)
					return 1;
			}
			return 0;
		});
	}

	


}
