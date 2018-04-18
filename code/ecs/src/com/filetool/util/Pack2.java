package com.filetool.util;

import com.filetool.pojo.Ecs;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Jay
 * @Date: Created in 21:27 2018/4/14
 * @Modified By:
 */
public class Pack2 {

    public void pack(int CPUNum,int MEMNum,List<Integer> cpuList, List<Integer> memList, List<String> nameList, String predType, List<Ecs> ecsList,int kkk) {

        int cpuAllCost = CPUNum, memAllCost = MEMNum;
        int cpuLength = cpuList.size();
        List<Integer> cpuListCopy = new ArrayList<>(cpuList);
        List<Integer> memListCopy = new ArrayList<>(memList);
        int dp[][] = new int[cpuAllCost+1][memAllCost+1];
        List<Integer> valueList = new ArrayList();
        if (predType.equals("CPU")){
            valueList = cpuList;
        }else{
            valueList = memList;
        }
//        int[][][] mark = new int[cpuLength][cpuAllCost+1][memAllCost+1];
        int[][] mark = new int[cpuLength][cpuAllCost+1];
        int[][] mark2 = new int[cpuLength][memAllCost+1];
//        int[][] mark = new int[cpuLength][cpuAllCost+1];
        int sum=0;

        for (int i = 0; i < cpuLength; i++) {
            for (int j = cpuAllCost; j >= cpuList.get(i); j--) {
                for (int k = memAllCost; k >=memList.get(i); k--) {
                    if(dp[j][k] < (dp[j - cpuList.get(i)][k - memList.get(i)] + valueList.get(i))){
                        dp[j][k]= dp[j - cpuList.get(i)][k - memList.get(i)] + valueList.get(i);
                        mark[i][j] =1;
                        mark2[i][k] =1;
//                        mark[i][j][k] =1;
                        sum=sum+1;
                    }else{
                        dp[j][k] = dp[j][k];
                    }
                }
            }
        }
        System.out.println(dp[cpuAllCost][memAllCost]);

//        写入路径
        int i= cpuLength-1;
        while(i>=0){
            if (mark[i][cpuAllCost]==1 && mark2[i][memAllCost]==1){
                cpuAllCost = cpuAllCost - cpuListCopy.get(i);
                memAllCost = memAllCost - memListCopy.get(i);
                Ecs ecs = ecsList.get(kkk);
                ecs.setNameList(nameList.get(i));
                cpuList.remove(i);
                memList.remove(i);
                nameList.remove(i);
            }
            i--;
        }

        ecsList.get(kkk).setMemNum(memAllCost);
        ecsList.get(kkk).setCpuNum(cpuAllCost);
        ecsList.get(kkk).setCpuPercent((double) (CPUNum-cpuAllCost)/CPUNum);
        ecsList.get(kkk).setMemPercent((double) (MEMNum-memAllCost)/MEMNum);

    }

}
