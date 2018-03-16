package com.filetool.util;

import com.filetool.pojo.Ecs;
import com.filetool.pojo.EcsCpuComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Author: Jay
 * @Date: Created in 21:02 2018/3/14
 * @Modified By:
 */
public class GreedMethod {

    //判断服务器集合中剩余最少的，然后遍历待分配的vm集合，将能放入的最大的放入（另一种策略是递归地找能放入的最大的组合），直至放不下为止
    public static void greed1(int CPUNum,int MEMNum,List<Integer> cpuList, List<Integer> memList, List<String> nameList,List<Ecs> ecsList,String preType){

        List<Integer> resList = new ArrayList<>();
        List<Integer> consList = new ArrayList<>();
        if (preType.equals("CPU")){
            resList = cpuList;
            consList = memList;
        }else {
            resList =  memList;
            consList =cpuList;
        }

        int listLength = resList.size();
        for (int i = listLength-1; i >= 0; i--) {

            int vmNext= Collections.max(resList);
            int index =  resList.indexOf(vmNext);
            int vmCons = consList.get(index);
            String flavorName = nameList.get(index);

            Collections.sort(ecsList,new EcsCpuComparator());

            boolean needNew = true;

            if(preType.equals("CPU")) {
                for (Ecs ecs : ecsList) {
                    int tmp1 = ecs.getCpuNum();
                    int tmp2 = ecs.getMemNum();
                    if (vmNext <= tmp1 && vmCons <= tmp2) {
                        ecs.setCpuNum(tmp1 - vmNext);
                        ecs.setMemNum(tmp2 - vmCons);
                        ecs.setNameList(flavorName);
                        needNew = false;
                        break;
                    }
                }
                if (needNew) {
                    ecsList.add(new Ecs(CPUNum, MEMNum));
                    ecsList.get(ecsList.size() - 1).setCpuNum(CPUNum - vmNext);
                    ecsList.get(ecsList.size() - 1).setMemNum(MEMNum - vmCons);
                    ecsList.get(ecsList.size() - 1).setNameList(flavorName);
                }
            }else{
                    for (Ecs ecs : ecsList){
                        int tmp1 = ecs.getMemNum();
                        int tmp2 = ecs.getCpuNum();
                        if(vmNext<=tmp1 && vmCons<=tmp2){
                            ecs.setMemNum(tmp1-vmNext);
                            ecs.setCpuNum(tmp2-vmCons);
                            ecs.setNameList(flavorName);
                            needNew = false;
                            break;
                        }
                    }
                    if(needNew){
                        ecsList.add(new Ecs(CPUNum,MEMNum));
                        ecsList.get(ecsList.size()-1).setCpuNum(CPUNum-vmCons);
                        ecsList.get(ecsList.size()-1).setMemNum(MEMNum-vmNext);
                        ecsList.get(ecsList.size()-1).setNameList(flavorName);
                    }
                }

            resList.remove(index);
            consList.remove(index);
            nameList.remove(index);
        }

    }

}
