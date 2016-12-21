package data.basic;

/**
 *
 */
public interface Rating extends Comparable<Rating> {
	// Getters
	public String getId();
	public int getNumber();
	public RatingType getTypeOfRating();
	public double getValue();
}
