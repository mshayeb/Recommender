package data.basic;

/**
 *
 */
final class NeedObj implements Need{
	// Local variables
	String _id;
	int _number;
	String _rawText;
	
	// Constructor - package private
	NeedObj(String id, int number, String text) {
		// Integrity checks
		if (id=="")
			throw new IllegalArgumentException("The String id can't be null");
		if (number<0)
			throw new IllegalArgumentException("The number has to be >= 0");
		if (text=="")
			throw new IllegalArgumentException("The Text can't be null");
		
		// Sets the local variables
		_id=id;
		_number=number;
		_rawText=text;
		
		// Prepares the text and calculates the frequencies.
		//_frequencies = Utilities.getTermFrequencies(_rawText);
	}
	
	// Getters
	public String getId() {return _id;}
	public int getNumber() {return _number;}
	public String getRawText() {return _rawText;}

	
	public int compareTo(Need e) {
		if (_number < e.getNumber()) {
			return -1;
		} else if (_number == e.getNumber()) {
			return 0;
		} else {
			return 1;
		}
	}	
}
