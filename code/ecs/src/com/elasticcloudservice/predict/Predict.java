package com.elasticcloudservice.predict;

import com.filetool.pojo.Ecs;
import com.filetool.util.DateUtil;
import com.filetool.util.Pack;

import java.util.*;

public class Predict {

	public static String[] predictVm(String[] ecsContent, String[] inputContent) {

		/** =========do your work here========== **/

		String[] results = new String[ecsContent.length];

		List<String> history = new ArrayList<String>();

//		input 文件的处理，vmMap中存储以flavorName为键的二维数组，CPU,内存（写个vm对象多好！！！）
//		物理服务器CPU核数 内存大小（GB） 硬盘大小（GB）
		int CPUNum = Integer.parseInt(inputContent[0].split(" ")[0]);
		int MEMNum = Integer.parseInt(inputContent[0].split(" ")[1]);
		int flavorNum = Integer.parseInt(inputContent[2]);
		String endDate = inputContent[inputContent.length-1].split(" ")[0];
		String startDate = inputContent[inputContent.length-2].split(" ")[0];
		String preType = inputContent[inputContent.length-4];
		Map<String,int[]> vmMap = new HashMap<>();
		for (int i = 3; i < 3+flavorNum; i++) {
			String[] tmp = inputContent[i].split(" ");
			vmMap.put(tmp[0],new int[]{Integer.parseInt(tmp[1]),Integer.parseInt(tmp[2])});
//			Integer.parseInt(inputContent[i])>>10;

		}

//		训练数据集的处理,vmNum中存训练集中的请求数据
//        vmSeries存取各个类别的时间序列
        Map<String,int[]> vmSeries = new HashMap<>();
        Set<String> keySet = vmMap.keySet();
        Map<String,Integer> vmNum = new HashMap<>();
        String startTrainTime = ecsContent[0].split("\t")[2].split(" ")[0];
        String endTrainTime = ecsContent[ecsContent.length-1].split("\t")[2].split(" ")[0];

        for (String str : keySet) {
            vmNum.put(str,0);
        }

        for (int i = 0; i < ecsContent.length; i++) {

			if (ecsContent[i].contains("\t")
					&& ecsContent[i].split("\t").length == 3) {
                String[] array = ecsContent[i].split("\t");
				String flavorName = array[1];
				if(keySet.contains(flavorName)){
				    vmNum.put(flavorName,vmNum.get(flavorName)+1);
				}
			}
		}

//		计算时间间隔，取平均取整（进一法），并将flavor的预测次数存到vmNum中。
        //算两个日期间隔多少天,先考虑时间训练数据集的最后一天与预测的起始日期是连续的
        int trainTime=DateUtil.getDayLength(startTrainTime,startDate)-1;
        int preTime=DateUtil.getDayLength(startDate,endDate);

//      下述是按平均值进行预测的：
        for (String str : keySet) {
            Double db = Double.valueOf(vmNum.get(str));
            vmNum.put(str, (int) (db/trainTime*preTime)+1);
//            注释掉的是按照四舍五入来做的
/*            BigDecimal dbb = new BigDecimal(db/trainTime*preTime).setScale(0, BigDecimal.ROUND_HALF_UP);
            vmNum.put(str,dbb.intValue() );*/
        }
//        预测方法二：ARIMA

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
            for (int i = 0; i < vmKey; i++) {
                cpuList.add(cpuCost);
                memList.add(memCost);
                nameList.add(str);
                cpuPredAll = cpuPredAll + cpuCost;
                memPredAll = memPredAll + memCost;
            }
        }

//        计算该预测结果下的理论利用率
//         理论理论最小服务器数
        int minEcsNum = Math.max(cpuPredAll/CPUNum+1,memPredAll/MEMNum+1);
        double percent = (double) cpuPredAll/(minEcsNum*CPUNum);
        System.out.println(percent);

