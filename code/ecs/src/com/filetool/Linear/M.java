package com.filetool.Linear;

import java.util.List;


public class M {
    public static int searchHeader (String[] header, String target) {
        for (int i=0; i<header.length; i++) {
            if (header[i].equals(target))
                return i;
        }
        return -1;
    } // end method searchHeader

    public static void printMatrix (double[][] matrix) {
		for (int i=0; i<matrix.length; i++) {
		    for (int j=0; j<matrix[i].length; j++)
		        System.out.print(matrix[i][j] + " ");
		    System.out.println();
		}
	} // end method printMatrix

    // [colStart, colEnd)
    public static double[][] parseDoubleMatrix (List<String[]> lines, int colStart, int colEnd) {
        int nRow = lines.size();
        int nCol = lines.get(0).length;
        double[][] matrix = new double[nRow][nCol-colStart-(nCol-colEnd)];

        for (int r=0; r<matrix.length; r++) {
            for (int c=colStart; c<colEnd; c++)
                matrix[r][c-colStart] = Double.parseDouble(lines.get(r)[c]);
        }
        return matrix;
    } // end method parseDoubleMatrix

    // [colStart, colEnd)
    public static double[][] subMatrix (double[][]input, int colStart, int colEnd) {
        int nRow = input.length;
        int nCol = input[0].length;
        double[][] temp = new double[nRow][nCol-colStart-(nCol-colEnd)];

        for (int r=0; r<input.length; r++) {
            for (int c=colStart; c<colEnd; c++)
                temp[r][c-colStart] = input[r][c];
        }
        return temp;
    } // end method subMatrix

    public static double[] getRowVal (double[][] matrix, int rowNum) {
        double[] rowVal = new double[matrix[0].length];
        for (int c=0; c<matrix[0].length; c++)
                rowVal[c] = matrix[rowNum][c];
        return rowVal;
    } // end method getRowVal

    public static double[] getColVal (double[][] matrix, int colNum) {
        double[] colVal = new double[matrix.length];
        for (int r=0; r<matrix.length; r++)
                colVal[r] = matrix[r][colNum];
        return colVal;
    } // end method getColVal

    public static double[][] transpose(double[][] m){
        double[][] temp = new double[m[0].length][m.length];
        for (int i = 0; i < m.length; i++)
            for (int j = 0; j < m[0].length; j++)
                temp[j][i] = m[i][j];
        return temp;
    } // end method transpose

    public static double[][] ones(int rowLen, int colLen){
        double[][] temp = new double[rowLen][colLen];
        for (int i = 0; i < rowLen; i++)
            for (int j = 0; j < colLen; j++)
                temp[i][j] = 1;
        return temp;
    } // end method ones

    public static double evaluate(double[] y_pred, double[] y_test) {
        if (y_pred.length != y_test.length)
            System.out.println("Warning! y_pred is unequal to y_test");
        int total = y_pred.length;
        double count = 0.0;
        for (int i=0; i<total; i++) {
            if (y_pred[i] == y_test[i])
                count += 1;
        }
        return count / total;
    } // end method evaluate
} // end class MatrixUtil
