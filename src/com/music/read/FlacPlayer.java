package com.music.read;

import javax.sound.sampled.*;
import java.io.IOException;

public class FlacPlayer {
    private boolean isPlaying;
    private PlayScheduleListener playScheduleListener;

    public void setPlayScheduleListener(PlayScheduleListener playScheduleListener) {
        this.playScheduleListener = playScheduleListener;
    }


    public void play(final MP3Info mp3Info, final PlayStateListener listener) {

        Thread thread = new Thread() {
            @Override
            public void run() {

                try {
                    AudioInputStream in = AudioSystem.getAudioInputStream(mp3Info.mp3File);

                    AudioFormat format = in.getFormat();
                    int ch = format.getChannels();
                    float rate = format.getSampleRate();
                    AudioFormat outFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, rate, 16, ch, ch * 2, rate, false);

                    DataLine.Info info = new DataLine.Info(SourceDataLine.class, outFormat);
                    SourceDataLine  line = (SourceDataLine) AudioSystem.getLine(info);
                    line.addLineListener(new LineListener() {
                        public void update(LineEvent event) {
                            if (listener != null) {
                                LineEvent.Type type = event.getType();
                                if (LineEvent.Type.OPEN == type) {
                                    listener.onOpen();
                                } else if (LineEvent.Type.CLOSE == type) {
                                    if (playScheduleListener != null) {
                                        playScheduleListener.onPlaying(0);
                                    }
                                    System.out.println("bo wan le 2");
                                    listener.onClose();
                                } else if (LineEvent.Type.START == type) {
                                    isPlaying = true;
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
                        line.stop();
                        line.close();
                        in.close();
                        System.out.println("bo wan le 1");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        };

        thread.start();


    }

    private void stream(AudioInputStream in, SourceDataLine line)
            throws IOException {
        byte[] buffer = new byte[65536];
        for (int n = 0; n != -1; n = in.read(buffer, 0, buffer.length)) {
            line.write(buffer, 0, n);
        }
    }


}
