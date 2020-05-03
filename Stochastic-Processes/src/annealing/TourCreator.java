package annealing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

/* Class responsible for all of the computation needed to solve the travelling salesman problem */
public class TourCreator {

	private AnnealingModel model; //Reference to the problem model
	private int numIterations = 1000; //Number of iterations the algorithm should run for
	private int increments = numIterations / 20; //Increments used for plotting tour a costs at specific iterations
	private HashMap<String, City> cities; //Map relating city IDs to their respective city objects
	private String startingCity; //Id of the starting city
	//Map relating an iteration the value of the tour in that iteration
	private LinkedHashMap<Integer, Double> utilities;
	private ArrayList<Tour> tours; //The tours that the algorithm has created
	private Random rand;
	
	public TourCreator(AnnealingModel model) {
		this.model = model;
	}
	
	/* Entry point into the algorithm to start finding the optimal tour */
	public List<Tour> createTour() {
		cities = model.getCities(); //Get the cities from the model
		startingCity = model.getStartingCity();  //Get the starting city from the model
		utilities = new LinkedHashMap<>();
		tours = new ArrayList<>();
		rand = new Random();
		List<String> initialState = createInitialState(); //Get the initial tour
		Tour initialTour = new Tour(model, "Initial Tour", new ArrayList<>(initialState),
				String.valueOf(utilities.get(0))); //Create a Tour representation of the initial tour
		tours.add(initialTour);
		List<String> finalState = createFinalState(initialState); //Get the final optimal tour
		//Create a Tour representation of the final tour
		Tour finalTour = new Tour(model, "Final Tour", new ArrayList<>(finalState), 
				String.valueOf(utilities.get((numIterations / increments))));
		tours.add(finalTour);
		return tours; //Send the list of created tours back to the model
	}
	
	public int getNumIterations() { return numIterations; }
	public void setNumIterations(int numIterations) {
		this.numIterations = numIterations;
		increments = numIterations / 20;
	}
	public LinkedHashMap<Integer, Double> getUtilities() { return utilities; }
	
	/* Generate an initial state from which to start the algorithm from */
	private List<String> createInitialState() {
		List<String> initialState = new ArrayList<String>(); //Create a new state
		initialState.add(startingCity); //Add the starting city as the first city to visit
		String currentCity = startingCity;
		String nextCity;
		//Repeatedly add cities at random to the tour until all cities are in the tour
		do {
			//Find the cities which can be reach from the current city
			List<String> reachableCities = new ArrayList<>(cities.get(currentCity).getReachableCitiesList());
			//Remove all cities from this list which have already been visited
			reachableCities.removeAll(initialState);
			//Choose a random city from the remaining cities to visit
			nextCity = reachableCities.get(rand.nextInt(reachableCities.size()));
			initialState.add(nextCity);
			currentCity = nextCity;
		} while(initialState.size() < cities.size());
		utilities.put(0, computeUtility(initialState)); //Add an initial utility value for the iteration 0
		return initialState;
	}
	
	/*Implementation of the simulated annealing algorithm */
	private List<String> createFinalState(List<String> initialState) {
		List<String> nextState = new ArrayList<>(initialState);
		List<String> solution = new ArrayList<>(initialState); //Set the initial solution to the initial state
		double utilSolution = computeUtility(solution); //Compute the cost of the initial solution
		//Run the algorithm for the specified number of iterations
		for(int n = 1; n <= numIterations; n++) {
			nextState = createNextState(solution); //Create a neighbour state
			double utilNext = computeUtility(nextState); //Compute the cost of the tour in the neighbouring state
			double delta = utilNext - utilSolution; //Difference in the two costs
			//If the neighbouring state has a lower cost move to it
			if(delta < 0) {
				solution = new ArrayList<>(nextState);
				utilSolution = utilNext;
			}
			//Otherwise move to it with some probability
			else {
				double lambda = Math.log(1 + n);
				double p = Math.exp(-1.0 * (delta / lambda)); //Threshold for moving to the neighbouring state
				double r = rand.nextDouble(); //Generate a uniformly random number from (0, 1)
				//If the random number is below the threshold move to the neighbouring state
				if(r < p) {
					solution = new ArrayList<>(nextState);
					utilSolution = utilNext;
				}
			}
			if(n % increments == 0)
				utilities.put((n / increments), computeUtility(solution)); //Add a utility value for the iteration
		}
		return solution;
	}
	
	/* Generate a neighbour state from a given state */
	private List<String> createNextState(List<String> currentState) {
		List<String> nextState = new ArrayList<>(currentState); //Set the neighbour state to the current state
		
		/* Modify the state in a small random way. In this scenario this is done by swapping the order of
		 * two randomly chosen cities in the tour. Any two cities can be chosen except for the starting city 
		*/
		int index1 = rand.nextInt(nextState.size());
		while(index1 == 0)
			index1 = rand.nextInt(nextState.size());
		
		int index2 = rand.nextInt(nextState.size());
		while(index1 == index2 || index2 == 0)
			index2 = rand.nextInt(nextState.size());
		
		String city1 = nextState.get(index1);
		String city2 = nextState.get(index2);
		nextState.set(index1, city2);
		nextState.set(index2, city1);
		return nextState;
	}
	
	/* Compute the cost of a tour represented by the given state */
	private double computeUtility(List<String> state) {
		double utility = 0;
		String currentCity;
		String nextCity;
		for(int i = 0; i < state.size() - 1; i++) {
			currentCity = state.get(i);
			nextCity = state.get(i + 1);
			utility += cities.get(currentCity).getCost(nextCity);
		}
		return utility;
	}
}
