package input;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import data.basic.BasicElementManager;
import data.basic.RatingType;
import data.basic.RecommenderType;
import data.parameters.ParameterManager;

final class LoaderTextFileObj implements Loader{
	// Local Variables
	String  _directoryName;	// Directory where all the input files are
	BasicElementManager _elementManager; // Basic Element Manager

	// Constructor - Package Private
	LoaderTextFileObj(ParameterManager parameters, BasicElementManager elementManager) {
		// Integrity checks
		if (parameters==null)
			throw new IllegalArgumentException("The parameter reference has to be a valid object");
		if (elementManager==null)
			throw new IllegalArgumentException("The element manager has to be a valid object");
		if(!parameters.containsParameter("inputdirectory"))
			throw new IllegalArgumentException("The text file loader requires the name of the directory where the input files are as a parameter.");
		
		// Sets the internal fields;
		_directoryName = parameters.readParameter("inputdirectory");
		_elementManager =  elementManager;
			
	}
	
	public void loadAllElements() throws IOException {
		loadStakeholders();
		loadNeeds();
		loadRatings();
		loadForums();
		loadNeedsOfForums();
		loadStakeholdersOfForums();
		loadRecommendations();		
	}
	
	public void loadStakeholders() throws IOException{
		BufferedReader in;
		in = new BufferedReader(new FileReader(_directoryName+"\\stakeholders.txt"));

		String str, id, name, description;
		String[] strs;		
		
		// The first line has headers, so it is ignored
		str = in.readLine();
        while ((str = in.readLine()) != null) {
        	// Reads the fields
        	strs = str.split("\\t");
        	id = strs[0].trim();
        	name = strs[1].trim();
        	description = strs[2].trim();
        	
        	// Adds the element
        	_elementManager.addStakeholder(id, name, description);
        }
	}
	public void loadNeeds() throws IOException{
		BufferedReader in;
		in = new BufferedReader(new FileReader(_directoryName+"\\needs.txt"));

		String str, id, text, stakeholderId;
		String[] strs;		
		
		// The first line has headers, so it is ignored
		str = in.readLine();
        while ((str = in.readLine()) != null) {
        	// Reads the fields
        	strs = str.split("\\t");
        	id = strs[0].trim();
        	text = strs[1].trim();
        	stakeholderId = strs[2].trim();
        	
        	// Adds the element
        	_elementManager.addNeed(id, stakeholderId, text);
        }
	}
	public void loadRatings() throws IOException{
		BufferedReader in;
		in = new BufferedReader(new FileReader(_directoryName+"\\ratings.txt"));

		String str, id, stakeholderId, needId, typeStr, valueStr;
		RatingType type;
		double value;
		String[] strs;		
		
		// The first line has headers, so it is ignored
		str = in.readLine();
        while ((str = in.readLine()) != null) {
        	// Reads the fields
        	strs = str.split("\\t");
        	id = strs[0].trim();
        	stakeholderId = strs[1].trim();        	        	
        	needId = strs[2].trim();
        	typeStr = strs[3].trim();
        	valueStr = strs[4].trim();
        	
    		if (typeStr.equalsIgnoreCase("creator")) {
    			type = RatingType.Creator;
    		} else if (typeStr.equalsIgnoreCase("commenter")) {
    			type = RatingType.Commenter;
    		} else if (typeStr.equalsIgnoreCase("voter")) {
    			type = RatingType.Voter;
    		} else {
    			// This default case should never happen
    			type = RatingType.Creator;
    		}
        	
    		value = Double.parseDouble(valueStr);
        	
        	// Adds the element
        	_elementManager.addRating(id, stakeholderId, needId, type, value);
        }
	}
	public void loadForums() throws IOException {
		BufferedReader in;
		in = new BufferedReader(new FileReader(_directoryName+"\\forums.txt"));

		String str, id, title;
		String[] strs;		
		
		// The first line has headers, so it is ignored
		str = in.readLine();
        while ((str = in.readLine()) != null) {
        	// Reads the fields
        	strs = str.split("\\t");
        	id = strs[0].trim();
        	title = strs[1].trim();
        	
        	// Adds the element
        	_elementManager.addForum(id, title);
        }				
	}
	public void loadRecommendations() throws IOException{
		BufferedReader in;
		in = new BufferedReader(new FileReader(_directoryName+"\\recommendations.txt"));

		String str, id, stakeholderId, forumId, typeStr, reason, valueStr;
		RecommenderType type;
		double value;
		String[] strs;		
		
		// The first line has headers, so it is ignored
		str = in.readLine();
        while ((str = in.readLine()) != null) {
        	// Reads the fields
        	strs = str.split("\\t");
        	id = strs[0].trim();
        	stakeholderId = strs[1].trim();
        	forumId = strs[2].trim();
        	typeStr = strs[3].trim();
        	reason = strs[4].trim();
        	valueStr = strs[5].trim();
        	
    		if (typeStr.equalsIgnoreCase("content")) {
    			type = RecommenderType.Content;
    		} else if (typeStr.equalsIgnoreCase("collaborative")) {
    			type = RecommenderType.Collaborative;
    		} else if (typeStr.equalsIgnoreCase("knowledge")) {
    			type = RecommenderType.Knowledge;
    		} else if (typeStr.equalsIgnoreCase("random")) {
    			type = RecommenderType.Random;
    		} else if (typeStr.equalsIgnoreCase("randomandcollaborative")) {
    			type = RecommenderType.RandomAndCollaborative;
    		} else {
    			// This default case should never happen
    			type = RecommenderType.Content;
    		}
        	
    		value = Double.parseDouble(valueStr);
        	        	
        	// Adds the element
        	_elementManager.addRecommendation(id, stakeholderId, forumId, type, reason, value);
        }			
	}
	public void loadNeedsOfForums() throws IOException {
		BufferedReader in;
		in = new BufferedReader(new FileReader(_directoryName+"\\needsofforums.txt"));

		String str, forumId, needId, scoreStr;
		double score;
		String[] strs;		
		
		// The first line has headers, so it is ignored
		str = in.readLine();
        while ((str = in.readLine()) != null) {
        	// Reads the fields
        	strs = str.split("\\t");
        	forumId = strs[0].trim();
        	needId = strs[1].trim();
        	scoreStr = strs[2].trim();
        	
        	score = Double.parseDouble(scoreStr);
        	
        	// Adds the element
        	_elementManager.addNeedToForum(forumId, needId, score);
        }				
	}
	public void loadStakeholdersOfForums() throws IOException {
		BufferedReader in;
		in = new BufferedReader(new FileReader(_directoryName+"\\stakeholdersofforums.txt"));

		String str, forumId, stakeholderId, scoreStr;
		double score;
		String[] strs;		
		
		// The first line has headers, so it is ignored
		str = in.readLine();
        while ((str = in.readLine()) != null) {
        	// Reads the fields
        	strs = str.split("\\t");
        	forumId = strs[0].trim();
        	stakeholderId = strs[1].trim();
        	scoreStr = strs[2].trim();
        	
        	score = Double.parseDouble(scoreStr);
        	
        	// Adds the element
        	_elementManager.addStakeholderToForum(forumId, stakeholderId, score);
        }				
	}
}
