package data.basic;

import java.util.Collection;

/**
 *
 * Interface for the Basic Element Manager.
 * This interface provides an abstraction layer to the blackboard so that it is easy to add, 
 * get and query the basic elements (stakeholders, needs, forums, ratings and recommendations)
 * 
 * Note that all the basic elements have two identifiers: 
 * 		1) String id - represents the unique identifier that the users have given the basic element
 * 		2) int number - represents the unique number that was auto-calculated when the element was entered
 * In all the methods used by the basic element manager the String id is used.
 * The number identifier is used to index the matrices that are later created from the basic elements
 */
public interface BasicElementManager {
	public String getId();

	// These methods add the basic elements 
	public void addStakeholder(String id,  String name, String description);
	public void addNeed(String id, String stakeholderId, String text);
	public void addRating(String id, String stakeholderId, String needId, RatingType type, double value);
	public void addForum(String id, String title);
	public void addRecommendation(String id, String stakeholderId, String forumId, RecommenderType type, String reason, double recommendationValue);

	// These methods link the basic elements together
	public void addNeedToForum(String forumId, String needId, Double score);
	public void addStakeholderToForum(String forumId, String stakeholderId, Double score);

	// Individual Getters - by String id
	public Stakeholder getStakeholder(String id);
	public Need getNeed(String id);
	public Term getTerm(String id);
	public Rating getRating(String id);
	public Forum getForum(String id);
	public Recommendation getRecommendation(String id);	
		
	// Individual Getters - by Number id
	public Stakeholder getStakeholder(int number);
	public Need getNeed(int number);
	public Forum getForum(int number);
	public Recommendation getRecommendation(int number);	
	public Term getTerm(int number);
	public Rating getRating(int number);

	// Collection Getters
	public Collection<Stakeholder> getStakeholders();
	public Collection<Need> getNeeds();
	public Collection<Forum> getForums();
	public Collection<Recommendation> getRecommendations();	
	public Collection<Term> getTerms();
	public Collection<Rating> getRatings();
	
	// Queries
	public Collection<Need> getNeedsByStakeholder(Stakeholder s);
	public Stakeholder getStakeholderOfNeed(Need n);
	public Collection<Recommendation> getRecommendationsForStakeholder(Stakeholder s);
	public Stakeholder getStakeholderOfRecommendation(Recommendation r);
	public Forum getForumOfRecommendation(Recommendation r);
	public Collection<Recommendation> getRecommendationsByForum (Forum f);
	public Collection<Need> getNeedsOfForum(Forum f);
	public Collection<Forum> getForumsOfNeed(Need n); 
	public Collection<Stakeholder> getStakeholdersOfForum(Forum f);
	public Collection<Forum> getForumsOfStakeholder(Stakeholder s);
	public Double getScoreOfNeedInForum(Forum f, Need n);
	public Double getScoreOfStakeholderInForum(Forum f, Stakeholder s);
	public Collection<Rating> getRatingsByStakeholder(Stakeholder s);
	public Stakeholder getStakeholderOfRating(Rating r);
	public Need getNeedOfRating(Rating r);
	public Collection<Rating> getRatingsByNeed(Need n);
	public Collection<Term> getTermsByNeed(Need n);
	public Collection<Need> getNeedsByTerm(Term t);
	public Double getScoreOfTermInNeed(Need n, Term t);
		
}
