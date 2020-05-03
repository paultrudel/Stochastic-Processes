package queueing;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;

import queueing.CopyJob.CopyType;
import view.Interface;

/* Implementation of a single server queue */
public class MM1Queue {

	/* Enumeration of the possible types of queues*/
	public static enum QueueType {
		FCFS("First come first serve"),
		PriorityLow("Priority given to low quality copies"),
		PriorityHigh("Priority given to high quality copies");
		
		private String type;
		
		private QueueType(String type) {
			this.type = type;
		}
		
		public String getType() { return type; }
	}

	private double maxTime = 100; //Maximum time the queue should run for
	private double arrivalRate = 2; //Rate of arrivals into the queue
	private Map<Double, Integer> totalArrivals; //Map relating a specific time to the total number of arrivals
	private double[] serviceRates = {4, 2}; //Rates of service for the two types of copies that can be made
	private int numServers = 1; //Number of servers in the queue
	private List<CopyJob> inService; //List of the customers which are currently in service
	private Map<Double, Integer> inSystem; //Map relating a specific time to the number of people in the queue
	private int maxCopiesAllowed = 1; //Maximum number of copies a customer is allowed to make
	private Queue<CopyJob> copyJobs; //Queue containing all of the customers in the system
	private QueueType queueType = QueueType.FCFS; //The type of the queue
	private double averageInSystem; //Average number of customers in the system
	private double averageSojournTime; //Average sojourn time for customers in the system
	private Interface view;
	private Random rand;
	
	public MM1Queue(Interface view) {
		this.view = view;
		rand = new Random();
	}
	
	public void setMaxTime(double maxTime) {
		this.maxTime = maxTime;
		view.showAlert("Maximum Time Set", "The maximum length of time for the queue to run has been set to: " +
				maxTime);
	}
	
	public void setRates(double arrivalRate, double[] serviceRates) {
		this.arrivalRate = arrivalRate;
		this.serviceRates = serviceRates;
		StringBuilder alertContent = new StringBuilder();
		alertContent.append("Rates have been set to:\n");
		alertContent.append("Arrival Rate: " + arrivalRate + "\n");
		alertContent.append("Service Rates: ");
		for(double serviceRate: serviceRates)
			alertContent.append(serviceRate + " ");
		view.showAlert("Rates Set", alertContent.toString());
	}
	
	public void setMaxCopiesAllowed(int maxCopiesAllowed) {
		this.maxCopiesAllowed = maxCopiesAllowed;
		view.showAlert("Max Copies Set", "the maximum number of copies allowed has been set to: " 
				+ maxCopiesAllowed);
	}
	
	public void setNumServers(int numServers) {
		this.numServers = numServers;
		view.showAlert("Number of Servers Set", "The number of servers has been set to: " + numServers);
	}
	
	public void setQueueType(String type) {
		if(type.equals(QueueType.FCFS.getType()))
			queueType = QueueType.FCFS;
		else if(type.equals(QueueType.PriorityLow.getType()))
			queueType = QueueType.PriorityLow;
		else
			queueType = QueueType.PriorityHigh;
		view.showAlert("Queue Type Set", "The type of queue has been set to: " + type);
	}
	
	public double getMaxTime() { return maxTime; }
	public int getNumServers() { return numServers; }
	public int getMaxCopiesAllowed() { return maxCopiesAllowed; }
	public double getArrivalRate() { return arrivalRate; }
	public double[] getServiceRates() { return serviceRates; }
	public Map<Double, Integer> getTotalArrivals() { return totalArrivals; }
	public Map<Double, Integer> getInSystem() { return inSystem; }
	public double getAverageInSystem() { return averageInSystem; }
	public double getAverageSojournTime() { return averageSojournTime; }
	
	/* Return the (time->number of customers in the system) as a Time representation */
	public List<Time> getTimes() {
		List<Time> times = new ArrayList<Time>();
		for(Map.Entry<Double, Integer> entry: inSystem.entrySet()) {
			Time time = new Time(entry.getKey(), entry.getValue());
			times.add(time);
		}
		return times;
	}
	
