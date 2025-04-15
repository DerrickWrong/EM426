

import javafx.application.Application;
import javafx.stage.Stage;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.ChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.javafx.swing.JavaFXSwingChartFactory;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.SurfaceBuilder;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.Quality;

public class DemoJavaFXSwingNode extends Application {

	public static void main(String[] args) {
	    Application.launch(args);
	  }

	@Override
	public void start(Stage stage) throws Exception {
		// TODO Auto-generated method stub
		
		stage.setTitle(DemoJavaFXSwingNode.class.getSimpleName());

	    // Jzy3d
	    JavaFXSwingChartFactory factory = new JavaFXSwingChartFactory();
	    Chart chart = getChart(factory);

	    factory.open(chart, stage);
		
	}
	
	private Chart getChart(ChartFactory factory) {
	    // Define a function to plot
	    Mapper mapper = new Mapper() {
	      @Override
	      public double f(double x, double y) {
	        return x * Math.sin(x * y);
	      }
	    };

	    // Create the object to represent the function over the given range.
	    final Shape surface = new SurfaceBuilder().orthonormal(mapper, new Range(-3, 3), 80);
	    surface.setColorMapper(new ColorMapper(new ColorMapRainbow(), surface, new Color(1, 1, 1, .5f)));
	    surface.setFaceDisplayed(true);
	    surface.setWireframeDisplayed(false);

	    // Create a chart
	    Quality quality = Quality.Advanced();
	    Chart chart = factory.newChart(quality);
	    chart.getScene().getGraph().add(surface);
	    return chart;
	  }
	
}
