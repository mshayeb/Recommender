package output;

import data.matrix.MatrixElement;
import java.io.IOException;

/**
 *
 * Interface that is used to output the data in the system
 */
public interface Output {
	public void writeToLog(String message) throws IOException;
	public void writeBasicElements() throws IOException;
	public void writeExtraElements() throws IOException;
	public void writeMatrices() throws IOException;
	public void writeMatrix(MatrixElement matrix) throws IOException;
	public void writeUserOutput() throws IOException;
}
