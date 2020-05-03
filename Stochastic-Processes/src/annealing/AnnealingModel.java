package annealing;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

import javafx.geometry.Point2D;
import view.Interface;

/*
 * Model for the travelling salesman problem
*/
public class AnnealingModel {
	
	private Interface view;
	private TourCreator tc;
	private LinkedHashMap<String, City> cities; //Maps city IDs to their related city objects
	private ArrayList<Cost> costs; //List of costs for each tour
	private ArrayList<Tour> tours;
	private String startingCity; //ID for the starting city
	private int numCitiesCreated; //Number of cities in the problem
	private Random rand;
	
	public AnnealingModel(Interface view) {
		this.view = view;
		tc = new TourCreator(this);
		cities = new LinkedHashMap<>();
		costs = new ArrayList<>();
		tours = new ArrayList<>();
		numCitiesCreated = 0;
		rand = new Random();
	}
	
	/* Add a city to the problem given its location on the map */
	public void addCity(Point2D location) {
		String name = "City " + numCitiesCreated++;
		City city = new City(this, name, location);
		cities.put(city.getId(), city);
	}
	
	public int getNumCities() { return cities.size(); }
	public LinkedHashMap<String, City> getCities() { return cities; }
	public City getCity(String cityId) { return cities.get(cityId); }
	public String getCityName(String cityId) { return cities.get(cityId).getName(); }
	public Point2D getCityLocation(String cityId) { return cities.get(cityId).getLocation(); }
	public ArrayList<Cost> getCosts() { return costs; }
	public ArrayList<Tour> getTours() { return tours; }
	public Tour getTour(int index) { return tours.get(index); }
	public String getStartingCity() { return startingCity; }
	public int getNumIterations() { return tc.getNumIterations(); }
	public void setNumIterations(int numIterations) {
		tc.setNumIterations(numIterations); 
		view.showAlert("Iterations Set", "The number of iterations has been set to " + numIterations);
	}
	public LinkedHashMap<Integer, Double> getUtilities() { return tc.getUtilities(); }
	
	/* Reset the problem and clear all the existing cities*/
	public void resetCities() { 
		cities = new LinkedHashMap<>();
		costs = new ArrayList<>();
		tours = new ArrayList<>();
		startingCity = null;
		numCitiesCreated = 0;
		view.showAlert("Reset Complete", "Cities have been successfully reset");
	}
	
	/* Create a random set of cities given the number of cities to be created */
	public void createCities(int numCities) {
		resetCities(); //Reset the current set of cities before creating a new one
		for(int i = 0; i < numCities; i++) {
			Point2D location = new Point2D((double) rand.nextInt(Interface.MAP_WIDTH), 
					(double) rand.nextInt(Interface.MAP_HEIGHT)); //Generate a random location for the city
			addCity(location);
		}
		createConnections("", 0, 0);
		view.showAlert("Cities Created", "Finished creating " + numCities + " cities");
	}
	
	/* Create the connections between the cities. The connection type allows for cities to be fully connected,
	 * i.e. all cities are directly reachable from every other city, or limited connection.
	 * City connections may be limited by distance, or number, or both 
	*/
	public void createConnections(String connectionType, int distLimit, int numLimit) {
		List<String> cityIds = new ArrayList<>(cities.keySet());
		for(int i = 0; i < cityIds.size(); i++) {
			City city1 = cities.get(cityIds.get(i));
			for(int j = i + 1; j < cityIds.size(); j++) {
				City city2 = cities.get(cityIds.get(j));
				double cost = computeCost(city1, city2);
				city1.addCity(city2.getId(), cost);
				city2.addCity(city1.getId(), cost);
				Cost c = new Cost(city1.getName(), city2.getName(), String.valueOf(cost));
				costs.add(c);
			}
			city1.setReachableCitiesProperty();
		}
		setStartingCity();
		//view.showAlert("Connections Created", "Finished creating connections between cities");
	}
	
	public double getDistance(City city1, City city2) { 
		return city1.getLocation().distance(city2.getLocation());
	}
	
	/*Compute the cost of travelling from one city to another. This cost is a random number lower bounded
	 * by the Euclidean distance between the cities and upper bounded by the triple of that distance 
	*/
	private double computeCost(City city1, City city2) {
		double distance = getDistance(city1, city2);
		double maxCost = distance * 3.0;
		double cost = distance + (maxCost - distance) * rand.nextDouble();
		return cost;
	}
	
	/* Generate the neat optimal tour */
	public void createTour() {
		if(startingCity == null)
			setStartingCity();
		tours = new ArrayList<>(tc.createTour());
		view.showAlert("Tours Created", "The algorithm has finished computing the cheapest tour");
		view.setTourToggleDisable(false);
		view.drawTour(tours.get(1));
	}
	
	/* Randomly choose a city from which to start the tour */
	public void setStartingCity() {
		List<String> cityIds = new ArrayList<>(cities.keySet());
		String cityId = cityIds.get(rand.nextInt(cities.size()));
		startingCity = cityId;
	}
	
	/* Enumeration of the connection types */
	public static enum Connection {
		FULL ("Fully Connected"),
		LIMITED ("Limited Connection");
		
		private String type;
		
		Connection(String type) {
			this.type = type;
		}
		
		public String getType() { return type; }
	}
}
