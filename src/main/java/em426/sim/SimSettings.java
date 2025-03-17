package em426.sim;


import java.io.*;
import java.util.*;


/**
 * A class to capture the settings specific to an analysis engine.
 *  Used to set parameters and later store them (along side related analysis results)
 * @author Bryan R. Moser
 *
 */
//TODO: Implement Map<K,V> interface??; KPM v1a5 20200707
public class SimSettings implements Serializable {

	private static final long serialVersionUID = -1135727278554264236L;
	private static final int CompressVersion = 1;  // V1.0 2021-03-14

	private ArrayList<Class<?>>		classes=new ArrayList<Class<?>>();
	private ArrayList<String>		names= new ArrayList<String>();
	private ArrayList<String>		descriptions= new ArrayList<String>();
	private ArrayList<Object>		values= new ArrayList<Object>();
	private ArrayList<String>		categories= new ArrayList<String>();
	// TODO - add a validation method, such as range, set, or method
	// TODO - should name be unique, and used as a key? or key additional?

	/**
	 * Create an empty settings set
	 */
	public SimSettings() {
	}

	/**
	 * Creates a single setting with type, name, and description
	 * @param type
	 * @param name - a unique title that identifies the setting
	 * @param description - a human readable description for user while viewing and setting the setting
	 *
	 * Example: Settings x = new Settings(Integer.class, "horizon", "how much an agent looks ahead", 4);
	 */
	public SimSettings(Class<?> type, String name, String description) {
		classes.add(type);
		names.add(name);
		descriptions.add(description);
	}

	public SimSettings(Class<?> type, String name, String description, Object value) {
		this(type, name, description);
		if (type.isInstance(value) ) {
			values.add(value);
		}
	}
	/**
	 *
	 * @param type	the Class type of this property (should be same as the value's class)
	 * @param category   a named grouping for this setting for showing with related setting
	 * @param name a unique title that identifies the setting
	 * @param description
	 * @param value
	 */
	public SimSettings(Class<?> type, String category, String name, String description, Object value) {
		this(type, name, description, value);
		categories.add(category);
	}

	// TODO convenience constructors for int, double, and string(set)

	public int getSize() {
		return names.size();
	}

	/**
	 *
	 * @param settingIndex -- the index of a setting, from 1 to the number of settings
	 * @return
	 */
	public Class<?> getType(int settingIndex){
		return classes.get(settingIndex);
	}
	public String getName(int settingIndex){
		return names.get(settingIndex);
	}
	public String getDescription(int settingIndex){
		return descriptions.get(settingIndex);
	}
	public Object getValue(String settingName) throws IndexOutOfBoundsException{
		return values.get(this.getIndex(settingName));
	}
	public Object getValue(int settingIndex) throws IndexOutOfBoundsException {
		return values.get(settingIndex);
	}

	/**
	 * Adds a new setting to the end of the list, with index the new size of the settings list
	 * @param type
	 * @param name
	 * @param description
	 */
	public void add(Class<?> type, String name, String description, Object value) {
		classes.add(type);
		names.add(name);
		descriptions.add(description);
		values.add(value); // default is empty, as setting value maybe changed later
		//TODO check for duplicate numbers
	}
	/*
	 * Searches for first occurrence of a setting with name settingName
	 * returns the index number (between 0 and 1 minus size of list), otherwise -1 if not contained
	 */
	public int getIndex(String settingName) {
		return names.indexOf(settingName);
	}

	/**
	 * Attempts to change the value of setting having the given name to value
	 * if the class type of value doesn't match the setting, does nothing
	 * @param settingName - key name of the setting
	 * @param value
	 */
	public void setValue(String settingName, Object value) {
		this.setValue(this.getIndex(settingName), value);
	}

	/**
	 * Attempts to change the value of setting at setting index to value
	 * if the class type of value doesn't match the setting, does nothing
	 * @param settingIndex - from 0 to the number of settings
	 * @param value
	 */
	public void setValue(int settingIndex, Object value) {

		if (settingIndex >= 0 && settingIndex < values.size() && classes.get(settingIndex).isInstance(value))
		{
			values.set(settingIndex,  value);
		}
	}

	public void removeSetting(int settingIndex) {
		classes.remove(settingIndex);
		names.remove(settingIndex);
		descriptions.remove(settingIndex);
		values.remove(settingIndex);
	}

	public String toString(int index) {
		return (names.get(index) + " = "+ values.get(index));
	}

	public void clear() {
		classes.clear();
		names.clear();
		descriptions.clear();
		values.clear();

	}

}

