package com.music.read

import java.text.SimpleDateFormat
import java.util.Date

/**
 * Created by xupanpan on 3/18/17.
 */
object TimeUtils {

    val FORMAT_SECONDS = "yy-MM-dd HH:mm:ss"
    val FORMAT_SECONDS2 = "yyyy-MM-dd HH:mm:ss"
    val FORMAT_SECONDS3 = "yyyy-MM-dd HH:mm:ss:ms"
    val FORMAT_TIME = "MM-dd HH:mm:ss"


    val currntTime: String
        get() = getTime(System.currentTimeMillis())

    fun getTime(time: Long): String {

        return convertTime(time, FORMAT_SECONDS)

    }

    fun getTime(time: String): String? {
        var time = time

        if (TextUtils.isEmpty(time)) {
            return time
        }

        time = checkTimeString(time)

        return convertTime(java.lang.Long.valueOf(time), FORMAT_SECONDS)
    }

    fun getTime(time: String, format: String): String? {
        var time = time

        if (TextUtils.isEmpty(time)) {
            return time
        }

        time = checkTimeString(time)

        return convertTime(java.lang.Long.valueOf(time), format)
    }

    private fun checkTimeString(time: String): String {

        return if (time.length != 13) {
            checkTimeString(time + "0")
        } else {
            time
        }
    }

    fun convertTime(timemills: Long, format: String): String {
        val simpleDateFormat = SimpleDateFormat(format)
        return simpleDateFormat.format(Date(timemills))
    }

}
