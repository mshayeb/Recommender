package recommender;

import java.util.List;
import java.util.Map;
import data.Node;
import data.basic.*;

/**
 * 
 * This interface provides the set of functions required for the recommender system, 
 * such as clustering, calculating similarities, calculating neighbors, etc.
 *
 * Some of these functions build on the output of the previous ones, it is the responsibility of the user to call them in the appropriate order and pass the correct parameters.
 * 
 * In general, most these functions will take their input from the blackboard.  A name parameter will indicate which matrix they need.
 */
public interface RecommenderFunctions {
	public void cluster(String matrixName, String resultMatrixName);
	public void calculateStakeholdersAverageRating(String matrix, String resultMatrixAverages);
	public void calculateStakeholdersTotalRatings(String matrix, String resultMatrixTotals);	
	public Map<Integer, List<Node>> getNeighbors(String similarityMatrixName, int numOfNeighbors);

	
	//Abstract Functions that depend on the kind of membership scores that are being used (ranges or binary)
	public void addStakeholdersToForums(NormalizationMethod method);
	public void calculateSimilarities(String matrixName, String resultMatrixName, String supportingMatrix, int penalize);
	public double getPredictionScore(Stakeholder s, Forum f, PredictionFormula formula, String ratingsMatrixName, String supportingMatrixName, Map<Integer, List<Node>> neighbors);
	public List<Node> getPredictionScores(Stakeholder s, PredictionFormula formula, String ratingsMatrixName, String supportingMatrixName, Map<Integer, List<Node>> neighbors);
	
	// Functions that are not yet coded
	public void generateForumsFromClusters(String clustersMatrixName);	
	public void generateContentRecommendations();
	public void generateCollaborativeRecommendations();
	
	public enum NormalizationMethod{
		None,
		Norm_Row,
		Norm_Col,
		Norm_MaxNum
	}
	
	public enum PredictionFormula{
		Typical,
		Bin_I,
		Bin_II,
		Bin_III,
		Bin_IV
	}
}


