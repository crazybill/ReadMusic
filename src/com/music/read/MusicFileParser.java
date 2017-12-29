package com.music.read;

import it.sauronsoftware.jave.*;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.id3.AbstractID3v2Frame;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.id3.framebody.FrameBodyAPIC;

import java.io.File;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by xupanpan on 03/11/2017.
 */
public class MusicFileParser {
    private Executor executor = Executors.newSingleThreadExecutor();
    private HomeView main;

    public MusicFileParser(HomeView main) {
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
                    loadFile(f);
                }
                Platform.runLater(new Runnable() {
                    public void run() {
                        DataManager.getInstans().sort(main.sortType);
                        PlayListManager.savePlayList(DataManager.getInstans().getList());
                        alert.getDialogPane().getButtonTypes().add(ButtonType.OK);
                        alert.close();
                    }
                });
            }
        });
        alert.showAndWait();
    }


    private void loadFile(File f) {

        if (f.isFile()) {
            String name = f.getName();
            if (isMusicFile(name)) {
                MP3Info bean = new MP3Info();
                bean.isChecked = main.isSelectAll;
                bean.fileName = name;
                bean.filePath = f.getPath();
                bean.size = Utils.getFileSize(f.length());
                if (parseMP3Info(bean)) {
                    updateLoad(bean);
                }
            } else if (isAmrFile(name)) {
                File targetFile = new File(f.getPath().replace(".amr", ".mp3"));
                if (convertAmr2MP3(f, targetFile)) {
                    MP3Info bean = new MP3Info();
                    bean.isChecked = main.isSelectAll;
                    bean.fileName = targetFile.getName();
                    bean.filePath = targetFile.getPath();
                    bean.size = Utils.getFileSize(targetFile.length());
                    if (parseMP3Info(bean)) {
                        updateLoad(bean);
                    }
                }
            } else {//不支持的格式，都转成MP3
                File targetFile = new File(f.getPath().concat(".mp3"));
                if (convertAmr2MP3(f, targetFile)) {
                    MP3Info bean = new MP3Info();
                    bean.isChecked = main.isSelectAll;
                    bean.fileName = targetFile.getName();
                    bean.filePath = targetFile.getPath();
                    bean.size = Utils.getFileSize(targetFile.length());
                    if (parseMP3Info(bean)) {
                        updateLoad(bean);
                    }
                } else {
                    System.out.println("无法转换"+f.getName()+"为mp3！！！！");
                }
            }
        } else {
            File[] files = f.listFiles();
            for (File ff : files) {
                loadFile(ff);
            }
        }
    }

    public void updateLoad(final MP3Info bean) {
        Platform.runLater(new Runnable() {
            public void run() {
                DataManager.getInstans().add2List(bean);
                alert.setContentText("正加载：" + bean.fileName);
            }
        });
    }

    private boolean isMusicFile(String name) {

        return name.endsWith(".mp3") || name.endsWith(".MP3") || name.endsWith(".flac") || name.endsWith(".FLAC") || name.endsWith(".wav") || name.endsWith(".ogg") || name.endsWith(".ape");
    }

    private boolean isAmrFile(String name) {
        return name.endsWith(".amr");
    }


    private boolean parseMP3Info(MP3Info bean) {

        try {
            AudioFile read = AudioFileIO.read(bean.getMusicFile());
            AudioHeader audioHeader = read.getAudioHeader();
            bean.time = audioHeader.getTrackLength();
            bean.timeShow = Utils.getMusicTime(bean.time);
            bean.bitRate = audioHeader.getBitRate() + "bit";

            Tag tag = read.getTag();
            if (tag != null) {
                bean.title = tag.getFirst(FieldKey.TITLE);
                bean.artist = tag.getFirst(FieldKey.ARTIST);
                bean.album = tag.getFirst(FieldKey.ALBUM);
                bean.genre = tag.getFirst(FieldKey.GENRE);
                if (tag instanceof AbstractID3v2Tag) {
                    AbstractID3v2Tag atag = (AbstractID3v2Tag) tag;
                    AbstractID3v2Frame frame = (AbstractID3v2Frame) atag.getFrame("APIC");
                    if (frame != null) {
                        bean.hasImage = true;
                    }
                }
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public static byte[] getMusicImage(File file) {
        if (file == null) {
            return null;
        }
        try {
            AudioFile read = AudioFileIO.read(file);
            Tag tag = read.getTag();
            if (tag != null) {
                if (tag instanceof AbstractID3v2Tag) {
                    AbstractID3v2Tag atag = (AbstractID3v2Tag) tag;
                    AbstractID3v2Frame frame = (AbstractID3v2Frame) atag.getFrame("APIC");
                    if (frame != null) {
                        FrameBodyAPIC body = (FrameBodyAPIC) frame.getBody();
                        return body.getImageData();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void executList() {

        for (MP3Info mp3Info : DataManager.getInstans().getList()) {
            executList(mp3Info, false);
        }

        main.listView.setItems(null);
        main.listView.setItems(DataManager.getInstans().getList());

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
            main.listView.setItems(DataManager.getInstans().getList());
        }
    }


    private void fixMP3Info(MP3Info bean) {

        try {
            AudioFile mp3File = AudioFileIO.read(bean.getMusicFile());
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

    private boolean convertAmr2MP3(File src, File target) {
        AudioAttributes audio = new AudioAttributes();
        audio.setCodec("libmp3lame");
        audio.setBitRate(new Integer(128000));
        audio.setChannels(new Integer(2));
        audio.setSamplingRate(new Integer(44100));
        Encoder encoder = new Encoder(new MyFFMPEGExecute());
        EncodingAttributes attrs = new EncodingAttributes();
        attrs.setFormat("mp3");
        attrs.setAudioAttributes(audio);
        try {
            encoder.encode(src, target, attrs);
            return true;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InputFormatException e) {
            e.printStackTrace();
        } catch (EncoderException e) {
            e.printStackTrace();
        }
        return false;
    }

}
