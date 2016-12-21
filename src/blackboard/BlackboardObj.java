package blackboard;

import java.util.HashMap;
import data.Element;


/**
 * 
 * Class that implements the Blackboard Architectural Pattern
 * It is basically a repository of 'Elements', which are indexed by name
 */
final class BlackboardObj implements Blackboard{
	// Local variables
	HashMap<String, Element> _elements;
	
	// Constructor - Package Private
	BlackboardObj() {
		// Creates the hashtable that holds all the elements
		_elements =  new HashMap<String, Element>();
	}
	
	public void store(String name, Element element){
		// Stores the element in the repository, if the element already exists it is over-written 
		_elements.put(name, element);		
	}
	public Element get(String name) {
		// Checks if the element is in the repository
		if (_elements.containsKey(name)) {
			return _elements.get(name);
		} else {
			throw new IllegalArgumentException("The element:" + name + " is not in the blackboard repository.");
		}
	}
	
	public boolean contains(String name) {
		// Checks if the element is in the repository
		if (_elements.containsKey(name)) {
			return true;
		} else {
			return false;
		}
		
	}
	
	public void remove(String name) {
		// Checks if the element is in the repository
		if (_elements.containsKey(name)) {
			_elements.remove(name);
		} else {
			throw new IllegalArgumentException("The element:" + name + " is not in the blackboard repository.");
		}
		
	}
	
}
