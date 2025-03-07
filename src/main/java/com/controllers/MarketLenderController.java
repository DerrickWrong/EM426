package com.controllers;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

@Component
public class MarketLenderController {

	@FXML
	TextField symbolName, stockPrice, volumeBox;

	@FXML
	Slider companySlider, fundSlider;

	@FXML
	Label floatingLabel;

	@FXML
	ComboBox marginBox;

	@PostConstruct
	public void init() {

	}

	@FXML
	public void companySlided() {

	}

	@FXML
	public void fundSlided() {

	}

}
