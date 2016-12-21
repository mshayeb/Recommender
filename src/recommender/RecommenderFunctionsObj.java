package recommender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import data.Node;
import utils.Utilities;
import data.basic.BasicElementManager;
import data.basic.Forum;
import data.basic.Stakeholder;
import data.matrix.*;

/**
 * 
 * This class contains the common functions used by the recommenders (content and collaborative).
 * The agents use these functions to produce the results.
 * 
 * In general, these functions will take their input from the blackboard.  A name parameter will indicate which matrix they need.
 * 
 */
abstract class RecommenderFunctionsObj implements RecommenderFunctions{
	// Local Variables
	BasicElementManager _elementManager; // Basic Element Manager
	MatrixManager _matrixManager;	// Matrix Manager

	// Constructor - Package Private
	RecommenderFunctionsObj(BasicElementManager elementManager, MatrixManager matrixManager) {
		// Integrity checks
		if (elementManager==null)
			throw new IllegalArgumentException("The element manager has to be a valid object");
		if (matrixManager==null)
			throw new IllegalArgumentException("The matrix manager has to be a valid object");
		
		// Sets the internal fields;
		_elementManager =  elementManager;
		_matrixManager = matrixManager;
	
	}
	
	public void cluster(String matrixName, String resultMatrixName) {
		// Calls the clustering API and clusters a matrix.
		//TODO There should also be a clustering method that allows for negative frequencies (for things like PCA, or centered matrices)
		
		MatrixElement matrix = _matrixManager.getMatrix(matrixName);
		Map<Integer, Map<Integer, Double>> input;	// Data structure that holds the input to the clustering algorithm
		Map<Integer, Set<Integer>> clusters;		// Data structure that holds the result of the clustering algorithm
		int rowsInput, colsInput;	// Number of rows and columns
		double[][] matrixInput; // Representation of the input matrix
		
		input = new HashMap<Integer, Map<Integer,Double>>();
		rowsInput = matrix.getNumRows();
		colsInput = matrix.getNumCols();
		matrixInput =  matrix.getArray();
		
		// Fills the data structure needed to call the clustering algorithm
		for (int i=0; i<rowsInput; i++){
			input.put(i, new HashMap<Integer, Double>());
			for (int j=0; j<colsInput; j++) {
				input.get(i).put(j, matrixInput[i][j]);
			}
		}
		
		// Call the clustering API
		// TODO Check why it is not working!
		System.out.println(input.toString());
		clusters = Utilities.getClutsters(input);
		
		// Creates a matrix with the results
		double[][] matrixOutput;
		int colsClusters = input.keySet().size();	// The number of columns corresponds to the number of clusters
		String[] rowData = new String[rowsInput];
		String[] colData = new String[colsClusters];
		matrixOutput = new double[rowsInput][colsClusters];
		int clusterId = 0;
		
		// Completes the matrix, here the rows will represent the same as the rows of the input, and the columns represent the clusters
		for (Integer j : clusters.keySet()) {
			colData[clusterId] = "Cluster_" + j.toString();
			for (Integer i : clusters.get(j)) {
				rowData[i] = matrix.getRowName(i);
				matrixOutput[i][j] = Utilities.getScoreOfDocumentInCluster(i, clusters.get(j), input);
			}
			clusterId++;
		}
		
		MatrixElement result = new MatrixElement(resultMatrixName, matrixOutput, colData, rowData);
		_matrixManager.storeMatrix(result);		
	}

	public void calculateStakeholdersAverageRating(String matrix, String resultMatrixAverages) {
		MatrixElement mat = _matrixManager.getMatrix(matrix);
		MatrixElement results = mat.averagesByRow(resultMatrixAverages);
		_matrixManager.storeMatrix(results);
	}

	public void calculateStakeholdersTotalRatings(String matrix, String resultMatrixTotals) {
		MatrixElement mat = _matrixManager.getMatrix(matrix);
		MatrixElement results = mat.totalsByRow(resultMatrixTotals);
		_matrixManager.storeMatrix(results);
	}
	
	public Map<Integer, List<Node>> getNeighbors(String similarityMatrixName, int numOfNeighbors) {
		// To calculate the neighbors we iterate over the similarity matrix and for each stakeholder record all the other similar
		// stakeholders in an list that will be ordered by the similarity score.
		
		MatrixElement similarityMatrix = _matrixManager.getMatrix(similarityMatrixName);
		
		Map<Integer, List<Node>> stakeholderNeighbors = new HashMap<Integer, List<Node>>();
		
		int numOfUsers = similarityMatrix.getNumRows(); 
		
		double[][] similarities = similarityMatrix.getArray();
		
		int i, j, k;
		for (i=0; i<numOfUsers; i++) {
			// Initializes the list of neighbors for this stakeholder
			stakeholderNeighbors.put(i, new ArrayList<Node>());
			for (j=0; j<numOfUsers; j++) {
				// If it is not himself and the similarity scores are different than zero, then we add this stakeholder to the set
				// Negative correlations will not be included - studies have shown that they are not effective.
				if ((i!=j) && (similarities[i][j]>0)) {
					stakeholderNeighbors.get(i).add(new Node(j, similarities[i][j]));
					
				}
			}
			
			// Orders the list of neighbors in descending order
			Collections.sort(stakeholderNeighbors.get(i));
			
			// Since the algorithm is only for K neighbors, we remove any additional neighbors
			int numToRemove = stakeholderNeighbors.get(i).size() - numOfNeighbors;
			if (numToRemove > 0) {
				for (k=0; k<numToRemove; k++) {
					// Removes numToRemove times the last neighbor
					stakeholderNeighbors.get(i).remove(stakeholderNeighbors.get(i).size()-1);
				}
			}
		}	
		return stakeholderNeighbors;
	}

	
	/********************
	 * Abstract Methods *
	 ********************/
	
	// The way that the stakeholders are added to the forums depends on whether the membership scores are binary or a range.
	public abstract void addStakeholdersToForums(NormalizationMethod method);
	
	// The computation of the similarities is also dependant on the type of membership scores.
	public abstract void calculateSimilarities(String matrixName, String resultMatrixName, String supportingMatrix, int penalize);
	
	// The generation of predictions is also dependant on the type of membership scores.
	public abstract double getPredictionScore(Stakeholder s, Forum f, PredictionFormula formula, String ratingsMatrixName, String supportingMatrixName, Map<Integer, List<Node>> neighbors);
	public abstract List<Node> getPredictionScores(Stakeholder s, PredictionFormula formula, String ratingsMatrixName, String supportingMatrixName, Map<Integer, List<Node>> neighbors);

	
	/***********************
	 * Un-finished Methods *
	 ***********************/
	
	public void generateForumsFromClusters(String clustersMatrixName) {
		//TODO Code this function
		/* Creates and adds the forums
		 * Adds the needs to the forums
		 * */
	}
	
	public void generateContentRecommendations() {
		// TODO Code this function
		/* Creates content recommendations based on the placement of the stakeholders in the forums.
		 */
	}
	
	public void generateCollaborativeRecommendations() {
		// TODO Code this function
		/* Creates collaborative recommendations based on the placement of the stakeholders in the forums.
		 */		
	}
}
