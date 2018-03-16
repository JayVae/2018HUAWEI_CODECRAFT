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
//                    改造一下，需要记录路径，选了谁
                    if(dp[j][k] < (dp[j - cpuList.get(i)][k - memList.get(i)] + valueList.get(i))){
//                        ecs.setNameList(nameList.get(i));
//                        cpuList.remove(i);
//                        memList.remove(i);
//                        nameList.remove(i);

//                        System.out.println(i);

                        dp[j][k]= dp[j - cpuList.get(i)][k - memList.get(i)] + valueList.get(i);
                        mark[i][j][k] =1;
                        sum=sum+1;

//                        System.out.println(i+" "+j+" "+k+" "+dp[j][k]);
                    }else{
                        dp[j][k] = dp[j][k];
                    }
//                   dp[j][k] = Math.max(dp[j][k], dp[j - cpuList.get(i)][k - memList.get(i)] + valueList.get(i));
                }
            }
        }
        System.out.println(dp[cpuAllCost][memAllCost]);

        int i= cpuLength-1;
        while(i>=0){
            if (mark[i][cpuAllCost][memAllCost]==1){
//                System.out.println("选择了第"+i+"个vm");
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
