package em426.api;

import em426.agents.*;
import em426.world.*;
import javafx.beans.property.*;
import javafx.scene.shape.*;

import java.util.*;

/**
 * A common interface for identify a model object, which allows it to be tagged with a metric sensor
 * Could be the whole world, an agent, a zone etc.
 * @author Bryan R. Moser
 *
 */
public interface EntityAPI {

	public UUID getId() ;
	public String getName();
	public void setName(String name);
	public StringProperty nameProperty();

	public Class<?> getType() ; // allows call to a specific class constructor

	boolean equals(EntityAPI obj);
	
	public EntityAPI getParent();
	public ListProperty<Place> childrenProperty();
	public void add(EntityAPI... children);

	public Shape getShape();
	public void setShape(Shape shape);
	public ObjectProperty<Shape> shapeProperty();

	public ListProperty<DemandAPI> demandsProperty();
	public void add(DemandAPI... demands);
	public void remove(DemandAPI... demands);
}
