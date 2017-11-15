package com.music.read;

/**
 * Created by xupanpan on 5/16/17.
 */
public class LogCat {


    public static final int I = 0;
    public static final int D = 1;
    public static int LOG_LEVER = 0;


    public static void i(String str, String str1, String str2) {
        i(str + " " + str1 + " " + str2);
    }

    public static void i(String str, String str1) {

        i(str + " " + str1);
    }

    public static void i(String str) {
        if (I == LOG_LEVER) {
            print("I", str);
        }
    }

    public static void d(String str, String str1, String str2) {
        d(str + " " + str1 + " " + str2);
    }

    public static void d(String str, String str1) {

        d(str + " " + str1);
    }

    public static void d(String str) {
        if (D == LOG_LEVER) {
            print("D", str);
        }
    }


    public static void print(String logLever, String str) {

        String time = TimeUtils.convertTime(System.currentTimeMillis(), TimeUtils.FORMAT_SECONDS3);

        String threadInfo = Thread.currentThread().getName() + ":" + Thread.currentThread().getId();

        System.out.println(time + " " + threadInfo + "/? " + logLever + "/ " + str);

    }

}
