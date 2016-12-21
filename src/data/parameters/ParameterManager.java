package data.parameters;

/**
 *
 * Interface that manages the storing and retrieving of parameters from the blackboard
 */
public interface ParameterManager {
	public boolean containsParameter(String name);
	public String readParameter(String name);
	public void writeParameter(String name, String value);
}
