package com.filetool.util;

import com.filetool.pojo.Ecs;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Jay
 * @Date: Created in 13:58 2018/3/16
 * @Modified By:
 */
public class Pack {

    public static void pack(int CPUNum,int MEMNum,List<Integer> cpuList, List<Integer> memList, List<String> nameList, String predType, List<Ecs> ecsList,int kkk) {

        int cpuAllCost = CPUNum, memAllCost = MEMNum;
        int cpuLength = cpuList.size();
        List<Integer> cpuListCopy = new ArrayList<>(cpuList);
        List<Integer> memListCopy = new ArrayList<>(memList);
        List<String> nameListCopy = new ArrayList<>(nameList);
        int dp[][] = new int[cpuAllCost+1][memAllCost+1];
        List<Integer> valueList = new ArrayList();
        if (predType.equals("CPU")){
            valueList = cpuList;
        }else{
            valueList = memList;
        }
        int[][][] mark = new int[cpuLength][cpuAllCost+1][memAllCost+1];
        int sum=0;

        for (int i = 0; i < cpuLength; i++) {
            for (int j = cpuAllCost; j >= cpuList.get(i); j--) {
                for (int k = memAllCost; k >=memList.get(i); k--) {
                    if(dp[j][k] < (dp[j - cpuList.get(i)][k - memList.get(i)] + valueList.get(i))){
                        dp[j][k]= dp[j - cpuList.get(i)][k - memList.get(i)] + valueList.get(i);
                        mark[i][j][k] =1;
                        sum=sum+1;
                    }else{
                        dp[j][k] = dp[j][k];
                    }
//                   dp[j][k] = Math.max(dp[j][k], dp[j - cpuList.get(i)][k - memList.get(i)] + valueList.get(i));
                }
            }
        }
        System.out.println(dp[cpuAllCost][memAllCost]);

//        写入路径
        int i= cpuLength-1;
        while(i>=0){
            if (mark[i][cpuAllCost][memAllCost]==1){
                cpuAllCost = cpuAllCost - cpuList.get(i);
                memAllCost = memAllCost - memList.get(i);
                Ecs ecs = ecsList.get(kkk);
                ecs.setNameList(nameList.get(i));
                cpuListCopy.remove(i);
                memListCopy.remove(i);
                nameListCopy.remove(i);
            }
            i--;
        }

        ecsList.get(kkk).setMemNum(memAllCost);
        ecsList.get(kkk).setCpuNum(cpuAllCost);
        ecsList.get(kkk).setCpuPercent((double) (CPUNum-cpuAllCost)/CPUNum);
        ecsList.get(kkk).setMemPercent((double) (MEMNum-memAllCost)/MEMNum);

        if (cpuListCopy.size()!=0){
            ecsList.add(new Ecs(CPUNum,MEMNum));
            kkk++;
            pack(CPUNum,MEMNum,cpuListCopy,memListCopy,nameListCopy,predType,ecsList,kkk);
        }

    }

}
