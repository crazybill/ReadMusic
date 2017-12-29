package com.music.read;

import it.sauronsoftware.jave.FFMPEGLocator;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class MyFFMPEGExecute extends FFMPEGLocator {
    protected String getFFMPEGExecutablePath() {

        Properties properties = System.getProperties();
        Set<Map.Entry<Object, Object>> entries1 = properties.entrySet();
        Iterator<Map.Entry<Object, Object>> iterator1 = entries1.iterator();

        while (iterator1.hasNext()) {
            Map.Entry<Object, Object> next = iterator1.next();
            System.out.println(next.getKey() + ":" + next.getValue());
        }
        String path;
        String property = properties.getProperty("os.name");
        if (property.contains("Mac")) {
            path = MyFFMPEGExecute.class.getResource("/res/mac/ffmpeg").getPath();
        } else if (property.contains("Windows")) {
            path = MyFFMPEGExecute.class.getResource("/res/windows/ffmpeg.exe").getPath();
        } else {
            path = MyFFMPEGExecute.class.getResource("/res/linux/ffmpeg").getPath();
        }
        return path;
    }
}
