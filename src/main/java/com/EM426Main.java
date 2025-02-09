package com;
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene; 
import javafx.stage.Stage;

public class EM426Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
    	
        FXMLLoader loader = new FXMLLoader(getClass().getResource("views/TestFXML.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("EM426Main");
        stage.setScene(scene);
        stage.show();
        
    }

    public static void main(String[] args) {
        launch();
    }

}