/**
 * 
 */
package data.basic;

import data.Element;
import java.util.HashMap;
import java.util.ArrayList;

/**
 *
 * This class holds all the basic elements (Stakeholder, Needs, Forums, Terms, Recommendations, and Ratings) that are stored in the 
 * blackboard.  All these elements are packaged in this class for easy storage and retrieval from the blackboard.
 * 
 * Note that the class is package private, since it is not meant to be used outside of this package.
 * All manipulation of the basic elements should be done via the BasicElementManager
 * 
 */

class BasicElements implements Element{
	// Local Variables
	String nameId;
	
	// Data structures that contain the basic elements of the models - since they can be indexed by String id or by number they are duplicated
	HashMap<String, Stakeholder> stakeholders;
	HashMap<String, Need> needs;
	HashMap<String, Term> terms;
	HashMap<String, Rating> ratings;
	HashMap<String, Forum> forums;
	HashMap<String, Recommendation> recommendations;
	
	HashMap<Integer, Stakeholder> stakeholdersN;
	HashMap<Integer, Need> needsN;
	HashMap<Integer, Term> termsN;
	HashMap<Integer, Rating> ratingsN;
	HashMap<Integer, Forum> forumsN;
	HashMap<Integer, Recommendation> recommendationsN;
	
	// Data structures that represent the relationships between the elements
	// A Stakeholder has many needs
	HashMap<Stakeholder, ArrayList<Need>> needsByStakeholder;
	// A Need was entered by one Stakeholder
	HashMap<Need, Stakeholder> stakeholderByNeed;
	// A Stakeholder can have many recommendations
	HashMap<Stakeholder, ArrayList<Recommendation>> recommendationsByStakeholder;
	// A recommendation is made to one stakeholder
	HashMap<Recommendation, Stakeholder> stakeholderByRecommendation;
	// A recommendation is for one Forum
	HashMap<Recommendation, Forum> forumByRecommendation;
	// A Forum can be recommended in multiple recommendations
	HashMap<Forum, ArrayList<Recommendation>> recommendationsByForum;
	// A Forum contains many Needs, and each need has a score indicating how well it belongs to the forum
	HashMap<Forum, HashMap<Need, Double>> needsAndScoresByForum;
	// A Need belongs to one or more Forum, and each need has a score indicating how well it belongs - allows for fuzzy clustering
	HashMap<Need, HashMap<Forum, Double>> forumsByNeed;
	// A Forum contains many Stakeholders, and each has a score indicating the 'rating' the stakeholder 'gave' the forum
	HashMap<Forum, HashMap<Stakeholder, Double>> stakeholdersAndScoresByForum;
	// A Stakeholder can be in many Forums
	HashMap<Stakeholder, ArrayList<Forum>> forumsByStakeholder;
	
	// A Stakeholder can have many Ratings
	HashMap<Stakeholder, ArrayList<Rating>> ratingsByStakeholder;
	// A Rating is entered by one Stakeholder
	HashMap<Rating, Stakeholder> stakeholderByRating;
	// A Rating is for one Need
	HashMap<Rating, Need> needByRating;
	// A Need can have many Ratings
	HashMap<Need, ArrayList<Rating>> ratingsByNeed;
	// A Need contains many Terms, and each one has a frequency
	HashMap<Need, HashMap<Term, Double>> termsByNeed;
	// A Term can be in many Needs, and have a different frequency in each one
	HashMap<Term, HashMap<Need, Double>> needsByTerm;	
	
	// Constructor - Package Private
	BasicElements(String name) {
		// Integrity checks
		if (name=="")
			throw new IllegalArgumentException("The name can't be null");
		
		// Sets the internal fields;
		nameId = name;
		
		// Creates all the required collections
		stakeholders = new HashMap<String, Stakeholder>();
		needs = new HashMap<String, Need>();
		terms = new HashMap<String, Term>();
		ratings = new HashMap<String, Rating>();
		forums = new HashMap<String, Forum>();
		recommendations = new HashMap<String, Recommendation>();
		stakeholdersN = new HashMap<Integer, Stakeholder>();
		needsN = new HashMap<Integer, Need>();
		termsN = new HashMap<Integer, Term>();
		ratingsN = new HashMap<Integer, Rating>();
		forumsN = new HashMap<Integer, Forum>();
		recommendationsN = new HashMap<Integer, Recommendation>();
		needsByStakeholder = new HashMap<Stakeholder, ArrayList<Need>>();
		stakeholderByNeed = new HashMap<Need, Stakeholder>();
		recommendationsByStakeholder = new HashMap<Stakeholder, ArrayList<Recommendation>>();
		stakeholderByRecommendation = new HashMap<Recommendation, Stakeholder>();
		forumByRecommendation = new HashMap<Recommendation, Forum>();
		recommendationsByForum = new HashMap<Forum, ArrayList<Recommendation>>();
		needsAndScoresByForum = new HashMap<Forum, HashMap<Need,Double>>();
		forumsByNeed = new HashMap<Need, HashMap<Forum,Double>>();
		stakeholdersAndScoresByForum = new HashMap<Forum, HashMap<Stakeholder,Double>>();
		forumsByStakeholder = new HashMap<Stakeholder, ArrayList<Forum>>();
		ratingsByStakeholder = new HashMap<Stakeholder, ArrayList<Rating>>();
		stakeholderByRating = new HashMap<Rating, Stakeholder>();
		needByRating = new HashMap<Rating, Need>();
		ratingsByNeed = new HashMap<Need, ArrayList<Rating>>();
		termsByNeed = new HashMap<Need, HashMap<Term,Double>>();
		needsByTerm = new HashMap<Term, HashMap<Need,Double>>();
		
	}
	
	// Method from the Element interface
	public String getName() {return nameId;}
	
	
}
