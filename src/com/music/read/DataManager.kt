package com.music.read

import javafx.collections.FXCollections
import javafx.collections.ObservableList

import java.util.ArrayList
import java.util.Random

class DataManager private constructor() {
    @get:Synchronized
    val list: ObservableList<MP3Info>

    val currentPlayPosition: Int
        @Synchronized get() {

            for (i in list.indices) {
                val mp3Info = list[i]
                if (mp3Info.isPlaying) {
                    return i
                }
            }
            return -1
        }

    val currentPlayInfo: MP3Info?
        @Synchronized get() {
            for (i in list.indices) {
                val mp3Info = list[i]
                if (mp3Info.isPlaying) {
                    return mp3Info
                }
            }
            return null
        }


    val listSize: Int
        @Synchronized get() = list.size


    val isListEmpty: Boolean
        @Synchronized get() = list.isEmpty()

    init {
        list = FXCollections.observableArrayList()
    }


    @Synchronized
    fun getMP3InfoByPosition(position: Int): MP3Info? {
        return if (position < 0 || position >= list.size) {
            null
        } else list[position]
    }


    @Synchronized
    fun add2List(mp3InfoList: List<MP3Info>) {
        list.addAll(mp3InfoList)
    }


    @Synchronized
    fun add2List(mp3Info: MP3Info) {
        list.add(mp3Info)
    }

    @Synchronized
    fun clearList() {
        list.clear()
        PlayListManager.savePlayList(list)
    }

    @Synchronized
    fun remove(mp3Info: MP3Info?) {
        if (mp3Info != null) {
            list.remove(mp3Info)
            PlayListManager.savePlayList(list)
        }

    }

    @Synchronized
    fun setAllCheckStatus(isCheck: Boolean) {
        for (mp3Info in list) {
            mp3Info.isChecked = isCheck
        }
    }

    @Synchronized
    fun removeSelected(): Boolean {

        var isPlayCurrent = false
        val rList = ArrayList<MP3Info>()
        for (i in list.indices) {
            val mp3Info = list[i]
            if (mp3Info.isChecked) {
                rList.add(mp3Info)
                if (mp3Info.isPlaying) {
                    isPlayCurrent = true
                }
            }
        }
        if (rList.size > 0) {
            for (info in rList) {
                list.remove(info)
            }
        }

        PlayListManager.savePlayList(list)

        return isPlayCurrent
    }

    @Synchronized
    fun removeMP3Info(info: MP3Info) {
        list.remove(info)
        PlayListManager.savePlayList(list)
    }


    @Synchronized
    fun setPlayNextRadomPosition() {
        if (list.size > 1) {
            clearCurrentPlayPosition()
            val random = Random()
            val i = random.nextInt(list.size - 1)

            if (i != -1) {
                list[i].isPlaying = true
            }
        }

    }


    @Synchronized
    fun setPlayNextPosition() {

        var nextPosition = -1
        for (i in list.indices) {
            val mp3Info = list[i]
            if (mp3Info.isPlaying) {
                if (i == list.size - 1) {
                    nextPosition = 0
                } else {
                    nextPosition = i + 1
                }
                mp3Info.isPlaying = false
                break
            }
        }
        if (nextPosition != -1) {
            list[nextPosition].isPlaying = true
        }
    }

    @Synchronized
    fun setPlayLastPosition() {

        var lastPosition = -1
        for (i in list.indices) {
            val mp3Info = list[i]
            if (mp3Info.isPlaying) {
                if (i == 0) {
                    lastPosition = list.size - 1
                } else {
                    lastPosition = i - 1
                }
                mp3Info.isPlaying = false
                break
            }
        }
        if (lastPosition != -1) {
            list[lastPosition].isPlaying = true
        }
    }

    @Synchronized
    fun clearCurrentPlayPosition() {
        for (info in list) {
            info.isPlaying = false
        }
    }


    @Synchronized
    fun setNewPlayPosition(info: MP3Info) {
        clearCurrentPlayPosition()
        info.isPlaying = true
    }

    companion object {

        val instans = DataManager()
    }

}
