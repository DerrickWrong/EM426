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

/**
 * A JavaFX widget which shows a list of demands in a table
 * The demands should adhere to the DemandAPI interface
 * @author Bryan R. Moser
 * @since 2024-02-22
 *
 */
public class DemandTable extends BorderPane
{
	// these declarations with @FXML imply the UI object is defined in the FXML file. 
	// the FXML loader below will handle the construction and setting of these objects.

	@FXML private Button addDemandButton, deleteDemandButton;

	@FXML private TableView<DemandAPI> demandTable;
	@FXML private TableColumn<DemandAPI, ActType> typeCol;
	@FXML private TableColumn<DemandAPI, DemandState> stateCol;
	@FXML private TableColumn<DemandAPI, String> colorCol, nameCol;
	@FXML private TableColumn<DemandAPI, Number> effortInitCol,  startCol, stopCol, everyCol, untilCol, effortRemainCol;
	@FXML private TableColumn<DemandAPI, Boolean> recurCol;

	@FXML private Label titleLbl;

	private ListProperty<DemandAPI> demands;
	private ObjectProperty<ColorFactory> colors;
	private ObjectProperty<DemandAPI> selectedDemand;
	private ObjectProperty<Class<?>> demandClass;
	
	/**
	 * A table control that shows and allows editing of a set of demands
	 * The control listens to the demands property for added, removed demands
	 * User can interactively add and delete demands
	 * Each demand is listened for changes to demand attributes
	 * A colors map is followed and applied to each demand that is added.
	 * The series shown for each demand is selectable.
	 * @author Bryan R. Moser
	 * @since February 2024
	 */
	public DemandTable() {
		// attach FXML to this control instance
		FXMLLoader loader = new FXMLLoader(getClass().getResource("DemandTable.fxml"));
		loader.setRoot(this);
		loader.setController(this);
		try {
			loader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		colors = new SimpleObjectProperty<ColorFactory>(new ColorFactory());

	    ObservableList<DemandAPI> demandsList = FXCollections.observableArrayList();
	    this.demands = new SimpleListProperty<DemandAPI>(demandsList);
	    
	    selectedDemand = new SimpleObjectProperty<>();
	    selectedDemand.addListener((obs, oldvalue, newvalue) -> {
	    	demandTable.getSelectionModel().select(newvalue);
		});
	    
	    demandClass = new SimpleObjectProperty<Class<?>>(null);
	    
	    demandTable.getSelectionModel().selectedItemProperty().addListener((obs, oldval, newval) -> {
	    	selectedDemand.set(newval);
	    });

	    demandTable.itemsProperty().bind(demands);
		demandTable.setEditable(true);
		
		nameCol.setCellValueFactory(d -> d.getValue().nameProperty());
		typeCol.setCellValueFactory(d -> d.getValue().typeProperty());
		stateCol.setCellValueFactory(d -> d.getValue().stateProperty());
		effortRemainCol.setCellValueFactory(d-> d.getValue().effortProperty());
		effortInitCol.setCellValueFactory(d-> d.getValue().effortInitialProperty());
		startCol.setCellValueFactory(d -> d.getValue().startProperty());
		stopCol.setCellValueFactory(d -> d.getValue().stopProperty());
		everyCol.setCellValueFactory(d -> d.getValue().everyProperty());
		untilCol.setCellValueFactory(d -> d.getValue().untilProperty());
		
		nameCol.setCellFactory(TextFieldTableCell.<DemandAPI>forTableColumn());
		
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
		
		startCol.setCellFactory(TextFieldTableCell.forTableColumn(cv));
		stopCol.setCellFactory(TextFieldTableCell.forTableColumn(cv));
		everyCol.setCellFactory(TextFieldTableCell.forTableColumn(cv));
		untilCol.setCellFactory(TextFieldTableCell.forTableColumn(cv));
		effortInitCol.setCellFactory(TextFieldTableCell.forTableColumn(cvHrs));
		effortRemainCol.setCellFactory(TextFieldTableCell.forTableColumn(cvHrs));
		
	    typeCol.setCellFactory(ComboBoxTableCell.<DemandAPI, ActType>forTableColumn(ActType.values()));
	    
    	recurCol.setCellValueFactory(d -> d.getValue().recurProperty());
    	recurCol.setCellFactory(p -> new CheckBoxTableCell<>());
    	
		colorCol.setCellValueFactory(cellData -> {
            DemandAPI d = cellData.getValue();
         return new ReadOnlyStringWrapper(colors.get().get(d.getId()));
        });
		
		colorCol.setCellFactory(column -> {
		    return new TableCell<DemandAPI, String>() {
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
		 * Note that the add Demand button is tied an actual Demand class that has been passed in
		 * because interfaces cannot have constructors. If you are using a different DemandAPI implementing class, change below
		 */
		addDemandButton.setOnAction(evt -> {
			DemandAPI d = null;
			try {
				if (demandClass.getValue() != null) d = (DemandAPI) demandClass.getValue().getDeclaredConstructor().newInstance();
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
			if (d != null ) {
				demands.add(d);
				d.setName("Demand " + demands.size());				
			}
		});

		deleteDemandButton.setOnAction(evt -> {
			DemandAPI d = demandTable.getSelectionModel().getSelectedItem();
			demands.remove(d);
		});
		
		deleteDemandButton.disableProperty().bind(demandTable.getSelectionModel().selectedItemProperty().isNull());
		addDemandButton.disableProperty().bind(demandClass.isNull());
	}

	/*
	 * Sets the title of the table to txt
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
	 * shows and hides the add and delete buttons
	 * @param show
	 */
	public void setButtonsShowing(boolean show) {
		addDemandButton.setVisible(show);
		deleteDemandButton.setVisible(show);
	}
	
	/**
	 * Sets the implemented DemandAPI class for use in creating new demands within FX control.
	 * If the myDemandClass is not assignable from DemandAPI, does nothing
	 * Since interfaces don't have constructors (e.g. Demand()), but only classes that implement
	 * the interface do, if a UI control wants to create a new Demand within the user experience,
	 * it needs to know which implemented class to use.
	 * @param myDemandClass
	 */
	public void setDemandAPIClass(Class<?> myDemandClass) {
		if (DemandAPI.class.isAssignableFrom(myDemandClass)) {
			demandClass.set(myDemandClass);
		}
	}
	
	/**
	 * @return This list of demands as a property that drives the underlying data model of this table
	 */
	public ListProperty<DemandAPI> demandsProperty() {
		return demands;
	}

	/**
	 * @return A map of colors to specific unique IDs. 
	 * If the UUID exists in the color map, the table shows the color in the row for that element.
	 * This table does not automatically generate any mapped colors
	 * 
	 */
	public ObjectProperty<ColorFactory> colorsProperty() {
		return colors;
	}

	/**
	 * 
	 * @return a property - the selected demand (a row) in the table
	 */
	public ObjectProperty<DemandAPI> selectedDemandProperty(){
		return selectedDemand;
	}
	

}
