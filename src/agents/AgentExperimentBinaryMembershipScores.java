package agents;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import input.Loader;
import input.LoaderStaticFactory;
import output.Output;
import output.OutputStaticFactory;
import recommender.RecommenderFunctions;
import recommender.RecommenderFunctionsStaticFactory;
import recommender.RecommenderFunctions.NormalizationMethod;
import recommender.RecommenderFunctions.PredictionFormula;
import blackboard.Blackboard;
import data.Node;
import data.basic.BasicElementManager;
import data.basic.BasicElementStaticFactory;
import data.basic.Forum;
import data.basic.Stakeholder;
import data.matrix.MatrixManager;
import data.matrix.MatrixManagerStaticFactory;
import data.parameters.ParameterManager;
import data.parameters.ParameterManagerStaticFactory;
import data.matrix.MatrixElement;

/**
 * 
 * This experiment tests what happens when the SxF matrix is reduced to a binary matrix.
 * The idea behind this is that by reducing the matrix to a binay one, we eliminate the uncertainty produced by the matrix multiplication 
 * of SxN X NxF.  This way we don't make any assumptions as to whether or not the stakeholder likes or dislikes the forum.
 * 
 *
 */

public class AgentExperimentBinaryMembershipScores implements Agent {

	// Local Variables
	Blackboard blackboard;
	BasicElementManager basicElements;
	MatrixManager matrices;
	ParameterManager parameters;
	Loader loader;
	Output output;
	RecommenderFunctions recommender;
	String message;
	
	// Constructor - Package Private
	AgentExperimentBinaryMembershipScores(Blackboard bb) {
		// Integrity checks
		if (bb==null)
			throw new IllegalArgumentException("The blackboard reference has to be a valid object");
		
		// Sets the internal fields;
		blackboard = bb;
	}	
		
	public void run() throws Exception {
		// ******************************************************************************
		// This is where the variable dataset is configured
		// ******************************************************************************
		parameters = ParameterManagerStaticFactory.newParameterManager("parameters", blackboard);
		parameters.writeParameter("inputdirectory", "C:\\eclipse\\workspace\\RecommenderPrototype\\input\\student");
		parameters.writeParameter("outputdirectory", "C:\\eclipse\\workspace\\RecommenderPrototype\\output\\student_exp");
		parameters.writeParameter("numberofneighbors","10");
		message = "Dataset being used: Student";
		
		// Run the experiments
		testPredictionForumlas();
//		testSimilaritiesAndNeighbors();		
//		testCalculations();

	}
	
	private enum TypeOfRecommender {
		Range,
		Binary
	}
	
	private void setup(TypeOfRecommender type) throws Exception {
		// Set up
		basicElements = BasicElementStaticFactory.newBasicElementManager("elements", blackboard);		
		matrices = MatrixManagerStaticFactory.newMatrixManager("matrix", blackboard, basicElements);
		if (type== TypeOfRecommender.Range) {
			recommender = RecommenderFunctionsStaticFactory.getRecommenderFunctionsForRangeMemberships(basicElements, matrices);
		} else {
			recommender = RecommenderFunctionsStaticFactory.getRecommenderFunctionsForBinaryMemberships(basicElements, matrices);
		}

		
		output = OutputStaticFactory.getOutput("textOutput", parameters, basicElements, matrices);
		output.writeToLog("Executing Agent: AgentExperimentBinaryMembershipScores");
		output.writeToLog(message);
		
		// Loads the data 
		output.writeToLog("Loading the data: Stakeholders, Needs, Ratings, Forums, Needs of Forums");
		loader = LoaderStaticFactory.getLoader("textloader", parameters, basicElements);
		loader.loadStakeholders();
		loader.loadNeeds();
		loader.loadRatings();
		loader.loadForums();
		loader.loadNeedsOfForums();

		// The only part that is missing from the data loaded is the relationships between the stakeholders and the forums, so this is inferred from the model
		output.writeToLog("Calculating Stakeholder Membership scores");
		if (type== TypeOfRecommender.Range) {
			recommender.addStakeholdersToForums(NormalizationMethod.Norm_Row);
		} else {
			recommender.addStakeholdersToForums(NormalizationMethod.None);
		}

		// Outputs the original the basic elements and the matrices
		output.writeToLog("Writing basic elements");
		output.writeBasicElements();
		output.writeToLog("Writing original matrices");
		output.writeMatrices();		
	}
	
	

