package com;
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene; 
import javafx.stage.Stage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext; 

// combining JavaFX with SpringBoot
// source - https://github.com/mvpjava

@SpringBootApplication
public class EM426Main extends Application {

	private ConfigurableApplicationContext springContext;
	private Parent rootNode;
	
	public static void main(String[] args) {
        launch();
    }
	
	@Override
	public void init() throws IOException {
		springContext = SpringApplication.run(EM426Main.class);
		FXMLLoader loader = new FXMLLoader(getClass().getResource("views/TestFXML.fxml"));
		loader.setControllerFactory(springContext::getBean);
		rootNode = loader.load();
	}
	 
    @Override
    public void start(Stage stage) {
        stage.setTitle("EM426Main");
        stage.setScene(new Scene(rootNode));
        stage.show(); 
    }
    
    @Override
    public void stop() {
    	springContext.close();
    } 
}