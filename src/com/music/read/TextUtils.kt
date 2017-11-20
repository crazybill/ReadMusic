package com.music.read

object TextUtils {
    fun isEmpty(str: String?): Boolean {
        return str == null || "" == str
    }
}
