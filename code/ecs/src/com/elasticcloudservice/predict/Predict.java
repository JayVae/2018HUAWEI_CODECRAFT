package com.elasticcloudservice.predict;

import com.filetool.MyArima.DataProcess;
import com.filetool.MyArima.OLS_RLS;
import com.filetool.pojo.Ecs;
import com.filetool.util.DateUtil;
import com.filetool.util.Pack2;

import java.util.*;

import static com.filetool.MyArima.DataProcess.*;
import static com.filetool.MyArima.SecondExSmooth.smooth;

public class Predict {

	public static String[] predictVm(String[] ecsContent, String[] inputContent) {

		/** =========do your work here========== **/

//		input 文件的处理，vmMap中存储以flavorName为键的二维数组，CPU,内存（写个vm对象多好！！！）
//		物理服务器CPU核数 内存大小（GB） 硬盘大小（GB）

		int CPUNum = Integer.parseInt(inputContent[0].split(" ")[0]);
		int MEMNum = Integer.parseInt(inputContent[0].split(" ")[1]);
		int flavorNum = Integer.parseInt(inputContent[2]);
        String endDate = inputContent[3+flavorNum+4].split(" ")[0];
		String startDate = inputContent[3+flavorNum+3].split(" ")[0];
        String preType = inputContent[3+flavorNum+1];
        Map<String,int[]> vmMap = new HashMap<>();
		for (int i = 3; i < 3+flavorNum; i++) {
			String[] tmp = inputContent[i].split(" ");
			vmMap.put(tmp[0],new int[]{Integer.parseInt(tmp[1]),Integer.parseInt(tmp[2])});
//			Integer.parseInt(inputContent[i])>>10;
		}

//		训练数据集的处理,vmNum中存训练集中的请求数据
//        vmSeries存取各个类别的时间序列
//        Map<String,Integer> vmRatio = new TreeMap<>();
		Map<Integer,List<String>> vmRatio = new TreeMap<>();
		Map<Integer,List<String>> cpuClass = new TreeMap<>();
		vmRatio.put(1,new ArrayList<String>());
		vmRatio.put(2,new ArrayList<String>());
		vmRatio.put(4,new ArrayList<String>());
		cpuClass.put(1,new ArrayList<String>());
		cpuClass.put(2,new ArrayList<String>());
		cpuClass.put(4,new ArrayList<String>());
		cpuClass.put(8,new ArrayList<String>());
		cpuClass.put(16,new ArrayList<String>());
        Map<String,int[]> vmSeries = new HashMap<>();
        Set<String> keySet = vmMap.keySet();
        Map<String,Integer> vmNum = new HashMap<>();
        String startTrainTime = ecsContent[0].split("\t")[2].split(" ")[0];
        String endTrainTime = ecsContent[ecsContent.length-1].split("\t")[2].split(" ")[0];

        //		计算时间间隔，取平均取整（进一法），并将flavor的预测次数存到vmNum中。
        //算两个日期间隔多少天,先考虑时间训练数据集的最后一天与预测的起始日期是连续的
        int trainTime=DateUtil.getDayLength(startTrainTime,startDate)-1;
        int preTime=DateUtil.getDayLength(startDate,endDate);
        for (String str : keySet) {
            vmNum.put(str,0);
            vmSeries.put(str,new int[trainTime]);
        }

        for (int i = 0; i < ecsContent.length; i++) {

			if (ecsContent[i].contains("\t")
					&& ecsContent[i].split("\t").length == 3) {
                String[] array = ecsContent[i].split("\t");
				String flavorName = array[1];
				String dateTime = array[2].split(" ")[0];
				int dateIndex = DateUtil.getDayLength(startTrainTime,dateTime)-1;

				if(keySet.contains(flavorName)){
				    vmNum.put(flavorName,vmNum.get(flavorName)+1);
				    int oriDateNum = vmSeries.get(flavorName)[dateIndex];
				    vmSeries.get(flavorName)[dateIndex]=oriDateNum+1;
				}
			}
		}

        for (String str: keySet) {
            double[] armaInput = new double[trainTime];
            for(int iiii=0;iiii<trainTime;iiii++){
                armaInput[iiii]=(double)vmSeries.get(str)[iiii];
            }

            int sum=0;
            int[] preResult=new int[preTime];
            int[] minResult = new int[preTime];
            int mov_len=7;
            double[] cut=dataCut(armaInput);
//            double[] seasonInput=preSeasonDiff(cut,6);
            double[] armaInput1=movAvg(cut,mov_len);
//            ARIMA arima = new ARIMA(armaInput1);
            int maxP=7,maxQ=7,maxD=3,maxL=7;
            double min_aic = Double.POSITIVE_INFINITY;
            for (int i = 0; i < maxP; i++) {
                for (int j = 0; j < maxQ; j++) {
                    if (i==0 && j==0){
                        continue;
                    }
                    for (int k = 0; k < maxD; k++) {
                        for (int l = 0; l < maxL; l++) {
                            DataProcess np = new DataProcess();
                            double[] diff_step=np.preSeasonDiff(armaInput1,l);
                            double[] diff_data=np.diff(diff_step,k);
                            double avg=avg(diff_data);
                            diff_data=center(diff_data);
                            OLS_RLS ols = new OLS_RLS(i,j,0,diff_data);
                            ols.setInitValue();
                            for (int m = 0; m < 5; m++) {
                                ols.iter();
                            }
                            boolean flag=false;
                            double[][] thetathis = ols.getTheta();
                            for (int m = 0; m < thetathis.length; m++) {
                                if (Math.abs(thetathis[m][0])>=1){
                                    flag=true;
                                    break;
                                }
                            }
                            if(flag){
                                continue;
                            }
                            double[] res = ols.forecast(preTime,avg);
                            double[] result=np.inDiff(diff_step,res,k);
                            for (int m = 0; m < result.length; m++) {
                                if (m<l){
                                    result[m]=result[m]+armaInput1[armaInput1.length-l+m];
                                }else {
                                    result[m]=result[m]+result[m-l];
                                }
                            }
                            double sumthis=0.0;
                            List<Double> sumList = new ArrayList<>();
                            for (int m = cut.length-1; m >cut.length-mov_len; m--) {
                                sumthis=sumthis+cut[m];
                                sumList.add(cut[m]);
                            }
                            for (int m = 0; m < result.length; m++) {
                                result[m]=(result[m]*mov_len-sumthis);
                                sumthis=sumthis-sumList.get(0)+result[m];
                                sumList.remove(0);
                                sumList.add(result[m]);
                                if (result[m]<0){
                                    preResult[m]=0;
                                }else{
                                    preResult[m]=(int)result[m];
                                }
                            }
                            if (avg(preResult)>(avg(armaInput)+2*std(armaInput)) || avg(preResult)<(avg(armaInput)-2*std(armaInput))){
                                continue;
                            }
                            double aic = ols.getAic();
                            if(aic<min_aic){
                                min_aic = aic;
                                System.arraycopy(preResult,0,minResult,0,preResult.length);
                            }
                        }
                    }
                }
            }
            if(min_aic==Double.POSITIVE_INFINITY){
//                Double db = Double.valueOf(vmNum.get(str));
//                sum=(int) (db / trainTime * preTime) + 1;
                int sum2=0;
                double alpha = 0.1, gamma = 0.1;
                double[] predict = smooth(armaInput,alpha,gamma,preTime);
                for(int i = armaInput.length; i < predict.length; i++){
                    if (predict[i]<0){
                        predict[i]=0;
                    }
                    sum2=sum2+(int)predict[i];
                }
                int sum3=0;

                sum=(int)(0.3*sum2+0.7*sum3);
            }else{
                int sum2=0;
                double alpha = 0.1, gamma = 0.1;
                double[] predict = smooth(armaInput,alpha,gamma,preTime);
                for(int i = armaInput.length; i < predict.length; i++){
                    if (predict[i]<0){
                        predict[i]=0;
                    }
                    sum2=sum2+(int)predict[i];
                }
                int sum3=0;
                Double db = Double.valueOf(vmNum.get(str));
                sum3=(int) (db / trainTime * preTime) + 1;
                sum=(int) (0.7*sum(minResult)+sum2*0.3);
                int sum4=0;
                for (int i = 0; i < preTime; i++) {
                    sum4=sum4+(int)armaInput[armaInput.length-preTime+i];
                }
                sum=(int) (0.7*sum(minResult)+sum2*0.2+sum3*0.1);
            }
            vmNum.put(str, sum);
        }

        List<Integer> cpuList = new ArrayList();
        List<Integer> memList = new ArrayList();
        List<String> nameList = new ArrayList<>();

        int vmPreNum = 0;

        int cpuPredAll = 0;
        int memPredAll = 0;

        for (String str : keySet) {
            int vmKey = vmNum.get(str);
            vmPreNum = vmPreNum + vmKey;
            int cpuCost = vmMap.get(str)[0];
            int memCost = vmMap.get(str)[1]>>10;
//            vmRatio.put(str,memCost/cpuCost);
            vmRatio.get(memCost/cpuCost).add(str);
            cpuClass.get(cpuCost).add(str);
            for (int i = 0; i < vmKey; i++) {
                cpuList.add(cpuCost);
                memList.add(memCost);
                nameList.add(str);
                cpuPredAll = cpuPredAll + cpuCost;
                memPredAll = memPredAll + memCost;
            }
        }

//      计算该预测结果下的理论利用率
//      理论理论最小服务器数
        int minEcsNum = Math.max(cpuPredAll/CPUNum+1,memPredAll/MEMNum+1);
        double percent = (double) cpuPredAll/(minEcsNum*CPUNum);
        System.out.println(percent);

        List<Ecs> ecsList = new ArrayList<>();
//        ecsList.add(new Ecs(CPUNum,MEMNum));

//        下面是采用背包来解
//        kkk用来索引ecsList
         int kkk = 0;
//         new Pack().pack(CPUNum,MEMNum,cpuList,memList,nameList,preType,ecsList,kkk);
         while(cpuList.size()!=0){
             ecsList.add(new Ecs(CPUNum,MEMNum));
             new Pack2().pack(CPUNum,MEMNum,cpuList,memList,nameList,preType,ecsList,kkk);
             kkk++;
         }


//      判断最后一个的利用率
        if (preType.equals("CPU")){
            if(ecsList.size()>1 && ecsList.get(ecsList.size()-1).getCpuPercent()< 0.3 ){
                List<String> delList = ecsList.get(ecsList.size()-1).getNameList();
                for (String str: delList){
                    if(keySet.contains(str)){
                        int oriNum = vmNum.get(str);
                        vmNum.put(str,oriNum-1);
                        vmPreNum=vmPreNum-1;
                    }
                }
                ecsList.remove(ecsList.size()-1);
            }else if (ecsList.get(ecsList.size()-1).getCpuPercent() >0.6 && ecsList.get(ecsList.size()-1).getCpuPercent()<0.8){
                int cpuAdp = ecsList.get(ecsList.size()-1).getCpuNum();
                int memAdp = ecsList.get(ecsList.size()-1).getMemNum();
//                改一个完全背包就行了,备选物品是1:1的那几个,子背包问题
                double ratio = (double) memAdp/cpuAdp;
                int cpuMin10 = Math.min(cpuAdp,memAdp);
                for(int l=4;l>=0;l--){
                    int tmpClass = 1<<l;
                    int tmpNum = cpuMin10 /tmpClass;
                    List<String> tmpList=new ArrayList<>(vmRatio.get(1));
                    if (!tmpList.isEmpty()){
                        if (tmpNum!=0 ){
                            tmpList.retainAll(cpuClass.get(tmpClass));
                            if((!tmpList.isEmpty()) && tmpNum!=0){
                                cpuMin10= cpuMin10-tmpNum*tmpClass;
                                vmPreNum = vmPreNum + tmpNum;
                                System.out.println(tmpList.isEmpty());
                                String name = tmpList.get(0);
                                int oriNum = vmNum.get(name);
                                vmNum.put(name,oriNum+tmpNum);
                                int memCostThis = vmMap.get(name)[1]>>10;
                                ecsList.get(ecsList.size()-1).setCpuNum(cpuAdp-tmpNum*tmpClass);
                                ecsList.get(ecsList.size()-1).setMemNum(memAdp-tmpNum*memCostThis);
                                ecsList.get(ecsList.size()-1).setCpuPercent(1-ecsList.get(ecsList.size()-1).getCpuNum()/CPUNum);
                                ecsList.get(ecsList.size()-1).setMemPercent(1-ecsList.get(ecsList.size()-1).getMemNum()/MEMNum);
                                for (int ttt=0;ttt<tmpNum;ttt++) {
                                    ecsList.get(ecsList.size() - 1).setNameList(name);
                                }
                            }
                        }
                        if (cpuMin10==0){
                            break;
                        }
                    }
                }
            }
        }else{
            if(ecsList.size()>1 && ecsList.get(ecsList.size()-1).getMemPercent()< 0.3 ){
                List<String> delList = ecsList.get(ecsList.size()-1).getNameList();
                for (String str: delList){
                    if(keySet.contains(str)){
                        int oriNum = vmNum.get(str);
                        vmNum.put(str,oriNum-1);
                        vmPreNum=vmPreNum-1;
                    }
                }
                ecsList.remove(ecsList.size()-1);
            }else if (ecsList.get(ecsList.size()-1).getMemPercent() >0.6 && ecsList.get(ecsList.size()-1).getMemPercent()<0.8){
                int cpuAdp = ecsList.get(ecsList.size()-1).getCpuNum();
                int memAdp = ecsList.get(ecsList.size()-1).getMemNum();
//                改一个完全背包就行了,备选物品是1:1的那几个,子背包问题
                double ratio = (double) memAdp/cpuAdp;
                int cpuMin10 = Math.min(cpuAdp,memAdp);
                for(int l=4;l>=0;l--){
                    int tmpClass = 1<<l;
                    int tmpNum = cpuMin10 /tmpClass;
                    List<String> tmpList=new ArrayList<>(vmRatio.get(1));
                    if (!tmpList.isEmpty()){
                        if (tmpNum!=0 ){
                            tmpList.retainAll(cpuClass.get(tmpClass));
                            if((!tmpList.isEmpty()) && tmpNum!=0){
                                cpuMin10= cpuMin10-tmpNum*tmpClass;
                                vmPreNum = vmPreNum + tmpNum;
                                System.out.println(tmpList.isEmpty());
                                String name = tmpList.get(0);
                                int oriNum = vmNum.get(name);
                                vmNum.put(name,oriNum+tmpNum);
                                int memCostThis = vmMap.get(name)[1]>>10;
                                ecsList.get(ecsList.size()-1).setCpuNum(cpuAdp-tmpNum*tmpClass);
                                ecsList.get(ecsList.size()-1).setMemNum(memAdp-tmpNum*memCostThis);
                                ecsList.get(ecsList.size()-1).setCpuPercent(1-ecsList.get(ecsList.size()-1).getCpuNum()/CPUNum);
                                ecsList.get(ecsList.size()-1).setMemPercent(1-ecsList.get(ecsList.size()-1).getMemNum()/MEMNum);
                                for (int ttt=0;ttt<tmpNum;ttt++) {
                                    ecsList.get(ecsList.size() - 1).setNameList(name);
                                }
                            }
                        }
                        if (cpuMin10==0){
                            break;
                        }
                    }
                }
            }
        }

        String[] results = new String[vmNum.size()+3+ecsList.size()];
//将结果输出，结果包括两部分，预测部分在vmNum，分配结果在ecsList中
        int j= 1;
        for (String str:keySet){
            int vmKey = vmNum.get(str);
            results[j]=str+" "+String.valueOf(vmKey);
            j++;
        }

        results[0] = String.valueOf(vmPreNum);

        results[vmNum.size()+1]= "";

        results[vmNum.size()+2]=String.valueOf(ecsList.size());

        int k=1;
        for (int i = vmNum.size()+3;i<ecsList.size()+vmNum.size()+3;i++ ){

            results[i]=k+ ecsList.get(k-1).Path();
            k++;
        }

        double percentAdp =0;
        System.out.println("每个ecs的利用率如下：");
        for (Ecs ecs : ecsList) {
            System.out.println(ecs.getCpuPercent());
            percentAdp=percentAdp+ecs.getCpuPercent();
        }
        //        评测分配阶段的利用率：
        System.out.println("原来所有ecs的利用率如下：");
        if (preType .equals("CPU")){
            System.out.println((double)cpuPredAll/((ecsList.size())*CPUNum));
        }else {
            System.out.println((double)memPredAll/((ecsList.size())*MEMNum));
        }

        System.out.println("现在所有ecs的利用率如下：");
        System.out.println(percentAdp/ecsList.size());

        return results;
	}
}
