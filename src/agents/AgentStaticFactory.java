package agents;

import blackboard.Blackboard;

/**
 *
 * Factory class that creates the output depending on the paramaters
 */
public class AgentStaticFactory {
	// A static class
	private AgentStaticFactory() {}
	
	public static Agent getAgent(String agentName, Blackboard blackboard) {
		if (agentName.equalsIgnoreCase("AgentExperimentRecSys08")) {
			return new AgentExperimentRecSys08(blackboard) ;
		} else if (agentName.equalsIgnoreCase("AgentExperimentRecSys08Reduced")) {
			return new AgentExperimentRecSys08Reduced(blackboard);			
		} else if (agentName.equalsIgnoreCase("AgentExperimentDifferentNormalizations")) {
			return new AgentExperimentDifferentNormalizations(blackboard);			
		} else if (agentName.equalsIgnoreCase("AgentExperimentBinaryMembershipScores")) {
			return new AgentExperimentBinaryMembershipScores(blackboard);			
		} else if (agentName.equalsIgnoreCase("AgentRecomputeMAE")) {
			return new AgentRecomputeMAE(blackboard);
		} else if (agentName.equalsIgnoreCase("AgentCollaborativePredictionScores")) {
			return new AgentCollaborativePredictionScores(blackboard);
		} else {
			throw new IllegalArgumentException("The specified agent object can not be found.  Please check the argument name.");
		}
	}	
}
