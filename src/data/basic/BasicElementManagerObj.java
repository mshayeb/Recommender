package data.basic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import blackboard.Blackboard;
import utils.Utilities;

/**
 *  
 * Class for the Basic Element Manager.
 * This class provides an abstraction layer to the blackboard so that it is easy to add, 
 * get and query the basic elements (stakeholders, needs, forums, ratings and recommendations)
 * 
 * Note that all the basic elements have two identifiers: 
 * 		1) String id - represents the unique identifier that the users have given the basic element
 * 		2) int number - represents the unique number that was auto-calculated when the element was entered
 * In all the methods used by the basic element manager the String id is used.
 * The number identifier is used to index the matrices that are later created from the basic elements
 */

final class BasicElementManagerObj implements BasicElementManager{
	// Local Variables
	String _id;		// Identifier of the object.  This is used to name the elements in the blackboard
	Blackboard _bb;	// Reference to the blackboard object

	// Variables that keep track of the number id of each element
	int stakeholderNumber = 0;
	int needNumber = 0;
	int termNumber = 0;
	int ratingNumber = 0;
	int forumNumber = 0;
	int recommendationNumber = 0;

	// Constructor - Package Private
	BasicElementManagerObj(String id, Blackboard blackboard) {
		// Integrity checks
		if (id=="")
			throw new IllegalArgumentException("The string identifier can't be null");
		if (blackboard==null)
			throw new IllegalArgumentException("The blackboard reference has to be a valid object");
		
		// Sets the internal fields;
		_id = id;
		_bb = blackboard;
		
		// Creates the required data structures and saves them in the blackboard.
		BasicElements elements = new BasicElements(_id + "_basicElements");
		_bb.store(_id + "_basicElements", elements);
	}
			
	// Returns the id of the basic element manager - the id is used to name the elements in the blackboard
	public String getId() {return _id;}

