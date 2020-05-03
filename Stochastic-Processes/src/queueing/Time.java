package queueing;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/* Utility class allowing for (time, number of customers in the system) pairs to be represented in a JavaFX table */
public class Time {

	private StringProperty time;
	public StringProperty time() {
		if(time == null)
			time = new SimpleStringProperty(this, "time");
		return time;
	}
	
	private StringProperty numInSystem;
	public StringProperty numInSystem() {
		if(numInSystem == null)
			numInSystem = new SimpleStringProperty(this, "number in system");
		return numInSystem;
	}
	
	public Time(double time, int numInSystem) {
		setTime(time);
		setNumInSystem(numInSystem);
	}
	
	private void setTime(double time) { time().set(String.valueOf(time)); }
	private void setNumInSystem(int numInSystem) { numInSystem().set(String.valueOf(numInSystem)); }
	
	public String getTime() { return time().get(); }
	public String getNumInSystem() { return numInSystem().get(); }
}
