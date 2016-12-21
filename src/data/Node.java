package data;

/**
 * 
 * The Node class represents a Pair of <Integer, Double>
 * It is used in several places: ex. the list of Neighbors, the list of recommendations
 * It has a compareTo method that allows for ordering of the nodes.
 *
 */
public class Node implements Comparable<Node>{
	int id;
	double value;
	// Constructor
	public Node(int i, double v){
		id = i; value = v;
	}
	// Compare method - orders the nodes in decreasing order by the value.
	public int compareTo(Node n) {
		double result = value - n.value;
		if (result < 0) {
			return 1;
		} else if (result == 0) {
			return 0;
		} else {
			return -1;
		}
	}
	public int getId() {return id;}
	public double getValue() {return value;}
}
