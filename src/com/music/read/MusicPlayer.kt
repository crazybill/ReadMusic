package com.music.read

import javax.sound.sampled.*
import java.io.File
import java.io.IOException
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Created by xupanpan on 01/11/2017.
 */
class MusicPlayer private constructor() {

    private val executorService = Executors.newSingleThreadExecutor()
    private var playScheduleListener: PlayScheduleListener? = null
    private var isFlac: Boolean = false
    private var file: File? = null

    private var currentPlayInfo: MP3Info? = null
    private var listener: PlayStateListener? = null

    private val mLineListener = LineListener { event ->
        if (listener != null) {
            val type = event.type
            if (LineEvent.Type.OPEN === type) {
                LogCat.i("3.open listener")
                listener!!.onOpen()
            } else if (LineEvent.Type.CLOSE === type) {
                LogCat.i("8.close listener")
                listener!!.onClose()
            } else if (LineEvent.Type.START === type) {
                LogCat.i("4.start listener " + file!!.name)
                listener!!.onStart()
            } else if (LineEvent.Type.STOP === type) {
                LogCat.i("7.stop listener")
                listener!!.onStop()
            }
        }
    }


    private val runnable = Runnable {
        try {
            val `in` = AudioSystem.getAudioInputStream(file)

            val format = `in`.format
            val ch = format.channels
            val rate = format.sampleRate
            val outFormat = AudioFormat(AudioFormat.Encoding.PCM_SIGNED, rate, 16, ch, ch * 2, rate, false)

            val info = DataLine.Info(SourceDataLine::class.java, outFormat)
            val line = AudioSystem.getLine(info) as SourceDataLine
            line.addLineListener(mLineListener)

            if (line != null) {
                isPlaying = true
                LogCat.i("1.line open!")
                line.open(outFormat)
                LogCat.i("2.line start!")
                line.start()
                startTimer()
                val buffer = ByteArray(2048)
                var bytesRead = -1
                val audioInputStream = AudioSystem.getAudioInputStream(outFormat, `in`)
                while (isPlaying && (bytesRead = audioInputStream.read(buffer)) != -1) {
                    line.write(buffer, 0, bytesRead)
                    //LogCat.i("playing.......");
                }
                LogCat.i("playing.......ending.........")
                line.drain()
                isPlaying = false
                stopTimer()
                playEndClose(line, `in`, audioInputStream)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private var timer: Timer? = null
    private var timePosition = 0
    @get:Synchronized
    var isPlaying: Boolean = false
        private set

    fun setPlayScheduleListener(playScheduleListener: PlayScheduleListener) {
        this.playScheduleListener = playScheduleListener
    }

    @Synchronized
    fun play(listener: PlayStateListener) {

        val currentPlayInfo = DataManager.instans.currentPlayInfo ?: return

        val musicFile = currentPlayInfo.musicFile

        if (musicFile == null || currentPlayInfo.time <= 0) {
            return
        }

        this.currentPlayInfo = currentPlayInfo
        isFlac = currentPlayInfo.fileName!!.endsWith(".flac")
        this.listener = listener

        if (isPlaying) {
            isPlaying = false
        }
        this.file = musicFile

        playMP3()
    }

    private fun playMP3() {
        executorService.submit(runnable)
    }


    @Synchronized
    fun closePlay() {
        if (isPlaying) {
            isPlaying = false
        }
    }


    private fun playEndClose(line: SourceDataLine, `in`: AudioInputStream, audioInputStream: AudioInputStream) {

        LogCat.i("5.line stop!")
        line.stop()
        LogCat.i("6.line close!")
        line.close()
        try {
            `in`.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        try {
            audioInputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        System.gc()
    }

    private fun startTimer() {
        timer = Timer()
        timer!!.schedule(object : TimerTask() {
            override fun run() {
                if (isPlaying) {
                    timePosition++
                    if (isFlac) {
                        if (timePosition == currentPlayInfo!!.time - 2) {
                            LogCat.i("flac file unclosed, timer close it!!!")
                            closePlay()
                        }
                    }
                    if (playScheduleListener != null) {
                        playScheduleListener!!.onPlaying(timePosition)
                    }
                }
            }
        }, 1000, 1000)
    }


    private fun stopTimer() {

        if (timer != null) {
            timer!!.cancel()
            timer = null
        }
        timePosition = 0
    }

    companion object {
        private var mMusicPlayer: MusicPlayer? = null

        val instans: MusicPlayer
            get() {
                if (mMusicPlayer == null) {
                    mMusicPlayer = MusicPlayer()
                }
                return mMusicPlayer
            }
    }


}
