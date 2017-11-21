package com.music.read;

import java.io.File;

/**
 * Created by xupanpan on 12/10/2017.
 */
public class MP3Info {

    public String fileName;

    public String title = "";
    public String album = "";
    public String artist = "";
    public String genre = "";
    public int time;
    public String timeShow;
    public String size;

    public String filePath;
    public String bitRate;
    public boolean isChecked;
    public boolean isPlaying;

    public File getMusicFile() {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }
        return file;
    }
}
