package ising;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

import view.Interface;

/*
 * Model class which holds all of the necessary information to represent all possible states in an Ising Model 
 */
public class IsingModel {

	private Interface view;
	private Simulation simulation;
	private double beta = 0.01; //Coupling constant set to an initial value
	private Random rand;
	private int numIterations = 1000; //Number of iterations used to simulate the transformation of the model
	private int latticeSize; //Size of the lattice corresponding the the size of the model
	private Spin[][] lattice; //Matrix of spin values used to represent a state in the model
	//Map representing the relationship between a site and its neighbouring sites 
	private HashMap<Tuple<Integer, Integer>, List<Tuple<Integer, Integer>>> neighbours;
	
	public IsingModel(Interface view) {
		this.view = view;
		simulation = new Simulation(this);
		neighbours = new HashMap<>();
		rand = new Random();
	}
	
	public void setBeta(double beta) { 
		this.beta = beta;
		view.showAlert("Beta Value Set", "The value of beta has been set to " + beta);
	}
	public void setNumIterations(int numIterations) {
		this.numIterations = numIterations;
		view.showAlert("Iterations Set", "The number of iterations has been set to " + numIterations);
	}
	
	/* Reset the model back to its default state */
	public void reset() {
		beta = 0.01;
		numIterations = 1000;
		lattice = new Spin[0][0];
		view.showAlert("Reset Complete", "Lattice, value of beta, and number of iterations have been reset");
	}
	
	public double getBeta() { return beta; }
	public int getNumIterations() { return numIterations; }
	public int getLatticeSize() { return latticeSize; }
	public List<Tuple<Integer, Integer>> getNeighbours(Tuple<Integer, Integer> tuple) { 
		return neighbours.get(tuple); 
	}
	public LinkedHashMap<Integer, Configuration> getConfigurations() { 
		return simulation.getConfigurations(); 
	}
	
	/* Create the lattice used to represent the states given a user specified size*/
	public void createLattice(int size) {
		latticeSize = size;
		lattice = new Spin[size][size];
		for(int i = 0; i < size; i++) {
			for(int j = 0; j < size; j++) {
				Tuple<Integer, Integer> site = new Tuple<Integer, Integer>(i, j); //Create a new site
				neighbours.put(site, computeNeighbours(size, i, j)); //Find the neighbours of this site
			}
		}
		initializeLattice(); //Create a random initialization
		view.fillIsingLattice(lattice);
		view.showAlert("Lattice Created", "Lattice of size " + size + " has been created and initialized");
	}
	
	/* Simulate the state transformations over time */
	public void simulate() {
		lattice = simulation.simulate(lattice, beta, numIterations);
		view.fillIsingLattice(lattice);
		view.showAlert("Simulation Complete", "Ising Model simulations complete");
	}
	
	/* Estimate the probability distribution of states in the model */
	public ArrayList<Estimate> estimate(int numToEstimate) {
		ArrayList<Estimate> estimates = new ArrayList<Estimate>();
		for(int n = 0; n < numToEstimate; n++) {
			Spin[][] lattice = createLatticeGivenState(latticeSize, n);
			double probability = simulation.estimate(lattice, beta);
			Estimate estimate = new Estimate("State " + n, probability, lattice);
			estimates.add(estimate);
		}
		return estimates;
	}
	
	/* Find the neighbours of a site given the site and the size of the model */
	private List<Tuple<Integer,Integer>> computeNeighbours(int size, int x, int y) {
		List<Tuple<Integer, Integer>> neighbours = new ArrayList<>();
		//Iterate over all possible neighbours
		for(Neighbour neighbour: Neighbour.values()) {
			Tuple<Integer, Integer> n = neighbour.getNeighbouringPoint(x, y); //Get the neighbouring point
			//If the point is in the model then it is a neighbouring site
			if(isInLattice(size, n.getX(), n.getY()))
				neighbours.add(n);
		}
		return neighbours;
	}
	
	/* Check if a point is in the model */
	private boolean isInLattice(int size, int x, int y) {
		return ((x >= 0 && x < size) && (y >= 0 && y < size));
	}
	
	/* Create a random initial state for the model */
	private void initializeLattice() {
		for(int i = 0; i < latticeSize; i++) {
			for(int j = 0; j < latticeSize; j++) {
				Spin spin = Spin.values()[rand.nextInt(Spin.values().length)];
				lattice[i][j] = spin;
			}
		}
	}
	
	/* Given a the size of the model create a specific state for the model. The states of a model are numbered 
	 * 0-2^((model size)^2). Given one of the numbers in that range the corresponding state can be generated.
	 * This is done using the binary representation of the number. Each 1 or 0 in the binary representation of 
	 * the state number corresponds to a site in the model. The spin value of that site is determined by the value.
	 * 0 indicating positive spin and 1 a negative spin 
	 */
	public Spin[][] createLatticeGivenState(int latticeSize, int latticeState) {
		StringBuilder binaryString = new StringBuilder(Integer.toBinaryString(latticeState));
		char[] tokens = ((binaryString.reverse()).toString()).toCharArray();
		Spin[][] lattice = new Spin[latticeSize][latticeSize];
		for(int i = 0; i < latticeSize; i++) {
			for(int j = 0; j < latticeSize; j++) {
				int tokenIndex = j + (i * latticeSize);
				if(tokenIndex < tokens.length) {
					int spinIndex = Integer.parseInt(String.valueOf(tokens[tokenIndex]));
					lattice[i][j] = Spin.values()[spinIndex];
				}
				else
					lattice[i][j] = Spin.POSITIVE;
			}
		}
		return lattice;
	}
	
	/* Enumeration of the possible spin values for each site */
	public static enum Spin {
		POSITIVE(1),
		NEGATIVE(-1);
		
		private int value;
		
		Spin(int value) {
			this.value = value;
		}
		
		public int getValue() { return this.value; }
	}
	
	/* Enumeration of the possible neighbours which a site can have. In this model any site can only have at 
	 * maximum four neighbours. A neighbour to the North, East, South, and West */
	public static enum Neighbour {
		NORTH(0, -1),
		EAST(1, 0),
		SOUTH(0, 1),
		WEST(-1, 0);
		
		private int xDiff;
		private int yDiff;
		
		Neighbour(int xDiff, int yDiff) {
			this.xDiff = xDiff;
			this.yDiff = yDiff;
		}
		
		public Tuple<Integer, Integer> getNeighbouringPoint(int x, int y) {
			int neighbourX = x + this.xDiff;
			int neighbourY = y + this.yDiff;
			return new Tuple<Integer, Integer>(neighbourX, neighbourY);
		}
	}
}
