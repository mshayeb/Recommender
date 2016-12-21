package data.parameters;

import data.Element;

/**
 * 
 * This class holds the basic parameter elements that are stored in the blackboard.
 * 
 * Note that the class is package private, since it is not meant to be used outside of this package.
 * All manipulation of the basic elements should be done via the ParameterElementManager
 */
final class ParameterElement implements Element{
	String _name;
	String _value;

	public ParameterElement(String name, String value) {
		if (name=="")
			throw new IllegalArgumentException("The name can't be null");
		if (value==null)
			throw new IllegalArgumentException("The value can't be null");
			
		// Sets the internal fields;
		_name = name;
		_value = value;
	}
	
	public String getName(){
		return _name;
	}	
	
	public String getValue() {
		return _value;	
	}
	
}