	// These methods add the basic elements 
	public void addStakeholder(String id,  String name, String description) {
		// Gets the basic elements from the blackboard
		BasicElements elements = (BasicElements) _bb.get(_id + "_basicElements");
		
		// Checks if the string id is unique
		if (elements.stakeholders.containsKey(id)) {
			throw new IllegalArgumentException("There is already a Stakeholder with this String id in the model.");
		}
				
		// Creates a new stakeholder - note that the constructor checks the other fields
		Stakeholder s = new StakeholderObj(id, stakeholderNumber, name, description); 
		
		// Updates the corresponding collections
		elements.stakeholders.put(s.getId(), s);
		elements.stakeholdersN.put(s.getNumber(), s);
		elements.needsByStakeholder.put(s, new ArrayList<Need>());
		elements.recommendationsByStakeholder.put(s, new ArrayList<Recommendation>());
		elements.forumsByStakeholder.put(s, new ArrayList<Forum>());
		elements.ratingsByStakeholder.put(s, new ArrayList<Rating>());
	
		// Increases the stakeholder number for the next one
		stakeholderNumber++;		
	}
	public void addNeed(String id, String stakeholderId,  String text) {
		// Gets the basic elements from the blackboard
		BasicElements elements = (BasicElements) _bb.get(_id + "_basicElements");
		
		// Checks if the string id is unique
		if (elements.needs.containsKey(id)) {
			throw new IllegalArgumentException("There is already a Need with this String id in the model.");
		} else if (!elements.stakeholders.containsKey(stakeholderId)) {
			throw new IllegalArgumentException("The Stakeholder is not in the model.");
		}
		 
		// Creates a new need - note that the constructor checks the other fields
		Need n = new NeedObj(id, needNumber, text);
		Stakeholder s = elements.stakeholders.get(stakeholderId);
		
		// Updates the corresponding collections
		elements.needs.put(n.getId(), n);
		elements.needsN.put(n.getNumber(), n);
		elements.stakeholderByNeed.put(n, s);		
		elements.needsByStakeholder.get(s).add(n);
		elements.forumsByNeed.put(n, new HashMap<Forum, Double>());
		elements.ratingsByNeed.put(n, new ArrayList<Rating>());
		elements.termsByNeed.put(n, new HashMap<Term, Double>());
		
		// The need now has to be broken down into terms
		HashMap<String, Integer> termFrequencies = Utilities.getTermFrequencies(n.getRawText());
		for(String term : termFrequencies.keySet()) {
			addTerm(term, n.getId(), term, (double) termFrequencies.get(term));
		}
		
		// Increases the need number for the next one
		needNumber++;		
		
	}
	private void addTerm(String id, String needId, String text, Double frequency) {
		// Gets the basic elements from the blackboard
		BasicElements elements = (BasicElements) _bb.get(_id + "_basicElements");

		// Checks if the terms already exists in the terms of the model
		if (!elements.terms.containsKey(id)) {
			// The term does NOT exists
			
			// Creates a new term
			Term t = new TermObj(id, termNumber, text);
			Need n = elements.needs.get(needId);
			
			// Updates the corresponding collections
			elements.terms.put(t.getId(), t);
			elements.termsN.put(t.getNumber(), t);
			elements.termsByNeed.get(n).put(t, frequency);
			elements.needsByTerm.put(t, new HashMap<Need, Double>());
			elements.needsByTerm.get(t).put(n, frequency);
			
			// Increases the term number for the next one
			termNumber++;
			
		} else {
			// The term already exists
			
			// Gets the term & the need
			Term t = elements.terms.get(id);
			Need n = elements.needs.get(needId);

			// Updates the corresponding collections
			elements.termsByNeed.get(n).put(t, frequency);
			elements.needsByTerm.get(t).put(n, frequency);
		}
	}	 
	public void addRating(String id, String stakeholderId, String needId, RatingType type, double value) {
		// Gets the basic elements from the blackboard
		BasicElements elements = (BasicElements) _bb.get(_id + "_basicElements");

		// Checks if the string id is unique
		if (elements.ratings.containsKey(id)) {
			throw new IllegalArgumentException("There is already a Rating with this String id in the model.");
		} else if (!elements.stakeholders.containsKey(stakeholderId)) {
			throw new IllegalArgumentException("The Stakeholder is not in the model.");
		} else if (!elements.needs.containsKey(needId)) {
			throw new IllegalArgumentException("The Need is not in the model.");
		}
		
		// Creates a new rating - note that the constructor checks the other fields
		Rating r = new RatingObj(id, ratingNumber, type, value);
		Stakeholder s = elements.stakeholders.get(stakeholderId);
		Need n = elements.needs.get(needId);
		
		// Updates the corresponding collections
		elements.ratings.put(r.getId(), r);
		elements.ratingsN.put(r.getNumber(), r);
		elements.ratingsByStakeholder.get(s).add(r);
		elements.ratingsByNeed.get(n).add(r);
		elements.stakeholderByRating.put(r, s);
		elements.needByRating.put(r, n);
	
		// Increases the rating number for the next one
		ratingNumber++;		
	}
	public void addForum(String id, String title) {
		// Gets the basic elements from the blackboard
		BasicElements elements = (BasicElements) _bb.get(_id + "_basicElements");

		// Checks if the string id is unique
		if (elements.forums.containsKey(id)) {
			throw new IllegalArgumentException("There is already a Forum with this id in the model.");
		}
		
		// Creates a new forum - note that the constructor checks the other fields
		Forum f = new ForumObj(id, forumNumber, title);
		
		// Updates the corresponding collections
		elements.forums.put(f.getId(), f);
		elements.forumsN.put(f.getNumber(), f);
		elements.recommendationsByForum.put(f, new ArrayList<Recommendation>());
		elements.needsAndScoresByForum.put(f, new HashMap<Need, Double>());
		elements.stakeholdersAndScoresByForum.put(f, new HashMap<Stakeholder, Double>());
		
		// Increases the forum number for the next one
		forumNumber++;			
	}
	public void addRecommendation(String id, String stakeholderId, String forumId, RecommenderType type, String reason, double recommendationValue){
		// Gets the basic elements from the blackboard
		BasicElements elements = (BasicElements) _bb.get(_id + "_basicElements");
		
		// Checks if the string id is unique
		if (elements.recommendations.containsKey(id)) {
			throw new IllegalArgumentException("There is already a Recommendation with this id in the model.");
		} else if(!elements.stakeholders.containsKey(stakeholderId) ) {
			throw new IllegalArgumentException("The Stakeholder is not in the model.");
		} else if (!elements.forums.containsKey(forumId) ) {
			throw new IllegalArgumentException("The Forum is not in the model.");
		}

		// Creates a new recommendation - note that the constructor checks the other fields
		Recommendation r = new RecommendationObj(id, recommendationNumber, type, reason, recommendationValue);
		Stakeholder s = elements.stakeholders.get(stakeholderId);
		Forum f = elements.forums.get(forumId);
		
		// Updates the corresponding collections		
		elements.recommendations.put(r.getId(), r);
		elements.recommendationsN.put(r.getNumber(), r);
		elements.recommendationsByStakeholder.get(s).add(r);
		elements.stakeholderByRecommendation.put(r, s);
		elements.forumByRecommendation.put(r, f);
		elements.recommendationsByForum.get(f).add(r);
		
		// Increases the recommendation number for the next one
		recommendationNumber++;				
	}

