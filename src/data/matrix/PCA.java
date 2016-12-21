package data.matrix;

/*
 * This method takes data X and returns a array of Matrices for:
 * 1) Principal Components of the rawData
 * 2) Z-Scores
 * 3) Eigenvalues of the covariance matrix of rawData (latent)
 * 4) Hotelling's T-squared statistic for each data point
 * 5) zeroMean of the raw data
 *
 * From: https://list.scms.waikato.ac.nz/pipermail/wekalist/2005-January/003244.html
 * 
 * References:
 * ----------
 * -  "A User's Guide to Principal Components", by J. Edward Jackson,
 *     pub by John Wiley & Sons, Inc. 1991 , Chapter 1.
 *
 * -  "Applied Multivariate Techniques" , by S. Sharma,
 *     pub by Wiley, Chapter 4.
 *
 * -  "Independent Component Analysis" , by A. Hyvarinen, J.Karhunen & E. Orja,
 *     pub by John Wiley, Chapter 6.
 */


import Jama.*; 

class PCA {

  private Matrix rawData;
  private Matrix zeroMeanData;
  private Matrix pca;
  private Matrix zScores;
  private Matrix latent;
  private Matrix hotellingTSquared;

  public PCA() { }

/*  
  // Sample code that shows the usage:
  public static void main(String[] args) {
   
    double[][] vals = {
    {3.30,0.90,27.60,0.90,8.20,19.10,6.20,26.60,7.20},
    {9.20,0.10,21.80,0.60,8.30,14.60,6.50,32.20,7.10},
    {10.80,0.80,27.50,0.90,8.90,16.80,6.00,22.60,5.70},
    {6.70,1.30,35.80,0.90,7.30,14.40,5.00,22.30,6.10},
    {23.20,1.00,20.70,1.30,7.50,16.80,2.80,20.80,6.10},
    {15.90,0.60,27.60,0.50,10.00,18.10,1.60,20.10,5.70},
    {7.70,3.10,30.80,0.80,9.20,18.50,4.60,19.20,6.20},
    {6.30,0.10,22.50,1.00,9.90,18.00,6.80,28.50,6.80},
    {2.70,1.40,30.20,1.40,6.90,16.90,5.70,28.30,6.40},
    {12.70,1.10,30.20,1.40,9.00,16.80,4.90,16.80,7.00},
    {13.00,0.40,25.90,1.30,7.40,14.70,5.50,24.30,7.60},
    {41.40,0.60,17.60,0.60,8.10,11.50,2.40,11.00,6.70},
    {9.00,0.50,22.40,0.80,8.60,16.90,4.70,27.60,9.40},
    {27.80,0.30,24.50,0.60,8.40,13.30,2.70,16.70,5.70},
    {22.90,0.80,28.50,0.70,11.50,9.70,8.50,11.80,5.50},
    {6.10,0.40,25.90,0.80,7.20,14.40,6.00,32.40,6.80},
    {7.70,0.20,37.80,0.80,9.50,17.50,5.30,15.40,5.70},
    {66.80,0.70,7.90,0.10,2.80,5.20,1.10,11.90,3.20},
    {23.60,1.90,32.30,0.60,7.90,8.00,0.70,18.20,6.70},
    {16.50,2.90,35.50,1.20,8.70,9.20,0.90,17.90,7.00},
    {4.20,2.90,41.20,1.30,7.60,11.20,1.20,22.10,8.40},
    {21.70,3.10,29.60,1.90,8.20,9.40,0.90,17.20,8.00},
    {31.10,2.50,25.70,0.90,8.40,7.50,0.90,16.10,6.90},
    {34.70,2.10,30.10,0.60,8.70,5.90,1.30,11.70,5.00},
    {23.70,1.40,25.80,0.60,9.20,6.10,0.50,23.60,9.30},
    {48.70,1.50,16.80,1.10,4.90,6.40,11.30,5.30,4.00}
    };
    Matrix Z = new Matrix(vals);
    Z.print(2, 2);    
    
    PCA principalComponents = new PCA();
    principalComponents.setRawData(Z);

    //main computation
    principalComponents.computePCA();

    Matrix pca = principalComponents.getPca();
    System.out.println("----- PCA -----");
    pca.print(4,4);

    System.out.println(" ");

    Matrix zScores = principalComponents.getZScores();
    System.out.println("----- Z-Scores -----");
    zScores.print(4,4);

    System.out.println(" ");

    Matrix latent = principalComponents.getLatent();
    System.out.println("----- Latent -----");
    latent.print(4,4);

    System.out.println(" ");

    Matrix tSquared = principalComponents.getHotellingTSquared();
    System.out.println("----- Hotellings T-Square -----");
    tSquared.print(4,4);
  }
*/  