        List<Ecs> ecsList = new ArrayList<>();
        ecsList.add(new Ecs(CPUNum,MEMNum));

//        下面是采用背包来解
//        kkk用来索引ecsList
            int kkk = 0;
            Pack.pack(CPUNum,MEMNum,cpuList,memList,nameList,preType,ecsList,kkk);

//            下面是采用贪心法来做

//        GreedMethod.greed1(CPUNum,MEMNum,cpuList,memList,nameList,ecsList,preType);

//      判断最后一个的利用率
        if (preType.equals("CPU")){
            if(ecsList.get(ecsList.size()-1).getCpuPercent()< 0.3 ){
                List<String> delList = ecsList.get(ecsList.size()-1).getNameList();
                for (String str: delList){
                    if(keySet.contains(str)){
                        int oriNum = vmNum.get(str);
                        vmNum.put(str,oriNum-1);
                        vmPreNum=vmPreNum-1;
                    }
                }
                ecsList.remove(ecsList.size()-1);
            }else if (ecsList.get(ecsList.size()-1).getCpuPercent() >0.6 && ecsList.get(ecsList.size()-1).getCpuPercent()<0.9){
                int cpuAdp = ecsList.get(ecsList.size()-1).getCpuNum();
                int memAdp = ecsList.get(ecsList.size()-1).getMemNum();
//                改一个完全背包就行了,备选物品是1:1的那几个

                String str = Collections.min(keySet);
                int cpuCost = vmMap.get(str)[0];
                int memCost = vmMap.get(str)[1]>>10;
                int numAdp = Math.min(cpuAdp/cpuCost,memAdp/memCost);
                vmPreNum = vmPreNum + numAdp;
                int oriNum = vmNum.get(str);
                vmNum.put(str,oriNum+numAdp);
                ecsList.get(ecsList.size()-1).setCpuNum(cpuAdp-numAdp*cpuCost);
                ecsList.get(ecsList.size()-1).setMemNum(memAdp-numAdp*memCost);
                ecsList.get(ecsList.size()-1).setCpuPercent(ecsList.get(ecsList.size()-1).getCpuNum()/CPUNum);
                ecsList.get(ecsList.size()-1).setMemPercent(ecsList.get(ecsList.size()-1).getMemNum()/MEMNum);
                for (int ttt=0;ttt<numAdp;ttt++) {
                    ecsList.get(ecsList.size() - 1).setNameList(str);
                }


            }
        }else{
            if(ecsList.get(ecsList.size()-1).getMemPercent()< 0.3 ){
                List<String> delList = ecsList.get(ecsList.size()-1).getNameList();
                for (String str: delList){
                    if(keySet.contains(str)){
                        int oriNum = vmNum.get(str);
                        vmNum.put(str,oriNum-1);
                        vmPreNum=vmPreNum-1;
                    }
                }
                ecsList.remove(ecsList.size()-1);
            }else if (ecsList.get(ecsList.size()-1).getMemPercent() >0.6 && ecsList.get(ecsList.size()-1).getMemPercent()<0.9){
                int cpuAdp = ecsList.get(ecsList.size()-1).getCpuNum();
                int memAdp = ecsList.get(ecsList.size()-1).getMemNum();
//                改一个完全背包就行了,备选物品是1:1的那几个

                String str = Collections.min(keySet);
                int cpuCost = vmMap.get(str)[0];
                int memCost = vmMap.get(str)[1]>>10;
                int numAdp = Math.min(cpuAdp/cpuCost,memAdp/memCost);
                vmPreNum = vmPreNum + numAdp;
                int oriNum = vmNum.get(str);
                vmNum.put(str,oriNum+numAdp);
                ecsList.get(ecsList.size()-1).setCpuNum(cpuAdp-numAdp*cpuCost);
                ecsList.get(ecsList.size()-1).setMemNum(memAdp-numAdp*memCost);
                ecsList.get(ecsList.size()-1).setCpuPercent(ecsList.get(ecsList.size()-1).getCpuNum()/CPUNum);
                ecsList.get(ecsList.size()-1).setMemPercent(ecsList.get(ecsList.size()-1).getMemNum()/MEMNum);
                for (int ttt=0;ttt<numAdp;ttt++){
                    ecsList.get(ecsList.size()-1).setNameList(str);
                }
            }
        }



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
            System.out.println((double)cpuPredAll/((ecsList.size()+1)*CPUNum));
        }else {
            System.out.println((double)memPredAll/((ecsList.size()+1)*MEMNum));
        }

        System.out.println("现在所有ecs的利用率如下：");
        System.out.println(percentAdp/ecsList.size());

        return results;
	}


}
