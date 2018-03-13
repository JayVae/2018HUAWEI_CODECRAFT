package com.filetool.pojo;

import java.util.Comparator;

/**
 * @Author: Jay
 * @Date: Created in 15:23 2018/3/13
 * @Modified By:
 */
public class EcsCpuComparator implements Comparator<Ecs> {

    @Override
    public int compare(Ecs o1, Ecs o2) {
        return (o1.getCpuNum() > o2.getCpuNum() ? -1 : (o1.getCpuNum() == o2.getCpuNum() ? 0 : 1));
    }

}
