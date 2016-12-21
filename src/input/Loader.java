package input;

import java.io.IOException;

/**
 * 
 * Interface that is used to load the data.  
 * Several classes will implement it, and a static factory will choose - during run time - which one to use
 */
public interface Loader {
	public void loadAllElements() throws IOException;
	public void loadStakeholders() throws IOException;
	public void loadNeeds() throws IOException;
	public void loadRatings() throws IOException;
	public void loadForums() throws IOException;
	public void loadRecommendations() throws IOException;
	public void loadNeedsOfForums() throws IOException;
	public void loadStakeholdersOfForums() throws IOException;
}
