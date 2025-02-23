package com;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

// combining JavaFX with SpringBoot
// source - https://github.com/mvpjava

@SpringBootApplication
public class EM426Main extends Application {

	public static void main(String[] args) {
		launch();
	}

	@Override
	public void start(Stage stage) throws IOException {

		ConfigurableApplicationContext springContext = SpringApplication.run(EM426Main.class);
		SpringFXLoader.getInstance().setSpringContext(springContext, stage);

		FXMLLoader loader = SpringFXLoader.getInstance().loadFxml("views/MainDashboard.fxml");

		stage.setTitle("EM426Main");
		stage.setScene(new Scene(loader.load()));
		stage.show();
	}

	@Override
	public void stop() {
		SpringFXLoader.getInstance().cleanUp();
	}

}