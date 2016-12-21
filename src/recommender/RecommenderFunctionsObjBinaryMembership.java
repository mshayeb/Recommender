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
class RecommenderFunctionsObjBinaryMembership extends RecommenderFunctionsObj {

	// Constructor - Package Private
	RecommenderFunctionsObjBinaryMembership(BasicElementManager elementManager, MatrixManager matrixManager) {
		super(elementManager, matrixManager);
	}

	public void addStakeholdersToForums(NormalizationMethod method) {
		/* Multiplies the SxN * NxF matrix to get the SxF one.
		 * This matrix is then converted into a binary matrix
		 * 
		 * The only 'method' parameter accepted is the None.
		 */
		
		switch (method) {
		case Norm_Row:
		case Norm_Col:
		case Norm_MaxNum:
			throw new IllegalArgumentException("Since the recommender being used is for binary membership scores, the only acceptable 'method' parameter is 'None'."); 
		case None:
			break;
		}

		MatrixElement SxN = _matrixManager.getStakeholdersNeedsMatrix(true);
		MatrixElement NxF = _matrixManager.getNeedsForumsMatrix(true);
		MatrixElement SxF = SxN.times(NxF, "SxF");
		
		// The matrix is converted to binary
		// TODO The threshold to convert a matrix to binary should be a parameter stored in the blackboard
		MatrixElement SxF_Bin = SxF.convertToBinary(0.00001, "SxF_bin");
		
		double[][] mat = SxF_Bin.getArray();
		int rows = SxF_Bin.getNumRows();
		int cols = SxF_Bin.getNumCols();
		
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
	
	public void calculateSimilarities(String matrixName, String resultMatrixName, String supportingMatrix, int penalize) {
		// Calculates the pairwise similarity between the users who have binary profiles.
		// PRECONDITION: matrixName has to be binary
		// This is done by using the L2 Normalization formula (cosine for binary vectors).
		// This formula is: (|intersect(S1,S2)|) / sqrt(|S1| * |S2|)
		// Similarities range between 1.0 (perfect agreement - the two sets are the same) and 0.0 (perfect disagreement - the two sets have nothing in common).
		// For this method the supportingMatrix is the matrix of the totals per user
		// Parameter penalize is not used.
		
		MatrixElement matrix = _matrixManager.getMatrix(matrixName);
		
		// Number of users and of the properties being examined to compute the similarities
		int numOfProperties = matrix.getNumCols();
		int numOfUsers = matrix.getNumRows(); 
		
		// The resulting matrix is a square matrix of size numOfUsers x numOfUsers
		double[][] similarities = new double[numOfUsers][numOfUsers]; 

		// Totals per user (per row)
		MatrixElement totalsPerUser = _matrixManager.getMatrix(supportingMatrix);
		double[][] totals = totalsPerUser.getArray();
		
		// String Ids of the users
		String[] userNames = new String[numOfUsers];
		
		// Ratings that server as input
		double[][] ratings = matrix.getArray();
		
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
				numCorratedItems = 0;
				
				for (int n=0; n<numOfProperties; n++) {
					ratingUserIItemN = ratings[i][n];
					ratingUserJItemN =ratings[j][n];
					
					if ((ratingUserIItemN != 0) && (ratingUserJItemN != 0)) {
						numCorratedItems++;
					}
				}
				// If any of the users have no ratings (his total=0) then the formula for the similarity is undefined and hence the similarity will be 0
				if ((totals[i][0]==0) || (totals[j][0]==0)) {
					similarityIJ = 0;
				} else {
					similarityIJ = numCorratedItems / (Math.sqrt(totals[i][0] * totals[j][0]));
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
		// Only the binary prediction formulas are supported for the 'formula' paramenter
		// The supportingMatrixName is not used 

		switch (formula) {
		case Typical:
			throw new IllegalArgumentException("Since the recommender being used is for binary membership scores, the only acceptable 'method' parameter is 'None'."); 
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
				
		double sumRatings = 0.0;
		double sumWeightedRating = 0.0;
		double sumOfSimilaries = 0.0;
		int numOfNeighbors = neighbors.get(stakeholderNum).size();
		int numOfNeighborsThatRatedItem = 0;
		
		// Iterates over all the neighbors of this user
		for (Node j : neighbors.get(stakeholderNum)) {
			sumRatings += ratings[j.getId()][forumNum];								// Adds a 1 if the stakeholder rated the item (stakeholder is in the forum)
			sumWeightedRating += (ratings[j.getId()][forumNum] * j.getValue());		// Rating of the stakeholder X the similarity
			sumOfSimilaries += j.getValue();										// Acumulation of the stakeholder similarities
			
			// if the neighbor has rated this item (is in the forum) then we add it to the numOfNeighborsThatRatedItem 
			if (ratings[j.getId()][forumNum] != 0) {
				numOfNeighborsThatRatedItem++;
			}
		}
		
		switch (formula) {
		case Bin_I:
			predictionScore = (numOfNeighbors==0) ? 0.0 : (sumRatings/numOfNeighbors); 
			break;
		case Bin_II:
			predictionScore = (sumOfSimilaries==0) ? 0.0 : (sumWeightedRating/sumOfSimilaries); 
			break;
		case Bin_III:
			predictionScore = (numOfNeighbors==0) ? 0.0 : (sumWeightedRating/numOfNeighbors); 
			break;
		case Bin_IV:
			predictionScore = (numOfNeighborsThatRatedItem==0) ? 0.0 : (sumWeightedRating/numOfNeighborsThatRatedItem); 
			break;
		}
		return predictionScore;
	}

	public List<Node> getPredictionScores(Stakeholder s, PredictionFormula formula, String ratingsMatrixName, String supportingMatrixName, Map<Integer, List<Node>> neighbors) {
		// Calculates the prediction score for all the forums particular for a particular stakeholder
		// Only the binary prediction formulas are supported for the 'formula' paramenter
		// The supportingMatrixName is not used 

		switch (formula) {
		case Typical:
			throw new IllegalArgumentException("Since the recommender being used is for binary membership scores, the only acceptable 'formula' parameter is 'Typical'."); 
		default :
			break;
		}		
		
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
				
				// Gets the prediction score
				predictionValue = getPredictionScore(s, _elementManager.getForum(f), formula, ratingsMatrixName, supportingMatrixName, neighbors);
								
				// Adds the result to the data structure
				stakeholderItemPredictions.add(new Node(f, predictionValue));					
				
			}
		}
		// Orders all of the predictions by the calculated prediction value
		Collections.sort(stakeholderItemPredictions);
		
		return stakeholderItemPredictions;
	}
	
}
