package com.music.read;

import java.io.File;

/**
 * Created by xupanpan on 06/11/2017.
 */
public class FileNameEditer {
    private HomeView homeView;

    public FileNameEditer(HomeView view) {
        this.homeView = view;
    }

    public void addStrList(String textWei, String textStr) {

        if (textWei == null || textWei.length() == 0 || textStr == null || textStr.length() == 0) {
            return;
        }

        int wei = 1;
        try {
            wei = Integer.parseInt(textWei);
            if (wei < 1) {
                wei = 1;
            }
        } catch (Exception e) {
            wei = 1;
        }

        for (MP3Info mp3Info : DataManager.getInstans().getList()) {
            if (mp3Info.isChecked) {
                String fileName = mp3Info.fileName;
                if (wei == 1) {
                    fileName = textStr + fileName;
                } else if (wei > fileName.length()) {
                    fileName = fileName + textStr;
                } else {
                    String substring = fileName.substring(0, wei - 1);
                    String substring2 = fileName.substring(wei - 1, fileName.length());
                    fileName = substring + textStr + substring2;
                }

                mp3Info.fileName = fileName;

                File newNameFile = new File(mp3Info.mp3File.getParent(), fileName);
                if (!newNameFile.exists()) {
                    mp3Info.mp3File.renameTo(newNameFile);
                    mp3Info.mp3File = newNameFile;
                }


            }
        }
        homeView.listView.setItems(null);
        homeView.listView.setItems(DataManager.getInstans().getList());

    }

    public void renameList(String textBefor, String textAfter) {

        if (textBefor == null || textBefor.length() == 0) {
            return;
        }
        if (textAfter == null) {
            textAfter = "";
        }

        for (MP3Info mp3Info : DataManager.getInstans().getList()) {
            if (mp3Info.isChecked) {
                String fileName = mp3Info.fileName;
                if (fileName.contains(textBefor)) {

                    String replace = fileName.replace(textBefor, textAfter);
                    mp3Info.fileName = replace;

                    File newNameFile = new File(mp3Info.mp3File.getParent(), replace);
                    if (!newNameFile.exists()) {
                        mp3Info.mp3File.renameTo(newNameFile);
                        mp3Info.mp3File = newNameFile;

                    }
                }
            }
        }

        homeView.listView.setItems(null);
        homeView.listView.setItems(DataManager.getInstans().getList());
    }


}
