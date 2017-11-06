package com.music.read;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import java.io.File;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by xupanpan on 03/11/2017.
 */
public class MusicParser {
    private Executor executor = Executors.newSingleThreadExecutor();
    private HomeView main;

    public MusicParser(HomeView main) {
        this.main = main;
    }

    private Alert alert;

    public void loadMp3Data(final List<File> files) {

        alert = new Alert(Alert.AlertType.NONE);
        alert.setHeaderText("加载中，请稍等...");
        alert.initOwner(main.primaryStage);
        executor.execute(new Runnable() {
            public void run() {
                for (int i = 0; i < files.size(); i++) {
                    File f = files.get(i);
                    String name = f.getName();
                    if (name.endsWith(".mp3") || name.endsWith(".MP3")) {
                        MP3Info bean = new MP3Info();
                        bean.isChecked = main.isSelectAll;
                        bean.fileName = name;
                        bean.mp3File = f;
                        parseMP3Info(bean);
                        updateLoad(bean, "已加载：" + (i + 1) + "/" + files.size());
                    }
                }

                Platform.runLater(new Runnable() {
                    public void run() {
                        alert.getDialogPane().getButtonTypes().add(ButtonType.OK);
                        alert.close();
                    }
                });
            }
        });
        alert.showAndWait();
    }

    public void updateLoad(final MP3Info bean, final String msg) {
        Platform.runLater(new Runnable() {
            public void run() {
                main.list.add(bean);
                alert.setContentText(msg);
            }
        });

    }


    private void parseMP3Info(MP3Info bean) {

        try {
            MP3File mp3File = (MP3File) AudioFileIO.read(bean.mp3File);
            AudioHeader audioHeader = mp3File.getAudioHeader();
            bean.time = audioHeader.getTrackLength();
            Tag tag = mp3File.getTag();
            if (tag != null) {

                bean.title = tag.getFirst(FieldKey.TITLE);
                bean.artist = tag.getFirst(FieldKey.ARTIST);
                bean.album = tag.getFirst(FieldKey.ALBUM);
                bean.genre = tag.getFirst(FieldKey.GENRE);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void executList() {

        for (MP3Info mp3Info : main.list) {
            executList(mp3Info, false);
        }

        main.listView.setItems(null);
        main.listView.setItems(main.list);

    }


    public void executList(MP3Info mp3Info, boolean update) {

        if (mp3Info.isChecked) {
            String fileName = mp3Info.fileName.replaceAll(".mp3", "");
            String title = mp3Info.title;
            String artist = mp3Info.artist;

            String text = main.splitText.getText();
            if (text == null || text.length() == 0) {
                text = main.DEFULT_PRE;
            }
            String[] split = fileName.split(text);

            String fileTile;
            String fileArtist;

            if (split.length == 1) {
                fileTile = fileName;
                fileArtist = "";
            } else {
                fileTile = split[1];
                fileArtist = split[0];
            }

            if (title == null || title.equals("")) {
                mp3Info.title = fileTile;
                mp3Info.artist = fileArtist;
                mp3Info.album = "";
                mp3Info.genre = "";

                fixMP3Info(mp3Info);

            } else {
                if (!fileName.contains(title)) {//是乱码
                    mp3Info.title = fileTile;
                    mp3Info.artist = fileArtist;
                    mp3Info.album = "";
                    mp3Info.genre = "";

                    fixMP3Info(mp3Info);
                }
            }
        }

        if (update) {
            main.listView.setItems(null);
            main.listView.setItems(main.list);
        }
    }


    private void fixMP3Info(MP3Info bean) {

        try {
            MP3File mp3File = (MP3File) AudioFileIO.read(bean.mp3File);
            Tag tag = mp3File.getTag();
            if (tag != null) {

                tag.setField(FieldKey.TITLE, bean.title);
                tag.setField(FieldKey.ALBUM, bean.album);
                tag.setField(FieldKey.ARTIST, bean.artist);
                tag.setField(FieldKey.GENRE, bean.genre);

            }
            mp3File.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