	private void testPredictionForumlas() {
		try {
	
			// Sets up the experiment - for the binary membership scores
			setup(TypeOfRecommender.Binary);

			// Gets the SxF matrix
			MatrixElement SxF = matrices.getStakeholdersForumsMatrix(false);
			
			// Calculates the stakholders total number of forums where they were placed
			recommender.calculateStakeholdersTotalRatings(SxF.getName(), "SxF_Totals");
			
			// Gets the matrix with the total number of forums in which the stakeholder is in.  This was previously calculated.
			MatrixElement SxF_Totals =  matrices.getMatrix("SxF_Totals");
			double[][] totalForumsOfStakeholder = SxF_Totals.getArray();
			
			// Cycles through the different prediction formulas
			PredictionFormula[] formulas = {PredictionFormula.Bin_I, PredictionFormula.Bin_II, PredictionFormula.Bin_III, PredictionFormula.Bin_IV,};
			for (PredictionFormula f : formulas) {
				testPredictionForumla(f, SxF, totalForumsOfStakeholder);
			}
			
			
		} catch (Exception e) {
			System.out.println("The following error ocurred:\n" + e.getMessage());
			System.out.println("Details:\n");
			e.printStackTrace();
		}
		
		
	}
	
	private void testPredictionForumla(PredictionFormula pf,  MatrixElement SxF, double[][] totalForumsOfStakeholder) throws IOException {

		// Gets the number of neigbors
		int numOfNeighbors = Integer.parseInt(parameters.readParameter("numberofneighbors"));
		
		System.out.println("Prediction Formula=" + pf.toString()+ ", K="+numOfNeighbors + ", MAE=");
		String fileName = "Prediction_Formula_" + pf.toString()+ "_K="+numOfNeighbors; 
		
		// Gets the output file ready
		String outputDir = parameters.readParameter("outputdirectory");
		File file = new File(outputDir + "\\" + fileName + ".csv");
		PrintWriter output1 = new PrintWriter( new BufferedWriter(new FileWriter(file)), true) ;

		// Figures our the number of stakeholders and the number of forums in the system.
		int numOfStakeholders = basicElements.getStakeholders().size();
		int numOfForums = basicElements.getForums().size();

		// Creates the new matrix that will show the results of the leave one out test.
		double[][] leaveOneOutMatrix = new double[numOfStakeholders][numOfForums];
		String[] rowNames = new String[numOfStakeholders];
		String[] colNames = new String[numOfForums];
		
		// Gets the ratings matrix (at this point this is a binary matrix)
		double[][] matrixData = SxF.getArray();


		// Outputs the headers for the csv files
		output1.println("StakeholderId, ForumId, ScoreStakeholderInForum, ForumGotRecommended, RecommendationScore, RankOfRecommendation, Error, AbsoluteError");
		Double sumOfErrors = 0.0;
		int totNumOfRecommendations = 0;
		
		// For each stakeholder
		for (Stakeholder s : basicElements.getStakeholders()) {
			rowNames[s.getNumber()] = s.getId();
			
			// Only if the stakeholder belongs to at least 3 forums he gets recommendations
			if (totalForumsOfStakeholder[s.getNumber()][0] >= 3) {

				// For each forum
				for (Forum f : basicElements.getForumsOfStakeholder(s)) {
					colNames[f.getNumber()] = f.getId();
					totNumOfRecommendations++;
					
					// Saves the membership value (which in this case is a 1 since the matrix is binary)
					double originalMembershipScore = 1.0;
					
					// Temporarily put a 0 in there - removing the stakeholder from the forum
					matrixData[s.getNumber()][f.getNumber()] = 0.0;
					
					// Calculates the stakholders total number of forums where they were placed
					recommender.calculateStakeholdersTotalRatings(SxF.getName(), "SxF_Totals");
					
					// Calculates the stakeholder similarities
					recommender.calculateSimilarities(SxF.getName(), "SxS_Similarities", "SxF_Totals", 1);
					
					// Gets the neighbors
					Map<Integer, List<Node>> neighborsBin = recommender.getNeighbors("SxS_Similarities", numOfNeighbors);
					
					// Gets all the recommendations for this user, in order to get the recomendation value and rank of the forum that was removed
					int rank = -1;
					int count = 0;
					double value = 0.0;
					for (Node n : recommender.getPredictionScores(s, pf, SxF.getName(), "" , neighborsBin)) {
						count++;
						if (n.getId() == f.getNumber()) {
							// The forum got recommended
							rank = count;
							value = n.getValue();
						}
						//System.out.println("Recommendation: " + basicElements.getForum(n.getId()).getTitle() + ", Prediction Score:" + n.getValue());
					}
					Double error = originalMembershipScore - value;
					Double absError = Math.abs(error);
					sumOfErrors += absError;
					output1.println(s.getId() + "," + f.getId() + "," + originalMembershipScore + "," + ((rank == -1) ? "0" : "1") + "," + value + "," + rank + ", " + error + ", " + absError);

					// Records the result (rank) in the leave one out matrix 
					leaveOneOutMatrix[s.getNumber()][f.getNumber()] = rank;
										
					// Restores the model 
					matrixData[s.getNumber()][f.getNumber()] = originalMembershipScore;
				}
			}
		}
		// Outputs the MAE
		output1.println();
		output1.println("SumOfAbsError, NumOfRecommendations, MAE");
		output1.println(sumOfErrors + "," + totNumOfRecommendations + "," + (sumOfErrors/totNumOfRecommendations));
		
		System.out.println((sumOfErrors/totNumOfRecommendations));
		output.writeToLog("Prediction Formula=" + pf.toString()+ ", K="+numOfNeighbors + ", MAE=" + (sumOfErrors/totNumOfRecommendations));
		
		// Writes the leave one out matrix
		MatrixElement leaveOneOut = new MatrixElement("Matrix"+fileName, leaveOneOutMatrix, colNames, rowNames);
		output.writeMatrix(leaveOneOut);
		
	}	
	
