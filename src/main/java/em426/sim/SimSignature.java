package em426.sim;


import java.io.*;
import java.util.*;

/**
 * A class to capture the signature of the Analysis Engine;  its type, name, and human readable desctipion
 * Does not including the specific settings of an analysis engine (see Settings class)
 * @author Bryan R. Moser
 *
 */
public class SimSignature implements Serializable {
	
	private static final long serialVersionUID = -586972011673952991L;
	private static final int CompressVersion = 1;  // V1.0 2021-03-14
	
	Type type;
	String name;
	String description;
	String	version;
	UUID id;
	
	public static enum Type {TIMEBASED_SIMULATOR, 
							EVENTBASED_SIMULATOR, 
							AGENTBASED_SIMULATOR, 
							MATRIX_METHOD,
							NETWORK_METHOD,
							LINEAR_PROGRAM, 
							MIXEDINTEGER_PROGRAM, 
							DYNAMIC_PROGRAM, 
							SYSTEM_DYNAMIC, 
							OTHER};
	
	// TODO - an enum or statics for the type
	
	public SimSignature(Type type, String name, String version,  String description) {
		this.id = UUID.randomUUID();
		this.type = type;
		this.version = version;
		this.name = name;
		this.description = description;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
	
	public String toString() {
		return name;
	}

}
