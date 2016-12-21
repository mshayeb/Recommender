package data.parameters;

import blackboard.Blackboard;

final class ParameterManagerObj implements ParameterManager{
	// Local Variables
	String _id;		// Identifier of the object.  This is used to name the elements in the blackboard
	Blackboard _bb;	// Reference to the blackboard object

	// Constructor - Package Private
	ParameterManagerObj(String id, Blackboard blackboard) {
		// Integrity checks
		if (id=="")
			throw new IllegalArgumentException("The string identifier can't be null");
		if (blackboard==null)
			throw new IllegalArgumentException("The blackboard reference has to be a valid object");
			
		// Sets the internal fields;
		_id = id;
		_bb = blackboard;
	}
	
	public boolean containsParameter(String name){
		if (name=="")
			throw new IllegalArgumentException("The name can't be null");

		String id = _id+name;		
		return _bb.contains(id);
	}
	
	public String readParameter(String name) {
		if (name=="")
			throw new IllegalArgumentException("The name can't be null");

		String id = _id+name;
		return ((ParameterElement)_bb.get(id)).getValue();		
	}
	public void writeParameter(String name, String value) {
		if (name=="")
			throw new IllegalArgumentException("The name can't be null");
		if (value=="")
			throw new IllegalArgumentException("The value can't be null");
		
		String id = _id+name;
		ParameterElement e = new ParameterElement(name, value);
		_bb.store(id, e);
	}

}
