package com.music.read;

import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xupanpan on 06/11/2017.
 */
public class PlayListManager {


    public static List<MP3Info> getHistoryPlayList() {

        File file = new File(FileLoadUtils.getLocalPath(), "play_list.json");
        if (!file.exists()) {
            return null;
        }

        try {

            String s = FileLoadUtils.readTextFile(new FileInputStream(file));

            if (s != null && !s.equals("")) {

                ArrayList<MP3Info> list = GsonUtils.gson.fromJson(s, new TypeToken<ArrayList<MP3Info>>() {
                }.getType());
                return list;
            }

        } catch (Exception e) {

        }
        return null;
    }


    public static void savePlayList(List<MP3Info> list) {
        if (list != null) {
            try {
                File f = new File(FileLoadUtils.getLocalPath(), "play_list.json");
                FileOutputStream fos = new FileOutputStream(f);
                fos.write(GsonUtils.gson.toJson(list).getBytes());
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


}
