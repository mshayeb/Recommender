package recommender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import data.Node;
import data.basic.BasicElementManager;
import data.basic.Forum;
import data.basic.Stakeholder;
import data.matrix.MatrixElement;
import data.matrix.MatrixManager;

/**
 * 
 * This class contains the functions that are specific for the Binary Membership cases.
 * The rest of the functions are implmented by the parent class.
 * 
 */
class RecommenderFunctionsObjRangeMemberships extends RecommenderFunctionsObj {

	// Constructor - Package Private
	RecommenderFunctionsObjRangeMemberships(BasicElementManager elementManager, MatrixManager matrixManager) {
		super(elementManager, matrixManager);
	}	
	
	public void addStakeholdersToForums(NormalizationMethod method) {
		/* Multiplies the SxN * NxF matrix to get the SxF one.
		 * This matrix can the be processed - normalized, or mean centered in several ways
		 * And then the Stakholders are added into the Forums
		 */
		MatrixElement SxN = _matrixManager.getStakeholdersNeedsMatrix(true);
		MatrixElement NxF = _matrixManager.getNeedsForumsMatrix(true);
		MatrixElement SxF = SxN.times(NxF, "SxF");
		
		// At this point the SxF matrix has 'ratings' that were created by the matrix multiplication
		// These ratings are not normalized, so it may be useful to normalize them either by: 
		// * Column: What % of the forum the stakeholder owns
		// * Row:  What % of the stakeholder's interest is in the forum
		// * Max number: reduces to 1 the maximum number in the matrix, and everything else is a fraction of that
		// TODO decide on the appropriate normalization for the SxF matrix
		
		MatrixElement SxF_Norm = SxF;  // This assignment is unnecesary - as it will be replaced, however it is needed for the compilation
		switch(method) {
		case Norm_Row:
			// Normalization by Row
			MatrixElement rowTotals = SxF.totalsByRow("Totals_Row");
			SxF_Norm = SxF.normalizeByRows(rowTotals, "SxF_Norm");			
			break;
		case Norm_Col:
			// Normalization by Column
			MatrixElement colTotals = SxF.totalsByColumn("Totals_Column");
			SxF_Norm = SxF.normalizeByColumns(colTotals, "SxF_Norm");
			break;
		case Norm_MaxNum:
			// Normalization by Number
			Double normNumber = SxF.maxNumber();
			SxF_Norm = SxF.normalizeByNumber(normNumber, "SxF_Norm");
			break;
		case None:
			// No normalization is done
			break;
		}
		
		double[][] mat = SxF_Norm.getArray();
		int rows = SxF_Norm.getNumRows();
		int cols = SxF_Norm.getNumCols();
		
		for (int i=0; i<rows; i++) {
			Stakeholder s = _elementManager.getStakeholder(i);
			for (int j=0; j<cols; j++) {
				if (mat[i][j]!=0) {
					Forum f = _elementManager.getForum(j);
					_elementManager.addStakeholderToForum(f.getId(), s.getId(), mat[i][j]);
				}
			}
		}
	}	
	
	public void calculateSimilarities(String matrixName, String resultMatrixName, String supportingMatrix, int penalize){
		// Calculates the pairwise similarity between the users.
		// This is done by using the Pearson Correlation formula, which is calculated by comparing ratings for all items that were rated by both users.
		// Similarities range between 1.0 (perfect agreement) and -1.0 (perfect disagreement).
		// For this method the supportingMatrix is the matrix of the averages per user
		// Parameter penalize is not used.
		
		MatrixElement matrix = _matrixManager.getMatrix(matrixName);
		
		// Number of users and of the properties being examined to compute the similarities
		int numOfProperties = matrix.getNumCols();
		int numOfUsers = matrix.getNumRows(); 
		
		// The resulting matrix is a square matrix of size numOfUsers x numOfUsers
		double[][] similarities = new double[numOfUsers][numOfUsers]; 

		// Averages per user
		MatrixElement averagesPerUser = _matrixManager.getMatrix(supportingMatrix);
		double[][] averages = averagesPerUser.getArray();
		
		// String Ids of the users
		String[] userNames = new String[numOfUsers];
		
		// Ratings that server as input
		double[][] ratings = matrix.getArray();
		
		double covarianceUsers;
		double varianceUserI;
		double varianceUserJ;

		double ratingUserIItemN;
		double ratingUserJItemN;
		double similarityIJ;
		
		int numCorratedItems;
		
		// Initializes the first value of the similarity matrix
		similarities[0][0] = 1;
		
		// Iterates over all the stakeholders, it keeps two stakeholders' references to compare i and j
		// Note that since the similarity matriz is symmetrical, we can safely start j at one more than i on every iteration
		for (int i=0; i<numOfUsers-1; i++) {
			userNames[i]=matrix.getRowName(i);	// Stores the names for the resulting matrix
			for (int j=i+1; j<numOfUsers; j++) {
				covarianceUsers = 0;
				varianceUserI = 0;
				varianceUserJ = 0;
				numCorratedItems = 0;
				
				for (int n=0; n<numOfProperties; n++) {
					ratingUserIItemN = ratings[i][n];
					ratingUserJItemN =ratings[j][n];
					
					if ((ratingUserIItemN != 0) && (ratingUserJItemN != 0)) {
						numCorratedItems++;
						covarianceUsers += (ratingUserIItemN - averages[i][0]) * (ratingUserJItemN - averages[j][0]);
						varianceUserI += Math.pow((ratingUserIItemN - averages[i][0]), 2);
						varianceUserJ += Math.pow((ratingUserJItemN - averages[j][0]), 2);
					}
				}
				// If any of the standard deviations is equal to 0 then the formula for the correlation is undefined and hence the similarity will be 0
				if ((varianceUserI==0) || (varianceUserJ==0)) {
					similarityIJ = 0;
				} else {
					similarityIJ = covarianceUsers / (Math.sqrt(varianceUserI) * Math.sqrt(varianceUserJ));
					// If the number of corrated items between these two users is small then we cannot be very confident about the score, and as such we need to penalize the computed similarity
					similarityIJ = similarityIJ * (Math.min((double)numCorratedItems, (double)penalize) / (double)penalize );
				}
				
				// Records the similarities in the matrix - since it is symmetric then it is recorded in both i,j and j,i
				similarities[i][j] = similarityIJ;
				similarities[j][i] = similarityIJ;
				similarities[j][j] = 1;
			}
		}
		userNames[numOfUsers-1]=matrix.getRowName(numOfUsers-1);	// The last name needs to be stored, as it was excluded from the cycle.
			
		// The Matrix with the similarities gets stored
		MatrixElement result = new MatrixElement(resultMatrixName, similarities, userNames, userNames);	
		_matrixManager.storeMatrix(result);
	}
	
