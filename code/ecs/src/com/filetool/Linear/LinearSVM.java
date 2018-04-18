package com.filetool.Linear;

import java.lang.Math;


public class LinearSVM {
    int nbSamples;
    int nbDims;
    double lambda = 0.0001;
    double lr = 0.001;
    double[] w;
    double cost;
    double[] grad;
    double[] yPred;

    public void optimize (double[][]X, double[]y) {
        /*
        cost = 1/2 * ||w|| ^2 + sum( max( 0, 1 - y[i](w * x[i] + b) ) )
        */
        for (int step=0; step<1000; step++) {
            cost = 0;
            for (int i=0; i<nbSamples; i++) {
                yPred[i] = 0;
                for (int j=0; j<nbDims; j++)
                    yPred[i] += X[i][j] * w[j];
                if (y[i] * yPred[i] - 1 < 0)
                    cost += (1 - y[i] * yPred[i]);
            } // end for
            for (int j=0; j<nbDims; j++)
                cost += 0.5 * lambda * w[j] * w[j];

            for (int j=0; j<nbDims; j++) {
                grad[j] = Math.abs(lambda*w[j]);
                for (int i=0; i<nbSamples; i++) {
                    if (y[i] * yPred[i] - 1 < 0)
                        grad[j] -= y[i] * X[i][j];
                } // end for
            } // end for

            for (int j=0; j<nbDims; j++)
                w[j] -= lr * grad[j]; // update graident
            
            System.out.println("Iteration: " + step + " | Cost: " + cost);
        } // end for
    } // end method optimize

    public void fit(double[][]X, double[]y) {
        nbSamples = X.length;
        nbDims = X[0].length;
        w = new double[nbDims];
        for (int j=0; j<nbDims; j++)
            w[j] = 0;
        grad = new double[nbDims];
        yPred = new double[nbSamples];

        optimize(X, y);
    } // end method fit

    public double[] predict(double[][]X) {
        double[] pred = new double[X.length];
        for (int i=0; i<X.length; i++) {
            double p = 0.0;
            for (int j=0; j<X[0].length; j++)
                p += X[i][j] * w[j];
            if (p >= 0.0)
                pred[i] = 1.0;
            else
                pred[i] = -1.0;
        }
        return pred;
    } // end method predict

} // end class LinearSVM
