package com.music.read

import java.io.*

/**
 * Created by xupanpan on 3/24/17.
 */
object FileLoadUtils {

    val localPath: File
        get() {

            val file = File(System.getProperty("user.home") + "/.MusicOcean/config/")
            if (!file.exists()) {
                file.mkdirs()
            }
            return file
        }


    fun readTextFile(file: File): String? {

        try {
            return readTextFile(FileInputStream(file))
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

    }


    fun readTextFile(inputStream: InputStream): String {
        val outputStream = ByteArrayOutputStream()

        val buf = ByteArray(1024)
        val len: Int
        try {
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len)
            }
            outputStream.close()
            inputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return outputStream.toString()
    }


    fun saveTextFile(file: File?, str: String?) {

        if (file != null && str != null) {
            try {
                val fos = FileOutputStream(file!!)
                fos.write(str!!.toByteArray(charset("UTF-8")))
                fos.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

}
