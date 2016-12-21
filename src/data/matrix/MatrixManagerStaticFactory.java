package data.matrix;

import blackboard.Blackboard;
import data.basic.BasicElementManager;

/**
 *
 * Static Factory that creates the Matrix Manager
 */
public class MatrixManagerStaticFactory {
	// A static class
	private MatrixManagerStaticFactory() {}
	
	public static MatrixManager newMatrixManager(String id, Blackboard blackboard, BasicElementManager elementManager) {
		return new MatrixManagerObj(id, blackboard, elementManager);
	}	
}
