package com.filetool.pojo;

import java.util.Comparator;

/**
 * @Author: Jay
 * @Date: Created in 15:23 2018/3/13
 * @Modified By:
 */
public class EcsMemComparator implements Comparator<Ecs> {

    @Override
    public int compare(Ecs o1, Ecs o2) {
        return (o1.getMemNum() > o2.getMemNum() ? -1 : (o1.getMemNum() == o2.getMemNum() ? 0 : 1));
    }

}
