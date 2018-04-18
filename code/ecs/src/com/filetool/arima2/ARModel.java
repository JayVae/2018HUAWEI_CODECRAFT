package com.filetool.arima2;

import java.util.Vector;

/**
 * @Author: Jay
 * @Date: Created in 21:50 2018/3/17
 * @Modified By:
 */
public class ARModel {

    private double [] data;
    private int p;

    public ARModel(double [] data, int p)
    {
        this.data = data;
        this.p = p;
    }

    public Vector<double []> solveCoeOfAR()
    {
        Vector<double []>vec = new Vector<>();
        double [] arCoe = new ARMAMethod().computeARCoe(this.data, this.p);

        vec.add(arCoe);

        return vec;
    }

}
