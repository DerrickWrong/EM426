package em426.test;

import em426.agents.*;
import em426.fx.*;
import javafx.application.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.*;

/**
 * Tests the creation of a table of demands and chart of demands, given a shared list
 * The 2 are placed on either side of a vertical split pane
 * The two FX ui controls are bound to one another so that changes in data and changes in color are shared
 * @author Bryan R. Moser
 *
 */
public class DemandsTableChartTest extends Application {
	protected Stage primaryStage;
	
	    public static void main(String[] args) {
	    	launch(args);
	    }
	    
	    @Override
	    public void start(Stage pStage) throws Exception {
	    	primaryStage = pStage;

	    	var demTable = new DemandTable();
	    	var demChart = new DemandsChart();
	    	
	    	// to add new demands in the UI, we need actual demand class which implements the API
	    	demTable.setDemandAPIClass(Demand.class);
	    	
	    	demChart.demandsProperty().bind(demTable.demandsProperty()); // Chart follows demands in table
			demChart.colorsProperty().bind(demTable.colorsProperty());  // Chart follows colors from table
	    	
			// selection in table shows in chart and vice versa
	    	demChart.selectedDemandProperty().bindBidirectional(demTable.selectedDemandProperty());

	    	var split = new SplitPane(demTable, demChart);
	    	split.setOrientation(Orientation.VERTICAL);
	    	
	    	var scene = new Scene(split); 
	    	primaryStage.setScene(scene);    
	    	primaryStage.show();  
	    	
	    }   
}