	// These methods link the basic elements together
	public void addNeedToForum(String forumId, String needId, Double score) {
		// Gets the basic elements from the blackboard
		BasicElements elements = (BasicElements) _bb.get(_id + "_basicElements");
		
		// Checks if the string id is unique		
		if (!elements.forums.containsKey(forumId) ) {
			throw new IllegalArgumentException("The Forum is not in the model.");
		} else if(!elements.needs.containsKey(needId) ) {
			throw new IllegalArgumentException("The Need is not in the model.");
		}
		
		// Gets the elements
		Forum f = elements.forums.get(forumId);
		Need n = elements.needs.get(needId);
		
		// Updates the corresponding collections				
		elements.forumsByNeed.get(n).put(f, score);
		elements.needsAndScoresByForum.get(f).put(n, score);
	}
	public void addStakeholderToForum(String forumId, String stakeholderId, Double score) {
		// Gets the basic elements from the blackboard
		BasicElements elements = (BasicElements) _bb.get(_id + "_basicElements");
		
		if (!elements.forums.containsKey(forumId) ) {
			throw new IllegalArgumentException("The Forum is not in the model.");
		} else if(!elements.stakeholders.containsKey(stakeholderId) ) {
			throw new IllegalArgumentException("The Stakeholder is not in the model.");
		}
		
		// Gets the elements
		Forum f = elements.forums.get(forumId);
		Stakeholder s = elements.stakeholders.get(stakeholderId);
		
		// elements.forumsByStakeholder.get(s).add(f);
		// elements.stakeholdersAndScoresByForum.get(f).put(s, score);

		// Updates the corresponding collections				
		// In case the element is already there, then it does not get added again, only the membership score is updated
		if (!elements.forumsByStakeholder.get(s).contains(f)) {
			elements.forumsByStakeholder.get(s).add(f);
		} 
		elements.stakeholdersAndScoresByForum.get(f).put(s, score);
	
	}

	// Individual Getters - by String id
	public Stakeholder getStakeholder(String id) {
		// Gets the basic elements from the blackboard
		BasicElements elements = (BasicElements) _bb.get(_id + "_basicElements");
		
		if (elements.stakeholders.containsKey(id)) {
			return elements.stakeholders.get(id);
		} else {
			throw new IllegalArgumentException("The String id does not correspond to a Stakeholder in the model.");
		}
	}
	public Need getNeed(String id) {
		// Gets the basic elements from the blackboard
		BasicElements elements = (BasicElements) _bb.get(_id + "_basicElements");

		if (elements.needs.containsKey(id)) {
			return elements.needs.get(id);
		} else {
			throw new IllegalArgumentException("The String id does not correspond to a Need in the model.");
		}
	}
	public Forum getForum(String id) {
		// Gets the basic elements from the blackboard
		BasicElements elements = (BasicElements) _bb.get(_id + "_basicElements");

		if (elements.forums.containsKey(id)) {
			return elements.forums.get(id);
		} else {
			throw new IllegalArgumentException("The String id does not correspond to a Forum in the model.");
		}
	}
	public Recommendation getRecommendation(String id) {
		// Gets the basic elements from the blackboard
		BasicElements elements = (BasicElements) _bb.get(_id + "_basicElements");

		if (elements.recommendations.containsKey(id)) {
			return elements.recommendations.get(id);
		} else {
			throw new IllegalArgumentException("The String id does not correspond to a Recommendation in the model.");
		}
	}		
	public Term getTerm(String id) {
		// Gets the basic elements from the blackboard
		BasicElements elements = (BasicElements) _bb.get(_id + "_basicElements");

		if (elements.terms.containsKey(id)) {
			return elements.terms.get(id);
		} else {
			throw new IllegalArgumentException("The String id does not correspond to a Term in the model.");
		}		
	}
	public Rating getRating(String id) {
		// Gets the basic elements from the blackboard
		BasicElements elements = (BasicElements) _bb.get(_id + "_basicElements");

		if (elements.ratings.containsKey(id)) {
			return elements.ratings.get(id);
		} else {
			throw new IllegalArgumentException("The String id does not correspond to a Rating in the model.");
		}		
	}
		
