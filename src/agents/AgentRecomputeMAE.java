package agents;

import java.io.BufferedReader;
import java.io.FileReader;
import input.Loader;
import input.LoaderStaticFactory;
import output.Output;
import output.OutputStaticFactory;
import recommender.RecommenderFunctions;
import recommender.RecommenderFunctionsStaticFactory;
import blackboard.Blackboard;
import data.basic.BasicElementManager;
import data.basic.BasicElementStaticFactory;
import data.matrix.MatrixManager;
import data.matrix.MatrixManagerStaticFactory;
import data.parameters.ParameterManager;
import data.parameters.ParameterManagerStaticFactory;

public class AgentRecomputeMAE implements Agent {
	// Local Variables
	Blackboard blackboard;
	BasicElementManager basicElements;
	MatrixManager matrices;
	ParameterManager parameters;
	Loader loader;
	Output output;
	RecommenderFunctions recommender;
	
	// Constructor - Package Private
	AgentRecomputeMAE(Blackboard bb) {
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
		parameters.writeParameter("inputdirectory", "C:\\eclipse\\workspace\\RecommenderPrototype\\output\\student-complete\\elements");
		parameters.writeParameter("leaveoneoutinputdirectory", "C:\\eclipse\\workspace\\RecommenderPrototype\\output\\student-complete");

		parameters.writeParameter("outputdirectory", "C:\\eclipse\\workspace\\RecommenderPrototype\\output\\student-modifiedMAE");
		output = OutputStaticFactory.getOutput("textOutput", parameters, basicElements, matrices);
		
		
		output.writeToLog("Executing Agent: Recalculating MAE for RecSys 08");
		output.writeToLog("Dataset being used: Sugar");
		
		// Loads the data 
		output.writeToLog("Loading all the data");
		loader = LoaderStaticFactory.getLoader("textloader", parameters, basicElements);
		loader.loadAllElements();

		int[] numNeighbors = {5,10,15,20,25,30,35};
		
		for (ArchitecuteType a : ArchitecuteType.values()) {		
			for (int i : numNeighbors) {	
				recalculateMAE(a, i, 3);
			}
		}		
	}

	private void recalculateMAE(ArchitecuteType architecture, int numNeighbors, int minNumOfForums) throws Exception {
		
		String filename = parameters.readParameter("leaveoneoutinputdirectory") + "\\LeaveOneOut_Arch=" + architecture.ordinal() + "_K=" + numNeighbors + ".csv";
		
		BufferedReader in;
		in = new BufferedReader(new FileReader(filename));

		String str;
		String[] strs;		
		String StakeholderId, ForumId, ScoreStakeholderInForum, ForumGotRecommended, RecommendationScore, RankOfRecommendation, Error, AbsoluteError;
		
		Double sumOfErrors = 0.0;
		Double sumOfErrorsAdjusted = 0.0;
		int totNumOfRecommendations = 0;		
		int totNumOfRecommendationsAdjusted = 0;		
		
		
		// The first line has headers, so it is ignored
		str = in.readLine();
		
        while ((str = in.readLine()) != null) {
        	// Reads the fields
        	strs = str.split(",");
        	
        	// Only processes lines that have the 8 fields (the other lines are summaries)
        	if (strs.length == 8) {
        	
        		StakeholderId = strs[0].trim();
        		ForumId = strs[1].trim();
        		ScoreStakeholderInForum = strs[2].trim();
        		ForumGotRecommended = strs[3].trim();
        		RecommendationScore = strs[4].trim();
        		RankOfRecommendation = strs[5].trim();
        		Error = strs[6].trim();
        		AbsoluteError = strs[7].trim();
        		
        		// Only if the stakeholder has at least minNumOfForums forums the recommendation gets counted in the adjusted
        		if (basicElements.getForumsOfStakeholder(basicElements.getStakeholder(StakeholderId)).size() >= minNumOfForums) {
        			sumOfErrorsAdjusted += Double.parseDouble(AbsoluteError);
        			totNumOfRecommendationsAdjusted++;
        			
        		}
        		// The old numbers are computed for sanity checks
        		sumOfErrors += Double.parseDouble(AbsoluteError);
        		totNumOfRecommendations++;
        	}
        }
		System.out.println("Architecture=" + architecture.ordinal()+ ", K="+numNeighbors + ", oldMAE=" + (sumOfErrors/totNumOfRecommendations) + ", newMAE=" + (sumOfErrorsAdjusted/totNumOfRecommendationsAdjusted));
		output.writeToLog("Architecture=" + architecture.ordinal()+ ", K="+numNeighbors + ", oldMAE=" + (sumOfErrors/totNumOfRecommendations) + ", newMAE=" + (sumOfErrorsAdjusted/totNumOfRecommendationsAdjusted));
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
