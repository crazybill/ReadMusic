package com.music.read;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by xupanpan on 01/11/2017.
 */
public class MusicPlayer {

    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private static MusicPlayer mMusicPlayer;
    private PlayScheduleListener playScheduleListener;
    private boolean isFlac;
    private File file;

    private MusicPlayer() {

    }

    public void setPlayScheduleListener(PlayScheduleListener playScheduleListener) {
        this.playScheduleListener = playScheduleListener;
    }

    public static MusicPlayer getInstans() {
        if (mMusicPlayer == null) {
            mMusicPlayer = new MusicPlayer();
        }
        return mMusicPlayer;
    }

    private MP3Info currentPlayInfo;
    private PlayStateListener listener;

    public synchronized void play(PlayStateListener listener) {

        MP3Info currentPlayInfo = DataManager.getInstans().getCurrentPlayInfo();
        if (currentPlayInfo == null) {
            return;
        }

        File musicFile = currentPlayInfo.getMusicFile();

        if (musicFile == null || currentPlayInfo.time <= 0) {
            return;
        }

        this.currentPlayInfo = currentPlayInfo;
        isFlac = currentPlayInfo.fileName.endsWith(".flac");
        this.listener = listener;

        if (isPlaying()) {
            isPlaying = false;
        }
        this.file = musicFile;

        playMP3();
    }

    private void playMP3() {
        executorService.submit(runnable);
    }

    public synchronized boolean isPlaying() {
        return isPlaying;
    }

    private LineListener mLineListener = new LineListener() {
        public void update(LineEvent event) {
            if (listener != null) {
                LineEvent.Type type = event.getType();
                if (LineEvent.Type.OPEN == type) {
                    LogCat.i("3.open listener");
                    listener.onOpen();
                } else if (LineEvent.Type.CLOSE == type) {
                    LogCat.i("8.close listener");
                    listener.onClose();
                } else if (LineEvent.Type.START == type) {
                    LogCat.i("4.start listener " + file.getName());
                    listener.onStart();
                } else if (LineEvent.Type.STOP == type) {
                    LogCat.i("7.stop listener");
                    listener.onStop();
                }
            }
        }
    };


    private final Runnable runnable = new Runnable() {
        public void run() {
            try {
                AudioInputStream in = AudioSystem.getAudioInputStream(file);

                AudioFormat format = in.getFormat();
                int ch = format.getChannels();
                float rate = format.getSampleRate();
                AudioFormat outFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, rate, 16, ch, ch * 2, rate, false);

                DataLine.Info info = new DataLine.Info(SourceDataLine.class, outFormat);
                SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
                line.addLineListener(mLineListener);

                if (line != null) {
                    isPlaying = true;
                    LogCat.i("1.line open!");
                    line.open(outFormat);
                    LogCat.i("2.line start!");
                    line.start();
                    startTimer();
                    byte[] buffer = new byte[2048];
                    int bytesRead = -1;
                    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(outFormat, in);
                    while (isPlaying && (bytesRead = audioInputStream.read(buffer)) != -1) {
                        line.write(buffer, 0, bytesRead);
                        //LogCat.i("playing.......");
                    }
                    LogCat.i("playing.......ending.........");
                    line.drain();
                    isPlaying = false;
                    stopTimer();
                    playEndClose(line, in, audioInputStream);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    public synchronized void closePlay() {
        if (isPlaying()) {
            isPlaying = false;
        }
    }


    private void playEndClose(SourceDataLine line, AudioInputStream in, AudioInputStream audioInputStream) {

        LogCat.i("5.line stop!");
        line.stop();
        LogCat.i("6.line close!");
        line.close();
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            audioInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.gc();
    }

    private Timer timer;
    private int timePosition = 0;
    private boolean isPlaying;

    private void startTimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (isPlaying) {
                    timePosition++;
                    if (isFlac) {
                        if (timePosition == (currentPlayInfo.time - 2)) {
                            LogCat.i("flac file unclosed, timer close it!!!");
                            closePlay();
                        }
                    }
                    if (playScheduleListener != null) {
                        playScheduleListener.onPlaying(timePosition);
                    }
                }
            }
        }, 1000, 1000);
    }


    private void stopTimer() {

        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        timePosition = 0;
    }


}