	/* Simulate the running of the queue */
	public void simulate() {
		createQueue(); //Initialize the queue
		totalArrivals = new LinkedHashMap<>();
		int numArrivals = 0;
		inSystem = new LinkedHashMap<>();
		inSystem.put(0.0, 0); //We are simulating a Poisson process so N(0)=0
		inService = new ArrayList<>();
		double t = 0;
		//Continue the simulation as long as the elapsed time is less than the maximum time the queue can run
		while(t < maxTime) {
			//If there are no customers in the queue
			if(copyJobs.size() == 0) {
				//Check if the elapsed time is greater than 30 if it is stop the simulation
				if(t > 30)
					break;
				t = t + randomExponential(arrivalRate); //Time of the next arrival
				CopyJob copyJob = createCopyJob(); //Create a random copy request
				copyJobs.add(copyJob); //Add the customer to the system
				inService.add(copyJob); //The customer automatically goes into service
				inSystem.put(t, copyJobs.size()); //Add an entry for the number of customers in the system at time t
				numArrivals++; //Increment the number of arrivals
				totalArrivals.put(t, numArrivals); //Add an entry for the total arrivals at time t
			}
			//If there are customers in the queue
			else {
				//Compute the service time for the current customers in service
				double currServiceTime = computeCurrentServiceTime();
				//Next event is determined by a competition between two exponential random variables
				t = t + randomExponential(arrivalRate + currServiceTime);
				double u = rand.nextDouble(); //Generate a uniformly random number from (0, 1)
				//If the time until the next arrival is less than the time until the next service completion
				if(u < (arrivalRate / (arrivalRate + currServiceTime))) {
					CopyJob copyJob = createCopyJob(); //Create a random copy request
					copyJobs.add(copyJob); //Add the customer to the system
					numArrivals++; //Increment the number of arrivals
					totalArrivals.put(t, numArrivals); //Add an entry for the total arrivals at time t
					inSystem.put(t, copyJobs.size()); //Add an entry for the number of customers in the system at t
					//If there is an available server
					if(inService.size() < numServers)
						inService.add(copyJob); //Customer that arrived goes into service
				}
				//Time until next service completion is less than time until next arrival
				else {
					CopyJob copyJob = copyJobs.poll(); //Customer at the front of the queue completes service
					inService.remove(copyJob); //Customer removed from being in service
					copyJob = copyJobs.peek(); //Get the next customer in the queue
					inService.add(copyJob); //Next customer in queue goes into service
					inSystem.put(t, copyJobs.size()); //Add entry for the number of customers in the system at time t
				}
			}
		}
		computeAverageInSystem(); //Compute the average number of customers in the system
		computeAverageSojournTime(); //Compute the average sojourn time for the customers
		view.showAlert("Simiulation Finished", "Simiulation of the queue has finished");
	}
	
	/* Computes the average number of customers in the system */
	private void computeAverageInSystem() {
		averageInSystem = 0;
		for(Integer numInSystem: inSystem.values())
			averageInSystem += numInSystem.intValue();
		averageInSystem = averageInSystem / inSystem.size();
	}
	
	/* Computes the average sojourn time for the customers*/
	private void computeAverageSojournTime() {
		averageSojournTime = 0;
		double maxTime = Collections.max(totalArrivals.keySet());
		for(Integer numInSystem: inSystem.values())
			averageSojournTime += numInSystem.intValue();
		averageSojournTime = averageSojournTime / totalArrivals.get(maxTime);
	}
	
	/* Computes the service rate for the customers that are currently in service*/
	private double computeCurrentServiceTime() {
		double currServiceTime = 0;
		for(CopyJob copyJob: inService) {
			if(copyJob == null)
				continue;
			int numCopies = copyJob.getNumCopies();
			if(copyJob.getCopyType() == CopyType.LOW)
				currServiceTime += serviceRates[0] * numCopies;
			else
				currServiceTime += serviceRates[1] * numCopies;
		}
		return currServiceTime;
	}
	
	/* Generate a random number from an exponential distribution */
	private double randomExponential(double rate) {
		double u = rand.nextDouble();
		double e = Math.log(1 - u) / (-1.0 * (1 / rate));
		return e;
	}
	
	/* Creates a random copy request */
	private CopyJob createCopyJob() {
		int numCopies = rand.nextInt(maxCopiesAllowed) + 1;
		int copyType;
		if(serviceRates[0] == 0)
			copyType = 1;
		else if(serviceRates[1] == 0)
			copyType = 0;
		else
			copyType = rand.nextInt(CopyJob.CopyType.values().length);
		CopyJob copyJob = new CopyJob(numCopies, copyType);
		return copyJob;
	}
	
	/* Initializes the queue based on the queue type */
	private void createQueue() {
		switch(queueType) {
			case FCFS:
				copyJobs = new ArrayDeque<CopyJob>();
				break;
			case PriorityLow:
				copyJobs = new PriorityQueue<CopyJob>(10, new CopyJobComparator());
				CopyJob.setPriority(CopyType.LOW);
				break;
			case PriorityHigh:
				copyJobs = new PriorityQueue<CopyJob>(10, new CopyJobComparator());
				CopyJob.setPriority(CopyType.HIGH);
			default:
				copyJobs = new ArrayDeque<CopyJob>();
				break;
		}
	}
}
