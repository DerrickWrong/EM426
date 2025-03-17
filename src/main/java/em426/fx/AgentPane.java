package em426.fx;

import em426.agents.*;
import em426.api.*;
import javafx.beans.property.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.scene.layout.*;
import javafx.util.*;
import javafx.util.converter.*;

import java.io.*;
import java.text.*;

/**
 * A JavaFX control for showing an agent
 *
 * @author Bryan R. Moser
 * @since 2024 March
 */
public class AgentPane extends BorderPane {

    @FXML private BorderPane wholeBP,internalBP, pendingBP, activeBP, completeBP, supplyBP, actsPaneBP, detailsHolderBP, detailsBP;
    @FXML private TextField agentTimeTF, nameTF, modeTF, selectedTF, siteTF;
    @FXML private SplitPane detailsSP;
    @FXML private GridPane behaviorGP;

    @FXML private TableView<Act> actsTV;
    @FXML private TableColumn<Act, Number> actStartCol, actEndCol, actEffortCol;
    @FXML private TableColumn<Act, String> actNameCol, actModeCol;
    @FXML private ToggleButton detailTBtn;

    @FXML private CheckBox randomObserveChx, randomSelectChx, randomActChx;
    @FXML private Spinner<Integer> pendingLmtSpin;
    @FXML private Spinner<Integer> activeLmtSpin;

	private ObjectProperty<AgentAPI> myAgent;
    private ObjectProperty<ColorFactory> colors;

    private DecimalFormat df;

    public DemandTable pendingDT, activeDT, completeDT;
    public SupplyTable supplyST;
    public ActsPane actsPn;

