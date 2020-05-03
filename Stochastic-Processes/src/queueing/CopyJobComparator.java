package queueing;

import java.util.Comparator;

/* Class which allows copy requests to compared with one another based on their priority value */
public class CopyJobComparator implements Comparator<CopyJob> {

	@Override
	public int compare(CopyJob cj1, CopyJob cj2) {
		if(cj1.getPriorityValue() < cj2.getPriorityValue())
			return 1;
		else if(cj1.getPriorityValue() > cj2.getPriorityValue())
			return -1;
		return 0;
	}

}
