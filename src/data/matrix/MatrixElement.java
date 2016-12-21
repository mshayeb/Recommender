package data.matrix;

import java.text.DecimalFormat;

import Jama.*;
import data.Element;

/**
 * 
 * Adapter class for the Jama.Matrix class, that also implements the Element interface.
 * This makes this class directly storable in the blackboard 
 * It also serves as an adapter to the Jama.Matrix class - to allow for future changes to the underlying matrix package 
 *
 */
public class MatrixElement implements Element{
	String _name;
	Matrix _matrix;
	String[] _colNames;
	String[] _rowNames;
	
	public MatrixElement(String name, double[][] matrix, String[] columnNames, String[] rowNames) {
		if (name=="")
			throw new IllegalArgumentException("The name can't be null");
		if (matrix==null)
			throw new IllegalArgumentException("The matrix reference has to be a valid object");
		if (columnNames==null)
			throw new IllegalArgumentException("The columnNames reference has to be a valid object");
		if (rowNames==null)
			throw new IllegalArgumentException("The rowNames reference has to be a valid object");
		if (matrix.length!=rowNames.length)
			throw new IllegalArgumentException("The rowNames vector has to be the same size as the n dimension of the matrix");
		if (matrix[0].length!=columnNames.length)
			throw new IllegalArgumentException("The columnNames vector has to be the same size as the m dimension of the matrix");
			
		
		// Sets the internal fields;
		_name = name;
		_matrix = new Matrix(matrix);
		_colNames = columnNames;
		_rowNames = rowNames;
	}
	
	public String getName(){
		return _name;
	}
	
	public double[][] getArray() {
		return _matrix.getArray();
		// TODO For security reasons it might be better to return the getArrayCopy, however for performance it is not doing that.
	}
	
	public int getNumRows() {
		return _matrix.getRowDimension();
	}
	
	public int getNumCols() {
		return _matrix.getColumnDimension();
	}
	
	public String getColumnName(int i) {
		return _colNames[i];
	}
	
	public String getRowName(int i) {
		return _rowNames[i];
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		DecimalFormat df = new DecimalFormat("#0.00");
		for (int i=0; i<_matrix.getRowDimension(); i++){
			for (int j=0; j<_matrix.getColumnDimension(); j++){
				buf.append("    " + df.format(_matrix.get(i, j)));
			}
			buf.append("\n");
		}
		
		return buf.toString();
	}
	
	public MatrixElement times (double s, String name) {
		Matrix mat = _matrix.times(s);
		return new MatrixElement(name, mat.getArrayCopy(), _colNames, _rowNames);
	}
	
	public MatrixElement times (MatrixElement m, String name) {
		Matrix mat = _matrix.times(m._matrix);
		return new MatrixElement(name, mat.getArrayCopy(), m._colNames, _rowNames);
	}
	
	public MatrixElement transpose(String name) {
		Matrix mat = _matrix.transpose();
		return new MatrixElement(name, mat.getArrayCopy(), _rowNames, _colNames);		
	}
	
	public MatrixElement averagesByRow(String name) {
		double[][] mat = _matrix.getArray();
		double[][] rows = new double[_matrix.getRowDimension()][1];
		String[] colName = {"Row Average"};
		int numOfEntries;
		
		for (int i=0; i<_matrix.getRowDimension(); i++){
			numOfEntries = 0;
			for (int j=0; j<_matrix.getColumnDimension(); j++) {
				if (mat[i][j]!=0.0) {
					numOfEntries++;
					rows[i][0]+=mat[i][j];
				}
			}
			rows[i][0] = (numOfEntries==0) ? 0 : rows[i][0] / numOfEntries; 
		}
		return new MatrixElement(name, rows, colName, _rowNames);
	}
	
	public MatrixElement averagesByColumn(String name) {
		double[][] mat = _matrix.getArray();
		double[][] cols = new double[1][_matrix.getColumnDimension()];
		String[] rowName = {"Column Average"};
		int numOfEntries;
		
		for (int j=0; j<_matrix.getColumnDimension(); j++){
			numOfEntries = 0;
			for (int i=0; i<_matrix.getRowDimension(); i++) {
				if (mat[i][j]!=0.0) {
					numOfEntries++;
					cols[0][j]+=mat[i][j];
				}
			}
			cols[0][j] = (numOfEntries==0) ? 0 : cols[0][j] / numOfEntries;
		}
		return new MatrixElement(name, cols, _colNames, rowName);
	}
	
	public MatrixElement totalsByRow(String name) {
		double[][] mat = _matrix.getArray();
		double[][] rows = new double[_matrix.getRowDimension()][1];
		String[] colName = {"Row Total"};
		
		for (int i=0; i<_matrix.getRowDimension(); i++){
			for (int j=0; j<_matrix.getColumnDimension(); j++) {
				if (mat[i][j]!=0.0) {
					rows[i][0]+=mat[i][j];
				}
			}
		}
		return new MatrixElement(name, rows, colName, _rowNames);
	}
	
	public MatrixElement totalsByColumn(String name) {
		double[][] mat = _matrix.getArray();
		double[][] cols = new double[1][_matrix.getColumnDimension()];
		String[] rowName = {"Column Total"};
		
		for (int j=0; j<_matrix.getColumnDimension(); j++){
			for (int i=0; i<_matrix.getRowDimension(); i++) {
				if (mat[i][j]!=0.0) {
					cols[0][j]+=mat[i][j];
				}
			}
		}	
		return new MatrixElement(name, cols, _colNames, rowName);
	}
	
