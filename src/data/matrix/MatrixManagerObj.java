package data.matrix;

import blackboard.Blackboard;
import data.basic.*;

/**
 *
 * Class for the Matrix Manager.
 * This class provides an abstraction layer to the blackboard so that it is easy to generate and get 
 * different matrix representations of the relationships between the basic elements.
 * The matrices follow a Lazy Instantiation design pattern.
 */
final class MatrixManagerObj implements MatrixManager{
	// Local Variables
	String _id;		// Identifier of the object.  This is used to name the elements in the blackboard
	Blackboard _bb;	// Reference to the blackboard object
	BasicElementManager _elements;	// Reference to the basic element mananger

	// Constructor - Package Private
	MatrixManagerObj(String id, Blackboard blackboard, BasicElementManager elementManager) {
		// Integrity checks
		if (id=="")
			throw new IllegalArgumentException("The string identifier can't be null");
		if (blackboard==null)
			throw new IllegalArgumentException("The blackboard reference has to be a valid object");
		if (elementManager==null)
			throw new IllegalArgumentException("The basic elements reference has to be a valid object");
			
		// Sets the internal fields;
		_id = id;
		_bb = blackboard;
		_elements = elementManager;
	}
		 
	public String getId() {
		return _id;
	}
	
	public MatrixElement getStakeholdersNeedsMatrix(boolean ForceRefresh){
		String name = _id+"_SxN";
		
		if (ForceRefresh || !_bb.contains(name)) {
			// Gets the dimension of the matrix
			int numS = _elements.getStakeholders().size();
			int numN = _elements.getNeeds().size();
			
			// Creates a new matrix
			double[][] mData = new double[numS][numN];
			String[] rowData = new String[numS];
			String[] colData = new String[numN];
			
			// Iterates over the collections to fill it
			for(int i=0; i<numS; i++) {
				Stakeholder s = _elements.getStakeholder(i);
				rowData[i]=s.getId();
				for (Rating r : _elements.getRatingsByStakeholder(s)) {
					Need n = _elements.getNeedOfRating(r);
					colData[n.getNumber()]=n.getId();
					mData[i][n.getNumber()]=r.getValue();
				}
			}
			
			// Stores the newly created matrix in the blackboard - if it existed it will get overwritten
			_bb.store(name, new MatrixElement(name, mData, colData, rowData));
		}
		return (MatrixElement) _bb.get(name);
	}
	
	public MatrixElement getStakeholdersForumsMatrix(boolean ForceRefresh){
		String name = _id+"_SxF";
		
		if (ForceRefresh || !_bb.contains(name)) {
			// Gets the dimension of the matrix
			int numS = _elements.getStakeholders().size();
			int numF = _elements.getForums().size();
			
			// Creates a new matrix
			double[][] mData = new double[numS][numF];
			String[] rowData = new String[numS];
			String[] colData = new String[numF];
			
			// Iterates over the collections to fill it
			for(int i=0; i<numS; i++) {
				Stakeholder s = _elements.getStakeholder(i);
				rowData[i]=s.getId();
				for (Forum f : _elements.getForumsOfStakeholder(s)) {
					colData[f.getNumber()]=f.getId();
					mData[i][f.getNumber()]=_elements.getScoreOfStakeholderInForum(f, s);
				}
			}
			
			// Stores the newly created matrix in the blackboard - if it existed it will get overwritten
			_bb.store(name, new MatrixElement(name, mData, colData, rowData));
		}
		return (MatrixElement) _bb.get(name);
	}
	public MatrixElement getStakeholdersTermsMatrix(boolean ForceRefresh){
		String name = _id+"_SxT";
		
		if (ForceRefresh || !_bb.contains(name)) {
			// Gets the dimension of the matrix
			int numS = _elements.getStakeholders().size();
			int numT = _elements.getTerms().size();
			
			// Creates a new matrix
			double[][] mData = new double[numS][numT];
			String[] rowData = new String[numS];
			String[] colData = new String[numT];				
			
			// Iterates over the collections to fill it
			for(int i=0; i<numS; i++) {
				Stakeholder s = _elements.getStakeholder(i);
				rowData[i]=s.getId();
				for (Need n : _elements.getNeedsByStakeholder(s)) {
					for (Term t : _elements.getTermsByNeed(n)) {
						colData[t.getNumber()]=t.getId();
						double accumulated = mData[i][t.getNumber()];
						mData[i][t.getNumber()] = accumulated + _elements.getScoreOfTermInNeed(n, t);
					}
				}
			}
			
			// Stores the newly created matrix in the blackboard - if it existed it will get overwritten
			_bb.store(name, new MatrixElement(name, mData, colData, rowData));
		}
		return (MatrixElement) _bb.get(name);
		
	}
	public MatrixElement getNeedsTermsMatrix(boolean ForceRefresh){
		String name = _id+"_NxT";
		
		if (ForceRefresh || !_bb.contains(name)) {
			// Gets the dimension of the matrix
			int numN = _elements.getNeeds().size();
			int numT = _elements.getTerms().size();
			
			// Creates a new matrix
			double[][] mData = new double[numN][numT];
			String[] rowData = new String[numN];
			String[] colData = new String[numT];				
			
			// Iterates over the collections to fill it
			for(int i=0; i<numN; i++) {
				Need n = _elements.getNeed(i);
				rowData[i]=n.getId();
				for (Term t : _elements.getTermsByNeed(n)) {
					colData[t.getNumber()]=t.getId();
					mData[i][t.getNumber()]=_elements.getScoreOfTermInNeed(n, t);
				}
			}
			
			// Stores the newly created matrix in the blackboard - if it existed it will get overwritten
			_bb.store(name, new MatrixElement(name, mData, colData, rowData));
		}
		return (MatrixElement) _bb.get(name);									
	}
	public MatrixElement getNeedsForumsMatrix(boolean ForceRefresh){
		String name = _id+"_NxF";
		
		if (ForceRefresh || !_bb.contains(name)) {
			// Gets the dimension of the matrix
			int numN = _elements.getNeeds().size();
			int numF = _elements.getForums().size();
			
			// Creates a new matrix
			double[][] mData = new double[numN][numF];
			String[] rowData = new String[numN];
			String[] colData = new String[numF];				
			
			// Iterates over the collections to fill it
			for(int i=0; i<numN; i++) {
				Need n = _elements.getNeed(i);
				rowData[i]=n.getId();
				for (Forum f : _elements.getForumsOfNeed(n)) {
					colData[f.getNumber()]=f.getId();
					mData[i][f.getNumber()]=_elements.getScoreOfNeedInForum(f, n);
				}
			}
			
			// Stores the newly created matrix in the blackboard - if it existed it will get overwritten
			_bb.store(name, new MatrixElement(name, mData, colData, rowData));
		}
		return (MatrixElement) _bb.get(name);						
	}
	
	public void storeMatrix(MatrixElement matrix) {
		_bb.store(matrix.getName(), matrix);
	}
	public MatrixElement getMatrix(String name) {
		return (MatrixElement) _bb.get(name);
	}
	public boolean containsMatrix(String name) {
		return _bb.contains(name);
	}
	public void removeMatrix(String name) {
		_bb.remove(name);
	}
}
