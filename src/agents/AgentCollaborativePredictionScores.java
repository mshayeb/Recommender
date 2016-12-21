package agents;

import input.Loader;
import input.LoaderStaticFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
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
 * Class that runs the Collaborative Recommender and outputs the prediction scores.
 *
 */
public class AgentCollaborativePredictionScores implements Agent{
	// Local Variables
	Blackboard blackboard;
	BasicElementManager basicElements;
	MatrixManager matrices;
	ParameterManager parameters;
	Loader loader;
	Output output;
	RecommenderFunctions recommender;
	
	// Constructor - Package Private
	AgentCollaborativePredictionScores(Blackboard bb) {
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
		parameters.writeParameter("inputdirectory", "C:\\eclipse\\workspace\\RecommenderPrototype\\input\\secondlife");
		parameters.writeParameter("outputdirectory", "C:\\eclipse\\workspace\\RecommenderPrototype\\output\\secondlife");
		output = OutputStaticFactory.getOutput("textOutput", parameters, basicElements, matrices);
		
		
		output.writeToLog("Executing Agent: Collaborative Prediction Scores");
		output.writeToLog("Dataset being used: SecondLife");
		
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
		recommender.addStakeholdersToForums(NormalizationMethod.Norm_Row);

		// Outputs the original the basic elements and the matrices
		output.writeToLog("Writing basic elements");
		output.writeBasicElements();
		output.writeToLog("Writing original matrices");
		output.writeMatrices();
				
		
		parameters.writeParameter("numberofneighbors","10");
		
		runTest();
		
	}
		
	private void runTest() {
		try {

			// Gets the number of neigbors
			int numOfNeighbors = Integer.parseInt(parameters.readParameter("numberofneighbors"));
			
			System.out.println("Experiment with K="+numOfNeighbors);
			
			String fileName = "Collaborative_Recommendations_K=" + numOfNeighbors;
			
			// Gets the output file ready
			String outputDir = parameters.readParameter("outputdirectory");
			File file = new File(outputDir + "\\" + fileName + ".csv");
			PrintWriter output1 = new PrintWriter( new BufferedWriter(new FileWriter(file)), true) ;
			
			// Gets the original ratings matrix
			MatrixElement SxF = matrices.getStakeholdersForumsMatrix(false);
			double[][] matrixData = SxF.getArray();
			
			// Calculates the stakholders average ratings
			recommender.calculateStakeholdersAverageRating(SxF.getName(), "SxF_Averages");
			
			// Calculates the stakeholder similarities
			recommender.calculateSimilarities(SxF.getName(), "SxS_Similarities", "SxF_Averages", 1);
			
			// Gets the neighbors
			Map<Integer, List<Node>> neighbors = recommender.getNeighbors("SxS_Similarities", numOfNeighbors);
						
			// Outputs the headers for the csv files
			output1.println("StakeholderId, ForumId, RecommendationScore");
			
			// For each stakeholder
			for (Stakeholder s : basicElements.getStakeholders()) {
				
				// Only if the stakeholder belongs to at least 3 forums he gets recommendations
				if (basicElements.getForumsOfStakeholder(s).size() >= 3) {
				
					// For each forum
					for (Forum f : basicElements.getForums()) {
						
						// A recommendation is made ONLY IF the stakeholder does not belong to the forum already
						if (matrixData[s.getNumber()][f.getNumber()] == 0.0) {
							
							// Gets all the recommendations for this user, in order to get the recomendation value and rank of the forum that was removed
							double value = recommender.getPredictionScore(s, f, PredictionFormula.Typical, SxF.getName(), "SxF_Averages", neighbors);
							
							output1.println(s.getId() + "," + f.getId() + "," +  value);
							
						}
					}
				}
			}
								
		} catch (Exception e) {
			System.out.println("The following error ocurred:\n" + e.getMessage());
			System.out.println("Details:\n");
			e.printStackTrace();
		}		
	}	
}
