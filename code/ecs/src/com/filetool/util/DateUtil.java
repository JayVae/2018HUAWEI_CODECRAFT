package com.filetool.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author: Jay
 * @Date: Created in 15:48 2018/3/17
 * @Modified By:
 */
public class DateUtil {

    //算两个日期间隔多少天
    public static int getDayLength(String startTime,String endTime){

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = null;
        Date date2 = null;
        Date date3 = null;
        try {
            date1 = format.parse(startTime);
            date2 = format.parse(endTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int dayLength = (int) ((date2.getTime() - date1.getTime()) / (1000*3600*24))+1;
        return dayLength;
    }

}
