package com.music.read

import java.io.File

/**
 * Created by xupanpan on 12/10/2017.
 */
class MP3Info {

    var fileName: String? = null

    var title = ""
    var album = ""
    var artist = ""
    var genre = ""
    var time: Int = 0

    var filePath: String? = null
    var isChecked: Boolean = false
    var isPlaying: Boolean = false

    val musicFile: File?
        get() {
            if (TextUtils.isEmpty(filePath)) {
                return null
            }
            val file = File(filePath!!)
            return if (!file.exists()) {
                null
            } else file
        }
}
