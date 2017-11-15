package com.music.read;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by xupanpan on 3/18/17.
 */
public class TimeUtils {

    public static final String FORMAT_SECONDS = "yy-MM-dd HH:mm:ss";
    public static final String FORMAT_SECONDS2 = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_SECONDS3 = "yyyy-MM-dd HH:mm:ss:ms";
    public static final String FORMAT_TIME = "MM-dd HH:mm:ss";


    public static String getCurrntTime() {
        return getTime(System.currentTimeMillis());
    }

    public static String getTime(long time) {

        return convertTime(time, FORMAT_SECONDS);

    }

    public static String getTime(String time) {

        if (TextUtils.isEmpty(time)) {
            return time;
        }

        time = checkTimeString(time);

        return convertTime(Long.valueOf(time), FORMAT_SECONDS);
    }
    public static String getTime(String time,String format) {

        if (TextUtils.isEmpty(time)) {
            return time;
        }

        time = checkTimeString(time);

        return convertTime(Long.valueOf(time), format);
    }

    private static String checkTimeString(String time) {

        if (time.length() != 13) {
            return checkTimeString(time + "0");
        } else {
            return time;
        }
    }

    public static String convertTime(long timemills, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(new Date(timemills));
    }

}
