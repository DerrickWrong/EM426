package em426.test;

import em426.agents.*;
import em426.api.*;
import em426.fx.*;
import em426.world.*;
import javafx.application.*;
import javafx.scene.*;
import javafx.stage.*;


public class AgentLoopsSingleTest extends Application {
	protected Stage primaryStage;
	
	    public static void main(String[] args) {
	    	launch(args);
	    }
	    
	    @Override
	    public void start(Stage pStage) throws Exception {
			//Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());

			primaryStage = pStage;
	    	
	    	AgentAPI a1 = new Agent();
	    	a1.setName("agent 1");

	    	a1.getSupplies().add(new Ability(ActType.WORK, 4*3600));
			a1.getSupplies().add(new Ability(ActType.COMM, 4*3600));

			// create the environment in which the agent will exist: A site (kind of model object)
			World mySite = new World("Ecosystem");
			Place place1 = new Place("Room 1");
			mySite.setPlaceRoot(place1);

			place1.add(new Demand(ActType.WORK, 80*3600));
			a1.setPlace(place1);

	    	var agentLV = new AgentLoopView(a1);
			agentLV.setWorld(mySite);
			agentLV.initializeSimulator();

	    	var scene = new Scene(agentLV);
	    	primaryStage.setScene(scene);
	    	primaryStage.show();
	    	
	    }   
}
