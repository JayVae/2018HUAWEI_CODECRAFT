package com.filetool.MyArima;

import static com.filetool.MyArima.DataProcess.avg;

/**
 * @Author: Jay
 * @Date: Created in 4:20 2018/4/10
 * @Modified By:
 */
public class ARIMA {

    double[] data = {};

    public ARIMA(double[] data) {
        this.data = data;
    }


    public static int[] bestPQ(int maxP,int maxQ,int maxD,double[] data){
        int[] best=new int[3];
        double aic=Double.POSITIVE_INFINITY;
        for (int i = 0; i < maxP; i++) {
            for (int j = 0; j < maxQ; j++) {
                if (i==0 && j==0){
                    continue;
                }
                for (int k = 0; k <= maxD; k++) {
                    double aictmp = new OLS_RLS(i,j,k,data).getAic();
                    if(aictmp<aic){
                        aic=aictmp;
                        best[0]=i;
                        best[1]=j;
                        best[2]=k;
                }
                }
            }
        }
        System.out.println(aic+"****"+best[0]+"__"+best[1]+"_____"+best[2]);
        return best;
    }

    public double[] ARIMAPredict(int step){
        int[] best =bestPQ(3,3,0,data);
        OLS_RLS ols = new OLS_RLS(best[0],best[1],best[2],data);
        ols.setInitValue();
        for (int i = 0; i < 5; i++) {
            ols.iter();
        }
//        todo
        double avg=avg(data);
        double[] res= ols.forecast(step,avg);
        return res;
    }

}
