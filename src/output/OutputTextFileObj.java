package output;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import data.basic.*;
import data.matrix.MatrixElement;
import data.matrix.MatrixManager;
import data.parameters.ParameterManager;

/**
 *
 */
final class OutputTextFileObj implements Output{
	// Local Variables
	BasicElementManager _elementManager; // Basic Element Manager
	MatrixManager _matrixManager;	// Matrix Manager
	ParameterManager _parameters;	// Parameters
	String _directoryName;			// Directory were the output files will be placed
	File logFile;					// Log file
	PrintWriter logOutput;			// Log writer
	
	// Constructor - Package Private
	OutputTextFileObj(ParameterManager parameters, BasicElementManager elementManager, MatrixManager matrixManager) throws IOException{
		// Integrity checks
		if (parameters==null)
			throw new IllegalArgumentException("The parameter reference has to be a valid object");
		if (elementManager==null)
			throw new IllegalArgumentException("The element manager has to be a valid object");
		if (matrixManager==null)
			throw new IllegalArgumentException("The matrix manager has to be a valid object");
		if(!parameters.containsParameter("outputdirectory"))
			throw new IllegalArgumentException("The text file output object requires the name of the directory where the files are to be placed as a parameter.");
		
		// Sets the internal fields;
		_parameters = parameters;
		_elementManager =  elementManager;
		_matrixManager = matrixManager;
		_directoryName = parameters.readParameter("outputdirectory");
		
		
		// Creates the output log
		File dir = new File(_directoryName); dir.mkdirs();				
		logFile = new File( _directoryName + "\\log.txt");
		logOutput = new PrintWriter(new BufferedWriter(new FileWriter(logFile)), true);
		
	}
	
	public void writeToLog(String message) throws IOException {
		logOutput.println(message);
	}
	
