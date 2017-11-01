package com.music.read;

/**
 * Created by xupanpan on 13/10/2017.
 */
public class Utils {


    /**
     * 判断是否为乱码
     *
     * @param str
     * @return
     */
    public static boolean isMessyCode(String str) {
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            // 当从Unicode编码向某个字符集转换时，如果在该字符集中没有对应的编码，则得到0x3f（即问号字符?）
            //从其他字符集向Unicode编码转换时，如果这个二进制数在该字符集中没有标识任何的字符，则得到的结果是0xfffd
            //System.out.println("--- " + (int) c);
            if ((int) c == 0xfffd) {
                // 存在乱码
                //System.out.println("存在乱码 " + (int) c);
                return true;
            }
        }
        return false;
    }


    public static String getMusicTime(int time) {

        String format = "%1$s:%2$s";

        if (time < 60) {
            return String.format(format, "00", time);
        } else {
            int m = time / 60;
            int s = time % 60;

            String mm = m < 10 ? ("0" + m) : String.valueOf(m);
            String ss = s < 10 ? ("0" + s) : String.valueOf(s);
            return String.format(format, mm, ss);
        }


    }


}
