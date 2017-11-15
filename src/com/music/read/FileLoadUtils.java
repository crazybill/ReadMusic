package com.music.read;

import java.io.*;

/**
 * Created by xupanpan on 3/24/17.
 */
public class FileLoadUtils {

    public static File getLocalPath() {

        File file = new File(System.getProperty("user.home") + "/.MusicOcean/config/");
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }


    public static String readTextFile(File file) {

        try {
            return readTextFile(new FileInputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static String readTextFile(InputStream inputStream) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputStream.toString();
    }


    public static void saveTextFile(File file, String str) {

        if (file != null && str != null) {
            try {
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(str.getBytes("UTF-8"));
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
