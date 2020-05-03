package searcher1;

public class Pair<T1, T2> {
	public T1 first;
	public T2 second;
	
	public boolean equals(Object o) {
		return (o != null) &&
				(o instanceof Pair) &&
				(((Pair<?, ?>)o).first.equals(this.first)) &&
				(((Pair<?, ?>)o).second.equals(this.second));
	}
	
	public int hashCode() {
		return this.first.hashCode() * this.second.hashCode();
	}
	
	public Pair(T1 first, T2 second) {
		this.first = first;
		this.second = second;
	}
}
