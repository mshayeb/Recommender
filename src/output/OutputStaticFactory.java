package output;

import data.basic.BasicElementManager;
import data.matrix.MatrixManager;
import data.parameters.ParameterManager;

/**
 *
 * Factory class that creates the output depending on the paramaters
 */
public class OutputStaticFactory {
	// A static class
	private OutputStaticFactory() {}
	
	public static Output getOutput(String nameOutput, ParameterManager parameters, BasicElementManager elementManager, MatrixManager matrixManager) throws Exception {
		if (nameOutput.equalsIgnoreCase("textOutput")) {
			return new OutputTextFileObj(parameters, elementManager, matrixManager);
		} else {
			throw new IllegalArgumentException("The specified output object can not be found.  Please check the argument name.");
		}
	}	
}
