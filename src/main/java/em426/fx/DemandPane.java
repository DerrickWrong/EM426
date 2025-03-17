package em426.fx;

import em426.api.ActType;
import em426.api.DemandAPI;
import em426.api.DemandState;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;

import java.io.IOException;

/**
 * A JavaFX widget which shows the attributes of a single demand
 * The demand should adhere to the DemandAPI interface
 * @author Bryan R. Moser
 * @since 2025-02-11
 *
 */
public class DemandPane extends BorderPane
{
	// these declarations with @FXML imply the UI object is defined in the FXML file. 
	// the FXML loader below will handle the construction and setting of these UX objects.

    @FXML private TextField nameTF, everyTF, untilTF, effortTF, startTF, stopTF;
    @FXML Label titleLbl;
    @FXML private ToggleButton recursToggle;
    @FXML private ComboBox<DemandState> stateCombo;
    @FXML private ComboBox<ActType> typeCombo;

	private ObjectProperty<DemandAPI> myDemand;

	
	/**
	 * A  control that shows and allows editing of a single demands adhering to DemandAPI
	 * @author Bryan R. Moser
	 * @since February 2025
	 */
	public DemandPane() {
		// attach FXML to this control instance
		FXMLLoader loader;
        loader = new FXMLLoader(getClass().getResource("DemandPane.fxml"));
        loader.setRoot(this);
		loader.setController(this);
		try {
			loader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	    myDemand = new SimpleObjectProperty<>();
	    
	    untilTF.disableProperty().bind(recursToggle.selectedProperty().not());
	    everyTF.disableProperty().bind(recursToggle.selectedProperty().not());	 
	    
	    stateCombo.getItems().setAll(DemandState.values());
	    typeCombo.getItems().setAll(ActType.values());
	    
	    // the default string to number converter
	    NumberStringConverter cv = new NumberStringConverter();
	    
	    // converts hours in the string field to a number in seconds
	    StringConverter<Number> cvHrs = new StringConverter<Number>() {
	    	@Override
	    	public String toString(Number object) {
	    		if (object != null)
	    			return Double.toString(object.doubleValue()/3600.0);
	    		else return null;
	    	}
	    	@Override
	    	public Double fromString(String string) {
	    		Double d = Double.parseDouble(string)*3600.0;
	    		
	    		return d;
	    	}
	    };
	    
	    titleLbl.textProperty().bind(nameTF.textProperty());
	    
	// when the value of myDemand changes, unbind UI controls from the old demand and bind to new ones.
	    myDemand.addListener((obs, oldV, newV) -> {
    		if (oldV != null) {
	    		nameTF.textProperty().unbindBidirectional(oldV.nameProperty());
	    		startTF.textProperty().unbindBidirectional(oldV.everyProperty());
	    		stopTF.textProperty().unbindBidirectional(oldV.untilProperty());
	    		effortTF.textProperty().unbindBidirectional(oldV.effortProperty());
	    		everyTF.textProperty().unbindBidirectional(oldV.everyProperty());
	    		untilTF.textProperty().unbindBidirectional(oldV.untilProperty());
	    		recursToggle.selectedProperty().unbindBidirectional(oldV.recurProperty());
	    	    stateCombo.valueProperty().unbindBidirectional(oldV.stateProperty());
	    	    typeCombo.valueProperty().unbindBidirectional(oldV.typeProperty());
    		}    		
    		if (newV != null) { 	
    			nameTF.textProperty().bindBidirectional(newV.nameProperty());
    			startTF.textProperty().bindBidirectional(newV.everyProperty(), cv);
    			stopTF.textProperty().bindBidirectional(newV.untilProperty(), cv);
    			effortTF.textProperty().bindBidirectional(newV.effortProperty(), cvHrs);
    			recursToggle.selectedProperty().bindBidirectional(newV.recurProperty());
    			everyTF.textProperty().bindBidirectional(newV.everyProperty(), cv);
    			untilTF.textProperty().bindBidirectional(newV.untilProperty(), cv);
	    	    stateCombo.valueProperty().bindBidirectional(newV.stateProperty());
	    	    typeCombo.valueProperty().bindBidirectional(newV.typeProperty());
    		}

		});
	    
	    }
	    

	public ObjectProperty<DemandAPI> demandProperty(){
		return myDemand;
	}
	public DemandAPI getDemand() {
		return myDemand.get();
	}
	public void setDemand(DemandAPI demand) {
		this.myDemand.set(demand);
	}
	
}
