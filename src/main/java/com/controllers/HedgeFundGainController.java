package com.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.configurations.SimConfiguration;
import jakarta.annotation.PostConstruct;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Scheduler;

@Component
public class HedgeFundGainController {

	@FXML
	BarChart hedgeGainLossHistogram;

	private final XYChart.Series series = new XYChart.Series<>();

	private final int bin[] = new int[7];

	@Autowired
	SimConfiguration simConfig; //

	@Autowired
	Sinks.Many<Double> resultSink;

	@Autowired
	@Qualifier("fxScheduler")
	Scheduler fxScheduler;

	@FXML
	public void initialize() {

		// add the series
		this.series.getData().add(new XYChart.Data<>("Loss > 10m", bin[0]));
		this.series.getData().add(new XYChart.Data<>("Loss < 7m", bin[1]));
		this.series.getData().add(new XYChart.Data<>("Loss < 5m", bin[2]));
		this.series.getData().add(new XYChart.Data<>("Tie", bin[3]));
		this.series.getData().add(new XYChart.Data<>("Gain < 5m", bin[4]));
		this.series.getData().add(new XYChart.Data<>("Gain < 7m", bin[5]));
		this.series.getData().add(new XYChart.Data<>("Gain > 10m", bin[6]));

		this.hedgeGainLossHistogram.getData().addAll(this.series);
		this.hedgeGainLossHistogram.setLegendVisible(false);
	}

	@PostConstruct
	void init() {

		// update histogram
		this.resultSink.asFlux().publishOn(fxScheduler).subscribe(data -> {

			this.updateHistogram(data);
		});

	}

	private void updateHistogram(double gainOrLoss) {

		if (gainOrLoss <= -10) {
			bin[0] = bin[0] + 1;

		} else if (gainOrLoss > -10 && gainOrLoss <= -7) {
			bin[1] = bin[1] + 1;

		} else if (gainOrLoss > -7 && gainOrLoss <= -5) {
			bin[2] = bin[2] + 1;

		} else if (gainOrLoss > -5 && gainOrLoss <= 5) {
			bin[3] = bin[3] + 1;

		} else if (gainOrLoss > 5 && gainOrLoss <= 7) {
			bin[4] = bin[4] + 1;

		} else if (gainOrLoss > 7 && gainOrLoss <= 10) {
			bin[5] = bin[5] + 1;

		} else {
			bin[6] = bin[6] + 1;
		}
	}

}
