package com.music.read

import java.io.File

/**
 * Created by xupanpan on 06/11/2017.
 */
class FileNameEditer(private val homeView: HomeView) {

    fun addStrList(textWei: String?, textStr: String?) {

        if (textWei == null || textWei.length == 0 || textStr == null || textStr.length == 0) {
            return
        }

        var wei = 1
        try {
            wei = Integer.parseInt(textWei)
            if (wei < 1) {
                wei = 1
            }
        } catch (e: Exception) {
            wei = 1
        }

        for (mp3Info in DataManager.instans.list) {
            if (mp3Info.isChecked) {
                var fileName = mp3Info.fileName
                if (wei == 1) {
                    fileName = textStr + fileName!!
                } else if (wei > fileName!!.length) {
                    fileName = fileName!! + textStr
                } else {
                    val substring = fileName!!.substring(0, wei - 1)
                    val substring2 = fileName!!.substring(wei - 1, fileName!!.length)
                    fileName = substring + textStr + substring2
                }

                val musicFile = mp3Info.musicFile ?: continue
                val newNameFile = File(musicFile.getParent(), fileName!!)
                if (!newNameFile.exists()) {
                    musicFile.renameTo(newNameFile)
                    mp3Info.filePath = newNameFile.getPath()
                    mp3Info.fileName = fileName
                }
            }
        }
        homeView.listView.setItems(null)
        homeView.listView.setItems(DataManager.instans.list)

    }

    fun renameList(textBefor: String?, textAfter: String?) {
        var textAfter = textAfter

        if (textBefor == null || textBefor.length == 0) {
            return
        }
        if (textAfter == null) {
            textAfter = ""
        }

        for (mp3Info in DataManager.instans.list) {
            if (mp3Info.isChecked) {
                val fileName = mp3Info.fileName
                if (fileName!!.contains(textBefor)) {
                    val musicFile = mp3Info.musicFile ?: continue
                    val replace = fileName!!.replace(textBefor, textAfter)
                    val newNameFile = File(musicFile.getParent(), replace)
                    if (!newNameFile.exists()) {
                        musicFile.renameTo(newNameFile)
                        mp3Info.filePath = newNameFile.getPath()
                        mp3Info.fileName = replace
                    }
                }
            }
        }

        homeView.listView.setItems(null)
        homeView.listView.setItems(DataManager.instans.list)
    }


}
