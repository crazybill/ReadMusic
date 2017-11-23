package com.music.read;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import net.sourceforge.pinyin4j.PinyinHelper;

import java.io.File;
import java.util.*;

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
        if (list.size() > 1) {
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

    public synchronized void sortByPath() {
        if (list.isEmpty()) {
            return;
        }

        LinkedHashMap<String, ArrayList<MP3Info>> map = new LinkedHashMap<String, ArrayList<MP3Info>>();

        for (MP3Info info : list) {
            String parent = new File(info.filePath).getParent();
            if (!map.containsKey(parent)) {
                map.put(parent, new ArrayList<MP3Info>());
            }
            map.get(parent).add(info);
        }


        ArrayList<MP3Info> mList = new ArrayList<MP3Info>();

        Set<Map.Entry<String, ArrayList<MP3Info>>> strings = map.entrySet();
        Iterator<Map.Entry<String, ArrayList<MP3Info>>> iterator = strings.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ArrayList<MP3Info>> next = iterator.next();
            ArrayList<MP3Info> value = next.getValue();
            mList.addAll(value);
        }

        list.clear();
        list.addAll(mList);

    }


    public synchronized void sortByTime() {
        if (list.isEmpty()) {
            return;
        }

        Collections.sort(list, new Comparator<MP3Info>() {
            public int compare(MP3Info o1, MP3Info o2) {
                if (o1.time > o2.time) {
                    return 1;
                }
                if (o1.time < o2.time) {
                    return -1;
                }
                return 0;
            }
        });
    }

    public synchronized void sortByMusicName() {
        if (list.isEmpty()) {
            return;
        }

        Collections.sort(list, new Comparator<MP3Info>() {
            public int compare(MP3Info o1, MP3Info o2) {
                char c1, c2;
                if (o1.title == null || o1.title.length() == 0) {
                    c1 = 'a';
                } else {
                    c1 = o1.title.charAt(0);
                }
                if (o2.title == null || o2.title.length() == 0) {
                    c2 = 'a';
                } else {
                    c2 = o2.title.charAt(0);
                }

                String[] strings = PinyinHelper.toHanyuPinyinStringArray(c1);
                String[] strings1 = PinyinHelper.toHanyuPinyinStringArray(c2);

                String s1;
                String s2;

                if (strings == null || strings.length == 0) {
                    s1 = String.valueOf(c1);
                } else {
                    s1 = concatPinyinStringArray(strings);
                }
                if (strings1 == null || strings1.length == 0) {
                    s2 = String.valueOf(c2);
                } else {
                    s2 = concatPinyinStringArray(strings1);
                }

                return s1.compareTo(s2);
            }
        });


    }

    public synchronized void sortByFileName() {
        if (list.isEmpty()) {
            return;
        }

        Collections.sort(list, new Comparator<MP3Info>() {
            public int compare(MP3Info o1, MP3Info o2) {

                char c1 = o1.fileName.charAt(0);
                char c2 = o2.fileName.charAt(0);

                String[] strings = PinyinHelper.toHanyuPinyinStringArray(c1);
                String[] strings1 = PinyinHelper.toHanyuPinyinStringArray(c2);


                String s1;
                String s2;

                if (strings == null || strings.length == 0) {
                    s1 = String.valueOf(c1);
                } else {
                    s1 = concatPinyinStringArray(strings);
                }
                if (strings1 == null || strings1.length == 0) {
                    s2 = String.valueOf(c2);
                } else {
                    s2 = concatPinyinStringArray(strings1);
                }

                return s1.compareTo(s2);
            }
        });


    }

    private String concatPinyinStringArray(String[] pinyinArray) {
        StringBuffer pinyinSbf = new StringBuffer();
        if ((pinyinArray != null) && (pinyinArray.length > 0)) {
            for (int i = 0; i < pinyinArray.length; i++) {
                pinyinSbf.append(pinyinArray[i]);
            }
        }
        return pinyinSbf.toString();
    }

    public synchronized void sortBySingerName() {
        if (list.isEmpty()) {
            return;
        }
        Collections.sort(list, new Comparator<MP3Info>() {
            public int compare(MP3Info o1, MP3Info o2) {

                char c1, c2;
                if (o1.artist == null || o1.artist.length() == 0) {
                    c1 = 'a';
                } else {
                    c1 = o1.artist.charAt(0);
                }
                if (o2.artist == null || o2.artist.length() == 0) {
                    c2 = 'a';
                } else {
                    c2 = o2.artist.charAt(0);
                }

                String[] strings = PinyinHelper.toHanyuPinyinStringArray(c1);
                String[] strings1 = PinyinHelper.toHanyuPinyinStringArray(c2);


                String s1;
                String s2;

                if (strings == null || strings.length == 0) {
                    s1 = String.valueOf(c1);
                } else {
                    s1 = concatPinyinStringArray(strings);
                }
                if (strings1 == null || strings1.length == 0) {
                    s2 = String.valueOf(c2);
                } else {
                    s2 = concatPinyinStringArray(strings1);
                }

                return s1.compareTo(s2);
            }
        });
    }

    public synchronized void sort(SortType type) {
        switch (type) {
            case MUSIC_NAME:
                sortByMusicName();
                break;
            case FILE_NAME:
                sortByFileName();
                break;
            case SINGER:
                sortBySingerName();
                break;
            case TIME:
                sortByTime();
                break;
            case PATH:
                sortByPath();
                break;
        }


    }


}
