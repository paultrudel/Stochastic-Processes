package annealing;

import java.util.ArrayList;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/* Simple utility class to allow for a tour to be represented in a JavaFX table */
public class Tour {

	private StringProperty name;
	public StringProperty nameProperty() {
		if(name == null)
			name = new SimpleStringProperty(this, "tour name");
		return name;
	}
	
	private ArrayList<String> cities;
	private StringProperty citiesProperty;
	public StringProperty citiesProperty() {
		if(citiesProperty == null)
			citiesProperty = new SimpleStringProperty(this, "cities");
		return citiesProperty;
	}
	
	private StringProperty cost;
	public StringProperty costProperty() {
		if(cost == null)
			cost = new SimpleStringProperty(this, "cost");
		return cost;
	}
	
	AnnealingModel model;
	
	public Tour(AnnealingModel model, String name, ArrayList<String> cities, String cost) {
		this.model = model;
		this.cities = cities;
		setNameProperty(name);
		setCitiesProperty(cities);
		setCostProperty(cost);
	}
	
	private void setNameProperty(String name) { nameProperty().set(name); }
	
	private void setCitiesProperty(ArrayList<String> cities) {
		StringBuilder sb = new StringBuilder();
		for(String cityId: cities)
			sb.append(model.getCityName(cityId) + " ");
		citiesProperty().set(sb.toString());
	}
	
	private void setCostProperty(String cost) { costProperty().set(cost); }
	
	public String getNameProperty() { return nameProperty().get(); }
	public String getCitiesProperty() { return citiesProperty().get(); }
	public String getCostProperty() { return costProperty().get(); }
	public ArrayList<String> getCities() { return cities; }
}
