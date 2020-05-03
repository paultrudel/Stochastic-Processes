package ising;

import ising.IsingModel.Spin;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/* Utility class allowing for the probability estimate of a state to represented in a JavaFX table */
public class Estimate {

	private StringProperty stateNum;
	public StringProperty stateNumProperty() {
		if(stateNum == null)
			stateNum = new SimpleStringProperty(this, "state number");
		return stateNum;
	}
	
	private double probability;
	private StringProperty probabilityProperty;
	public StringProperty probabilityProperty() {
		if(probabilityProperty == null)
			probabilityProperty = new SimpleStringProperty(this, "probability");
		return probabilityProperty;
	}
	
	private Spin[][] lattice;
	
	public Estimate(String stateNum, double probability, Spin[][] lattice) {
		setStateNumProperty(stateNum);
		this.probability = probability;
		setProbabilityProperty(String.valueOf(probability));
		this.lattice = lattice;
	}
	
	private void setStateNumProperty(String stateNum) { stateNumProperty().set(stateNum); }
	private void setProbabilityProperty(String probability) { probabilityProperty().set(probability); }
	
	public String getStateNumProperty() { return stateNumProperty().get(); }
	public double getProbability() { return probability; }
	public String getProbabilityProperty() { return probabilityProperty().get(); }
	public Spin[][] getLattice() { return lattice; }
}
