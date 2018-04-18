package com.filetool.MyArima;

import static com.filetool.util.Matrix.*;

/**
 * @Author: Jay
 * @Date: Created in 20:10 2018/4/8
 * @Modified By:
 */
public class OLS_RLS {

    private int p;
    private int q;
    private int d;
    private double[] oriData;
    private double[] y;
    private double[] data;

    public double[][] getTheta() {
        return theta;
    }

    private double[][] theta;
    private double[][] fi;


    private double[][] P;
    private double sj;
    private double fc2;

    public OLS_RLS(int p, int q,int d, double[] data) {
        this.p = p;
        this.q = q;
        this.d=d;
        this.oriData=data;
        double[] diffData = new DataProcess().diff(data,d);
//        this.data=center(diffData);
        this.data=data;
    }

    public void setInitValue(){
        theta=new double[p+q][1];
        P = Imat(p+q);
        y=new double[p];
        sj=0;
        fi=new double[p+q][1];
        fc2=0;
    }

    public void iter(){
        fi=new double[p+q][1];
        P = Imat(p+q);
        y=new double[p];
        sj=0;
        fc2=0;
        for (int i = 0; i < data.length; i++) {

            if (p != 0 && q!=0){
                for (int j = p-1; j > 0; j--) {
                    fi[j][0]=fi[j-1][0];
                }
                if(i==0){
                    fi[0][0]=0;
                }else {
                    fi[0][0]= 0-data[i-1];
                }
                for (int j = p+q-1; j >p; j--) {
                        fi[j][0]=fi[j-1][0];
                }
                fi[p][0]=sj;
            }
            if( p ==0 ){
                for (int j = p+q-1; j >p; j--) {
                    if ((j-1)>=0){
                        fi[j][0]=fi[j-1][0];
                    }
                }
                fi[p][0]=sj;
            }
            if (q==0){
                for (int j = p-1; j > 0; j--) {
                    fi[j][0]=fi[j-1][0];
                }
                if(i==0){
                    fi[0][0]=0;
                }else {
                    fi[0][0]= 0-data[i-1];
                }
            }
            double[][] tmp=mul(P,fi);
            double[][] fiT=transpose(fi);
            double tmpValue = 1+mul(fiT,tmp)[0][0];
            theta=add(theta,mulA(tmp,(data[i]-mul(fiT,theta)[0][0])/tmpValue));
            P=minus(P,mulA(mul(tmp,transpose(tmp)),1/tmpValue));
            double pretmp = mul(fiT,theta)[0][0];
            sj=data[i]-mul(fiT,theta)[0][0];
/*            if (q!=0){
                fi[p][0]=sj;
            }*/
            fc2=fc2+(sj*sj-fc2)/(i+1);
        }
    }

    public double getAic(){
        double aic=Double.POSITIVE_INFINITY;
/*        setInitValue();
        for (int i = 0; i < 5; i++) {
            iter();
        }*/
        if(fc2 == 0){
            aic= Math.log(data.length)*(p+q+1);
        }else{
            aic=data.length*Math.log(fc2)+Math.log(data.length)*(p+q+1);
        }
        return aic;
    }

    public double[] forecast(int step,double avg){
        double[] res = new double[step];
/*        System.out.println("theta matrix");
        for (int i = 0; i < theta.length; i++) {
            for (int j = 0; j < theta[0].length; j++) {
                System.out.print("\t" + theta[i][j]);
            }
            System.out.println();
        }*/
        for (int i = 0; i < step; i++) {
            if (p != 0 && q!=0){
                for (int j = p-1; j > 0; j--) {
                    fi[j][0]=fi[j-1][0];
                }
                if(i==0){
                    fi[0][0]= 0-data[data.length-1];
                }else {
                    fi[0][0]= 0-res[i-1];
                }
                for (int j = p+q-1; j >p; j--) {
                    fi[j][0]=fi[j-1][0];
                }
                fi[p][0]=sj;
            }
            if( p ==0 ){
                for (int j = p+q-1; j >p; j--) {
                    if ((j-1)>=0){
                        fi[j][0]=fi[j-1][0];
                    }
                }
                fi[p][0]=sj;
            }
            if (q==0){
                for (int j = p-1; j > 0; j--) {
                    fi[j][0]=fi[j-1][0];
                }
                if(i==0){
                    fi[0][0]=0;
                }else {
                    fi[0][0]= 0-res[i-1];
                }
            }
            res[i]=mul(transpose(fi),theta)[0][0]+avg;
//            sj=new Random().nextGaussian()*Math.sqrt(fc2);
            sj=0;
        }
        return res;
    }
}
