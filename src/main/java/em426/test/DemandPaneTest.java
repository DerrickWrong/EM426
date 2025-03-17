package em426.test;

import em426.agents.*;
import em426.api.*;
import em426.fx.*;
import javafx.application.*;
import javafx.scene.*;
import javafx.stage.*;


/**
 * Tests the creation of a table of demands based on the DemandAPI interface 
 * @author Bryan R. Moser
 *
 */
public class DemandPaneTest extends Application {
	protected Stage primaryStage;
	
	    public static void main(String[] args) {
	    	launch(args);
	    }
	    
	    @Override
	    public void start(Stage pStage) throws Exception {
	    	primaryStage = pStage;

	    	var demPane = new DemandPane();
	    	
	    	// create a test demand to set for the pane
	    	Demand d = new Demand(ActType.WORK, 8*3600, 9, 17, "daily job");
	    	
	    	// set the demand property of the pane
	    	demPane.setDemand(d);
	    	
	    	// add the pane to the scene, and scene to the stage
	    	var scene = new Scene(demPane); 
	    	primaryStage.setScene(scene);    
	    	primaryStage.show();  
	    	
	    }   
}
