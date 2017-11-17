package com.music.read;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DataManager {

    private static DataManager mDataManager = new DataManager();
    private ObservableList<MP3Info> list;

    private DataManager() {
        list = FXCollections.observableArrayList();
    }

    public static DataManager getInstans() {
        return mDataManager;
    }

    public synchronized ObservableList<MP3Info> getList() {
        return list;
    }


    public synchronized MP3Info getMP3InfoByPosition(int position) {
        if (position < 0 || position >= list.size()) {
            return null;
        }
        return list.get(position);
    }


    public synchronized void add2List(List<MP3Info> mp3InfoList) {
        list.addAll(mp3InfoList);
    }


    public synchronized void add2List(MP3Info mp3Info) {
        list.add(mp3Info);
    }

    public synchronized void clearList() {
        list.clear();
        PlayListManager.savePlayList(list);
    }

    public synchronized void remove(MP3Info mp3Info) {
        if (mp3Info != null) {
            list.remove(mp3Info);
            PlayListManager.savePlayList(list);
        }

    }

    public synchronized void setAllCheckStatus(boolean isCheck) {
        for (MP3Info mp3Info : list) {
            mp3Info.isChecked = isCheck;
        }
    }

    public synchronized boolean removeSelected() {

        boolean isPlayCurrent = false;
        List<MP3Info> rList = new ArrayList<MP3Info>();
        for (int i = 0; i < list.size(); i++) {
            MP3Info mp3Info = list.get(i);
            if (mp3Info.isChecked) {
                rList.add(mp3Info);
                if (mp3Info.isPlaying) {
                    isPlayCurrent = true;
                }
            }
        }
        if (rList.size() > 0) {
            for (MP3Info info : rList) {
                list.remove(info);
            }
        }

        PlayListManager.savePlayList(list);

        return isPlayCurrent;
    }

    public synchronized void removeMP3Info(MP3Info info) {
        list.remove(info);
        PlayListManager.savePlayList(list);
    }

    public synchronized int getCurrentPlayPosition() {

        for (int i = 0; i < list.size(); i++) {
            MP3Info mp3Info = list.get(i);
            if (mp3Info.isPlaying) {
                return i;
            }
        }
        return -1;
    }

    public synchronized MP3Info getCurrentPlayInfo() {
        for (int i = 0; i < list.size(); i++) {
            MP3Info mp3Info = list.get(i);
            if (mp3Info.isPlaying) {
                return mp3Info;
            }
        }
        return null;
    }


    public synchronized void setPlayNextRadomPosition() {
        if(list.size()>1){
            clearCurrentPlayPosition();
            Random random = new Random();
            int i = random.nextInt(list.size() - 1);

            if (i != -1) {
                list.get(i).isPlaying = true;
            }
        }

    }


    public synchronized void setPlayNextPosition() {

        int nextPosition = -1;
        for (int i = 0; i < list.size(); i++) {
            MP3Info mp3Info = list.get(i);
            if (mp3Info.isPlaying) {
                if (i == list.size() - 1) {
                    nextPosition = 0;
                } else {
                    nextPosition = i + 1;
                }
                mp3Info.isPlaying = false;
                break;
            }
        }
        if (nextPosition != -1) {
            list.get(nextPosition).isPlaying = true;
        }
    }

    public synchronized void setPlayLastPosition() {

        int lastPosition = -1;
        for (int i = 0; i < list.size(); i++) {
            MP3Info mp3Info = list.get(i);
            if (mp3Info.isPlaying) {
                if (i == 0) {
                    lastPosition = list.size() - 1;
                } else {
                    lastPosition = i - 1;
                }
                mp3Info.isPlaying = false;
                break;
            }
        }
        if (lastPosition != -1) {
            list.get(lastPosition).isPlaying = true;
        }
    }

    public synchronized void clearCurrentPlayPosition() {
        for (MP3Info info : list) {
            info.isPlaying = false;
        }
    }


    public synchronized int getListSize() {
        return list.size();
    }


    public synchronized boolean isListEmpty() {
        return list.isEmpty();
    }


    public synchronized void setNewPlayPosition(MP3Info info) {
        clearCurrentPlayPosition();
        info.isPlaying = true;
    }

}
