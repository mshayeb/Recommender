package agents;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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
import data.matrix.MatrixElement;
import data.matrix.MatrixManager;
import data.matrix.MatrixManagerStaticFactory;
import data.parameters.ParameterManager;
import data.parameters.ParameterManagerStaticFactory;

/**
 * 
 * This experiment tests different ways of normalizing the SxF matrix.
 * In particular it tests the normalization by row, by column and by overall max.
 *
 */
public class AgentExperimentDifferentNormalizations implements Agent{
	// Local Variables
	Blackboard blackboard;
	BasicElementManager basicElements;
	MatrixManager matrices;
	ParameterManager parameters;
	Loader loader;
	Output output;
	RecommenderFunctions recommender;
	
	// Constructor - Package Private
	AgentExperimentDifferentNormalizations(Blackboard bb) {
		// Integrity checks
		if (bb==null)
			throw new IllegalArgumentException("The blackboard reference has to be a valid object");
		
		// Sets the internal fields;
		blackboard = bb;
	}	
		
	public void run() throws Exception {
		// Set up
		basicElements = BasicElementStaticFactory.newBasicElementManager("elements", blackboard);		
		matrices = MatrixManagerStaticFactory.newMatrixManager("matrix", blackboard, basicElements);
		recommender = RecommenderFunctionsStaticFactory.getRecommenderFunctionsForRangeMemberships(basicElements, matrices);
		parameters = ParameterManagerStaticFactory.newParameterManager("parameters", blackboard);
		parameters.writeParameter("inputdirectory", "C:\\eclipse\\workspace\\RecommenderPrototype\\input\\student");
		parameters.writeParameter("outputdirectory", "C:\\eclipse\\workspace\\RecommenderPrototype\\output\\student_normalization");
		output = OutputStaticFactory.getOutput("textOutput", parameters, basicElements, matrices);
		
		
		output.writeToLog("Executing Agent: Experiment Different Types of Normalization");
		output.writeToLog("Dataset being used: Student");
		
		// Loads the data 
		output.writeToLog("Loading the data: Stakeholders, Needs, Ratings, Forums, Needs of Forums");
		loader = LoaderStaticFactory.getLoader("textloader", parameters, basicElements);
		loader.loadStakeholders();
		loader.loadNeeds();
		loader.loadRatings();
		loader.loadForums();
		loader.loadNeedsOfForums();

		NormalizationMethod[] methods = {NormalizationMethod.Norm_Row, NormalizationMethod.Norm_Col, NormalizationMethod.Norm_MaxNum};
		for (NormalizationMethod m : methods) {
			runTest(m);
		}
	}

	
	private void runTest(NormalizationMethod m) {
		try {

			// ***************************************************
			// * This is the part that varies in this experiment *
			// ***************************************************
			// The only part that is missing from the data loaded is the relationships between the stakeholders and the forums, so this is inferred from the model
			output.writeToLog("Calculating Stakeholder Membership scores with the Method: " + m.toString());
			recommender.addStakeholdersToForums(m);
		
			// The number of neighbors is fixed in this test 
			int numOfNeighbors = 10;
			
			System.out.print("Normalization Method=" + m.toString() + ", K="+numOfNeighbors + ", MAE=");
			String fileName = "LeaveOneOut_Method_=" + m.toString() + "_K=" + numOfNeighbors;
			
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

			// Gets the original ratings matrix
			MatrixElement SxF = matrices.getStakeholdersForumsMatrix(false);
			double[][] matrixData = SxF.getArray();
			
			// Outputs the headers for the csv files
			output1.println("StakeholderId, ForumId, ScoreStakeholderInForum, ForumGotRecommended, RecommendationScore, RankOfRecommendation, Error, AbsoluteError");
			Double sumOfErrors = 0.0;
			int totNumOfRecommendations = 0;
			
			// For each stakeholder
			for (Stakeholder s : basicElements.getStakeholders()) {
				rowNames[s.getNumber()] = s.getId();
				
				
				// Note, the recommendations are only made to those stakeholders that are in 3 or more forums
				System.out.println("Stakeholder:" + s.getId() + " is in " + basicElements.getForumsOfStakeholder(s).size() + " forums." );
				if(basicElements.getForumsOfStakeholder(s).size() >= 3) {
				
					// For each forum
					for (Forum f : basicElements.getForumsOfStakeholder(s)) {
						colNames[f.getNumber()] = f.getId();
						totNumOfRecommendations++;
						
						// Saves the membership value
						double originalMembershipScore = basicElements.getScoreOfStakeholderInForum(f, s);
						
						// Temporarily put a 0 in there - removing the stakeholder from the forum
						matrixData[s.getNumber()][f.getNumber()] = 0.0;
						
						// Recalculates the stakholders average rating - as it will change
						recommender.calculateStakeholdersAverageRating(SxF.getName(), "SxF_Averages");
							
						// Calculates the similarities between the stakeholders
						recommender.calculateSimilarities(SxF.getName(), "SxS_Similarities", "SxF_Averages", 1);
						
						// Gets the neighbors
						Map<Integer, List<Node>> neighbors = recommender.getNeighbors("SxS_Similarities", numOfNeighbors);
						
						// TEMP code that prints the lists of neighbors:
						/*
						StringBuffer buffer = new StringBuffer();
						buffer.append("Stakeholder:, " + s.getName() + ", Neighbors:, ");
						for (Node n : neighbors.get(s.getNumber())) {
							buffer.append(basicElements.getStakeholder(n.getId()).getName() + " (" + n.getValue() + "), ");
						}
						output.writeToLog(buffer.toString());
						*/
						
						// Gets all the recommendations for this user, in order to get the recomendation value and rank of the forum that was removed
						int rank = -1;
						int count = 0;
						double value = 0.0;
						for (Node n : recommender.getPredictionScores(s, PredictionFormula.Typical, SxF.getName(), "SxF_Averages", neighbors)) {
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
			output.writeToLog("Normalization Method =" + m.toString() + ", K="+ numOfNeighbors + ", MAE=" + (sumOfErrors/totNumOfRecommendations));
			
			// Writes the leave one out matrix
			MatrixElement leaveOneOut = new MatrixElement("Matrix"+fileName, leaveOneOutMatrix, colNames, rowNames);
			output.writeMatrix(leaveOneOut);
		
		} catch (Exception e) {
			System.out.println("The following error ocurred:\n" + e.getMessage());
			System.out.println("Details:\n");
			e.printStackTrace();
		}
		
	}		

	/*	
	public void run() throws Exception {
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
		System.out.println("Row Totals");
		MatrixElement rowTotals = m.totalsByRow("Totals_Row");
		System.out.print(rowTotals.toString());
		
		System.out.println("");
		System.out.println("Column Totals");
		MatrixElement colTotals = m.totalsByColumn("Totals_Column");
		System.out.print(colTotals.toString());
		
		System.out.println("");
		Double normNumber = m.maxNumber();
		System.out.println("MaxNumber = " + normNumber);

		System.out.println("");
		System.out.println("Normalize by Rows");
		System.out.print(m.normalizeByRows(rowTotals, "Norm_Row").toString());
		
		System.out.println("");
		System.out.println("Normalize by Columns");
		System.out.print(m.normalizeByColumns(colTotals, "Norm_Col").toString());
		
		System.out.println("");
		System.out.println("Normalize by Max Number");
		System.out.print(m.normalizeByNumber(normNumber, "Norm_Num").toString());
	}
*/		
}
