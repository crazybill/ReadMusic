package com.music.read

import javafx.application.Platform

/**
 * Created by xupanpan on 03/11/2017.
 */
class PlayManager(private val homeView: HomeView) {

    private var isUserControl: Boolean = false

    var playType = PlayType.RECYCLE

    private val listener = PlayScheduleListener { position -> Platform.runLater { homeView.playTimeLabel.text = Utils.getMusicTime(position) } }


    private val mPlayStateListener = object : PlayStateListener {

        override fun onOpen() {
            updateUIShow()
        }

        override fun onStart() {
            isUserControl = false
            Platform.runLater {
                val currentPlayInfo = DataManager.instans.currentPlayInfo
                homeView.setButtonStop()
                homeView.setCurrentPlayTitle(DataManager.instans.currentPlayPosition + 1 + " / " + DataManager.instans.listSize + " # " + currentPlayInfo!!.fileName + "   " + currentPlayInfo!!.filePath)
            }
        }

        override fun onStop() {

            Platform.runLater { homeView.setButtonPlay() }
        }

        override fun onClose() {
            if (!isUserControl) {
                Platform.runLater { playNextAuto() }
            }
        }
    }

    enum class PlayType {
        SINGLE, RECYCLE, RADOM
    }


    init {
        MusicPlayer.instans.setPlayScheduleListener(listener)
    }

    fun play(position: Int) {
        play(DataManager.instans.getMP3InfoByPosition(position))
    }


    fun play(info: MP3Info?) {
        if (info == null || info.musicFile == null) {
            return
        }

        DataManager.instans.setNewPlayPosition(info)
        isUserControl = true
        playMusic()
    }

    fun playNextAuto() {

        when (playType) {
            PlayManager.PlayType.RADOM -> DataManager.instans.setPlayNextRadomPosition()
            PlayManager.PlayType.RECYCLE -> DataManager.instans.setPlayNextPosition()
            PlayManager.PlayType.SINGLE -> {
            }
        }

        playMusic()
        homeView.scrollToShow()
    }

    fun playNext() {
        isUserControl = true
        playNextAuto()
    }


    fun playLast() {
        isUserControl = true
        DataManager.instans.setPlayLastPosition()
        playMusic()
        homeView.scrollToShow()
    }

    fun stopAndStart() {

        if (MusicPlayer.instans.isPlaying) {
            isUserControl = true
            MusicPlayer.instans.closePlay()

        } else {
            playMusic()
            homeView.setButtonPlay()
            homeView.scrollToShow()
        }
    }


    private fun playMusic() {

        MusicPlayer.instans.play(mPlayStateListener)

    }


    fun closePlay() {
        isUserControl = true
        MusicPlayer.instans.closePlay()

    }


    private fun updateUIShow() {
        Platform.runLater {
            homeView.listView.setItems(null)
            homeView.listView.setItems(DataManager.instans.list)
            val currentPlayPosition = DataManager.instans.currentPlayPosition
            if (currentPlayPosition != -1) {
                homeView.listView.selectionModel.select(currentPlayPosition)
            }
        }

    }
}