	// Individual Getters - by Number id
	public Stakeholder getStakeholder(int number) {
		// Gets the basic elements from the blackboard
		BasicElements elements = (BasicElements) _bb.get(_id + "_basicElements");
		
		if (elements.stakeholdersN.containsKey(number)) {
			return elements.stakeholdersN.get(number);
		} else {
			throw new IllegalArgumentException("The String id does not correspond to a Stakeholder in the model.");
		}
	}
	public Need getNeed(int number) {
		// Gets the basic elements from the blackboard
		BasicElements elements = (BasicElements) _bb.get(_id + "_basicElements");

		if (elements.needsN.containsKey(number)) {
			return elements.needsN.get(number);
		} else {
			throw new IllegalArgumentException("The String id does not correspond to a Need in the model.");
		}
	}
	public Forum getForum(int number) {
		// Gets the basic elements from the blackboard
		BasicElements elements = (BasicElements) _bb.get(_id + "_basicElements");

		if (elements.forumsN.containsKey(number)) {
			return elements.forumsN.get(number);
		} else {
			throw new IllegalArgumentException("The String id does not correspond to a Forum in the model.");
		}
	}
	public Recommendation getRecommendation(int number) {
		// Gets the basic elements from the blackboard
		BasicElements elements = (BasicElements) _bb.get(_id + "_basicElements");

		if (elements.recommendationsN.containsKey(number)) {
			return elements.recommendationsN.get(number);
		} else {
			throw new IllegalArgumentException("The String id does not correspond to a Recommendation in the model.");
		}
	}		
	public Term getTerm(int number) {
		// Gets the basic elements from the blackboard
		BasicElements elements = (BasicElements) _bb.get(_id + "_basicElements");

		if (elements.termsN.containsKey(number)) {
			return elements.termsN.get(number);
		} else {
			throw new IllegalArgumentException("The String id does not correspond to a Term in the model.");
		}		
	}
	public Rating getRating(int number) {
		// Gets the basic elements from the blackboard
		BasicElements elements = (BasicElements) _bb.get(_id + "_basicElements");

		if (elements.ratingsN.containsKey(number)) {
			return elements.ratingsN.get(number);
		} else {
			throw new IllegalArgumentException("The String id does not correspond to a Rating in the model.");
		}		
	}
	
	// Collection Getters
	public Collection<Stakeholder> getStakeholders(){
		BasicElements elements = (BasicElements) _bb.get(_id + "_basicElements");		
		return Collections.unmodifiableCollection(elements.stakeholders.values());
	}
	public Collection<Need> getNeeds(){
		BasicElements elements = (BasicElements) _bb.get(_id + "_basicElements");
		return Collections.unmodifiableCollection(elements.needs.values());
	}
	public Collection<Forum> getForums(){
		BasicElements elements = (BasicElements) _bb.get(_id + "_basicElements");
		return Collections.unmodifiableCollection(elements.forums.values());
	}
	public Collection<Recommendation> getRecommendations(){
		BasicElements elements = (BasicElements) _bb.get(_id + "_basicElements");
		return Collections.unmodifiableCollection(elements.recommendations.values());
	}		
	public Collection<Term> getTerms(){
		BasicElements elements = (BasicElements) _bb.get(_id + "_basicElements");
		return Collections.unmodifiableCollection(elements.terms.values());		
	}
	public Collection<Rating> getRatings(){
		BasicElements elements = (BasicElements) _bb.get(_id + "_basicElements");
		return Collections.unmodifiableCollection(elements.ratings.values());
	}
	
