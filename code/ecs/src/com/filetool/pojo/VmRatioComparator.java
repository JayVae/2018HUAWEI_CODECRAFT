package com.filetool.pojo;

import java.util.Comparator;
import java.util.Map;

/**
 * @Author: Jay
 * @Date: Created in 20:14 2018/3/18
 * @Modified By:
 */
public class VmRatioComparator implements Comparator<Map.Entry<String, Integer>>{

    @Override
    public int compare(Map.Entry<String, Integer> me1, Map.Entry<String,Integer> me2) {
        return me1.getValue().compareTo(me2.getValue());
    }

}
