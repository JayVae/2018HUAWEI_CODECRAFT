package com.filetool.util;

/**
 * @Author: Jay
 * @Date: Created in 1:41 2018/4/10
 * @Modified By:
 */
public class Matrix {

    public final static int OPERATION_ADD = 1;
    public final static int OPERATION_SUB = 2;
    public final static int OPERATION_MUL = 3;

//    实现单位阵
    public static double[][] Imat(int n){
        double[][] result = new double[n][n];
        for (int i = 0; i < n; i++) {
            result[i][i]=100000;
        }
        return result;
    }

//    实现两个矩阵相加
    public static double[][] add(double[][] matrixa, double[][] matrixb) {
        double[][] result = new double[matrixa.length][matrixb[0].length];
            for (int i = 0; i < matrixa.length; i++) {
                for (int j = 0; j < matrixa[0].length; j++) {
                    result[i][j] = matrixa[i][j] + matrixb[i][j];
                }
            }
        return result;
    }

//实现两个矩阵相减
    public static double[][] minus(double[][] matrixa, double[][] matrixb) {
        double[][] result = new double[matrixa.length][matrixb[0].length];
            for (int i = 0; i < matrixa.length; i++) {
                for (int j = 0; j < matrixa[0].length; j++) {
                    result[i][j] = matrixa[i][j] - matrixb[i][j];
                }
            }
        return result;
    }

//实现两个矩阵相乘
    public static double[][] mul(double[][] matrixa, double[][] matrixb) {
        double[][] result = new double[matrixa.length][matrixb[0].length];
            for (int i = 0; i < matrixa.length; i++) {
                for (int j = 0; j < matrixb[0].length; j++) {
                    result[i][j] = calculateSingleResult(matrixa, matrixb, i, j);
                }
            }
            return result;
    }

    static double calculateSingleResult(double[][] matrixa, double[][] matrixb, Integer row, int col) {
        double result = 0;
        for (int i = 0; i < matrixa[0].length; i++) {
            result += matrixa[row][i] * matrixb[i][col];
        }
        return result;
    }

    //实现数乘操作
    public static double[][] mulA(double[][] matrixa, double b) {
        double[][] result3 = new double[matrixa.length][matrixa[0].length];
        for (int i = 0; i < matrixa.length; i++) {
            for (int j = 0; j < matrixa[0].length; j++) {
                result3[i][j] = matrixa[i][j] * b;
            }
        }
        return result3;
    }
    //    转置
    public static double[][] transpose(double[][] mat){
        double[][] result = new double[mat[0].length][mat.length];
        for (int i = 0; i < mat.length; i++) {
            for (int j = 0; j < mat[0].length; j++) {
                result[j][i]=mat[i][j];
            }
        }
        return result;
    }

//    一维数组相乘1*N N*1
    public double calarraymularray(double[] arr1,double[] arr2){
        double result = 0.0;
        for (int i=0;i<arr1.length;i++){
            result=result+arr1[i]*arr2[i];
        }
        return result;
    }

//实现

    public static void main(String[] args){
        double[][] a = new double[][] { {1,0},{0,1} };
        double[][] b = new double[][] { {3 }, { 4 } };
        Matrix bmm = new Matrix();
        System.out.println("mul two matrix");
        double[][] result = bmm.mul(a, b);
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[0].length; j++) {
                System.out.print("\t" + result[i][j]);
            }
            System.out.println();
        }
        double[][] ca = new double[][] { {1,2},{3,4} };
        double[][] res=bmm.transpose(ca);
        for (int i = 0; i < res.length; i++) {
            for (int j = 0; j < res[0].length; j++) {
                System.out.print("\t" + res[i][j]);
            }
            System.out.println();
        }
        int[][] re = new int[3][1];
        for (int i = 0; i < re.length; i++) {
            for (int j = 0; j < re[0].length; j++) {
                System.out.print("\t" + re[i][j]);
            }
            System.out.println();
        }
    }



}
