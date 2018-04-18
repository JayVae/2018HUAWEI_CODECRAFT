/*package com.elasticcloudservice.predict;

*//**
 * @Author: Jay
 * @Date: Created in 20:53 2018/4/13
 * @Modified By:
 *//*
 *
public class tmp2 {

    //      判断最后一个的利用率
        if (preType.equals("CPU")){
        if(ecsList.size()>1 &&ecsList.get(ecsList.size()-1).getCpuPercent()< 0.3 ){
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
//                改一个完全背包就行了,备选物品是1:1的那几个,子背包问题
//                vmRatio = OtherUtil.sortMapByValue(vmRatio);  //按Key进行升序排序
//                String str = Collections.min(keySet);
//                int cpuCost = vmMap.get(str)[0];
//                int memCost = vmMap.get(str)[1]>>10;
//                int numAdp = Math.min(cpuAdp/cpuCost,memAdp/memCost);
//                vmPreNum = vmPreNum + numAdp;
//                int oriNum = vmNum.get(str);
//                vmNum.put(str,oriNum+numAdp);
//                ecsList.get(ecsList.size()-1).setCpuNum(cpuAdp-numAdp*cpuCost);
//                ecsList.get(ecsList.size()-1).setMemNum(memAdp-numAdp*memCost);
//                ecsList.get(ecsList.size()-1).setCpuPercent(ecsList.get(ecsList.size()-1).getCpuNum()/CPUNum);
//                ecsList.get(ecsList.size()-1).setMemPercent(ecsList.get(ecsList.size()-1).getMemNum()/MEMNum);
//                for (int ttt=0;ttt<numAdp;ttt++) {
//                    ecsList.get(ecsList.size() - 1).setNameList(str);
//                }

            int cpuMin10 = Math.min(cpuAdp,memAdp);
            for(int l=4;l>=0;l--){
                int tmpClass = 1<<l;
                int tmpNum = cpuMin10 /tmpClass;
                List<String> tmpList = new ArrayList<>(vmRatio.get(1));
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
        }else if (ecsList.get(ecsList.size()-1).getMemPercent() >0.6 && ecsList.get(ecsList.size()-1).getMemPercent()<0.9){
            int cpuAdp = ecsList.get(ecsList.size()-1).getCpuNum();
            int memAdp = ecsList.get(ecsList.size()-1).getMemNum();
//                改一个完全背包就行了,备选物品是1:1的那几个,子背包问题
//                vmRatio = OtherUtil.sortMapByValue(vmRatio);  //按Key进行升序排序
//                String str = Collections.min(keySet);
//                int cpuCost = vmMap.get(str)[0];
//                int memCost = vmMap.get(str)[1]>>10;
//                int numAdp = Math.min(cpuAdp/cpuCost,memAdp/memCost);
//                vmPreNum = vmPreNum + numAdp;
//                int oriNum = vmNum.get(str);
//                vmNum.put(str,oriNum+numAdp);
//                ecsList.get(ecsList.size()-1).setCpuNum(cpuAdp-numAdp*cpuCost);
//                ecsList.get(ecsList.size()-1).setMemNum(memAdp-numAdp*memCost);
//                ecsList.get(ecsList.size()-1).setCpuPercent(ecsList.get(ecsList.size()-1).getCpuNum()/CPUNum);
//                ecsList.get(ecsList.size()-1).setMemPercent(ecsList.get(ecsList.size()-1).getMemNum()/MEMNum);
//                for (int ttt=0;ttt<numAdp;ttt++) {
//                    ecsList.get(ecsList.size() - 1).setNameList(str);
//                }

            int cpuMin10 = Math.min(cpuAdp,memAdp);
            for(int l=4;l>=0;l--){
                int tmpClass = 1<<l;
                int tmpNum = cpuMin10 /tmpClass;
                List<String> tmpList = new ArrayList<>(vmRatio.get(1));
                if (!tmpList.isEmpty()){
                    if (tmpNum!=0 ){
                        if(tmpNum!=0 && tmpList.retainAll(cpuClass.get(tmpClass))){
                            cpuMin10= cpuMin10-tmpNum*tmpClass;
                            vmPreNum = vmPreNum + tmpNum;
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

}*/

//            下面是采用贪心法来做

//        GreedMethod.greed1(CPUNum,MEMNum,cpuList,memList,nameList,ecsList,preType);
//        变换之前的利用率
/*        System.out.println("变幻之前，每个ecs的利用率如下：");
        for (Ecs ecs : ecsList) {
            System.out.println(ecs.getCpuPercent());
        }*/
