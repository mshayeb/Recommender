package data.basic;

/**
 *
 */
public class TermObj implements Term{
	// Local Variables
	String _id;
	int _number;
	String _term;
	
	// Constructor - package private
	TermObj(String id, int number,  String term) {
		// Integrity checks
		if (id=="")
			throw new IllegalArgumentException("The String id can't be null");
		if (number<0)
			throw new IllegalArgumentException("The number has to be >= 0");
		if (term=="")
			throw new IllegalArgumentException("The Term can't be null");
		
		
		// Sets the local variables
		_id=id;
		_number=number;
		_term=term;
	}
	
	// Getters
	public String getId() {return _id;}
	public int getNumber() {return _number;}
	public String getTerm() {return _term;}
		
	public int compareTo(Term e) {
		if (_number < e.getNumber()) {
			return -1;
		} else if (_number == e.getNumber()) {
			return 0;
		} else {
			return 1;
		}
	}
	
}
