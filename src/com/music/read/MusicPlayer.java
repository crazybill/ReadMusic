package com.music.read;

import javafx.application.Platform;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by xupanpan on 01/11/2017.
 */
public class MusicPlayer {

    private static MusicPlayer mMusicPlayer;
    private PlayScheduleListener playScheduleListener;
    private boolean isFlac;

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

    public void play(PlayStateListener listener) {

        currentPlayInfo = DataManager.getInstans().getCurrentPlayInfo();
        if (currentPlayInfo == null || currentPlayInfo.mp3File == null || !currentPlayInfo.mp3File.exists() || currentPlayInfo.time <= 0) {
            return;
        }
        isFlac = currentPlayInfo.fileName.endsWith(".flac");
        this.listener = listener;
        playMP3(currentPlayInfo.mp3File);
    }


    private SourceDataLine line;
    private AudioInputStream in;
    private Thread thread;
    private AudioInputStream audioInputStream;
    private Runnable runnable = new Runnable() {
        public void run() {
            try {
                in = AudioSystem.getAudioInputStream(file);

                AudioFormat format = in.getFormat();
                int ch = format.getChannels();
                float rate = format.getSampleRate();
                AudioFormat outFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, rate, 16, ch, ch * 2, rate, false);

                DataLine.Info info = new DataLine.Info(SourceDataLine.class, outFormat);
                line = (SourceDataLine) AudioSystem.getLine(info);
                line.addLineListener(new LineListener() {
                    public void update(LineEvent event) {
                        if (listener != null) {
                            LineEvent.Type type = event.getType();
                            if (LineEvent.Type.OPEN == type) {
                                System.out.println("3.open listener");
                                listener.onOpen();
                            } else if (LineEvent.Type.CLOSE == type) {
                                System.out.println("8.close listener");
                                stopTime();
                                if (playScheduleListener != null) {
                                    playScheduleListener.onPlaying(0);
                                }
                                listener.onClose();
                            } else if (LineEvent.Type.START == type) {
                                System.out.println("4.start listener " + file.getName());
                                isPlaying = true;
                                startTime();
                                listener.onStart();
                            } else if (LineEvent.Type.STOP == type) {
                                System.out.println("7.stop listener");
                                isPlaying = false;
                                listener.onStop();
                            }
                        }
                    }
                });

                if (line != null) {
                    System.out.println("1.line open!");
                    line.open(outFormat);
                    System.out.println("2.line start!");
                    line.start();

                    byte[] buffer = new byte[65536];
                    int bytesRead = -1;
                    audioInputStream = AudioSystem.getAudioInputStream(outFormat, in);
                    while ((bytesRead = audioInputStream.read(buffer)) != -1) {
                        line.write(buffer, 0, bytesRead);
                        System.out.println(Thread.currentThread().getName());
                    }

                    line.drain();

                    closePlay();

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private File file;

    private void playMP3(File file) {
        if (isPlaying()) {
            closePlay();
        }

        this.file = file;

        thread = new Thread(runnable);
        thread.start();
    }


    public boolean isPlaying() {
        return line != null && line.isOpen() && line.isRunning();
    }

    public void closePlay() {
        if (line != null) {
            System.out.println("5.line stop!");
            line.stop();
            System.out.println("6.line close!");
            line.close();
        }
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (audioInputStream != null) {
            try {
                audioInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private Timer timer;
    private int timePosition = 0;
    private boolean isPlaying;

    private void startTime() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (isPlaying) {
                    timePosition++;
                    if (isFlac) {
                        if (timePosition >= (currentPlayInfo.time + 3)) {
                            System.out.println("flac file unclosed, timer close it!!!");
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


    private void stopTime() {
        timer.cancel();
        timer = null;
        timePosition = 0;
    }
}
