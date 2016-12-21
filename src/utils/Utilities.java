package utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Set;

import data.basic.BasicElementManager;
import data.matrix.MatrixElement;
import data.matrix.MatrixManager;
import utils.poirot.WordsGenerator;
import macduan.clustering.elite.*;
import macduan.clustering.postprocessing.*;

/**
 * 
 * Class that contains several utilities that call upon code that was not created as part of this prototype.
 * It includes functions that call to Poirot functions (such as remove stop words, stem the text, count the words, etc.)
 * It also includes functions that call the Clustering API.
 * These functions are here to reduce the coupling and dependency of this prototype on outside code. 
 *
 */
public final class Utilities {
	public static HashMap<String, Integer> getTermFrequencies(String rawText){
		// Uses the Poirot code to generate the hashtable of words and frequencies.
		Hashtable t = WordsGenerator.getWordsList(rawText);
		// It converts it to a new HashMap (Hashtable is old and does not conform to the Collections Framework.
		HashMap<String, Integer> termFrequencies =  new HashMap<String, Integer>(t); 
		return termFrequencies;
	}
	
	public static Map<Integer, Set<Integer>> getClutsters(Map<Integer, Map<Integer, Double>> frequencies) {
		// Sets the clustering parameters
		String algorithm = "BS";				// Bisecting 
		int averageClusterSize = -1;			// Not set: -1
		int expectedNumberClusters = -1;		// Not set: -1
		
		// Calls the clustering algorithm.
		Map<Integer, Set<Integer>> clusters = ClsAPI.goClustering(algorithm, frequencies, expectedNumberClusters, averageClusterSize);
		return clusters;
	}		
	
	public static double getScoreOfDocumentInCluster(int documentId, Set<Integer> cluster, Map<Integer, Map<Integer, Double>> frequencies) {
		// Calls the Clustering API to get the score of the document in the cluster
		// TODO Meet with Chuan to see if there is a more efficient way of doing this.
		return MembershipScore.centroidScore(documentId, cluster, frequencies);
	}	

	public static void printNeedTermFrequencyFile(String filename, MatrixManager matrices) {
		File file;					
		PrintWriter output;		
		
		try {
			file = new File(filename);
			output = new PrintWriter(new BufferedWriter(new FileWriter(file)), true);
			
			MatrixElement NxT = matrices.getNeedsTermsMatrix(true); 
			
			int rows = NxT.getNumRows();
			int cols = NxT.getNumCols();
			double[][] matrixData = NxT.getArray();
			
			for (int i=0; i<rows; i++) {
				for (int j=0; j<cols; j++) {
					if (matrixData[i][j] != 0.0) {
						output.println(NxT.getRowName(i) + ", " + NxT.getColumnName(j) + ", " + matrixData[i][j]);
					}
				}
			}	
		} catch (IOException e) {
			System.out.println("An error has occured while attempting to write to a file. The error is: " + e.getMessage());
		}
	}
}
