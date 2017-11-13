package com.music.read;

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


    public void play(PlayStateListener listener) {

        MP3Info currentPlayInfo = DataManager.getInstans().getCurrentPlayInfo();
        if (currentPlayInfo == null || currentPlayInfo.mp3File == null || !currentPlayInfo.mp3File.exists() || currentPlayInfo.time <= 0) {
            return;
        }

        playMP3(currentPlayInfo.mp3File, listener);
    }


    private SourceDataLine line;

    private void playMP3(final File file, final PlayStateListener listener) {
        if (isPlaying()) {
            closePlay();
        }

        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    AudioInputStream in = AudioSystem.getAudioInputStream(file);

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
                                    listener.onOpen();
                                } else if (LineEvent.Type.CLOSE == type) {
                                    stopTime();
                                    if (playScheduleListener != null) {
                                        playScheduleListener.onPlaying(0);
                                    }
                                    listener.onClose();
                                } else if (LineEvent.Type.START == type) {
                                    isPlaying = true;
                                    startTime();
                                    listener.onStart();
                                } else if (LineEvent.Type.STOP == type) {
                                    isPlaying = false;
                                    listener.onStop();
                                }
                            }
                        }
                    });

                    if (line != null) {
                        line.open(outFormat);
                        line.start();
                        stream(AudioSystem.getAudioInputStream(outFormat, in), line);
                        line.drain();
                        closePlay();
                        in.close();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }

    public void startPlay() {
        if (line != null) {
            line.start();
        }
    }

    public boolean isPlaying() {
        return line != null && line.isOpen() && line.isRunning();
    }

    public void closePlay() {
        if (line != null) {
            line.stop();
            line.close();
        }
    }

    public void stopPlay() {
        if (line != null) {
            line.stop();
        }
    }


    private void stream(AudioInputStream in, SourceDataLine line)
            throws IOException {
        byte[] buffer = new byte[65536];
        for (int n = 0; n != -1; n = in.read(buffer, 0, buffer.length)) {
            line.write(buffer, 0, n);
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
                    if (playScheduleListener != null) {
                        playScheduleListener.onPlaying(timePosition);
                    }
                }
            }
        }, 1000, 1000);
    }


    private void stopTime() {
        timer.cancel();
        timePosition = 0;
    }
}
