package data.matrix;

/**
 * 
 * Interface to the Matrix Manager.
 * This interface provides an abstraction layer to the blackboard so that it is easy to generate and get 
 * different matrix representations of the relationships between the basic elements
 * It also provides the functionality to store and retrieve a matrix by its name - this is useful for user defined intermediary matrices
 *
 */
public interface MatrixManager {
	public String getId();
	
	public MatrixElement getStakeholdersNeedsMatrix(boolean ForceRefresh);
	public MatrixElement getStakeholdersForumsMatrix(boolean ForceRefresh);
	public MatrixElement getStakeholdersTermsMatrix(boolean ForceRefresh);
	public MatrixElement getNeedsTermsMatrix(boolean ForceRefresh);
	public MatrixElement getNeedsForumsMatrix(boolean ForceRefresh);
	
	public void storeMatrix(MatrixElement matrix);
	public MatrixElement getMatrix(String name);
	public boolean containsMatrix(String name);
	public void removeMatrix(String name);
	
}
