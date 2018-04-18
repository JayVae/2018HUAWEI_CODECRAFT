package com.elasticcloudservice.predict;

import com.filetool.arima2.ARIMAModel;

import java.util.ArrayList;
import java.util.Map;

/**
 * @Author: Jay
 * @Date: Created in 13:28 2018/4/14
 * @Modified By:
 */
public class predictMethod {

    //        double [] a = flavor_ratio(vmSeries);
//        for (int i = 0; i < a.length; i++) {
//            System.out.println(a[i]);
//        }
//      下述是按平均值进行预测的：
/*        for (String str : keySet) {
            Double db = Double.valueOf(vmNum.get(str));
            vmNum.put(str, (int) (db / trainTime * preTime) + 1);
        }*/
//            注释掉的是按照四舍五入来做的
/*            BigDecimal dbb = new BigDecimal(db/trainTime*preTime).setScale(0, BigDecimal.ROUND_HALF_UP);
            vmNum.put(str,dbb.intValue() );*/
//        预测方法二：ARIMA
//        被注释掉的部分是按照星期来做ARMA 的
/*        for (String str: keySet) {
            double[] armaInput = new double[trainTimeWeek];
//            System.arraycopy(vmSeries.get(str),0,armaInput,0,trainTime);
            for(int iiii=0;iiii<trainTimeWeek;iiii++){

                int sumWeek =0;
                for (int iii=0;iii<preTime;iii++){
                    sumWeek=sumWeek+vmSeries.get(str)[iiii*preTime+trainTimeLeft+iii];
                }
                armaInput[iiii]=(double)sumWeek;
            }
            int[] ans = new int[preTime];
            int sumOf7 = 0;*/

    private static void ARMAProcess(Map<String, Integer> vmNum, int preTime, String str, double[] armaInput, int[] ans, int sumOf7) {
        int [] bestModel = new int[3];
        int countbset=0;

        for (int ii=0; ii<preTime; ii++){
/*                ARIMA arima=new ARIMA(armaInput);
            int []model=arima.getARIMAmodel();
            int ansThis= arima.aftDeal(arima.predictValue(model[0],model[1]));
            for (int k=0;k<armaInput.length-1;k++){
                armaInput[k]=armaInput[k+1];
            }
            armaInput[armaInput.length-1]=ansThis;
            ans[ii]=ansThis;
            sumOf7 = sumOf7 + ansThis;*/
            ARIMAModel arima = new ARIMAModel(armaInput);
            ArrayList<int []> list = new ArrayList<>();
            int period = 7;
            int modelCnt = 1, cnt = 0;			//通过多次预测的平均值作为预测值
            int [] tmpPredict = new int [modelCnt];
            for (int k = 0; k < modelCnt; ++k)			//控制通过多少组参数进行计算最终的结果
            {
                bestModel = arima.getARIMAModel(period, list, (k == 0) ? false : true);

//                    System.out.println(arima.getArimaCoe());
                if (bestModel.length == 0)
                {
                    tmpPredict[k] = (int)armaInput[armaInput.length - period];
                    cnt++;
                    break;
                } else {
                    if (str.equals("flavor4")){
//                            System.out.println(ii+" "+bestModel[0]+" "+bestModel[1]+" "+" "+k+" "+arima.getArimaCoe());

                    }

                    int predictDiff = arima.predictValue(bestModel[0], bestModel[1], period);
                    tmpPredict[k] = arima.aftDeal(predictDiff, period);
                    cnt++;
                    System.out.println(str+ii+" "+bestModel[0]+" "+bestModel[1]+" "+" "+k+" "+arima.getArimaCoe());
                }
//                    System.out.println("BestModel is " + bestModel[0] + " " + bestModel[1]);
                list.add(bestModel);
            }
            double sumPredict = 0.0;
            for (int k = 0; k < cnt; ++k)
            {
                sumPredict += (double)tmpPredict[k] / (double)cnt;
            }
            int predict = (int)Math.round(sumPredict);
//                System.out.println("Predict value="+predict);
            for (int k=0;k<armaInput.length-1;k++){
                armaInput[k]=armaInput[k+1];
            }
            armaInput[armaInput.length-1]=predict;
            ans[ii]=predict;
            sumOf7 = sumOf7 + predict;
            countbset++;

        }
        if(sumOf7>=0){
            vmNum.put(str, sumOf7);
        }else{
            sumOf7=0;
            vmNum.put(str, sumOf7);
        }
    }

}
