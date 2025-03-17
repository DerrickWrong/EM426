package em426.test;

import em426.agents.*;
import em426.fx.*;
import javafx.application.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.*;


public class SupplyTableTest extends Application {
	protected Stage primaryStage;
	
	    public static void main(String[] args) {
	    	launch(args);
	    }
	    
	    @Override
	    public void start(Stage pStage) throws Exception {
	    	primaryStage = pStage;

	    	var supTable = new SupplyTable();
	    	var supChart = new SupplyChart();
	    	
	    	supChart.suppliesProperty().bind(supTable.suppliesProperty()); // Chart follows demands in table
	    	supTable.colorsProperty().bind(supChart.colorsProperty());  // Table follows colors from chart
	    	
	    	supChart.selectedSupplyProperty().bindBidirectional(supTable.selectedSupplyProperty());
	    	
	    	// so that we can add new demands in the UI, we need our actual demand class based on the API
	    	supTable.setSupplyAPIClass(Ability.class); // put your implementation of DemandAPI here
	    	
	    	var split = new SplitPane(supTable, supChart);
	    	split.setOrientation(Orientation.VERTICAL);
	    	
	    	var scene = new Scene(split); 
	    	primaryStage.setScene(scene);    
	    	primaryStage.show();  
	    	
	    }   
}
