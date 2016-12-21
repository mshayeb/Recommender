package data.basic;


public interface Recommendation extends Comparable<Recommendation>{
	// Getters
	public String getId();
	public int getNumber();
	public RecommenderType getTypeOfRecommender();
	public String getReasonOfRecommendation();
	public double getRecommendationValue();
}