	public double getPredictionScore(Stakeholder s, Forum f, PredictionFormula formula, String ratingsMatrixName, String supportingMatrixName, Map<Integer, List<Node>> neighbors) {
		// Calculates the prediction score of a particular forum for a particular stakeholder
		// TODO Determine if prediction score formula should be modified so that average scores are calculated only on co-rated items
		// The binary prediction formulas are not supported for the 'formula' paramenter
		// The supportingMatrixName is for the matrix with all the average ratings of the stakeholders

		switch (formula) {
		case Bin_I:
		case Bin_II:
		case Bin_III:
		case Bin_IV:
			throw new IllegalArgumentException("Since the recommender being used is for range membership scores, the binary prediction formulas are not allowed for the paramter 'formula'"); 
		default :
			break;
		}				
		double predictionScore = 0.0;
		
		// Gets the id of the stakeholder and the forum
		int stakeholderNum = s.getNumber();
		int forumNum = f.getNumber();
		
		// Gets the ratings matrix
		MatrixElement ratingMatrix = _matrixManager.getMatrix(ratingsMatrixName);
		double[][] ratings = ratingMatrix.getArray();
		
		// Gets the matrix with the average ratings of the stakeholders
		MatrixElement averagesMatrix = _matrixManager.getMatrix(supportingMatrixName);
		double[][] averages = averagesMatrix.getArray();
		
		double weightedRating;
		double sumOfSimilaries;
		
		weightedRating = 0;
		sumOfSimilaries = 0;
		// Iterates over all the neighbors of this user
		for (Node j : neighbors.get(stakeholderNum)) {
			// Only if the neighbor has rated this item do we include him in the calculation of the prediction value.
			if (ratings[j.getId()][forumNum] != 0) {
				// Similarity of the stakeholders (j.value) x (the rating of the neighbor - the average rating of the neighbor)
				weightedRating += j.getValue() * (ratings[j.getId()][forumNum] - averages[j.getId()][0]);
				sumOfSimilaries += j.getValue();
			}
		}
		
		// Calculates the input of the neighbors:
		double neighborsInput;
		if (sumOfSimilaries==0) {
			neighborsInput = 0.0;
		} else {
			neighborsInput = weightedRating / sumOfSimilaries;
		}
		
		// Calculates the prediction score:
		predictionScore = averages[stakeholderNum][0] + neighborsInput;
		
		return predictionScore;
	}

	public List<Node> getPredictionScores(Stakeholder s, PredictionFormula formula, String ratingsMatrixName, String supportingMatrixName, Map<Integer, List<Node>> neighbors) {
		// Resulting data structure
		List<Node> stakeholderItemPredictions;

		// Gets the id of the stakeholder 
		int stakeholderNum = s.getNumber();
		int f; // Used to range over forums
		
		MatrixElement ratingMatrix = _matrixManager.getMatrix(ratingsMatrixName);
		double[][] ratings = ratingMatrix.getArray();		
		int numOfForums = ratingMatrix.getNumCols(); 
		
		double predictionValue;
		
		// Initializes the list of predictions for this stakeholder
		stakeholderItemPredictions = new ArrayList<Node>();
		
		// For all the forums
		for (f=0; f<numOfForums; f++) {
			// Checks that the stakeholder hasn't rated that forum yet
			if (ratings[stakeholderNum][f] == 0) {
				
				predictionValue = getPredictionScore(s, _elementManager.getForum(f), formula, ratingsMatrixName, supportingMatrixName, neighbors);
								
				// TODO Determine which prediction scores to include!
				
				/* OPTION 1:
				 * Do not include:  
				 *   Prediction scores that are lower than the average (or negative) (ie when the neighborsInput is negative)
				 *   Prediction scores that are equal to the average (ie when the neighborsInput is 0 - no neighbors) 
				 * This can be simplified to asking if the neighborsInput is > 0
				 *
				if (neighborsInput > 0) {
					// Records it in the list
					_stakeholderItemPredictions.add(new Node(f, predictionValue));					
				}
				*/
				
				/* OPTION 2: 
				 * Include all the items whatever their prediction scores are
				 */
				stakeholderItemPredictions.add(new Node(f, predictionValue));					
				
			}
		}
		// Orders all of the predictions by the calculated prediction value
		Collections.sort(stakeholderItemPredictions);
		
		return stakeholderItemPredictions;
	}
	
	
}
