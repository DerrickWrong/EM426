package com.controllers;

import org.springframework.stereotype.Component;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

@Component
public class ConfigurationController {

	@FXML
	TextField periodityField;
	
	@FXML
	TextField simTimeField;
	
	@FXML
	TextField expiredField;
	
	@FXML
	CheckBox marginCheckbox;
	
	
	
}
