package input;

import data.parameters.ParameterManager;
import data.basic.BasicElementManager;

/**
 * 
 * Factory class that creates the loader depending on the paramaters
 */
public class LoaderStaticFactory {
	// A static class
	private LoaderStaticFactory() {}
	
	public static Loader getLoader(String nameLoader, ParameterManager parameters, BasicElementManager elementManager) throws Exception {
		if (nameLoader.equals("textloader")) {
			return new LoaderTextFileObj(parameters, elementManager);
		} else {
			throw new IllegalArgumentException("The specified loader can not be found.  Please check the argument name.");
		}
	}
}
