package em426.fx;

import em426.agents.*;
import em426.api.*;
import javafx.application.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;

import java.io.*;
import java.text.*;

/**
 * A JavaFX widget which shows the acts of an agent  graphically.
 *
 * @author Bryan R. Moser
 * @since 2024-03-25
 */
public class ActsPane extends BorderPane {
    // these declarations with @FXML imply the UI object is defined in the FXML file.
    // the FXML loader below will handle the construction and setting of these UX objects.

    @FXML
    private Canvas actsCanvas;
    @FXML
    private Label nameLbl;
    @FXML
    private AnchorPane actsAP;

    private ListProperty<Act> acts, emptyList;
    private ObjectProperty<AgentAPI> myAgent;
    private ObjectProperty<ColorFactory> colors;

    DecimalFormat df;

    // TODO consider associating with 1 or more agents as properties
	GraphicsContext gc;
    private double w, h;   // width and height of canvas


    /**
     * A  control that shows and allows editing of a single demands adhering to DemandAPI
     *
     * @author Bryan R. Moser
     * @since February 2024
     */
    public ActsPane() {
        // attach FXML to this control instance
        FXMLLoader loader;
        loader = new FXMLLoader(ActsPane.this.getClass().getResource("ActsPane.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        acts = new SimpleListProperty<Act>(FXCollections.observableArrayList());
        emptyList = new SimpleListProperty<Act>(FXCollections.observableArrayList());
        myAgent = new SimpleObjectProperty<AgentAPI>();

        colors = new SimpleObjectProperty<ColorFactory>(new ColorFactory());

        //acts.bind(myAgent.flatMap(AgentAPI::actsProperty).orElse(emptyList));

        //nameLbl.textProperty().bind(myAgent.flatMap(AgentAPI::nameProperty).orElse("--"));

        // the default string to number converter
        df = new DecimalFormat("#.00");

        Platform.runLater(this::initCanvas);  // dop this after this initialization is complete
    }

    public void setAgent(AgentAPI agent) {
        myAgent.setValue(agent);
    }

    public ObjectProperty<ColorFactory> colorsProperty() {
        return colors;
    }

    private void initCanvas() {

		 gc = actsCanvas.getGraphicsContext2D();

        // add listener to draw canvas TODO only draw partially for partial updates
        actsCanvas.widthProperty().bind(actsAP.widthProperty());
        actsCanvas.heightProperty().bind(actsAP.heightProperty());

		w = 300;
		h = 80;

        this.widthProperty().addListener((obs, oldV, newV) -> {
            draw();
        });

		this.heightProperty().addListener((obs, oldV, newV) -> {
			draw();
		});

        acts.addListener((ListChangeListener.Change<? extends Act> change) -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    draw();
                }
                if (change.wasRemoved()) {
                    draw();
                }
                if (change.wasUpdated()) {
                    int from = change.getFrom();
                    int to = change.getTo();
                    System.out.println("Act(s) from index " + from + " to " + to + " were changed");
                    draw();
                }
            }
        });

    draw();

    }


    public void draw() {
        if (gc == null) return;

        Platform.runLater(() -> {

            gc.clearRect(0, 0, w, h);
            if (acts.isEmpty()) return;  // no acts to draw

            w = actsCanvas.getWidth();
            h = actsCanvas.getHeight();


            // set up basic measures
            double padW = 12, padN = 32, padE = 12, padS = 6; // padding on each side
            // TODO get colors from the enum for act state
            // assume acts are in order, with last the latest
            double startMin = acts.getFirst().getStart();
            double stopMax = acts.getLast().getEnd();
            double dur = stopMax - startMin; // total duration
            double wRatio = (w - padW - padE) / dur; // drawable canvas to time ratio
            double barH = h - padN - padS;  // height of bar

            gc.setLineWidth(4);
            gc.setStroke(Color.WHITE);
            gc.setFont(new  javafx.scene.text.Font("Calibri", 9));
            double newX, newW;
            // iterate through the acts
            for (Act a : acts) {
                // find color for the act's demand
                    newX = padW + (a.start - startMin) * wRatio;
                    newW = (a.end - a.start - startMin) * wRatio;
                    gc.strokeRect(newX, padN, newW, barH);
                    gc.fillRect(newX, padN, newW, barH);
                    gc.fillText(df.format(a.start / 3600.0), newX, padN * .8);
                if (a.demand != null) {
                    gc.setFill(colors.get().getColor(a.demand.getId()));
                }
            }
        });
    }
}
