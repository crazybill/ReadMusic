package com.music.read;

import sun.audio.AudioPlayer;

import javax.sound.sampled.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by xupanpan on 01/11/2017.
 */
public class MusicPlayer {

    private Executor executor = Executors.newSingleThreadExecutor();
    private static MusicPlayer mMusicPlayer;

    private MusicPlayer() {

    }

    public static MusicPlayer getInstans() {
        if (mMusicPlayer == null) {
            mMusicPlayer = new MusicPlayer();
        }
        return mMusicPlayer;
    }


    private SourceDataLine line;

    public void play(final File file) {

        executor.execute(new Runnable() {
            public void run() {
                try {
                    AudioInputStream in = AudioSystem.getAudioInputStream(file);
                    AudioFormat outFormat = getOutFormat(in.getFormat());
                    DataLine.Info info = new DataLine.Info(SourceDataLine.class, outFormat);
                    line = (SourceDataLine) AudioSystem.getLine(info);

                    if (line != null) {
                        line.open(outFormat);
                        line.start();
                        stream(AudioSystem.getAudioInputStream(outFormat, in), line);
                        line.drain();
                        line.stop();
                    }

                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
        });
    }

    public void stopPlay(){
        if(line!= null){
            executor.execute(new Runnable() {
                public void run() {
                    line.isOpen();







                }
            });
        }




    }




    private AudioFormat getOutFormat(AudioFormat inFormat) {
        final int ch = inFormat.getChannels();
        final float rate = inFormat.getSampleRate();
        return new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, rate, 16, ch, ch * 2, rate, false);
    }

    private void stream(AudioInputStream in, SourceDataLine line)
            throws IOException {
        final byte[] buffer = new byte[65536];
        for (int n = 0; n != -1; n = in.read(buffer, 0, buffer.length)) {
            line.write(buffer, 0, n);
        }
    }


}
