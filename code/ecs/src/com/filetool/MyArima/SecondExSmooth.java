package com.filetool.MyArima;

/**
 * @Author: Jay
 * @Date: Created in 22:26 2018/4/9
 * @Modified By:
 */
public class SecondExSmooth {

    public static double[] smooth(double[] y, double alpha, double gama, int m) {

        if (y == null) {
            return null;
        }

        double[] s = new double[y.length];
        double[] b = new double[y.length];
        double[] f = new double[y.length + m];

        s[0] = y[0];
        b[0] = (y[y.length-1]-y[0])/y.length;

        for (int i = 1; i < y.length; i++) {
            s[i] = alpha*y[i]+(1-alpha)*(s[i-1]+b[i-1]);
            b[i] = gama*(s[i]-s[i-1]) + (1-gama)*b[i-1];
            f[i+m]=s[i]+m*b[i];
        }

        return f;
    }

}
