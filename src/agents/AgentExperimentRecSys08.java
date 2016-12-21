package agents;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.Exception;
import java.util.Map;
import java.util.List;
import blackboard.*;
import output.*;
import recommender.*;
import recommender.RecommenderFunctions.NormalizationMethod;
import recommender.RecommenderFunctions.PredictionFormula;
import input.*;
import data.*;
import data.basic.*;
import data.matrix.*;
import data.parameters.*;


/**
 *
 * Class that runs the leave one out experiment for the RecSys08 paper.
 * This experiment varies the number of neighbors through various types of architectures:
 * 1) Neighbors calculated with the SxF
 * 2) Neighbors calculated with the SxT
 * 
 */
final class AgentExperimentRecSys08 implements Agent{
	// Local Variables
	Blackboard blackboard;
	BasicElementManager basicElements;
	MatrixManager matrices;
	ParameterManager parameters;
	Loader loader;
	Output output;
	RecommenderFunctions recommender;
	
	// Constructor - Package Private
	AgentExperimentRecSys08(Blackboard bb) {
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
		parameters.writeParameter("outputdirectory", "C:\\eclipse\\workspace\\RecommenderPrototype\\output\\student");
		output = OutputStaticFactory.getOutput("textOutput", parameters, basicElements, matrices);
		
		
		output.writeToLog("Executing Agent: Experiment RecSys 08");
		output.writeToLog("Dataset being used: Student");
		
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
		
		// Utilities.printNeedTermFrequencyFile("C:\\eclipse\\workspace\\RecommenderPrototype\\output\\secondlife\\termFreq.csv", matrices);
		
		//int[] numNeighbors = {5,10,15,20,25,30,35,40,45,50,75,100,200,300,400,500,600,700,800,900,1000,2000,3000,4000};
		//int[] numNeighbors = {5,10,15,20,25,30,35,40,45,50,75,100,200,300,400,500};
		int[] numNeighbors = {5,10,15,20,25,30,35};

		// The test is executed for each several times for each architecture and increasing the number of neighbors	
		ArchitecuteType[] architectures = {
				ArchitecuteType.SxF,
				ArchitecuteType.SxT,
				ArchitecuteType.SxF_Sim_Mod,
				ArchitecuteType.SxT_Sim_Mod,
				ArchitecuteType.SxF_PCA,
				ArchitecuteType.SxT_PCA
			};
		
		for (ArchitecuteType a : architectures) {		
			for (int i : numNeighbors) {				
				Integer numOfNeighbors = i;
				parameters.writeParameter("numberofneighbors", numOfNeighbors.toString());				
				runTest(a);
			}
		}
		
	}
		
	private void runTest(ArchitecuteType arch) {
		try {

			// Sets the number of neigbors
			int numOfNeighbors = Integer.parseInt(parameters.readParameter("numberofneighbors"));
			
			System.out.print("Architecture=" + arch.ordinal()+ ", K="+numOfNeighbors + ", MAE=");
			
			String fileName = "LeaveOneOut_Arch=" + arch.ordinal() + "_K=" + numOfNeighbors;
			
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
			
			// Also gets the Stakeholder x Term matrix, as this one is also used
			MatrixElement SxT = matrices.getStakeholdersTermsMatrix(false);
			
			// Outputs the headers for the csv files
			output1.println("StakeholderId, ForumId, ScoreStakeholderInForum, ForumGotRecommended, RecommendationScore, RankOfRecommendation, Error, AbsoluteError");
			Double sumOfErrors = 0.0;
			int totNumOfRecommendations = 0;
			
			// For each stakeholder
			for (Stakeholder s : basicElements.getStakeholders()) {
				rowNames[s.getNumber()] = s.getId();
				
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
						
					// The way the similarities are calculated depends on the architecture
					switch (arch) {
						case SxF:
							// Similarities are calculated using the SxF matrix
							recommender.calculateSimilarities(SxF.getName(), "SxS_Similarities", "SxF_Averages", 1);
							break;
						case SxT:
							// Similarities are calculated using the SxT
							recommender.calculateStakeholdersAverageRating(SxT.getName(), "SxT_Averages");
							recommender.calculateSimilarities(SxT.getName(), "SxS_Similarities", "SxT_Averages", 1);
							break;
						case SxF_Sim_Mod:
							// Similarities are calculated using the SxF matrix, but penalized when the number of co-rated items is less than 5
							recommender.calculateSimilarities(SxF.getName(), "SxS_Similarities", "SxF_Averages", 5);							
							break;
						case SxT_Sim_Mod:
							// Similarities are calculated using the SxT matrix, but penalized when the number of co-rated items is less than 5
							recommender.calculateStakeholdersAverageRating(SxT.getName(), "SxT_Averages");
							recommender.calculateSimilarities(SxT.getName(), "SxS_Similarities", "SxT_Averages", 5);
							break;
						case SxF_PCA:
							// Similarities are calculated using the SxF after it PCA was applied to it
							MatrixElement SxF_PCA = SxF.compressByPCA("SxF_PCA", 0.9);
							matrices.storeMatrix(SxF_PCA);
							recommender.calculateStakeholdersAverageRating("SxF_PCA", "SxF_PCA_Averages");
							recommender.calculateSimilarities("SxF_PCA", "SxS_Similarities", "SxF_PCA_Averages", 1);
							break;
						case SxT_PCA:
							// Similarities are calculated using the SxT after it PCA was applied to it
							MatrixElement SxT_PCA = SxT.compressByPCA("SxT_PCA", 0.9);
							matrices.storeMatrix(SxT_PCA);
							recommender.calculateStakeholdersAverageRating("SxT_PCA", "SxT_PCA_Averages");
							recommender.calculateSimilarities("SxT_PCA", "SxS_Similarities", "SxT_PCA_Averages", 1);
							break;						
					}
					
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
			// Outputs the MAE
			output1.println();
			output1.println("SumOfAbsError, NumOfRecommendations, MAE");
			output1.println(sumOfErrors + "," + totNumOfRecommendations + "," + (sumOfErrors/totNumOfRecommendations));
			
			System.out.println((sumOfErrors/totNumOfRecommendations));
			output.writeToLog("Architecture=" + arch.ordinal()+ ", K="+numOfNeighbors + ", MAE=" + (sumOfErrors/totNumOfRecommendations));
			
			// Writes the leave one out matrix
			MatrixElement leaveOneOut = new MatrixElement("Matrix"+fileName, leaveOneOutMatrix, colNames, rowNames);
			output.writeMatrix(leaveOneOut);
		
		} catch (Exception e) {
			System.out.println("The following error ocurred:\n" + e.getMessage());
			System.out.println("Details:\n");
			e.printStackTrace();
		}
		
	}	
	
	private enum ArchitecuteType {
		SxF,
		SxT,
		SxF_Sim_Mod,
		SxT_Sim_Mod,
		SxF_PCA,
		SxT_PCA
	}
	
}
