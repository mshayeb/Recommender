/**
 * 
 */
package blackboard;

/**
 *
 * Static Factory that creates the Blackboard objects
 */
public class BlackboardStaticFactory {
	// A static class
	private BlackboardStaticFactory() {}
	
	public static Blackboard newBlackboard() {
		return new BlackboardObj();
	}	
}
