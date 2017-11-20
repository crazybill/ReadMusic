package com.music.read

/**
 * Created by xupanpan on 5/16/17.
 */
object LogCat {


    val I = 0
    val D = 1
    var LOG_LEVER = 0


    fun i(str: String, str1: String, str2: String) {
        i("$str $str1 $str2")
    }

    fun i(str: String, str1: String) {

        i(str + " " + str1)
    }

    fun i(str: String) {
        if (I == LOG_LEVER) {
            print("I", str)
        }
    }

    fun d(str: String, str1: String, str2: String) {
        d("$str $str1 $str2")
    }

    fun d(str: String, str1: String) {

        d(str + " " + str1)
    }

    fun d(str: String) {
        if (D == LOG_LEVER) {
            print("D", str)
        }
    }


    fun print(logLever: String, str: String) {

        val time = TimeUtils.convertTime(System.currentTimeMillis(), TimeUtils.FORMAT_SECONDS3)

        val threadInfo = Thread.currentThread().name + ":" + Thread.currentThread().id

        println("$time $threadInfo/? $logLever/ $str")

    }

}
