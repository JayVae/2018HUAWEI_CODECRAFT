package com.elasticcloudservice.predict;

import com.filetool.pojo.Ecs;
import com.filetool.pojo.EcsCpuComparator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
        Set<String> keySet = vmMap.keySet();
        Map<String,Integer> vmNum = new HashMap<>();
        String startTrainTime = ecsContent[0].split("\t")[2].split(" ")[0];

        for (String str : keySet) {
            vmNum.put(str,0);
        }

        for (int i = 0; i < ecsContent.length; i++) {

			if (ecsContent[i].contains("\t")
					&& ecsContent[i].split("\t").length == 3) {

				String[] array = ecsContent[i].split("\t");
//				String uuid = array[0];
				String flavorName = array[1];
//				String createTime = array[2].split(" ")[0];

//				if( createTime.compareTo(startTrainTime)<0){
//				    startTrainTime = createTime;
//                }
				if(keySet.contains(flavorName)){
				    vmNum.put(flavorName,vmNum.get(flavorName)+1);

				}

			}
		}

//		计算时间间隔，取平均取整（进一法），并将flavor的预测次数存到vmNum中。
        //算两个日期间隔多少天
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = null;
        Date date2 = null;
        Date date3 = null;
        try {
            date1 = format.parse(startTrainTime);
            date2 = format.parse(startDate);
            date3 = format.parse(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int trainTime = (int) ((date2.getTime() - date1.getTime()) / (1000*3600*24));
        int  preTime = (int) ((date3.getTime() - date2.getTime()+1) / (1000*3600*24));
        for (String str : keySet) {
            Double db = Double.valueOf(vmNum.get(str));
            vmNum.put(str, (int) (db/trainTime*preTime)+1);
        }
/*        先用贪心算法进行分配：
        1.先计算非预测资源约束（先不考虑）；
            计算预测资源约束；
        2.

                终止*/


        List<Integer> cpuList = new ArrayList();
        List<Integer> memList = new ArrayList();
        List<String> nameList = new ArrayList<>();

        int vmPreNum = 0;
        int j= 1;
        for (String str : keySet) {
            int vmKey = vmNum.get(str);
            vmPreNum = vmPreNum + vmKey;
            results[j]=str+" "+String.valueOf(vmKey);
            j++;
            int cpuCost = vmMap.get(str)[0];
            int memCost = vmMap.get(str)[1]>>10;
            for (int i = 0; i < vmKey; i++) {
                cpuList.add(cpuCost);
                memList.add(memCost);
                nameList.add(str);
            }
        }


        List<Ecs> ecsList = new ArrayList<>();
        ecsList.add(new Ecs(CPUNum,MEMNum));

        int cpuListLength = cpuList.size();

        if(preType.equals("CPU")){

//从大到小排序
            for (int i = cpuListLength-1; i >= 0; i--) {

                int vmNext= Collections.max(cpuList);
                int index =  cpuList.indexOf(vmNext);
                int vmCons = memList.get(index);
                String flavorName = nameList.get(index);

                Collections.sort(ecsList,new EcsCpuComparator());

                boolean needNew = true;

                for (Ecs ecs : ecsList){
                    int tmp1 = ecs.getCpuNum();
                    int tmp2 = ecs.getMemNum();
                    if(vmNext<tmp1 && vmCons<tmp2){
                        System.out.println(ecs.getCpuNum());
                        ecs.setCpuNum(tmp1-vmNext);
                        ecs.setMemNum(tmp2-vmCons);
                        ecs.setNameList(flavorName);
                        needNew = false;
                        break;
                    }
                }
                if(needNew){
                    //            下面这个是跟for是else的关系
                    ecsList.add(new Ecs(CPUNum,MEMNum));
                    ecsList.get(ecsList.size()-1).setCpuNum(CPUNum-vmNext);
                    ecsList.get(ecsList.size()-1).setMemNum(MEMNum-vmCons);
                    ecsList.get(ecsList.size()-1).setNameList(flavorName);
                }

//                int index =  cpuList.indexOf(vmNext);
//                int vmCons = memList.get(index);
//                String flavorName = nameList.get(index);
                cpuList.remove(index);
                memList.remove(index);
                nameList.remove(index);
            }
        }else {
//从大到小排序
            for (int i = cpuListLength-1; i >= 0; i--) {

                int vmNext= Collections.max(memList);
                int index =  memList.indexOf(vmNext);
                int vmCons = cpuList.get(index);
                String flavorName = nameList.get(index);
//                int vmNext =  memList.get(i);
//                int vmCons = cpuList.get(i);
//                String flavorName = nameList.get(i);

                Collections.sort(ecsList,new EcsCpuComparator());

                boolean needNew = true;

                for (Ecs ecs : ecsList){
                    int tmp1 = ecs.getMemNum();
                    int tmp2 = ecs.getCpuNum();
                    if(vmNext<tmp1 && vmCons<tmp2){
                        System.out.println(ecs.getCpuNum());
                        ecs.setMemNum(tmp1-vmNext);
                        ecs.setCpuNum(tmp2-vmCons);
                        ecs.setNameList(flavorName);
                        needNew = false;
                        break;
                    }
                }
                if(needNew){
                    //            下面这个是跟for是else的关系
                    ecsList.add(new Ecs(CPUNum,MEMNum));
                    ecsList.get(ecsList.size()-1).setCpuNum(CPUNum-vmCons);
                    ecsList.get(ecsList.size()-1).setMemNum(MEMNum-vmNext);
                    ecsList.get(ecsList.size()-1).setNameList(flavorName);
                }

//                int index =  cpuList.indexOf(vmNext);
//                int vmCons = memList.get(index);
//                String flavorName = nameList.get(index);
                cpuList.remove(index);
                memList.remove(index);
                nameList.remove(index);
            }

        }


//将结果输出，结果包括两部分，预测部分在vmNum，分配结果在ecsList中
        results[0] = String.valueOf(vmPreNum);

        results[vmNum.size()+1]= "";

        results[vmNum.size()+2]=String.valueOf(ecsList.size());

        int k=1;
        for (int i = vmNum.size()+3;i<ecsList.size()+vmNum.size()+3;i++ ){

            results[i]=k+ ecsList.get(k-1).Path();
            k++;
        }
		return results;
	}


}
