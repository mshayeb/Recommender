package data.basic;

import blackboard.Blackboard;

/**
 * 
 * Static Factory that creates the Basic Element Manager
 *
 */
public class BasicElementStaticFactory {
	// A static class
	private BasicElementStaticFactory() {}
	
	public static BasicElementManager newBasicElementManager(String id, Blackboard blackboard) {
		return new BasicElementManagerObj(id, blackboard);
	}	
	
}

