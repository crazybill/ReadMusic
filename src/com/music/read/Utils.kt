package com.music.read

/**
 * Created by xupanpan on 13/10/2017.
 */
object Utils {


    private val format = "%1\$s:%2\$s"


    /**
     * 判断是否为乱码
     *
     * @param str
     * @return
     */
    fun isMessyCode(str: String): Boolean {
        for (i in 0 until str.length) {
            val c = str[i]
            // 当从Unicode编码向某个字符集转换时，如果在该字符集中没有对应的编码，则得到0x3f（即问号字符?）
            //从其他字符集向Unicode编码转换时，如果这个二进制数在该字符集中没有标识任何的字符，则得到的结果是0xfffd
            //System.out.println("--- " + (int) c);
            if (c.toInt() == 0xfffd) {
                // 存在乱码
                //System.out.println("存在乱码 " + (int) c);
                return true
            }
        }
        return false
    }

    fun getMusicTime(time: Int): String {

        if (time < 60) {
            return String.format(format, "00", if (time > 9) time else "0" + time)
        } else {
            val m = time / 60
            val s = time % 60

            val mm = if (m < 10) "0" + m else m.toString()
            val ss = if (s < 10) "0" + s else s.toString()
            return String.format(format, mm, ss)
        }


    }


}
