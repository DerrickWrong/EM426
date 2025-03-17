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
public class DemandTableTest extends Application {
	protected Stage primaryStage;
	
	    public static void main(String[] args) {
	    	launch(args);
	    }
	    
	    @Override
	    public void start(Stage pStage) throws Exception {
	    	primaryStage = pStage;

	    	var demTable = new DemandTable();
	    	
	    	// so that we can add new demands in the UI, we need our actual demand class based on the API
	    	demTable.setDemandAPIClass(Demand.class); // put your implementation of DemandAPI here

			Demand d1 = new Demand(ActType.WORK, 8*3600, 9, 17, "daily job");
			Demand d2 = new Demand(ActType.COMM, 16*3600, 9, 17, "daily job");

			demTable.demandsProperty().addAll(d1, d2);

			var scene = new Scene(demTable);
	    	primaryStage.setScene(scene);    
	    	primaryStage.show();  
	    	
	    }   
}