  public void setRawData(Matrix rawData){
    this.rawData = rawData;
    zeroMeanData = null;
    pca = null;
    zScores = null;
    latent = null;
    hotellingTSquared = null;
   }

  public Matrix getRawData(){
    return rawData;
   }

  public Matrix getPca(){
    return pca;
   }

  public Matrix getZeroMeanData(){
    return zeroMeanData;
   }

  public Matrix getZScores(){
    return zScores;
   }

  public Matrix getLatent(){
    return latent;
   }

  public Matrix getHotellingTSquared(){
    return this.hotellingTSquared;
   }

  /**
   */
  public void computePCA(){
    Matrix X = rawData;
    int m = X.getRowDimension();
    int n = X.getColumnDimension();
    int rank = Math.min(m-1,n);

    //The following is to prevent 'out-of-bound array exception' thrown
    //in SingularValueDecomposition
    // This is no longer needed, as the revised SVD2 fixes this.
    // if((m+1)<n)
    //	throw new IllegalArgumentException("pca - data has more variables than observations (wide matrix or under-determined linear systems) :");
    if(m<2)
      throw new IllegalArgumentException("pca - there must be at least 2 observations (2 rows) in the data :");

    Matrix average = mean(X);
    //center the data by first removing the mean (average)
    zeroMeanData = X.minus(tile(average,m,1));

    SingularValueDecomposition2 svd = new SingularValueDecomposition2(zeroMeanData.arrayRightDivide(new Matrix(m,n,Math.sqrt((double)m - 1.0))));
    
    pca = svd.getV(); // PCA
    zScores  = zeroMeanData.times(pca); //Z-Scores
    Matrix temp = diag(svd.getS());
    latent    = temp.arrayTimes(temp); //Eigenvalues of the covariance matrix of X

    if(rank<n){
      latent = mergeV(latent.getMatrix(0,rank-1,0,0) , new Matrix(n-rank,1));
      zScores.setMatrix(0,m-1,rank,n-1,new Matrix(m,n-rank,0.0D));
     }

    Matrix temp2 = latent.getMatrix(0,rank-1,0,0);
    Matrix ones  = new Matrix(temp2.getRowDimension(), temp2.getColumnDimension(),1.0D);

    temp = sqrt(ones.arrayRightDivide(temp2)).transpose().times(zScores.getMatrix(0,m-1,0,rank-1).transpose());
    hotellingTSquared = sum(temp.arrayTimes(temp)).transpose(); 
    //Hotelling's T-squared statistic
  }



  /**
 * This method sums the elements of matrix 'S' along the column
 * @param S Matrix
 * @return Matrix
 */
private  Matrix sum(Matrix S){
 double[][] internal = S.getArray();
 double[][] summing = null;
 double temp = 0.0;

 int row = S.getRowDimension();
 int col = S.getColumnDimension();

 summing = new double[1][col];
 for(int j=0 ; j<col ; j++){
    for(int i=0 ; i<row ; i++){ temp += internal[i][j] ; }
    summing[0][j] = temp;
    temp = 0.0;
  }
  return new Matrix(summing);
}


