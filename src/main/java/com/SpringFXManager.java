package com;

import org.springframework.context.ConfigurableApplicationContext;

import jakarta.annotation.PreDestroy;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

public class SpringFXManager {

	private static SpringFXManager _instance;

	private ConfigurableApplicationContext springContext;
	private Stage mainFxStage;

	private SpringFXManager() {
	}

	public static SpringFXManager getInstance() {
		if (_instance == null) {
			_instance = new SpringFXManager();
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

	public ConfigurableApplicationContext getSpringContext() {

		return this.springContext;
	}

	@PreDestroy
	public void cleanUp() {
		this.springContext.close();
	}

}
