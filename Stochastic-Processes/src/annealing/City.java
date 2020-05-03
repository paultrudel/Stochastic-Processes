package annealing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Point2D;

/* Representation of a city in the travelling salesman problem */
public class City {

	private StringProperty id; //The city's ID
	public StringProperty idProperty() {
		if(id == null)
			id = new SimpleStringProperty(this, "id");
		return id;
	}
	
	private StringProperty name; //The city's human readable name
	public StringProperty nameProperty() {
		if(name == null)
			name = new SimpleStringProperty(this, "name");
		return name;
	}
	
	private Point2D location; //The city's location on the map
	private StringProperty locationProperty;
	public StringProperty locationProperty() {
		if(locationProperty == null)
			locationProperty = new SimpleStringProperty(this, "location");
		return locationProperty;
	}
	
	//Map relating the cities which are directly reachable from this one and the associated cost
	private HashMap<String, Double> reachableCities;
	private StringProperty reachableCitiesProperty;
	public StringProperty reachableCitiesProperty() {
		if(reachableCitiesProperty == null)
			reachableCitiesProperty = new SimpleStringProperty(this, "reachable cities");
		return reachableCitiesProperty;
	}
	
	private AnnealingModel model; //Reference to the problem model
	
	public City(AnnealingModel model, String name, Point2D location) {
		this.model = model;
		idProperty().set(UUID.randomUUID().toString());
		this.location = location;
		setNameProperty(name);
		setLocationProperty(location.toString());
		reachableCities = new HashMap<>();
	}
	
	private void setNameProperty(String name) { nameProperty().set(name); }
	private void setLocationProperty(String location) { locationProperty().set(location); }
	
	public String getId() { return idProperty().get(); }
	public String getName() { return nameProperty().get(); }
	public Point2D getLocation() { return location; }
	public String getLocationProperty() { return locationProperty().get(); }
	public void addCity(String cityId, double cost) { reachableCities.put(cityId, cost); }
	public double getCost(String cityId) { return reachableCities.get(cityId); }
	public boolean isReachable(String cityId) { return reachableCities.containsKey(cityId); }
	public HashMap<String, Double> getReachableCities() { return reachableCities; }
	public List<String> getReachableCitiesList() { return new ArrayList<>(reachableCities.keySet()); }
	public String getReachableCitiesProperty() { return reachableCitiesProperty().get(); }
	
	public void setReachableCitiesProperty() {
		if(reachableCities.size() == model.getNumCities() - 1)
			reachableCitiesProperty().set("All Cities");
		else {
			StringBuilder sb = new StringBuilder();
			for(String cityId: reachableCities.keySet())
				sb.append(model.getCityName(cityId) + " ");
			reachableCitiesProperty.set(sb.toString());
		}
	}
}
