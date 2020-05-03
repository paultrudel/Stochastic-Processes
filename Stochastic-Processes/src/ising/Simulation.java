package ising;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

import ising.IsingModel.Spin;

/* Class responsible for all of the computation in simulating the model and estimating probability distributions */
public class Simulation {

	private IsingModel model; //A reference to the model
	private LinkedHashMap<Integer, Configuration> configurations; //Map of state numbers to their respective states
	private int increments; //Increments used for plotting the probability values of states
	private Random rand;
	
	public Simulation(IsingModel model) {
		this.model = model;
		configurations = new LinkedHashMap<>();
		rand = new Random();
	}
	
	 /* Simulate transformations to the state of the model for a specified number of iterations given 
	  * an initial state configuration and value of beta */
	public Spin[][] simulate(Spin[][] lattice, double beta, int numIterations) {
		increments = numIterations / 10;
		configurations = new LinkedHashMap<>();
		//Create a Configuration representation of the initial state
		Configuration initialConfiguration = new Configuration(0, lattice, 
				computePositiveSpinProportion(lattice), computeNeighbourOppositeSpinProportion(lattice));
		configurations.put(0, initialConfiguration); //Add an entry for the initial configuration
		Spin[][] currLattice = cloneLattice(lattice); //Create a copy of the initial state
		//Make transformations to the state for the specified number of iterations
		for(int n = 1; n <= numIterations; n++) {
			currLattice = update(beta, lattice); //Update the current state
			if((n % increments) == 0) {
				//Create a Configuration representation of the new state 
				Configuration configuration = new Configuration(n, currLattice, 
						computePositiveSpinProportion(currLattice), 
						computeNeighbourOppositeSpinProportion(currLattice));
				//Add an entry for the new configuration
				configurations.put((n / increments), configuration);
			}
		}
		return currLattice; //Return the final state of the model after the transformations
	}
	
	/* Estimate the probability of a given state */
	public double estimate(Spin[][] lattice, double beta) {
		double probability = 1.0; //Initialize the probability to 1
		//Iterate over all of the sites in the model
		for(int i = 0; i < lattice.length; i++) {
			for(int j = 0; j < lattice.length; j++) {
				//Create a Tuple representation of the site
				Tuple<Integer, Integer> site = new Tuple<>(i, j);
				//Find the neighbouring sites
				List<Tuple<Integer, Integer>> neighbours = model.getNeighbours(site);
				Spin siteSpin = lattice[i][j]; //The spin of the current site
				//Iterate over the neighbouring sites
				for(Tuple<Integer, Integer> neighbour: neighbours) {
					//The spin of the neighbour site
					Spin neighbourSpin = lattice[neighbour.getX()][neighbour.getY()];
					//Update the probability
					probability = probability * Math.exp((beta * siteSpin.getValue() * 
							neighbourSpin.getValue()));
				}
			}
		}
		return (probability / Math.pow(2, Math.pow(model.getLatticeSize(), 2)));
	}
	
	public LinkedHashMap<Integer, Configuration> getConfigurations() { return configurations; }
	
	/* Compute the proportion of sites in the state that have a positive spin */
	public double computePositiveSpinProportion(Spin[][] lattice) {
		int positiveCount = 0;
		int totalCount = 0;
		for(int i = 0; i < lattice.length; i++) {
			for(int j = 0; j < lattice.length; j++) {
				if(lattice[i][j] == Spin.POSITIVE)
					positiveCount++;
				totalCount++;
			}
		}
		return ((double) positiveCount / (double) totalCount);
	}
	
