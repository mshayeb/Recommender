package data.basic;

/**
 *
 */
final class RecommendationObj implements Recommendation {
	// Local variables
	String _id;
	int _number;
	RecommenderType _type;
	String _reason;
	double _recommendationValue;
	
	// Constructor - package private
	RecommendationObj(String id, int number, RecommenderType type, String reason, double recommendationValue) {
		// Integrity checks
		if (id=="")
			throw new IllegalArgumentException("The String id can't be null");
		if (number<0)
			throw new IllegalArgumentException("The number has to be >= 0");
		if (type==null)
			throw new IllegalArgumentException("The RecommenderType can't be null");
		if (reason=="")
			throw new IllegalArgumentException("The Reason can't be null");
		//if (recommendationValue<0)
		//	throw new IllegalArgumentException("The recommendation value can't be a negative number");
		
		// Sets the local variables
		_id=id;
		_number=number;
		_type=type;
		_reason=reason;
		_recommendationValue=recommendationValue;
	}	
	
	public String getId() {return _id;}
	public int getNumber() {return _number;}
	public RecommenderType getTypeOfRecommender() {return _type;}
	public String getReasonOfRecommendation() {return _reason;}
	public double getRecommendationValue() {return _recommendationValue;}
	
	public int compareTo(Recommendation e) {
		if (_number < e.getNumber()) {
			return -1;
		} else if (_number == e.getNumber()) {
			return 0;
		} else {
			return 1;
		}
	}		
}
