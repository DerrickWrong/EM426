package em426.fx;

import em426.agents.*;
import em426.api.*;
import em426.results.*;
import em426.sim.*;
import em426.world.*;
import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.util.*;

import java.io.*;
import java.text.*;

/**
 * A JavaFX control for showing an agent in response to external demand signals
 * @author Bryan R. Moser
 * @since 2024 March
 *
 */
public class AgentLoopView extends BorderPane{
	
	   	@FXML private BorderPane externalBP, topBP, actsPaneBP, signalsBP;
	   	
	   	@FXML private VBox internalVBox;
	    @FXML private TextField globalTimeTF; // global time in secs
	    @FXML private ProgressBar simProgress, runsProgress;
	    @FXML private Button continueBtn, resetBtn, settingsBtn, recentResultBtn;
		@FXML private ToggleButton startTBtn;

	    public DemandTable signalsDT;
		private SimResult recentResult = null;

		private WorldAPI myWorld;// a model object for this domain

		private ListProperty<AgentAPI> agentsList;
	    private Simulator sim;

	public AgentLoopView(AgentAPI... agents) {
		super();
		
		// attach FXML to this control instance
		FXMLLoader loader = new FXMLLoader(getClass().getResource("AgentLoopView.fxml"));
		loader.setRoot(this);
		loader.setController(this);
		try {
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}


		// add the actsPane
		signalsDT = new DemandTable();

    	// so that we can add new demands in the UI, we need our actual demand class based on the API
		signalsDT.setDemandAPIClass(Demand.class); // put your implementation of DemandAPI here

		// set separate titles for each of the demand tables
    	
    	signalsDT.setTitle("Signals");
    	signalsDT.setAvailabiltyColumnsShowing(false);

		signalsBP.setCenter(signalsDT);

		// TODO set a sim completion flag as property
		startTBtn.selectedProperty().addListener((obs, oldV, newV) -> {
			if (!newV) return; // the button was set to false
			// otherwise set to true, so starting
			// TODO collect result;
			 //recentResult = (SimResult)
			simulate();
		});

//		recentResultBtn.setOnAction(e -> {
//			if (recentResult == null) return;
//			var srv = new SimResultViewer();
//			Scene sc = new Scene(srv);
//
//			Stage stage = new Stage();
//			stage.setScene(sc);
//			stage.setTitle("Simulation Result Viewer");
//			stage.show();
//			srv.setResult(recentResult);
//		});

		
		settingsBtn.setOnAction(e -> {
			settingsSelected();
		});

		//TODO create poporty to indiciate simulator in process (rather than agent level)
		//continueBtn.disableProperty().bind(myAgent.modeProperty().isNotEqualTo(AgentMode.PAUSED));
		continueBtn.setOnAction(e -> {
			continueSim();
		});

		resetBtn.setOnAction(e -> {
			sim.reset();
			startTBtn.setSelected(false);
		});

		
		// establish list of agents...
		agentsList = new SimpleListProperty<AgentAPI>(FXCollections.observableArrayList(agents));
		
		// add a pane for each of the agents
		agentsList.forEach(a -> {
			AgentPane ap = new AgentPane(a);
			internalVBox.getChildren().add(ap);
			VBox.setVgrow(ap, Priority.ALWAYS);
			ap.colorsProperty().bind(signalsDT.colorsProperty());
		});


	}

	public void initializeSimulator(){
		// create and initialize simulator - domain specific
		// TODO since sim is domain specific, identity simulator through API and downcast to domain
		sim = new Simulator();
		sim.setWorld((World) this.myWorld);
		sim.setAgents(agentsList); // TODO change to property with binding

		// hide the start button while simulator is running
		sim.runningProperty().addListener((obs, oldV,newV) -> {
			startTBtn.visibleProperty().set(!newV);
		});
		// the continue button only visible while sim is running
		continueBtn.visibleProperty().bind(sim.runningProperty());


		// the converter below will make the underlying seconds appear as hours in the UI
		StringConverter<Number> cvHrs = new StringConverter<Number>() {
			@Override
			public String toString(Number object) {
				var df = new DecimalFormat("#.00");
				if (object != null)
					return df.format(object.doubleValue()/3600.0);
				else return null;
			}
			@Override
			public Double fromString(String string) {
				Double d = Double.parseDouble(string)*3600.0;
				return d;
			}
		};

		simProgress.progressProperty().bind(Bindings.createDoubleBinding(() -> {
			double limit = sim.timeLimitProperty().get();
			double current = sim.globalTimeProperty().get();
			return limit == 0 ? 0d : current / limit;
		}, 	sim.globalTimeProperty(), sim.timeLimitProperty()));

		globalTimeTF.textProperty().bindBidirectional(sim.globalTimeProperty(), cvHrs);
	}

	private void continueSim(){
		sim.continueRun();
	}


	public WorldAPI getWorld() {
		return myWorld;
	}


	public void setWorld(World aSite) {
		signalsDT.demandsProperty().unbind();
		myWorld = aSite;
		signalsDT.demandsProperty().bind(myWorld.placeRootProperty().get().demandsProperty());
		// For now we will place the demands in the root place.  Later we show by place.
	}

	private void settingsSelected() {

		SettingsViewer settings = new SettingsViewer();
		settings.set(sim.getSettings());
	 // TODO move detached window into Viewer
		Stage appStage = (Stage) this.getScene().getWindow();
		Stage detachedWindow = new Stage();
		detachedWindow.initModality(Modality.NONE);
		detachedWindow.initOwner(appStage);
		detachedWindow.setOnCloseRequest(e -> {

		});

		BorderPane sdRoot = new BorderPane();

		detachedWindow.setScene(new Scene(sdRoot, 480, 520));

		sdRoot.setCenter(settings);

		detachedWindow.setX(appStage.getX() + appStage.getWidth()/2);
		detachedWindow.setY(appStage.getY()+20);
		detachedWindow.show();
	}

	
	private ResultsAPI simulate() {
		runsProgress.progressProperty().unbind();
		runsProgress.progressProperty().bind(Bindings.createDoubleBinding(() -> {
			double limit = sim.getRuns();
			double current = sim.runProperty().get();
			return limit == 0 ? 0d : current / limit;
		}, 	 sim.runProperty()));


		boolean done = sim.run();
		return sim.getResults();
	}

}
