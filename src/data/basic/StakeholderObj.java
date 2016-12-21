package data.basic;

/**
 *
 */
final class StakeholderObj implements Stakeholder {
	// Local Variables
	String _id;
	int _number;
	String _name;
	String _description;
	
	// Constructor - package private
	StakeholderObj(String id, int number,  String name, String description) {
		// Integrity checks
		if (id=="")
			throw new IllegalArgumentException("The String id can't be null");
		if (number<0)
			throw new IllegalArgumentException("The number has to be >= 0");
		if (name=="")
			throw new IllegalArgumentException("The Name can't be null");
		if (description=="")
			throw new IllegalArgumentException("The Description can't be null");
		
		
		// Sets the local variables
		_id=id;
		_number=number;
		_name=name;
		_description=description;
	}
	
	// Getters
	public String getId() {return _id;}
	public int getNumber() {return _number;}
	public String getName() {return _name;}
	public String getDescription() {return _description;}
		
	public int compareTo(Stakeholder e) {
		if (_number < e.getNumber()) {
			return -1;
		} else if (_number == e.getNumber()) {
			return 0;
		} else {
			return 1;
		}
	}
}
