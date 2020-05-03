package ising;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/* Utility class allowing for a state to represented in a JavaFX table */
public class Configuration {

	private int iterationNum;
	private StringProperty iterationNumProperty;
	public StringProperty iterationNumProperty() {
		if(iterationNumProperty == null)
			iterationNumProperty = new SimpleStringProperty(this, "iteration number");
		return iterationNumProperty;
	}
	
	private IsingModel.Spin[][] lattice;
	
	private double positiveSpinProportion;
	private StringProperty positiveSpinProportionProperty;
	public StringProperty positiveSpinProportionProperty() {
		if(positiveSpinProportionProperty == null)
			positiveSpinProportionProperty = new SimpleStringProperty(this, "positive spin proportion");
		return positiveSpinProportionProperty;
	}
	
	private double neighbourOppositeSpinProportion;
	private StringProperty neighbourOppositeSpinProportionProperty;
	public StringProperty neighbourOppositeSpinProportionProperty() {
		if(neighbourOppositeSpinProportionProperty == null)
			neighbourOppositeSpinProportionProperty = new SimpleStringProperty(this, "proportion of "
					+ "neighbours with oppposite spins");
		return neighbourOppositeSpinProportionProperty;
	}
	
	public Configuration(int iterationNum, IsingModel.Spin[][] lattice, double positiveSpinProportion, double 
			neighbourOppositeSpinProportion) {
		this.iterationNum = iterationNum;
		this.lattice = lattice;
		this.positiveSpinProportion = positiveSpinProportion;
		this.neighbourOppositeSpinProportion = neighbourOppositeSpinProportion;
		setIterationNumProperty(String.valueOf(iterationNum));
		setPositiveSpinProportionProperty(String.valueOf(positiveSpinProportion));
		setNeighbourOppositeSpinProportionProperty(String.valueOf(neighbourOppositeSpinProportion));
	}
	
	private void setIterationNumProperty(String iterationNum) { iterationNumProperty().set(iterationNum); }
	private void setPositiveSpinProportionProperty(String positiveSpinProportion) { 
		positiveSpinProportionProperty().set(positiveSpinProportion);
	}
	private void setNeighbourOppositeSpinProportionProperty(String neighbourOppositeSpinProportion) {
		neighbourOppositeSpinProportionProperty().set(neighbourOppositeSpinProportion);
	}
	
	public int getIterationNum() { return iterationNum; }
	public String getIterationNumProperty() { return iterationNumProperty().get(); }
	public IsingModel.Spin[][] getLattice() { return lattice; }
	public double getPositiveSpinProportion() { return positiveSpinProportion; }
	public String getPositiveSpinProportionProperty() { return positiveSpinProportionProperty().get(); }
	public double getNeighbourOppositeSpinProportion() { return neighbourOppositeSpinProportion; }
	public String getNeighbourOppositeSpinProportionProperty() { 
		return neighbourOppositeSpinProportionProperty.get(); 
	}
}
