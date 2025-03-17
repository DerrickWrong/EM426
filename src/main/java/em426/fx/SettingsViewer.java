package em426.fx;

import em426.api.*;
import em426.results.*;
import em426.sim.*;
import javafx.beans.value.*;
import javafx.fxml.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;

import java.io.*;
import java.time.*;
import java.time.format.*;
import java.util.*;

public class SettingsViewer extends BorderPane{
	
	private SimSettings mySettings;
	private GridPane settingsGrid;
	private Control[] values;

    @FXML
    private GridPane topGrid, bottomGrid;
    
    @FXML
    private Button submitButton, cancelButton;

    @FXML
    private AnchorPane centerPane;
    
    @FXML
    private void initialize() {
    	
    	// set up a grid for the settings and attach to the centerPane
    	settingsGrid = new GridPane();
    	centerPane.getChildren().add(settingsGrid);

    	centerPane.setBottomAnchor(settingsGrid, 0.0);
    	centerPane.setLeftAnchor(settingsGrid, 0.0);
    	centerPane.setTopAnchor(settingsGrid, 0.0);	
    	centerPane.setRightAnchor(settingsGrid, 0.0);

    	ColumnConstraints cc1 = new ColumnConstraints(40, 200, 200, Priority.NEVER, HPos.CENTER, true);
    	ColumnConstraints cc2 = new ColumnConstraints(40, 200, Double.MAX_VALUE, Priority.ALWAYS, HPos.LEFT, true);
    	settingsGrid.getColumnConstraints().addAll(cc1, cc2);

    	submitButton.setOnAction(event -> buttonSubmitClicked());
    }
    
    private void buttonSubmitClicked() {
    	Stage stage = (Stage) this.getScene().getWindow();
    	stage.close();
	}

	public SettingsViewer() {
  		// attach FXML to this control instance
  		FXMLLoader loader = new FXMLLoader(getClass().getResource("SettingsViewer.fxml"));
  		loader.setRoot(this);
  		loader.setController(this);
  		try {
  			loader.load();
  		} catch (IOException e1) {
  			e1.printStackTrace();
  		}

      }

