package com.music.read;

import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xupanpan on 06/11/2017.
 */
public class PlayListManager {

    public static final String PLAY_LIST_FILE = "play_list.json";
    public static final String CONFIG_FILE = "config.json";



    public static List<MP3Info> getHistoryPlayList() {

        return getLocalArray(PLAY_LIST_FILE, new TypeToken<ArrayList<MP3Info>>() {
        }.getType());
    }


    public static void savePlayList(List<MP3Info> list) {
        save2Local(list, PLAY_LIST_FILE);
    }


    public static void savePlayConfig(Config config) {
        save2Local(config, CONFIG_FILE);
    }


    public static Config getPlayConfig() {
        return getLocalObject(CONFIG_FILE, Config.class);
    }




    public static void save2Local(Object obj, String fileName) {
        if (obj != null) {
            try {
                File f = new File(FileLoadUtils.getLocalPath(), fileName);
                FileOutputStream fos = new FileOutputStream(f);
                fos.write(GsonUtils.gson.toJson(obj).getBytes("UTF-8"));
                fos.flush();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static <T> T getLocalObject(String fileName, Class<T> tClass) {

        File file = new File(FileLoadUtils.getLocalPath(), fileName);
        if (!file.exists()) {
            return null;
        }
        try {
            String s = FileLoadUtils.readTextFile(new FileInputStream(file));
            if (s != null && !s.equals("")) {
                T t = GsonUtils.gson.fromJson(s, tClass);
                return t;
            }

        } catch (Exception e) {

        }
        return null;
    }


    public static <T> T getLocalArray(String fileName, Type type) {

        File file = new File(FileLoadUtils.getLocalPath(), fileName);
        if (!file.exists()) {
            return null;
        }
        try {
            String s = FileLoadUtils.readTextFile(new FileInputStream(file));
            if (s != null && !s.equals("")) {
                T t = GsonUtils.gson.fromJson(s, type);
                return t;
            }

        } catch (Exception e) {

        }
        return null;
    }


}
