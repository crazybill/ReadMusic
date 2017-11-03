package com.music.read;

import javafx.application.Platform;

/**
 * Created by xupanpan on 03/11/2017.
 */
public class PlayManager {
    private HomeView homeView;
    private int currentPosition;

    private MP3Info mp3Info;
    private boolean isUserPlay;


    public PlayManager(HomeView view) {
        this.homeView = view;
    }


    public void play(MP3Info info) {
        if (info == null || !info.mp3File.exists()) {
            return;
        }
        mp3Info = info;
        currentPosition = homeView.list.indexOf(info);
        isUserPlay = true;
        playMusic();
    }

    private void playNextAuto() {

        System.out.println("自动波下一收");

        int size = homeView.list.size();

        if (size > 0) {
            if (size != 1) {
                if (currentPosition + 1 < size) {
                    currentPosition = currentPosition + 1;
                } else {
                    currentPosition = 0;
                }
                mp3Info = homeView.list.get(currentPosition);
            }
            playMusic();
        }
    }


    private void playMusic() {

        MusicPlayer.getInstans().play(mp3Info.mp3File, new PlayStateListener() {

            public void onOpen() {
                updateUIShow();
                System.out.println("打开了");
            }

            public void onStart() {
                isUserPlay = false;
                System.out.println("开始播了");
            }

            public void onStop() {
                System.out.println("播停止了");
            }

            public void onClose() {
                System.out.println("播完了");
                if (!isUserPlay) {
                    playNextAuto();
                }
            }
        });
    }


    private void updateUIShow(){
        Platform.runLater(new Runnable() {
            public void run() {
                for (MP3Info info:homeView.list){
                    info.isPlaying =false;
                }
                mp3Info.isPlaying = true;
                homeView.listView.setItems(null);
                homeView.listView.setItems(homeView.list);
            }
        });

    }
}
