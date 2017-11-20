package com.music.read

import javafx.application.Platform
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import org.jaudiotagger.audio.AudioFile
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.audio.AudioHeader
import org.jaudiotagger.audio.mp3.MP3File
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.Tag

import java.io.File
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Created by xupanpan on 03/11/2017.
 */
class MusicFileParser(private val main: HomeView) {
    private val executor = Executors.newSingleThreadExecutor()

    private var alert: Alert? = null

    fun loadMp3Data(files: List<File>) {

        alert = Alert(Alert.AlertType.NONE)
        alert!!.headerText = "加载中，请稍等..."
        alert!!.initOwner(main.primaryStage)
        executor.execute {
            for (i in files.indices) {
                val f = files[i]
                loadFile(f)
            }
            Platform.runLater {
                PlayListManager.savePlayList(DataManager.instans.list)
                alert!!.dialogPane.buttonTypes.add(ButtonType.OK)
                alert!!.close()
            }
        }
        alert!!.showAndWait()
    }


    private fun loadFile(f: File) {

        if (f.isFile) {
            val name = f.name
            if (isMusicFile(name)) {
                val bean = MP3Info()
                bean.isChecked = main.isSelectAll
                bean.fileName = name
                bean.filePath = f.path
                if (parseMP3Info(bean)) {
                    updateLoad(bean)
                }
            }
        } else {
            val files = f.listFiles()
            for (ff in files!!) {
                loadFile(ff)
            }
        }
    }

    fun updateLoad(bean: MP3Info) {
        Platform.runLater {
            DataManager.instans.add2List(bean)
            alert!!.contentText = "正加载：" + bean.fileName!!
        }
    }

    private fun isMusicFile(name: String): Boolean {

        return name.endsWith(".mp3") || name.endsWith(".MP3") || name.endsWith(".flac") || name.endsWith(".FLAC") || name.endsWith(".wav") || name.endsWith(".ogg") || name.endsWith(".ape")
    }


    private fun parseMP3Info(bean: MP3Info): Boolean {

        try {
            val read = AudioFileIO.read(bean.musicFile)
            val audioHeader = read.audioHeader
            bean.time = audioHeader.trackLength
            val tag = read.tag
            if (tag != null) {
                bean.title = tag.getFirst(FieldKey.TITLE)
                bean.artist = tag.getFirst(FieldKey.ARTIST)
                bean.album = tag.getFirst(FieldKey.ALBUM)
                bean.genre = tag.getFirst(FieldKey.GENRE)
                return true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }


    fun executList() {

        for (mp3Info in DataManager.instans.list) {
            executList(mp3Info, false)
        }

        main.listView.setItems(null)
        main.listView.setItems(DataManager.instans.list)

    }


    fun executList(mp3Info: MP3Info, update: Boolean) {

        if (mp3Info.isChecked) {
            val fileName = mp3Info.fileName!!.replace(".mp3".toRegex(), "")
            val title = mp3Info.title
            val artist = mp3Info.artist

            var text: String? = main.splitText.text
            if (text == null || text.length == 0) {
                text = main.DEFULT_PRE
            }
            val split = fileName.split(text.toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()

            val fileTile: String
            val fileArtist: String

            if (split.size == 1) {
                fileTile = fileName
                fileArtist = ""
            } else {
                fileTile = split[1]
                fileArtist = split[0]
            }

            if (title == null || title == "") {
                mp3Info.title = fileTile
                mp3Info.artist = fileArtist
                mp3Info.album = ""
                mp3Info.genre = ""

                fixMP3Info(mp3Info)

            } else {
                if (!fileName.contains(title)) {//是乱码
                    mp3Info.title = fileTile
                    mp3Info.artist = fileArtist
                    mp3Info.album = ""
                    mp3Info.genre = ""

                    fixMP3Info(mp3Info)
                }
            }
        }

        if (update) {
            main.listView.setItems(null)
            main.listView.setItems(DataManager.instans.list)
        }
    }


    private fun fixMP3Info(bean: MP3Info) {

        try {
            val mp3File = AudioFileIO.read(bean.musicFile) as MP3File
            val tag = mp3File.tag
            if (tag != null) {

                tag.setField(FieldKey.TITLE, bean.title)
                tag.setField(FieldKey.ALBUM, bean.album)
                tag.setField(FieldKey.ARTIST, bean.artist)
                tag.setField(FieldKey.GENRE, bean.genre)

            }
            mp3File.commit()
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

}