    public AgentPane(AgentAPI agent) {
        super();

        // attach FXML to this control instance
        FXMLLoader loader = new FXMLLoader(getClass().getResource("AgentPane.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

		// set up agent property
		myAgent = new SimpleObjectProperty<AgentAPI>(agent);
        // Set colors property
        colors = new SimpleObjectProperty<ColorFactory>(new ColorFactory());
        // added nested custom controls by hand
        pendingDT = new DemandTable();
        activeDT = new DemandTable();
        completeDT = new DemandTable();
        supplyST = new SupplyTable();

        pendingBP.setCenter(pendingDT);
        activeBP.setCenter(activeDT);
        completeBP.setCenter(completeDT);
        supplyBP.setCenter(supplyST);

        // hide the behavior pane for now
        setBehaviorPaneVisible(true);

        df = new DecimalFormat("#.00");

        // the converter below will make the underlying seconds appear as hours in the UI
        StringConverter<Number> cvHrs = new NumberStringConverter() {
            @Override
            public String toString(Number object) {
                if (object != null){
                    return df.format(object.doubleValue()/3600.0);
                }
                else return null;
            }

            @Override
            public Number fromString(String string) {
                return (int) (Double.parseDouble(string) * 3600);
            }
        };
        detailTBtn.selectedProperty().addListener((obs, oldV, newV) -> {
                detailsHolderBP.setCenter(newV ? detailsBP : null);
                detailsHolderBP.getScene().getWindow().sizeToScene();
        });
        detailTBtn.setSelected(false);
        detailsHolderBP.setCenter(null);

        //agentTimeTF.textProperty().bind(myAgent.flatMap(a-> a.agentTimeProperty().divide(3600.0).asString("%.2f")));
        //modeTF.textProperty().bind(myAgent.flatMap(a-> a.modeProperty().asString()));
        //selectedTF.textProperty().bind(myAgent.flatMap(a-> a.selectedDemandProperty().asString()));

        // Agent behavior checkboxes
        randomObserveChx.selectedProperty().bindBidirectional(myAgent.get().randomObserveProperty());
        randomSelectChx.selectedProperty().bindBidirectional(myAgent.get().randomSelectProperty());

        pendingLmtSpin.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1,100));
        activeLmtSpin.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1,100));

        // Agent behavior pending and active limits (how many at same time) // NOT WORKNG?
        myAgent.get().pendingLimitProperty().bind(pendingLmtSpin.getValueFactory().valueProperty());
        myAgent.get().activeLimitProperty().bind(activeLmtSpin.getValueFactory().valueProperty());

        // TODO is there a better way to use flatmap here for bidrectional? For now, every time the agent changes unbind old and bind new
        myAgent.addListener((obs, oldV, newV) -> {
            if (oldV != null) {
                randomObserveChx.selectedProperty().unbindBidirectional(oldV.randomObserveProperty());
                randomSelectChx.selectedProperty().unbindBidirectional(oldV.randomSelectProperty());
                randomActChx.selectedProperty().unbindBidirectional(oldV.randomActProperty());
                myAgent.get().pendingLimitProperty().unbind();
                myAgent.get().activeLimitProperty().unbind();
            }
            if (newV != null) {
                randomObserveChx.selectedProperty().bindBidirectional(newV.randomObserveProperty());
                randomSelectChx.selectedProperty().bindBidirectional(newV.randomSelectProperty());
                randomSelectChx.selectedProperty().bindBidirectional(newV.randomActProperty());
                myAgent.get().pendingLimitProperty().bind(pendingLmtSpin.getValueFactory().valueProperty());
                myAgent.get().activeLimitProperty().bind(activeLmtSpin.getValueFactory().valueProperty());
            }
        });

        // connect the agent to the tables
        // TODO bind to filtered list
        //pendingDT.demandsProperty().bind(myAgent.flatMap(AgentAPI::demandsProperty));
        //activeDT.demandsProperty().bind(myAgent.flatMap(AgentAPI::demandsProperty));
        //completeDT.demandsProperty().bind(myAgent.flatMap(AgentAPI::demandsProperty));

      // nameTF.textProperty().bind(myAgent.flatMap(AgentAPI::nameProperty).orElse("-"));
       //siteTF.textProperty().bind(myAgent.flatMap(AgentAPI::placeProperty).flatMap(EntityAPI::nameProperty).orElse("-"));

        //supplyST.suppliesProperty().bind(myAgent.flatMap(AgentAPI::suppliesProperty));
        // so that we can add new demands in the UI, we need our actual demand class based on the API
        supplyST.setSupplyAPIClass(Ability.class); // put your implementation of DemandAPI here

        // set separate titles for each of the demand tables
        pendingDT.setTitle("Pending");
        pendingDT.setAvailabiltyColumnsShowing(false);
        pendingDT.setButtonsShowing(false);

        activeDT.setTitle("Active");
        activeDT.setAvailabiltyColumnsShowing(false);
        activeDT.setButtonsShowing(false);

        supplyST.setAvailabiltyColumnsShowing(false);

        completeDT.setTitle("Complete/Committed");
        completeDT.setAvailabiltyColumnsShowing(false);
        completeDT.setButtonsShowing(false);

		// Acts table and pane TODO combine

		actsPn = new ActsPane();
		actsPaneBP.setCenter(actsPn);

		actsPn.setAgent(myAgent.get());

        //actsTV.itemsProperty().bind(myAgent.flatMap(AgentAPI::actsProperty));

        actNameCol.setCellValueFactory(a -> a.getValue().demand.nameProperty());
        actModeCol.setCellValueFactory(a ->  new SimpleObjectProperty<>(a.getValue().state.toString()));
        actStartCol.setCellValueFactory(a -> new SimpleObjectProperty<>(a.getValue().start));
        actEndCol.setCellValueFactory(a -> new SimpleObjectProperty<>(a.getValue().end));
        actEffortCol.setCellValueFactory(a -> new SimpleObjectProperty<>(a.getValue().effort));

        actStartCol.setCellFactory(TextFieldTableCell.forTableColumn(cvHrs));
        actEndCol.setCellFactory(TextFieldTableCell.forTableColumn(cvHrs));
        actEffortCol.setCellFactory(TextFieldTableCell.forTableColumn(cvHrs));

        // Set all colors to follow property
        pendingDT.colorsProperty().bind(colors);
        activeDT.colorsProperty().bind(colors);
        completeDT.colorsProperty().bind(colors);

    }
    public void setBehaviorPaneVisible(boolean show){
        this.behaviorGP.setVisible(show);
    }

    public ObjectProperty<ColorFactory> colorsProperty() {
        return colors;
    }

    public AgentAPI getMyAgent() {
        return myAgent.get();
    }
	public ObjectProperty<AgentAPI> agentProperty() {
		return myAgent;
	}
    public void setMyAgent(AgentAPI myAgent) {
        this.myAgent.set(myAgent);
    }
}