  /**
   * Taking the square-roots of each entry of matrix X
   * @param X Matrix
   * @return Matrix
   */
  private Matrix sqrt(Matrix X){
    int m = X.getRowDimension();
    int n = X.getColumnDimension();
    double[][] xArray = X.getArray();
    Matrix R = new Matrix(m,n);
    double[][] C = R.getArray();
    for(int i=0; i<m; i++){
      for(int j=0; j<n; j++){
        C[i][j] = Math.sqrt(xArray[i][j]);
      }
    }
    return R;
  }

  /**
   * Merge two matrices vertically. If the 2 matrices have different 
column numbers,
   * an exception is thrown. Merging 'A' and 'B' lead to a larger matrix of:
   *      /   \
   *      | A |
   *  C = |   |
   *      | B |
   *      \   /
   *
   * @param A Matrix
   * @param B Matrix
   * @return Matrix
   */
  private Matrix mergeV(Matrix A, Matrix B){
    int m   = A.getRowDimension();
    int n   = A.getColumnDimension();
    int b_m = B.getRowDimension();
    int b_n = B.getColumnDimension();

    if(n!=b_n){
      throw new IllegalArgumentException(" mergeV : Matrix column dimensions must agree (same).");
     }
    int newRow = m+b_m;
    Matrix R = new Matrix(newRow,n);
    R.setMatrix(0,m-1,0,n-1,A);
    R.setMatrix(m,m+b_m-1,0,n-1,B);
    return R;
  }

  /**
   * Find the mean along each columns of Matrix X
   * @param X Matrix
   * @return Matrix
   */
  private Matrix mean(Matrix X){
    int rows = X.getRowDimension();
    int cols = X.getColumnDimension();
    double[][] xarray = X.getArray();
    Matrix R = new Matrix(1,cols);
    double[][] C = R.getArray();
    double sum = 0.0;
    for(int j=0; j<cols; j++){
      for(int i=0; i<rows; i++){
        sum += xarray[i][j];
      }
     C[0][j] = sum/((double)rows);
     sum = 0.0; //reset sum to zero
    }
   return R;
  }

  /**
   * Tiling of matrix X in [rowWise by colWise] dimension. Tiling creates a larger
   * matrix than the original data X. Example, if X is to be tiled in a 
[3 x 4] manner, then
   *     /            \
   *     | X  X  X  X |
   * C = | X  X  X  X |
   *     | X  X  X  X |
   *     \           /
   * @param X Matrix
   * @param rowWise int
   * @param colWise int
   * @return Matrix
   */
  private Matrix tile(Matrix X, int rowWise, int colWise){
     double[][] xArray = X.getArray() ;
     int countRow = 0, countColumn = 0;
     int m = X.getRowDimension();
     int n = X.getColumnDimension();

     if( rowWise<1 || colWise<1 ){
       throw new ArrayIndexOutOfBoundsException("tile : Array index is out-of-bound.");
      }

     int newRowDim = m*rowWise;
     int newColDim = n*colWise;
     double[][] result = new double[newRowDim][];

     for(int i=0 ; i<newRowDim ; i++){
       double[] holder = new double[newColDim];
       for(int j=0 ; j<newColDim ; j++){
          holder[j] = xArray[countRow][countColumn++];
          //reset the column-index to zero to avoid reference to out-of-bound index in xArray[][]
          if(countColumn == n){ countColumn = 0; }
         }//end for
       countRow++;
       //reset the row-index to zero to avoid reference to out-of-bound index in xArray[][]
       if(countRow == m){ countRow = 0; }
       result[i] = holder;
     }//end for

      return new Matrix(result);
   }

   /**
    * Return a column vector matrix where its elements are the main diagonals of X
    * @param X Matrix
    * @return Matrix
    */
   private Matrix diag(Matrix X){
    int rows = X.getRowDimension();
    int cols = X.getColumnDimension();

    double[][] xArray = X.getArray();
    int minDim = Math.min(rows,cols);

    Matrix R = new Matrix(minDim,1);
    double[][] C = R.getArray();

    for(int i=0; i<minDim; i++){
      C[i][0] = xArray[i][i];
    }
    return R;
  }
}