	/* Compute the proportion of a neighbouring states that have a different spin*/
	public double computeNeighbourOppositeSpinProportion(Spin[][] lattice) {
		int oppositeSpinCount = 0;
		int totalCount = 0;
		for(int i = 0; i < lattice.length; i++) {
			for(int j = 0; j < lattice.length; j++) {
				Tuple<Integer, Integer> currSite = new Tuple<>(i, j);
				Spin currSiteSpin = lattice[i][j];
				List<Tuple<Integer, Integer>> neighbours = model.getNeighbours(currSite);
				for(Tuple<Integer, Integer> neighbour: neighbours) {
					Spin neighbourSpin = lattice[neighbour.getX()][neighbour.getY()];
					if(currSiteSpin != neighbourSpin)
						oppositeSpinCount++;
					totalCount++;
				}
			}
		}
		return ((double) oppositeSpinCount / (double) totalCount);
	}
	
	/* Create a copy of a given state */
	private Spin[][] cloneLattice(Spin[][] latticeToClone) {
		int latticeSize = latticeToClone.length;
		Spin[][] clonedLattice = new Spin[latticeSize][latticeSize];
		for(int i = 0; i < latticeSize; i++) {
			Spin[] row = new Spin[latticeSize];
			for(int j = 0; j < latticeSize; j++)
				row[j] = latticeToClone[i][j];
			clonedLattice[i] = row;
		}
		return clonedLattice;
	}
	
	/* Update the state of the model given the current state and a value of beta */
	private Spin[][] update(double beta, Spin[][] lattice) {
		Spin[][] newLattice = cloneLattice(lattice);
		int latticeSize = newLattice.length;
		int siteX = rand.nextInt(latticeSize);
		int siteY = rand.nextInt(latticeSize);
		Tuple<Integer, Integer> site = new Tuple<>(siteX, siteY);
		Tuple<Integer, Integer> neighbourSimilarities = computeNeighbourSimilarities(newLattice, site);
		double conditionalProbability = computeConditionalProbability(beta, neighbourSimilarities);
		double u = rand.nextDouble();
		if(u < conditionalProbability) {
			Spin siteSpin = newLattice[siteX][siteY];
			if(siteSpin == Spin.POSITIVE)
				siteSpin = Spin.NEGATIVE;
			else
				siteSpin = Spin.POSITIVE;
			newLattice[siteX][siteY] = siteSpin;
		}
		return newLattice;
	}
	
	/* Given a site in the state compute the number of neighbours with the same spin and the number of neighbours
	 * with a different spin 
	*/
	private Tuple<Integer, Integer> computeNeighbourSimilarities(Spin[][] lattice, 
			Tuple<Integer, Integer> site) {
		int sameSpinCount = 0;
		int diffSpinCount = 0;
		List<Tuple<Integer, Integer>> neighbours = model.getNeighbours(site);
		Spin siteSpin = lattice[site.getX()][site.getY()];
		for(Tuple<Integer, Integer> neighbour: neighbours) {
			Spin neighbourSpin = lattice[neighbour.getX()][neighbour.getY()];
			if(siteSpin == neighbourSpin)
				sameSpinCount++;
			else
				diffSpinCount++;
		}
		return new Tuple<Integer, Integer>(sameSpinCount, diffSpinCount);
	}
	
	/* Compute the sum of the spins of the neighbours of a site */
	private int computeNeighbourSum(Spin[][] lattice, Tuple<Integer, Integer> site) {
		int sum = 0;
		List<Tuple<Integer, Integer>> neighbours = model.getNeighbours(site);
		Spin siteSpin = lattice[site.getX()][site.getY()];
		for(Tuple<Integer, Integer> neighbour: neighbours) {
			Spin neighbourSpin = lattice[neighbour.getX()][neighbour.getY()];
			if(siteSpin == neighbourSpin)
				sum++;
			else
				sum--;
		}
		return sum;
	}
	
	private double computeConditionalProbability(double beta, 
			Tuple<Integer, Integer> neighbourSimilarities) {
		double negBeta = -1.0 * beta;
		double numerator = Math.exp(negBeta * neighbourSimilarities.getX());
		double denominator = Math.exp(negBeta * neighbourSimilarities.getY()) + 
				Math.exp(negBeta * neighbourSimilarities.getX());
		return (numerator / denominator);
	}
}
