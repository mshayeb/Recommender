package recommender;

import data.basic.BasicElementManager;
import data.matrix.MatrixManager;

/**
 *
 * Factory class that creates the recommender function object 
 */
public class RecommenderFunctionsStaticFactory {
	// A static class
	private RecommenderFunctionsStaticFactory() {}
	
	public static RecommenderFunctions getRecommenderFunctionsForRangeMemberships(BasicElementManager elementManager, MatrixManager matrixManager) {
		return new RecommenderFunctionsObjRangeMemberships(elementManager, matrixManager);
	}

	public static RecommenderFunctions getRecommenderFunctionsForBinaryMemberships(BasicElementManager elementManager, MatrixManager matrixManager) {
		return new RecommenderFunctionsObjBinaryMembership(elementManager, matrixManager);
	}

	
}
