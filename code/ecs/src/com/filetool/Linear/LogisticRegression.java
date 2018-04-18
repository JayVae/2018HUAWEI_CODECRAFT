package com.filetool.Linear;

import java.lang.Math;

public class LogisticRegression {
    int nbSamples;
    int nbDims;
    double lr = 0.001;
    double[] w;
    double cost;
    double[] grad;
    double[] yPred;

    private static double sigmoid(double z) {
        return 1.0 / (1.0 + Math.exp(-z));
    } // end method sigmoid

    public void fit(double[][]X, double[]y) {
        /*
        shapes
        X: (n_sample, n_features)
        theta: (n_feature, 1)
        y: (n_sample, 1)
        */
        nbSamples = X.length;
        nbDims = X[0].length;
        w = new double[nbDims];
        for (int j=0; j<nbDims; j++)
            w[j] = 0;
        grad = new double[nbDims];
        yPred = new double[nbSamples];

        optimize(X, y);
    } // end method fit

    public void optimize (double[][]X, double[]y) {
        for (int step=0; step<1000; step++) {
            for (int i=0; i<nbSamples; i++) {
                yPred[i] = 0;
                for (int j=0; j<nbDims; j++)
                    yPred[i] += (X[i][j] * w[j]);
            } // end for

            for (int i=0; i<nbSamples; i++)
                yPred[i] = sigmoid(yPred[i]);

            double[] diff = new double[nbSamples];
            for (int i=0; i<nbSamples; i++)
                diff[i] = yPred[i] - y[i];
            
            double squSum = 0;
            for (int i=0; i<nbSamples; i++)
                squSum += Math.pow(Math.abs(diff[i]), 2);
            double cost = squSum / (2 * nbSamples);
            System.out.println("Iteration: " + step + " | Cost: " + cost);
            /*
            Gradient Descent
            w = w - gradient * learning rate
            where gradient = X_T * difference
            */
            for (int j=0; j<nbDims; j++) {
                grad[j] = 0;
                for (int i=0; i<nbSamples; i++)
                    grad[j] += diff[i] * X[i][j] / nbSamples;
            } // end for

            for (int j=0; j<nbDims; j++)
                w[j] -= grad[j] * lr;
        } // end for
    } // end method fit

    public double[] predict (double[][]X) {
        double[] pred = new double[X.length];
        for (int i=0; i<X.length; i++) {
            double res = 0;
            for (int j=0; j<X[0].length; j++)
                res += X[i][j] * w[j];
            if (sigmoid(res) >= 0.5)
                pred[i] = 1.0;
            else
                pred[i] = 0.0;
        } // end for
        return pred;
    } // end method predict
} // end class LogisticRegression