	public MatrixElement normalizeByRows(MatrixElement rowTotals, String name) {
		double[][] mat = _matrix.getArrayCopy();
		double[][] totals = rowTotals.getArray();
		
		for (int i=0; i<_matrix.getRowDimension(); i++){
			for (int j=0; j<_matrix.getColumnDimension(); j++){
				mat[i][j] = (totals[i][0]==0) ? 0 : mat[i][j] / totals[i][0];
			}
		}	
		return new MatrixElement(name, mat, _colNames, _rowNames);
	}
	
	public MatrixElement normalizeByColumns(MatrixElement colTotals, String name) {
		double[][] mat = _matrix.getArrayCopy();
		double[][] totals = colTotals.getArray();
		
		for (int i=0; i<_matrix.getRowDimension(); i++){
			for (int j=0; j<_matrix.getColumnDimension(); j++){
				mat[i][j] = (totals[0][j]==0) ? 0 : mat[i][j] / totals[0][j];
			}
		}	
		return new MatrixElement(name, mat, _colNames, _rowNames);
	}
	
	public MatrixElement normalizeByNumber(double number, String name) {
		double[][] mat = _matrix.getArrayCopy();
		
		for (int i=0; i<_matrix.getRowDimension(); i++){
			for (int j=0; j<_matrix.getColumnDimension(); j++){
				mat[i][j] = mat[i][j] / number;
			}
		}	
		return new MatrixElement(name, mat, _colNames, _rowNames);
	}
	
	public MatrixElement convertToBinary(double threshold, String name) {
		// This method will turn the matrix into a binary matrix (with only 0 & 1).
		// Any entry (i,j) that is higher than the threshold will be a 1, otherwise it will be a 0
		
		double[][] mat = _matrix.getArrayCopy();
		
		for (int i=0; i<_matrix.getRowDimension(); i++){
			for (int j=0; j<_matrix.getColumnDimension(); j++){
				mat[i][j] = (mat[i][j] >= threshold) ? 1 : 0;
			}
		}	
		return new MatrixElement(name, mat, _colNames, _rowNames);
		
		
	}
	
	public double maxNumber() {
		double[][] mat = _matrix.getArray();
		double result = -1;
		for (int i=0; i<_matrix.getRowDimension(); i++){
			for (int j=0; j<_matrix.getColumnDimension(); j++){
				if (mat[i][j] > result)
					result = mat[i][j];
			}
		}	
		return result;
	}
	
	public MatrixElement compressByPCA(String name, double variability) {
		// This method compresses a matrix using Principal Component Analysis. 
		// The resulting matrix still preserves a given percentage of the variability of the original data.
				
		// Uses a class that using JAMA performs PCA by calculating SVD on the adjusted data matrix
		PCA principalComponents = new PCA();
		principalComponents.setRawData(_matrix);
		principalComponents.computePCA();
			    
	    // Once the PCA analysis is complete, we need to determine how many PC to keep, in order to preserve the desired level of variability.
	    // This is done by analyzing the Eigenvalues of the covariance matrix
		Matrix latent = principalComponents.getLatent();
	    int maxNumOfPC = _matrix.getColumnDimension();
	    double total = 0.0;
	    for (int i=0; i<latent.getRowDimension(); i++) {
	    	total += latent.get(i, 0);
	    }
	    double cumulative = 0.0;
	    int numPC = -1;
	    while ((cumulative < variability) && (numPC < maxNumOfPC)) {
	    	numPC++;
	    	cumulative += latent.get(numPC, 0) / total;
	    }
	    // We need to add 1 - since the array is zero based, but the number of principal components is not
	    numPC++;
	    
	    // A new matrix has to be built that holds only the correct number of PCs
	    Matrix pca = principalComponents.getPca();
	    Matrix pcaReduced = new Matrix(pca.getRowDimension(), numPC);

	    for (int i=0; i<pca.getRowDimension(); i++) {
	    	for (int j=0; j<numPC; j++) {
	    		pcaReduced.set(i, j, pca.get(i, j));
	    	}
	    }

	    // The ZScores need to be recomputed, but this time using the reduced PCA matrix
	    Matrix centered = principalComponents.getZeroMeanData();
	    Matrix scores = centered.times(pcaReduced);
	
	    // In order to return a MatrixElement, the columns (ie the PCs) need names
		String[] colNames = new String[numPC];
		for (int i=0; i<numPC; i++) {
			colNames[i] = "PrincipalComponent" + (i+1);
		}
	    
/*		
	    // Debugging output:
	    System.out.println(" ");

	    System.out.println("----- Original PCA -----");
	    pca.print(4,4);
	    System.out.println(" ");

	    Matrix zScores = principalComponents.getZScores();
	    System.out.println("----- Original Scores -----");
	    zScores.print(4,4);
	    System.out.println(" ");

	    System.out.println("----- Eigenvalues of the Covariance Matrix -----");
	    latent.print(4,4);
	    System.out.println(" ");
	    
	    System.out.println("----- Number of principal components required -----");	    
	    System.out.println("Required PC:" + numPC + " in order to maintain " + variability + " of the variability.");
	    System.out.println(" ");
	    
	    System.out.println("----- Reduced PCA -----");
	    pcaReduced.print(4, 4);
	    System.out.println(" ");
	    
	    System.out.println("----- New Scores - Reduced dimensionality Matrix -----");
	    scores.print(4, 4);
	    System.out.println(" ");
*/	    
	    
	    return new MatrixElement(name, scores.getArray(), colNames, _rowNames);
	}
}
