package com;

import org.springframework.context.ConfigurableApplicationContext;

import jakarta.annotation.PreDestroy;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

public class SpringFXLoader {

	private static SpringFXLoader _instance;

	private ConfigurableApplicationContext springContext;
	private Stage mainFxStage;

	private SpringFXLoader() {
	}

	public static SpringFXLoader getInstance() {
		if (_instance == null) {
			_instance = new SpringFXLoader();
		}
		return _instance;
	}

	// setting spring context
	public void setSpringContext(final ConfigurableApplicationContext spring, final Stage mainStage) {
		this.springContext = spring;
		this.mainFxStage = mainStage;
	}

	// Path is relative to this class
	public FXMLLoader loadFxml(String fxmlPath) {

		FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
		loader.setControllerFactory(springContext::getBean);
		return loader;
	}

	public Stage getMainStage() {

		return mainFxStage;
	}

	@PreDestroy
	public void cleanUp() {
		this.springContext.close();
	}

}