	public void writeBasicElements() throws IOException {
		File file;					
		PrintWriter output;		
		
		File dir = new File(_directoryName+"\\elements"); dir.mkdirs();
		
		// Writes the stakeholders:
		List<Stakeholder> stakeholders;
		stakeholders = new ArrayList<Stakeholder>(_elementManager.getStakeholders());
		Collections.sort(stakeholders);
		file = new File( _directoryName + "\\elements\\stakeholders.txt");
		output = new PrintWriter(new BufferedWriter(new FileWriter(file)), true);
		
		output.println("id\tname\tdescription");
		for (Stakeholder s : stakeholders) {
			output.println(s.getId() + "\t" + s.getName() + "\t" + s.getDescription());
		}
		
		// Writes the needs:
		List<Need> needs;
		needs = new ArrayList<Need>(_elementManager.getNeeds());
		Collections.sort(needs);
		file = new File( _directoryName + "\\elements\\needs.txt");
		output = new PrintWriter(new BufferedWriter(new FileWriter(file)), true);
		
		output.println("id\ttextneed\tstakeholderid");
		for (Need n : needs) {
			output.println(n.getId() + "\t" + n.getRawText() + "\t" + _elementManager.getStakeholderOfNeed(n).getId());
		}
		
		// Write the ratings:
		List<Rating> ratings;
		ratings = new ArrayList<Rating>(_elementManager.getRatings());
		Collections.sort(ratings);
		file = new File( _directoryName + "\\elements\\ratings.txt");
		output = new PrintWriter(new BufferedWriter(new FileWriter(file)), true);
		
		output.println("id\tstakeholderid\tneedid\ttype\tvalue");
		for (Rating r : ratings) {
			output.println(r.getId() + "\t" + _elementManager.getStakeholderOfRating(r).getId() + "\t" + _elementManager.getNeedOfRating(r).getId() + "\t" + r.getTypeOfRating().toString() + "\t" + r.getValue());
		}
		
		// Write the terms:
		List<Term> terms;
		terms = new ArrayList<Term>(_elementManager.getTerms());
		Collections.sort(terms);
		file = new File( _directoryName + "\\elements\\terms.txt");
		output = new PrintWriter(new BufferedWriter(new FileWriter(file)), true);
		
		output.println("id\tterm");
		for (Term t : terms) {
			output.println(t.getId() + "\t" + t.getTerm());
		}

		// Write the recommendations:
		List<Recommendation> recommendations;
		recommendations = new ArrayList<Recommendation>(_elementManager.getRecommendations());
		Collections.sort(recommendations);
		file = new File( _directoryName + "\\elements\\recommendations.txt");
		output = new PrintWriter(new BufferedWriter(new FileWriter(file)), true);
		
		output.println("id\tstakeholderid\tforumid\ttype\treason\tvalue");
		for (Recommendation r : recommendations) {
			output.println(r.getId() + "\t" + _elementManager.getStakeholderOfRecommendation(r).getId() + "\t" + _elementManager.getForumOfRecommendation(r).getId() + "\t" + r.getTypeOfRecommender().toString() + "\t" + r.getReasonOfRecommendation() + "\t" + r.getRecommendationValue());
		}

		
		// Write the forums, the needs of the forums and the stakeholders of the forums:
		File fileNF, fileSF;					
		PrintWriter outputNF, outputSF;	
		
		List<Forum> forums;
		forums = new ArrayList<Forum>(_elementManager.getForums());
		Collections.sort(forums);

		file = new File( _directoryName + "\\elements\\forums.txt");
		fileNF = new File( _directoryName + "\\elements\\needsofforums.txt");
		fileSF = new File( _directoryName + "\\elements\\stakeholdersofforums.txt");
		
		output = new PrintWriter(new BufferedWriter(new FileWriter(file)), true);
		outputNF = new PrintWriter(new BufferedWriter(new FileWriter(fileNF)), true);
		outputSF = new PrintWriter(new BufferedWriter(new FileWriter(fileSF)), true);
		
		output.println("id\ttitle");
		outputNF.println("forumid\tneedid\tscore");
		outputSF.println("forumid\tstakeholderid\tscore");
		
		for (Forum f : forums) {
			output.println(f.getId() + "\t" + f.getTitle());
			
			needs = new ArrayList<Need>(_elementManager.getNeedsOfForum(f));
			Collections.sort(needs);
			for (Need n : needs) {
				outputNF.println(f.getId() + "\t" + n.getId() + "\t" + _elementManager.getScoreOfNeedInForum(f, n).toString());
			}
			
			stakeholders = new ArrayList<Stakeholder>(_elementManager.getStakeholdersOfForum(f));
			Collections.sort(stakeholders);			
			for (Stakeholder s : stakeholders) {
				outputSF.println(f.getId() + "\t" + s.getId() + "\t" + _elementManager.getScoreOfStakeholderInForum(f, s).toString());
			}	
		}		
	}
	
	public void writeExtraElements() throws IOException {
		// TODO Code writing of extra elements
	}
	
	public void writeMatrices() throws IOException {
		writeMatrix(_matrixManager.getStakeholdersNeedsMatrix(true));
		writeMatrix(_matrixManager.getNeedsTermsMatrix(true));
		writeMatrix(_matrixManager.getStakeholdersTermsMatrix(true));
		writeMatrix(_matrixManager.getStakeholdersForumsMatrix(true));
		writeMatrix(_matrixManager.getNeedsForumsMatrix(true));
	}
	
	public void writeMatrix(MatrixElement matrix) throws IOException{
		File file;					
		PrintWriter output;		
		StringBuffer buffer;
		
		File dir = new File(_directoryName+"\\matrices"); dir.mkdirs();
		
		file = new File( _directoryName + "\\matrices\\" + matrix.getName() + ".csv");
		output = new PrintWriter(new BufferedWriter(new FileWriter(file)), true);
		
		int rows = matrix.getNumRows();
		int cols = matrix.getNumCols();
		double[][] matrixData = matrix.getArray();
		
		// Writes the headers
		buffer = new StringBuffer();
		for (int j=0; j<cols; j++) {
			buffer.append(","+matrix.getColumnName(j));
		}
		output.println(buffer.toString());

		for (int i=0; i<rows; i++) {
			buffer = new StringBuffer();
			buffer.append(matrix.getRowName(i));
			
			for (int j=0; j<cols; j++) {
				buffer.append("," + matrixData[i][j]);
			}
			output.println(buffer.toString());
		}		
	}
	
	public void writeUserOutput() throws IOException {
		//TODO Code user output
	}

}
