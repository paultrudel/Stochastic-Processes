package annealing;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/* Simple utility class to allow for the cost between two cities to be represented in a JavaFX table */
public class Cost {

	private StringProperty firstCity;
	public StringProperty firstCityProperty() {
		if(firstCity == null)
			firstCity = new SimpleStringProperty(this, "first city");
		return firstCity;
	}
	
	private StringProperty secondCity;
	public StringProperty secondCityProperty() {
		if(secondCity == null)
			secondCity = new SimpleStringProperty(this, "second city");
		return secondCity;
	}
	
	private StringProperty cost;
	public StringProperty costProperty() {
		if(cost == null)
			cost = new SimpleStringProperty(this, "cost");
		return cost;
	}
	
	public Cost(String firstCity, String secondCity, String cost) {
		setFirstCityProperty(firstCity);
		setSecondCityProperty(secondCity);
		setCostProperty(cost);
	}
	
	private void setFirstCityProperty(String firstCity) { firstCityProperty().set(firstCity); }
	private void setSecondCityProperty(String secondCity) { secondCityProperty().set(secondCity); }
	private void setCostProperty(String cost) { costProperty().set(cost); }
	
	public String getFirstCityProperty() { return firstCityProperty().get(); }
	public String getSeconfCityProperty() { return secondCityProperty().get(); }
	public String getCostProperty() { return costProperty().get(); }
}
