package com.filetool.arima2;

import java.util.Vector;

/**
 * @Author: Jay
 * @Date: Created in 21:47 2018/3/17
 * @Modified By:
 */
public class MAModel {

    private double [] data;
    private int q;

    public MAModel(double [] data, int q)
    {
        this.data = data;
        this.q = q;
    }

    public Vector<double []> solveCoeOfMA()
    {
        Vector<double []>vec = new Vector<>();
        double [] maCoe = new ARMAMethod().computeMACoe(this.data, this.q);

        vec.add(maCoe);

        return vec;
    }

}
