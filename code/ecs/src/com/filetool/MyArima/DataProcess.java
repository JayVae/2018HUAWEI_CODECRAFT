package com.filetool.MyArima;

/**
 * @Author: Jay
 * @Date: Created in 20:42 2018/4/10
 * @Modified By:
 */
public class DataProcess {

    //数据限幅
    public static double[] dataCut(double[] data){
        double[] result=new double[data.length];
        double std = std(data);
        double avg =avg(data);
        if(std<0.5){
            for (int i = 0; i < data.length; i++) {
                result[i]=data[i];
            }
        }else{
            for (int i = 0; i < data.length; i++) {
                if (data[i]>(avg+2*std)){
                    result[i]=avg+2*std;
                }else if(data[i]<(avg-2*std)){
                    result[i]=avg-2*std;
                }else{
                    result[i]=data[i];
                }
            }
        }
        return result;
    }
//    数据中心化：
    public static double[] center(double[] data){
        double[] result = new double[data.length];
        double avg = avg(data);
        for (int i = 0; i < data.length; i++) {
            result[i] = data[i]-avg;
        }
        return result;
    }

    public static int sum(int[] data){
        int sum=0;
        for (int i = 0; i < data.length; i++) {
            sum=sum+data[i];
        }
        return sum;
    }

    public static double avg(double[] data){
        double sum = 0;
        for (int i = 0; i < data.length; i++) {
            sum+=data[i];
        }
        return sum/data.length;
    }

    public static double avg(int[] data){
        double sum = 0;
        for (int i = 0; i < data.length; i++) {
            sum+=data[i];
        }
        return sum/data.length;
    }

    public static double std(double[] data){
        double std=0;
        double sum = avg(data);
        for (int i = 0; i < data.length; i++) {
            std += (data[i]-sum)*(data[i]-sum);
        }
        return  Math.sqrt(std/(data.length - 1));
    }
//移动平滑
    public static double[] movAvg(double[] data,int step){
        double[] result = new double[data.length-step+1];
        for (int i = 0; i < data.length-step+1; i++) {
            double sum=0;
            for (int j = 0; j < step; j++) {
                sum +=data[i+j];
            }
            result[i]=sum/step;
        }
        return result;
    }

    public double [] preFirDiff(double [] data)		//一阶差分(1)
    {
        double [] tmpData = new double[data.length - 1];
        for (int i = 0; i < data.length - 1; ++i)
        {
            tmpData[i] = data[i + 1] - data[i];
        }
        return tmpData;
    }

    public  double [] preSeasonDiff(double [] preData,int d)
    {
        double [] tmpData = new double[preData.length - d];
        for (int i = 0; i < preData.length - d; ++i)
        {
            tmpData[i] = preData[i + d] - preData[i];
        }
        return tmpData;
    }

    public double[] diff(double[] data, int k){
        double[] result;
        if (k!=0){
            result=preFirDiff(data);
            k--;
            result= diff(result,k);
        }else{
            result=data;
        }
        return result;
    }

    public double[] inDiff(double[] oriData,double[] preData,int step){
        double[] result = new double[preData.length];
        System.arraycopy(preData,0,result,0,preData.length);
        if(step==0){
            result=preData;
        }else{
            for (int i = step-1; i >= 0; i--) {
                double[] tmp=diff(oriData,i);
                result[0] = tmp[tmp.length-1] + result[0];
                for (int j = 1; j < preData.length; j++) {
                    result[j]=result[j-1]+result[j];
                }
            }
        }
        return result;
    }
}
