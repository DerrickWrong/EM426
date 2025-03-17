package em426.fx;

import em426.api.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.scene.layout.*;
import javafx.util.*;
import javafx.util.converter.*;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

public class SupplyTable extends BorderPane
{
	// these declarations with @FXML imply the UI object is defined in the FXML file. 
	// the FXML loader below will handle the construction and setting of these objects.
	@FXML
	private Button addSupplyButton, deleteSupplyButton;
	@FXML
	private TableView<SupplyAPI> SupplyTable;
	@FXML
	private TableColumn<SupplyAPI, ActType> typeCol;
	@FXML
	private TableColumn<SupplyAPI, String> colorCol, nameCol;
	@FXML
	private TableColumn<SupplyAPI, Number> capacityCol, efficiencyCol, startCol, stopCol, everyCol, untilCol;
	@FXML
	private TableColumn<SupplyAPI, Boolean> recurCol;
	@FXML
	private Label titleLbl;

	private ListProperty<SupplyAPI> supplies;
	private MapProperty<UUID, String> colors;
	private ObjectProperty<SupplyAPI> selectedSupply;
	
	private ObjectProperty<Class<?>> supplyClass;
	
	/**
	 * A table control that shows and allows editing of a set of Supplys
	 * The control listens to the Supplys property for added, removed Supplys
	 * User can interactively add and delete Supplys
	 * Each Supply is listened for changes to Supply attributes
	 * A colors map is followed and applied to each Supply that is added.
	 * The series shown for each Supply is selectable.
	 * @author Bryan R. Moser
	 * @since February 2022
	 */
	public SupplyTable() {
		// attach FXML to this control instance
		FXMLLoader loader = new FXMLLoader(getClass().getResource("SupplyTable.fxml"));
		loader.setRoot(this);
		loader.setController(this);
		try {
			loader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		colors = new SimpleMapProperty<UUID, String>(); // available to bind to a related chart or data

	    ObservableList<SupplyAPI> SupplysList = FXCollections.observableArrayList();
	    this.supplies = new SimpleListProperty<SupplyAPI>(SupplysList);
	    
	    supplyClass = new SimpleObjectProperty<Class<?>>(null);
	    
	    selectedSupply = new SimpleObjectProperty<>();
	    selectedSupply.addListener((obs, oldvalue, newvalue) -> {
	    	SupplyTable.getSelectionModel().select(newvalue);
		});
	    
	    SupplyTable.getSelectionModel().selectedItemProperty().addListener((obs, oldval, newval) -> {
	    	selectedSupply.set(newval);
	    });

	    SupplyTable.itemsProperty().bind(supplies);
		SupplyTable.setEditable(true);
		
		nameCol.setCellValueFactory(d -> d.getValue().nameProperty());
		typeCol.setCellValueFactory(d -> d.getValue().typeProperty());
		capacityCol.setCellValueFactory(d-> d.getValue().capacityProperty());
		efficiencyCol.setCellValueFactory(d-> d.getValue().efficiencyProperty());
		startCol.setCellValueFactory(d -> d.getValue().startProperty());
		stopCol.setCellValueFactory(d -> d.getValue().stopProperty());
		everyCol.setCellValueFactory(d -> d.getValue().everyProperty());
		untilCol.setCellValueFactory(d -> d.getValue().untilProperty());
		
		nameCol.setCellFactory(TextFieldTableCell.<SupplyAPI>forTableColumn());
		
		NumberStringConverter cv = new NumberStringConverter();
		
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
        
		capacityCol.setCellFactory(TextFieldTableCell.forTableColumn(cvHrs));
		efficiencyCol.setCellFactory(TextFieldTableCell.forTableColumn(cv));
		startCol.setCellFactory(TextFieldTableCell.forTableColumn(cv));
		stopCol.setCellFactory(TextFieldTableCell.forTableColumn(cv));
		everyCol.setCellFactory(TextFieldTableCell.forTableColumn(cv));
		untilCol.setCellFactory(TextFieldTableCell.forTableColumn(cv));
		
	    typeCol.setCellFactory(ComboBoxTableCell.<SupplyAPI, ActType>forTableColumn(ActType.values()));
	    
    	recurCol.setCellValueFactory(d -> d.getValue().recurProperty());
    	recurCol.setCellFactory(p -> new CheckBoxTableCell<>());
    	
		colorCol.setCellValueFactory(cellData -> {
            SupplyAPI d = cellData.getValue();
         return new ReadOnlyStringWrapper(colors.get(d.getId()));
        });
		
		colorCol.setCellFactory(column -> {
		    return new TableCell<SupplyAPI, String>() {
		        @Override
		        protected void updateItem(String item, boolean empty) {
		            super.updateItem(item, empty);

		            if (item == null || empty) {
		                setText(null);
		                setStyle("");
		            } else {
		            	setStyle("-fx-background-color: " + item);
		            }
		        }
		    };
		});
		
		/**
		 * Note that the add Supply button is tied an actual Supply class that has been passed in
		 * because interfaces cannot have constructors. If you are using a different SupplyAPI implementing class, change below
		 */
		addSupplyButton.setOnAction(evt -> {

			SupplyAPI s = null;
			try {
				if (supplyClass.getValue() != null) s = (SupplyAPI) supplyClass.getValue().getDeclaredConstructor().newInstance();
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
			if (s != null ) {
				supplies.add(s);
				s.setName("Supply " + supplies.getSize());			
			}
		});

		deleteSupplyButton.setOnAction(evt -> {
			SupplyAPI d = SupplyTable.getSelectionModel().getSelectedItem();
			supplies.remove(d);
		});
		
		deleteSupplyButton.disableProperty().bind(SupplyTable.getSelectionModel().selectedItemProperty().isNull());
		addSupplyButton.disableProperty().bind(supplyClass.isNull());
		
	}
	
	/**
	 * Sets the implemented SupplyAPI class for use in creating new supplies within FX control.
	 * If the mySupplyClass is not assignable from DemandAPI, does nothing
	 * Since interfaces don't have constructors (e.g. Demand()), but only classes that implement
	 * the interface do, if a UI control wants to create a new Demand within the user experience,
	 * it needs to know which implemented class to use.
	 * @param myDemandClass
	 */
	public void setSupplyAPIClass(Class<?> mySupplyClass) {
		if (SupplyAPI.class.isAssignableFrom(mySupplyClass)) {
			supplyClass.set(mySupplyClass);
		}
	}

	@FXML
	public void initialize()
	{        
	}
	
	/**
	 * changes the title at the top of the control (default is Supplies)
	 * @param txt
	 */
	public void setTitle(String txt) {
		titleLbl.setText(txt);
	}
	
	/**
	 * shows and hides the columns related to availability
	 * @param show
	 */
	public void setAvailabiltyColumnsShowing(boolean show)
	{
		startCol.setVisible(show); 
		stopCol.setVisible(show);  
		everyCol.setVisible(show);  
		untilCol.setVisible(show); 
		recurCol.setVisible(show); 
		}
/**
 * This list of Supplys is a property that drives the underlying data model of this table
 * It can be bound to a list
 * @return
 */
	public ListProperty<SupplyAPI> suppliesProperty() {
		return supplies;
	}
	
	
	public MapProperty<UUID, String> colorsProperty(){
		return colors;
	}
	
	public ObjectProperty<SupplyAPI> selectedSupplyProperty(){
		return selectedSupply;
	}
	

}
