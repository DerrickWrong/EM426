package em426.test;

import em426.agents.*;
import em426.api.*;
import em426.fx.*;
import javafx.application.*;
import javafx.scene.*;
import javafx.stage.*;


public class ActsPaneTest extends Application {
	protected Stage primaryStage;
	
	    public static void main(String[] args) {
	    	launch(args);
	    }
	    
	    @Override
	    public void start(Stage pStage) throws Exception {
	    	primaryStage = pStage;
	    	
	    	AgentAPI testAgent = new Agent();
			testAgent.setName("test");

			Act a1 = new Act();
			a1.agent= testAgent; a1.start = 0; a1.end = 24; a1.effort=12;

			Act a2 = new Act();
			a2.agent= testAgent; a2.start = 28; a2.end = 34; a2.effort=48;

			Act a3 = new Act();
			a3.agent= testAgent; a3.start = 36; a3.end = 48; a3.effort=4;

			testAgent.getActs().addAll(a1, a2, a3); // TODO consider addActs in API; today internal

	    	var actsP = new ActsPane();
			actsP.setAgent(testAgent);

	    	var scene = new Scene(actsP);
	    	primaryStage.setScene(scene);
	    	primaryStage.show();
	    }
}
