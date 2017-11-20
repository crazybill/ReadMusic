package com.music.read;

import javafx.application.Platform;

/**
 * Created by xupanpan on 03/11/2017.
 */
public class PlayManager {
    private HomeView homeView;

    private boolean isUserControl;

    public enum PlayType {
        SINGLE, RECYCLE, RADOM
    }

    private PlayType playType = PlayType.RECYCLE;

    public PlayType getPlayType() {
        return playType;
    }

    public void setPlayType(PlayType playType) {
        this.playType = playType;
    }

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
        play(DataManager.getInstans().getMP3InfoByPosition(position));
    }


    public void play(MP3Info info) {
        if (info == null || info.getMusicFile() == null) {
            return;
        }

        DataManager.getInstans().setNewPlayPosition(info);
        isUserControl = true;
        playMusic();
    }

    public void playNextAuto() {

        switch (playType) {
            case RADOM:
                DataManager.getInstans().setPlayNextRadomPosition();
                break;
            case RECYCLE:
                DataManager.getInstans().setPlayNextPosition();
                break;
            case SINGLE:

                break;
        }

        playMusic();
        homeView.scrollToShow();
    }

    public void playNext() {
        isUserControl = true;
        playNextAuto();
    }


    public void playLast() {
        isUserControl = true;
        DataManager.getInstans().setPlayLastPosition();
        playMusic();
        homeView.scrollToShow();
    }

    public void stopAndStart() {

        if (MusicPlayer.getInstans().isPlaying()) {
            isUserControl = true;
            MusicPlayer.getInstans().closePlay();

        } else {
            playMusic();
            homeView.setButtonPlay();
            homeView.scrollToShow();
        }
    }


    private PlayStateListener mPlayStateListener = new PlayStateListener() {

        public void onOpen() {
            updateUIShow();
        }

        public void onStart() {
            isUserControl = false;
            Platform.runLater(new Runnable() {
                public void run() {
                    MP3Info currentPlayInfo = DataManager.getInstans().getCurrentPlayInfo();
                    homeView.setButtonStop();
                    homeView.setCurrentPlayTitle((DataManager.getInstans().getCurrentPlayPosition() + 1) + " / " + DataManager.getInstans().getListSize() + " # " + currentPlayInfo.fileName + "   " + currentPlayInfo.filePath);
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
                Platform.runLater(new Runnable() {
                    public void run() {
                        playNextAuto();
                    }
                });
            }
        }
    };


    private void playMusic() {

        MusicPlayer.getInstans().play(mPlayStateListener);

    }


    public void closePlay() {
        isUserControl = true;
        MusicPlayer.getInstans().closePlay();

    }


    private void updateUIShow() {
        Platform.runLater(new Runnable() {
            public void run() {
                homeView.listView.setItems(null);
                homeView.listView.setItems(DataManager.getInstans().getList());
                int currentPlayPosition = DataManager.getInstans().getCurrentPlayPosition();
                if (currentPlayPosition != -1) {
                    homeView.listView.getSelectionModel().select(currentPlayPosition);
                }

            }
        });

    }
}
