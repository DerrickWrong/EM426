package em426.test;

import em426.agents.*;
import em426.api.*;
import em426.fx.*;
import javafx.application.*;
import javafx.scene.*;
import javafx.stage.*;


public class AgentPaneTest extends Application {
	protected Stage primaryStage;
	
	    public static void main(String[] args) {
	    	launch(args);
	    }
	    
	    @Override
	    public void start(Stage pStage) throws Exception {
	    	primaryStage = pStage;
	    	
	    	AgentAPI testAgent = new Agent();

	    	var ap = new AgentPane(testAgent);

	    	var scene = new Scene(ap);
	    	primaryStage.setScene(scene);    
	    	primaryStage.show();

	    	
	    }   
}
