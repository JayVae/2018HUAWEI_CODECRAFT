package com.filetool.pojo;

import java.util.*;

/**
 * @Author: Jay
 * @Date: Created in 14:22 2018/3/13
 * @Modified By:
 */
public class Ecs {

    private int cpuNum ;

    private int memNum;

    private List<String> nameList = new ArrayList<>();

    private double cpuPercent;

    private double memPercent;

    public double getMemPercent() {
        return memPercent;
    }

    public void setMemPercent(double memPercent) {
        this.memPercent = memPercent;
    }

    public String Path() {
        StringBuffer sb = new StringBuffer();
        List<String> list = this.nameList;
        Map<String, Integer> map = new HashMap<>();
        for (String str : list) {
            Integer num = map.get(str);
            map.put(str, num == null ? 1 : num + 1);
        }
        Set set = map.entrySet();
        Iterator it = set.iterator();
        while (it.hasNext()) {
            Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>) it.next();
            sb.append( " "+entry.getKey()+" "+entry.getValue());
//            System.out.println("key " + entry.getKey() + " value : " + entry.getValue());
        }
        return sb.toString();
    }

    public void setNameList(String flavorName) {
        this.nameList.add(flavorName);
    }

    public List<String> getNameList() {
        return nameList;
    }

    public double getCpuPercent() {
        return cpuPercent;
    }

    public void setCpuPercent(double cpuPercent) {
        this.cpuPercent = cpuPercent;
    }

    public Ecs(int cpuNum, int memNum) {
        this.cpuNum = cpuNum;
        this.memNum = memNum;
    }

    public int getCpuNum() {
        return cpuNum;
    }

    public void setCpuNum(int cpuNum) {
        this.cpuNum = cpuNum;
    }

    public int getMemNum() {
        return memNum;
    }

    public void setMemNum(int memNum) {
        this.memNum = memNum;
    }
}
