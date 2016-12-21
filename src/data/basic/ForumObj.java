package data.basic;

/**
 *
 */
final class ForumObj implements Forum{
	// Local variables
	String _id;
	int _number;
	String _title;
	
	
	// Constructor - package private
	ForumObj(String id, int number, String title) {
		// Integrity checks
		if (id=="")
			throw new IllegalArgumentException("The String id can't be null");
		if (number<0)
			throw new IllegalArgumentException("The number has to be >= 0");
		if (title=="")
			throw new IllegalArgumentException("The Text can't be null");
		
		// Sets the local variables
		_id=id;
		_number=number;
		_title=title;		
	}
	
	// Getters
	public String getId() {return _id;}
	public int getNumber() {return _number;}
	public String getTitle() {return _title;}
	
	public int compareTo(Forum e) {
		if (_number < e.getNumber()) {
			return -1;
		} else if (_number == e.getNumber()) {
			return 0;
		} else {
			return 1;
		}
	}	
}
