package com.music.read;

import javafx.application.Platform;

/**
 * Created by xupanpan on 03/11/2017.
 */
public class PlayManager {
    private HomeView homeView;
    public int currentPosition = -1;

    public MP3Info mp3Info;
    private boolean isUserControl;


    private PlayScheduleListener listener = new PlayScheduleListener() {
        public void onPlaying(final int position) {
            Platform.runLater(new Runnable() {
                public void run() {
                    homeView.playTimeLabel.setText(Utils.getMusicTime(position));
                }
            });
        }
    };


    public PlayManager(HomeView view) {
        this.homeView = view;
        MusicPlayer.getInstans().setPlayScheduleListener(listener);
    }

    public void play(int position) {
        if (position < 0 || position >= homeView.list.size()) {
            return;
        }
        play(homeView.list.get(position));
    }


    public void play(MP3Info info) {
        if (info == null || !info.mp3File.exists()) {
            return;
        }
        updateCurrentPosition();
        mp3Info = info;
        currentPosition = homeView.list.indexOf(info);
        isUserControl = true;
        playMusic();
    }

    public void playNextAuto() {

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
            homeView.listView.scrollTo(currentPosition);
        }
    }

    public void updateCurrentPosition() {

        if (mp3Info == null) {
            return;
        }
        currentPosition = homeView.list.indexOf(mp3Info);

    }

    public void stopAndStart() {

        if (MusicPlayer.getInstans().isPlaying()) {
            isUserControl = true;
            MusicPlayer.getInstans().closePlay();

        } else {
            play(currentPosition);
            homeView.setButtonPlay();
        }
    }

    private void playMusic() {

        if (mp3Info == null) {
            return;
        }

        MusicPlayer.getInstans().play(mp3Info.mp3File, new PlayStateListener() {

            public void onOpen() {
                updateUIShow();
            }

            public void onStart() {
                isUserControl = false;
                Platform.runLater(new Runnable() {
                    public void run() {
                        homeView.setButtonStop();
                        homeView.setCurrentPlayTitle((currentPosition + 1) + " # " + mp3Info.fileName + "   " + mp3Info.mp3File.getPath());
                    }
                });
            }

            public void onStop() {

                Platform.runLater(new Runnable() {
                    public void run() {
                        homeView.setButtonPlay();
                    }
                });
            }

            public void onClose() {
                if (!isUserControl) {
                    playNextAuto();
                }
            }
        });

    }


    public void closePlay(){
        isUserControl = true;
        MusicPlayer.getInstans().closePlay();

    }


    private void updateUIShow() {
        Platform.runLater(new Runnable() {
            public void run() {
                for (MP3Info info : homeView.list) {
                    info.isPlaying = false;
                }
                mp3Info.isPlaying = true;
                homeView.listView.setItems(null);
                homeView.listView.setItems(homeView.list);
            }
        });

    }
}