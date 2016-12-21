package data.basic;

/**
 *
 */
public class RatingObj implements Rating{
	// Local Variables
	String _id;
	int _number;
	RatingType _type;
	double _value;
	
	// Constructor - package private
	RatingObj(String id, int number, RatingType type, double value) {
		// Integrity checks
		if (id=="")
			throw new IllegalArgumentException("The String id can't be null");
		if (number<0)
			throw new IllegalArgumentException("The number has to be >= 0");
		if (type==null)
			throw new IllegalArgumentException("The Type can't be null");
		if (value<0)
			throw new IllegalArgumentException("The value has to be >= 0");
		
		
		// Sets the local variables
		_id=id;
		_number=number;
		_type=type;
		_value=value;
	}
	
	// Getters
	public String getId() {return _id;}
	public int getNumber() {return _number;}
	public RatingType getTypeOfRating() {return _type;}
	public double getValue() {return _value;}
		
	public int compareTo(Rating e) {
		if (_number < e.getNumber()) {
			return -1;
		} else if (_number == e.getNumber()) {
			return 0;
		} else {
			return 1;
		}
	}
}
