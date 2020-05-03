package queueing;

/* Class which represents a customer's copy request */
public class CopyJob {

	private static CopyType priority; //The type of copies that have priority
	
	/* Enumeration of the different types of copies that can be made */
	public static enum CopyType {
		LOW,
		HIGH;
	}
	
	private int numCopies; //Number of copies the customer wants to make
	private CopyType copyType; //The type of copy the customer wants to make
	private double priorityValue; //The priority of this copy request in the queue
	
	public CopyJob(int numCopies, int copyType) {
		this.numCopies = numCopies;
		this.copyType = CopyType.values()[copyType];
		setPriority();
	}
	
	public static void setPriority(CopyType p) { priority = p; }
	
	/* Compute the priority value of a request. The priority value is determined by the type of copy the customer 
	 * wants to make and how many copies they want to make
	*/
	private void setPriority() {
		if(copyType == priority)
			priorityValue = 10000 / numCopies;
		else
			priorityValue = 100 / numCopies;
	}
	
	public int getNumCopies() { return numCopies; }
	public CopyType getCopyType() { return copyType; }
	public double getPriorityValue() { return priorityValue; }
}
