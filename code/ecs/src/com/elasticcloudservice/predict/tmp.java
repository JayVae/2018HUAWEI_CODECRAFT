package com.elasticcloudservice.predict;

/**
 * @Author: Jay
 * @Date: Created in 16:56 2018/4/13
 * @Modified By:
 */
public class tmp {

    //            ARMAProcess(vmNum, preTime, str, armaInput, ans, sumOf7);

//            使用三阶平滑
/*            double alpha = 0.1, beta = 0.3, gamma = 0.1;
            int period = 7, m = preTime;
            boolean debug = true;
            double[] predict = forecast(armaInput, alpha, beta, gamma, period, m, debug);
            System.out.println("-----------predict----------------------------------");
            for(int i = armaInput.length; i < predict.length; i++){
                System.out.println(predict[i]);
                sum=sum+predict[i];
            }
            System.out.println("%%%%%%%%"+sum);
            if (sum<0){
                Double db = Double.valueOf(vmNum.get(str));
                vmNum.put(str, (int) (db / trainTime * preTime) + 1);
            }else {
                BigDecimal dbb = new BigDecimal(sum).setScale(0, BigDecimal.ROUND_HALF_UP);
                vmNum.put(str,dbb.intValue() );
            }*/
//使用二阶平滑
/*            double alpha = 0.1, gamma = 0.1;
            int m = preTime;
            double[] predict = smooth(armaInput,alpha,gamma,m);
            System.out.println("-----------predict----------------------------------");
            for(int i = armaInput.length; i < predict.length; i++){
                System.out.println(predict[i]);
                sum=sum+predict[i];
            }
            System.out.println("%%%%%%%%"+sum);
            if (sum<0){
                Double db = Double.valueOf(vmNum.get(str));
                vmNum.put(str, (int) (db / trainTime * preTime) + 1);
            }else {
                BigDecimal dbb = new BigDecimal(sum).setScale(0, BigDecimal.ROUND_HALF_UP);
                vmNum.put(str,dbb.intValue() );
            }*/
    //    double[] armaInput1=movAvg((dataCut(armaInput)),7);
//            double[] seasonInput=preSeasonDiff(armaInput1,7);
//            double[] mov=center(movAvg(armaInput1,7));
}
