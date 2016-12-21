package blackboard;

import data.Element;

public interface Blackboard {
	public void store(String name, Element element);
	public Element get(String name);
	public boolean contains(String name);
	public void remove(String name);
}
