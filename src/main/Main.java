package main;

import java.util.GregorianCalendar;
import agents.*;
import agents.AgentStaticFactory;
import blackboard.*;

/**
 * 
 * This class runs the prototype execution.
 * Note that it is a static class.
 * 
 */
public class Main {

	// Static - not instantiable
	private Main() {}
		
	public static void main(String[] args) {
		try{
			System.out.println("Start Time: " + (new GregorianCalendar()).getTime().toString() );		
			
			// Creates the common repository
			Blackboard blackboard = BlackboardStaticFactory.newBlackboard();
			
			// Instantiates an agent based on the commandline parameter
			Agent a = AgentStaticFactory.getAgent(args[0], blackboard);
			a.run();
			
			
			// TODO Temporary code:
			
			/*
			
			BasicElementManager basicElements = BasicElementStaticFactory.newBasicElementManager("elements", blackboard);
			MatrixManager matrices = MatrixManagerStaticFactory.newMatrixManager("matrix", blackboard, basicElements);
			
			ParameterManager parameters = ParameterManagerStaticFactory.newParameterManager("parameters", blackboard);
			parameters.writeParameter("inputdirectory", "C:\\eclipse\\workspace\\RecommenderPrototype\\input\\test1");
			parameters.writeParameter("outputdirectory", "C:\\eclipse\\workspace\\RecommenderPrototype\\output\\test");
			parameters.writeParameter("numberofneighbors", "5");
			
			Loader loader = LoaderStaticFactory.getLoader("textloader", parameters, basicElements);
			Output output = OutputStaticFactory.getOutput("textOutput", parameters, basicElements, matrices);
			
			RecommenderFunction recommender = RecommenderFunctionsStaticFactory.getRecommenderFunction(parameters, basicElements, matrices);
			
			
			//loader.loadAllElements();
			loader.loadStakeholders();
			loader.loadNeeds();
			loader.loadRatings();
			loader.loadForums();
			loader.loadNeedsOfForums();
			
			recommender.addStakeholdersToForums();
			
			output.writeBasicElements();
			output.writeMatrices();
			
			MatrixElement SxF = matrices.getStakeholdersForumsMatrix(false);
			recommender.calculateStakeholdersAverageRating(SxF.getName(), "SxF_Averages");
			output.writeMatrix(matrices.getMatrix("SxF_Averages"));
			
			MatrixElement SxT = matrices.getStakeholdersTermsMatrix(false);
			recommender.calculateStakeholdersAverageRating(SxT.getName(),"SxT_Averages");
			output.writeMatrix(matrices.getMatrix("SxT_Averages"));
			
			// The similarities can be calculated on any matrix and its averages
			recommender.calculateSimilarities(SxT.getName(), "SxT_Averages", "SxS_Similarities");
			MatrixElement sim = matrices.getMatrix("SxS_Similarities");
			output.writeMatrix(sim);
			
			// Gets the neighbors
			Map<Integer, List<Node>> neighbors = recommender.getNeighbors(sim.getName());
			
			for(Stakeholder s : basicElements.getStakeholders()) {
				System.out.println("\nStakeholder: " + s.getName());
				for (Node n : neighbors.get(s.getNumber())) {
					System.out.println("Neighbor: " + basicElements.getStakeholder(n.getId()).getName() + ", Similarity: " + n.getValue());
				}
				// Calculates the prediciton scores - note that the original SxF matrix and the corresponding averages need to be used.
				for (Node n : recommender.getPredictionScores(s, SxF.getName(), "SxF_Averages", neighbors)) {
					System.out.println("Recommendation: " + basicElements.getForum(n.getId()).getTitle() + ", Prediction Score:" + n.getValue());
				}
			}
			
			*/
			
			
			
			
			/*
			// A few matrix tests
			double[][] vals = {{3.0,4.0,2.0},{2.0,3.0,-1.0}};
			String[] cols = {"a", "b", "c"};
			String[] rows = {"1", "2"};
			MatrixElement m = new MatrixElement("Original Matrix", vals, cols, rows);
			output.writeMatrix(m);

			MatrixElement aveRow = m.averagesByRow("Averages Row");
			MatrixElement aveCol = m.averagesByColumn("Averages Column");
			output.writeMatrix(aveRow);
			output.writeMatrix(aveCol);
			
			MatrixElement totRow = m.totalsByRow("Totals Row");
			MatrixElement totCol = m.totalsByColumn("Totals Column");
			output.writeMatrix(totRow);
			output.writeMatrix(totCol);
			
			MatrixElement m1 = m.normalizeByRows(totRow, "Norm by Rows");
			MatrixElement m2 = m.normalizeByColumns(totCol, "Norm by Cols");
			output.writeMatrix(m1);
			output.writeMatrix(m2);
			*/
						
			/*
			Utilities.printNeedTermFrequencyFile("c:\\NeedTerm.csv", matrices);
			*/
			
			/*
			MatrixElement NxT = matrices.getNeedsTermsMatrix(true);
			MatrixElement NxC = RecommenderFunctions.cluster(NxT, "NxC");
			output.writeMatrix(NxC);
			*/
			
			System.out.println("End Time: " + (new GregorianCalendar()).getTime().toString() );					
		} catch (Exception e) {
			System.out.println("The following error ocurred:\n" + e.getMessage());
			System.out.println("Details:\n");
			e.printStackTrace();
		}
		

	}

}
