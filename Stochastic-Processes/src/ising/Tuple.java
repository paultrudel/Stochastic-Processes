package ising;

import java.util.Objects;

/* Utility class used to represent each site in the model as an ordered pair of (x, y) coordinates*/
public class Tuple<T, U> {

	private T x;
	private U y;
	
	public Tuple(T x, U y) {
		this.x = x;
		this.y = y;
	}
	
	public T getX() { return x; }
	public U getY() { return y; }
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if(obj == null || obj.getClass() != this.getClass())
			return false;
		Tuple<Integer, Integer> tuple = (Tuple<Integer, Integer>) obj;
		return ((tuple.x == this.x) && (tuple.y == this.y));
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}
}
