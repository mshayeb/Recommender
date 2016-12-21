package agents;

import java.lang.Exception;

/**
 * 
 * Interface that all the controlling agents need to implements.
 * The control agents are the ones that run the execution.
 *
 */
public interface Agent {
	public void run() throws Exception;
}
