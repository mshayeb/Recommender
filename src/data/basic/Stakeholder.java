package data.basic;

/**
 *
 */

public interface Stakeholder extends Comparable<Stakeholder>{
	// Getters
	public String getId();
	public int getNumber();
	public String getName();
	public String getDescription();
}
