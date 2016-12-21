package data.parameters;

import blackboard.Blackboard;

/**
 *
 * Static Factory that creates the Matrix Manager
 */
public class ParameterManagerStaticFactory {
	// A static class
	private ParameterManagerStaticFactory() {}
	
	public static ParameterManager newParameterManager(String id, Blackboard blackboard) {
		return new ParameterManagerObj(id, blackboard);
	}	
}