	private void testSimilaritiesAndNeighbors() {
		try {
			String originalOutputDir = parameters.readParameter("outputdirectory");
			
			// ******************************************************************************
			// First the similarities and the neighbors are computed in the traditional way
			// ******************************************************************************
			String outputDir = originalOutputDir + "\\traditional";
			parameters.writeParameter("outputdirectory", outputDir);
			setup(TypeOfRecommender.Range);
			// Gets the number of neigbors
			int numOfNeighbors = Integer.parseInt(parameters.readParameter("numberofneighbors"));
			// Gets the original ratings matrix
			MatrixElement SxF = matrices.getStakeholdersForumsMatrix(false);
			
			// Calculates the stakholders average ratings
			recommender.calculateStakeholdersAverageRating(SxF.getName(), "SxF_Averages");
			// Calculates the stakeholder similarities
			recommender.calculateSimilarities(SxF.getName(), "SxS_Similarities", "SxF_Averages", 1);
			MatrixElement SxS_Similarities = matrices.getMatrix("SxS_Similarities");
			output.writeMatrix(SxS_Similarities);
			// Gets the neighbors
			Map<Integer, List<Node>> neighbors = recommender.getNeighbors("SxS_Similarities", numOfNeighbors);
			
			// ******************************************************************************
			// The list of neighbors is printed - for the traditional method
			// ******************************************************************************
			output.writeToLog("");
			output.writeToLog("Neighbors - Traditional");
			output.writeToLog("");
			StringBuffer buffer = new StringBuffer();
			buffer.append("Stakeholder");
			for (int i=0; i<numOfNeighbors; i++) {
				buffer.append(",N_" + (i+1) + ",Sim_" + (i+1));
			}
			output.writeToLog(buffer.toString());
			for (Stakeholder s : basicElements.getStakeholders()) {
				buffer = new StringBuffer();
				buffer.append(s.getId());
				for (Node n : neighbors.get(s.getNumber())) {
					buffer.append("," + basicElements.getStakeholder(n.getId()).getId()+ "," + n.getValue());
				}
				output.writeToLog(buffer.toString());
			}
			
			
			// *******************************************************************************************
			// Then the similarities and the neighbors are computed in using the binary membership scores
			// *******************************************************************************************
			outputDir = originalOutputDir + "\\binary";
			parameters.writeParameter("outputdirectory", outputDir);
			setup(TypeOfRecommender.Binary);
			// Gets the number of neigbors
			numOfNeighbors = Integer.parseInt(parameters.readParameter("numberofneighbors"));
			// Gets the original ratings matrix
			SxF = matrices.getStakeholdersForumsMatrix(false);
			
			// Calculates the stakholders total number of forums where they were placed
			recommender.calculateStakeholdersTotalRatings(SxF.getName(), "SxF_Totals_Bin");
			// Calculates the stakeholder similarities
			recommender.calculateSimilarities(SxF.getName(), "SxS_Similarities_Bin", "SxF_Totals_Bin", 1);
			MatrixElement SxS_Similarities_Bin = matrices.getMatrix("SxS_Similarities_Bin");
			output.writeMatrix(SxS_Similarities_Bin);
			// Gets the neighbors
			Map<Integer, List<Node>> neighborsBin = recommender.getNeighbors("SxS_Similarities_Bin", numOfNeighbors);
						

			// ******************************************************************************
			// The list of neighbors is printed - for the binary method
			// ******************************************************************************
			output.writeToLog("");
			output.writeToLog("Neighbors - Binary");
			output.writeToLog("");
			buffer = new StringBuffer();
			buffer.append("Stakeholder");
			for (int i=0; i<numOfNeighbors; i++) {
				buffer.append(",N_" + (i+1) + ",Sim_" + (i+1));
			}
			output.writeToLog(buffer.toString());
			for (Stakeholder s : basicElements.getStakeholders()) {
				buffer = new StringBuffer();
				buffer.append(s.getId());
				for (Node n : neighborsBin.get(s.getNumber())) {
					buffer.append("," + basicElements.getStakeholder(n.getId()).getId()+ "," + n.getValue());
				}
				output.writeToLog(buffer.toString());
			}			
			
		} catch (Exception e) {
			System.out.println("The following error ocurred:\n" + e.getMessage());
			System.out.println("Details:\n");
			e.printStackTrace();
		}
		
	}	
	
	
	
	
	private void testCalculations() {
		
		basicElements = BasicElementStaticFactory.newBasicElementManager("elements", blackboard);		
		matrices = MatrixManagerStaticFactory.newMatrixManager("matrix", blackboard, basicElements);
		recommender = RecommenderFunctionsStaticFactory.getRecommenderFunctionsForBinaryMemberships(basicElements, matrices);
		
		// Sample code - to double check that the normalization calculations are right.
		double[][] data = {	{5,	4,	2,	0,	3},
							{0,	2,	4,	4,	5},
							{1,	3,	0,	0,	5}};	
		String[] cols = {"col_0", "col_1", "col_2", "col_3", "col_4"};
		String[] rows = {"ror_0", "row_1", "row_2"};
		MatrixElement m = new MatrixElement("test", data, cols, rows);
		
		System.out.println("");
		System.out.println("Original Matrix");
		System.out.print(m.toString());
		
		System.out.println("");
		System.out.println("Binary");
		MatrixElement bin = m.convertToBinary(1, "bin");
		System.out.print(bin.toString());
		
		System.out.println("");
		System.out.println("Totals per row");
		MatrixElement tot = bin.totalsByRow("totals");
		matrices.storeMatrix(tot);
		System.out.print(tot.toString());
		
		System.out.println("");
		System.out.println("User Similarities");
		matrices.storeMatrix(bin);
		recommender.calculateSimilarities("bin", "sim", "totals", 1);
		System.out.print(matrices.getMatrix("sim").toString());
		
	}
	 
}