	// Queries
	public Collection<Need> getNeedsByStakeholder(Stakeholder s){
		BasicElements elements = (BasicElements) _bb.get(_id + "_basicElements");
		if (!elements.stakeholders.containsValue(s) ) {
			throw new IllegalArgumentException("The Stakeholder is not in the model.");
		}
		return Collections.unmodifiableCollection(elements.needsByStakeholder.get(s));
	}	
	public Stakeholder getStakeholderOfNeed(Need n){
		BasicElements elements = (BasicElements) _bb.get(_id + "_basicElements");
		if (!elements.needs.containsValue(n) ) {
			throw new IllegalArgumentException("The Need is not in the model.");
		}
		return elements.stakeholderByNeed.get(n);
	}	
	public Collection<Recommendation> getRecommendationsForStakeholder (Stakeholder s){
		BasicElements elements = (BasicElements) _bb.get(_id + "_basicElements");
		if (!elements.stakeholders.containsValue(s) ) {
			throw new IllegalArgumentException("The Stakeholder is not in the model.");
		}
		return Collections.unmodifiableCollection(elements.recommendationsByStakeholder.get(s));
	}
	public Stakeholder getStakeholderOfRecommendation(Recommendation r) {
		BasicElements elements = (BasicElements) _bb.get(_id + "_basicElements");
		if (!elements.recommendations.containsValue(r) ) {
			throw new IllegalArgumentException("The Recommendation is not in the model.");
		}
		return elements.stakeholderByRecommendation.get(r);		
	}	
	public Forum getForumOfRecommendation(Recommendation r){
		BasicElements elements = (BasicElements) _bb.get(_id + "_basicElements");
		if (!elements.recommendations.containsValue(r) ) {
			throw new IllegalArgumentException("The Recommendation is not in the model.");
		}
		return elements.forumByRecommendation.get(r);
	}
	public Collection<Recommendation> getRecommendationsByForum (Forum f){
		BasicElements elements = (BasicElements) _bb.get(_id + "_basicElements");
		if (!elements.forums.containsValue(f) ) {
			throw new IllegalArgumentException("The Forum is not in the model.");
		}
		return Collections.unmodifiableCollection(elements.recommendationsByForum.get(f));
	}	
	public Collection<Need> getNeedsOfForum(Forum f){
		BasicElements elements = (BasicElements) _bb.get(_id + "_basicElements");
		if (!elements.forums.containsValue(f) ) {
			throw new IllegalArgumentException("The Forum is not in the model.");
		}		
		return Collections.unmodifiableCollection(elements.needsAndScoresByForum.get(f).keySet());
	}	
	public Collection<Forum> getForumsOfNeed(Need n) {
		BasicElements elements = (BasicElements) _bb.get(_id + "_basicElements");
		if (!elements.needs.containsValue(n) ) {
			throw new IllegalArgumentException("The Need is not in the model.");
		} else if (!elements.forumsByNeed.containsKey(n)) {
			throw new IllegalArgumentException("The Need is not yet in any Forum.");
		}		
		return Collections.unmodifiableCollection(elements.forumsByNeed.get(n).keySet());
	}
	public Collection<Stakeholder> getStakeholdersOfForum(Forum f){
		BasicElements elements = (BasicElements) _bb.get(_id + "_basicElements");
		if (!elements.forums.containsValue(f) ) {
			throw new IllegalArgumentException("The Forum is not in the model.");
		}
		return Collections.unmodifiableCollection(elements.stakeholdersAndScoresByForum .get(f).keySet());
	}	
	public Collection<Forum> getForumsOfStakeholder(Stakeholder s){
		BasicElements elements = (BasicElements) _bb.get(_id + "_basicElements");
		if (!elements.stakeholders.containsValue(s) ) {
			throw new IllegalArgumentException("The Stakeholder is not in the model.");
		}
		return Collections.unmodifiableCollection(elements.forumsByStakeholder.get(s));
	}
	public Double getScoreOfNeedInForum(Forum f, Need n){
		BasicElements elements = (BasicElements) _bb.get(_id + "_basicElements");
		if (!elements.forums.containsValue(f) ) {
			throw new IllegalArgumentException("The Forum is not in the model.");
		} else if (!elements.needs.containsValue(n) ) {
			throw new IllegalArgumentException("The Need is not in the model.");
		} else if (!elements.needsAndScoresByForum.get(f).containsKey(n)) {
			throw new IllegalArgumentException("The Need is not in the Forum and therefore has no score.");
		}	
		return elements.needsAndScoresByForum.get(f).get(n);
	}
	public Double getScoreOfStakeholderInForum(Forum f, Stakeholder s){
		BasicElements elements = (BasicElements) _bb.get(_id + "_basicElements");
		if (!elements.forums.containsValue(f) ) {
			throw new IllegalArgumentException("The Forum is not in the model.");
		} else if (!elements.stakeholders.containsValue(s) ) {
			throw new IllegalArgumentException("The Stakeholder is not in the model.");
		} else if (!elements.stakeholdersAndScoresByForum.get(f).containsKey(s)) {
			throw new IllegalArgumentException("The Stakeholder is not in the Forum and therefore has no score.");
		}
		return elements.stakeholdersAndScoresByForum.get(f).get(s);
	}
	public Collection<Rating> getRatingsByStakeholder(Stakeholder s){
		BasicElements elements = (BasicElements) _bb.get(_id + "_basicElements");
		if (!elements.stakeholders.containsValue(s) ) {
			throw new IllegalArgumentException("The Stakeholder is not in the model.");
		}
		return Collections.unmodifiableCollection(elements.ratingsByStakeholder.get(s));
	}
	public Stakeholder getStakeholderOfRating(Rating r) {
		BasicElements elements = (BasicElements) _bb.get(_id + "_basicElements");
		if (!elements.ratings.containsValue(r) ) {
			throw new IllegalArgumentException("The Rating is not in the model.");
		}
		return elements.stakeholderByRating.get(r);
	}
	public Need getNeedOfRating(Rating r) {
		BasicElements elements = (BasicElements) _bb.get(_id + "_basicElements");
		if (!elements.ratings.containsValue(r) ) {
			throw new IllegalArgumentException("The Rating is not in the model.");
		}
		return elements.needByRating.get(r);
	}
	public Collection<Rating> getRatingsByNeed(Need n) {
		BasicElements elements = (BasicElements) _bb.get(_id + "_basicElements");
		if (!elements.needs.containsValue(n) ) {
			throw new IllegalArgumentException("The Need is not in the model.");
		}
		return Collections.unmodifiableCollection(elements.ratingsByNeed.get(n));
	}
	public Collection<Term> getTermsByNeed(Need n) {
		BasicElements elements = (BasicElements) _bb.get(_id + "_basicElements");
		if (!elements.needs.containsValue(n) ) {
			throw new IllegalArgumentException("The Need is not in the model.");
		}
		return Collections.unmodifiableCollection(elements.termsByNeed.get(n).keySet());
	}
	public Collection<Need> getNeedsByTerm(Term t) {
		BasicElements elements = (BasicElements) _bb.get(_id + "_basicElements");
		if (!elements.terms.containsValue(t) ) {
			throw new IllegalArgumentException("The Term is not in the model.");
		}
		return Collections.unmodifiableCollection(elements.needsByTerm.get(t).keySet());

	}
	public Double getScoreOfTermInNeed(Need n, Term t) {
		BasicElements elements = (BasicElements) _bb.get(_id + "_basicElements");
		if (!elements.needs.containsValue(n) ) {
			throw new IllegalArgumentException("The Need is not in the model.");
		} else if (!elements.terms.containsValue(t) ) {
			throw new IllegalArgumentException("The Term is not in the model.");
		} else if (!elements.termsByNeed.get(n).containsKey(t)) {
		throw new IllegalArgumentException("The Term is not in the Need and therefore has no score.");
	}
	return elements.termsByNeed.get(n).get(t);
	}
	
}
