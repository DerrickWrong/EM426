package em426.api;

import java.util.*;

/**
 * A common interface for identify a model object, which allows it to be tagged with a metric sensor
 * Could be the whole world, an agent, a zone etc.
 * @author Bryan R. Moser
 *
 */
public interface EntityAPI {

	public String getName(); 
	public void setName(String name);
	public UUID getId() ;
	public Class<?> getType() ;
	boolean equals(EntityAPI obj);
	
	public EntityAPI getParent();
	public List<EntityAPI> getChildren();
	public void add(EntityAPI... children);
}