    public void set(SimSettings set) {
    	mySettings = set;
    	values = new TextArea[mySettings.getSize()];
    	int rowHeight =  32;
    	
    	for (int n=0; n<mySettings.getSize(); n++) {
    		Label x = new Label(mySettings.getName(n));
    		x.setAlignment(Pos.TOP_RIGHT);
    		x.setWrapText(true);
    		x.setTooltip(new Tooltip(mySettings.getDescription(n)));
    		x.setPrefHeight(rowHeight-8);

    		x.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    		GridPane.setFillWidth(x, true);
    		GridPane.setMargin(x, new Insets(4, 4, 4, 2));
    		settingsGrid.add(x, 0, n);
    		
    		// column of values
    		TextArea y = new TextArea();
    		y.setPrefHeight(rowHeight-8);
    		
    		if (mySettings.getType(n) == Integer.class)  {
    			Integer val = (Integer)mySettings.getValue(n);
    			y.setText((val == null) ? "" : val.toString());
    			y.textProperty().addListener(new myChangeListener(mySettings, n));
    		}
    		if (mySettings.getType(n) == String.class)  {
    			y.setText((String) mySettings.getValue(n));
    			y.textProperty().addListener(new myChangeListener(mySettings, n));
    			rowHeight = 72;
    		}
    		
    		if (mySettings.getType(n) == Double.class)  {
    			Double val = (Double)mySettings.getValue(n);
    			y.setText((val == null) ? "" : val.toString());
    			y.textProperty().addListener(new myChangeListener(mySettings, n));
    		}
    		//TODO if type is derived from ENUM, then create a drop downbox
    		if (mySettings.getType(n) == TimeUnit.class)  {
    			y.setText( ((TimeUnit)mySettings.getValue(n)).toString());
    			y.textProperty().addListener(new myChangeListener(mySettings, n));
    		}
    		if (mySettings.getType(n) == Date.class)  {
    			DatePicker dp = new DatePicker(); //TODO .. dont use DATE for these, use INSTANT!
    			Date val = (Date)mySettings.getValue(n);
    			LocalDate ld;
    			if (val == null) 
    				ld =  LocalDate.ofInstant(Instant.now(), ZoneId.systemDefault());
    			else 
    				ld =  LocalDate.ofInstant(val.toInstant(), ZoneId.systemDefault());
    			dp.setValue(ld);
    			String name = mySettings.getName(n);
    			
    			LocalTime lt;
    			TextField time = new TextField();
    			if (val == null) 
    				lt =  LocalTime.ofInstant(Instant.now(), ZoneId.systemDefault());
    			else
    				lt =  LocalTime.ofInstant(val.toInstant(), ZoneId.systemDefault());
        
        		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
         		time.setText(formatter.format(lt));
         		
    			dp.valueProperty().addListener((obs, oldV, newV) -> {
    				Date newD = Date.from(newV.atStartOfDay(ZoneId.systemDefault()).toInstant());
    				LocalTime t = LocalTime.parse(time.getText(), formatter);
    				newD.setHours(t.getHour());
    				newD.setMinutes(t.getMinute());
    				mySettings.setValue(name, newD);
    			});
    			
    			time.textProperty().addListener((obs, oldV, newV) -> {
    				LocalTime t;
					try {
						t = LocalTime.parse(newV, formatter);
					} catch (Exception e) {
						return;
					}
    				Date newD = Date.from(dp.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
    				newD.setHours(t.getHour());
    				newD.setMinutes(t.getMinute());
    				mySettings.setValue(name, newD);
    			});
         		
         		GridPane.setMargin(dp, new Insets(2, 4, 2, 2));
         		GridPane.setMargin(time, new Insets(2, 4, 2, 2));
         		GridPane.setValignment(dp, VPos.TOP);
         		GridPane.setValignment(time, VPos.TOP);
        		settingsGrid.add(dp, 1, n);
        		settingsGrid.add(time, 2, n);
    		}
    		else if (mySettings.getType(n) == Boolean.class)  {
        		CheckBox yBool = new CheckBox();
        		yBool.setSelected((Boolean) mySettings.getValue(n));
    			yBool.selectedProperty().addListener(new myBooleanChangeListener(mySettings, n));
        		GridPane.setMargin(yBool, new Insets(2, 4, 4, 2));
         		GridPane.setValignment(yBool, VPos.TOP);
         		GridPane.setMargin(yBool, new Insets(2, 4, 2, 2));
        		settingsGrid.add(yBool, 1, n);
    		} 
    		else // this is a TextArea
    		{
        		y.setMaxSize(Double.MAX_VALUE, rowHeight);
        		y.setPrefSize(rowHeight-4, rowHeight);
         		GridPane.setValignment(y, VPos.TOP);
        		GridPane.setFillWidth(y, true);
        		GridPane.setMargin(y, new Insets(2, 4, 4, 2));
        		settingsGrid.add(y, 1, n);
        		values[n]=y;
    		}
    		
        	
        	RowConstraints rc = new RowConstraints(8, rowHeight, Double.MAX_VALUE, Priority.ALWAYS, VPos.CENTER, true);
        	settingsGrid.getRowConstraints().add(rc);

    	}
    	
    }
    

	class myBooleanChangeListener implements ChangeListener<Boolean> {
        private final int index;
        private final SimSettings mySettings;
        public myBooleanChangeListener(SimSettings set, int n) {
            this.index = n;
            this.mySettings = set;
        }
		@Override
		public void changed(ObservableValue<? extends Boolean> observable, Boolean oldVal, Boolean newVal) {
    		mySettings.setValue(index, newVal);
		}    
	}
	
    class myChangeListener implements ChangeListener<String> {
        private final int index;
        private final SimSettings mySettings;
        public myChangeListener(SimSettings set, int n) {
            this.index = n;
            this.mySettings = set;
        }

        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        	try {
				if (mySettings.getType(index).isAssignableFrom(Integer.class) ) {
				    try {
				        Integer.parseInt((String) newValue);
				        mySettings.setValue(index, Integer.valueOf((String) newValue));
						
				    } catch (final NumberFormatException e) {
				    	TextArea y = (TextArea) values[index];
				        y.setText(oldValue);
				    }
				}
				else if (mySettings.getType(index).isAssignableFrom(Double.class) ) {
				    try {
				        Double.parseDouble((String) newValue);
						mySettings.setValue(index, Double.valueOf((String) newValue));
						
				    } catch (final NumberFormatException e) {
				    	TextArea y = (TextArea) values[index];
				        y.setText(oldValue);
				    }

				}
				else if (mySettings.getType(index).isAssignableFrom(TimeUnit.class) ) {
				    	//TODO why use a bespoke TimeUnit;  why not the standard?
						mySettings.setValue(index, TimeUnit.valueOf(newValue));
				}
				
				else
					mySettings.setValue(index, newValue);
				
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
        }

    }
}
