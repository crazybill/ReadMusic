package com.music.read;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.IOException;

/**
 * Created by xupanpan on 09/08/2017.
 */
public class MP3ListCell extends ListCell<MP3Info> {
    private HomeView homeView;

    public MP3ListCell(HomeView homeView) {
        this.homeView = homeView;
        emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
            if (isNowEmpty) {
                setContextMenu(null);
            } else {
                setContextMenu(getCM());
            }
        });

    }

    private ContextMenu getCM() {

        ContextMenu contextMenu = new ContextMenu();
        MenuItem playItem = new MenuItem();
        playItem.setText("播放");
        playItem.setOnAction(event -> {
            MP3Info selectedItem = getListView().getSelectionModel().getSelectedItem();
            homeView.playManager.play(selectedItem);
        });
        MenuItem codeItem = new MenuItem();
        codeItem.setText("乱码处理");
        codeItem.setOnAction(event -> {
            MP3Info selectedItem = getListView().getSelectionModel().getSelectedItem();
            homeView.musicParser.executList(selectedItem, true);

        });
        MenuItem deleteItem = new MenuItem();
        deleteItem.setText("删除");
        deleteItem.setOnAction(event -> {
            MP3Info selectedItem = getListView().getSelectionModel().getSelectedItem();
            if (homeView.playManager.mp3Info == selectedItem) {

                int currentPosition = homeView.playManager.currentPosition;
                homeView.list.remove(selectedItem);
                homeView.playManager.play(currentPosition);
            } else {
                homeView.list.remove(selectedItem);
                 homeView.playManager.updateCurrentPosition();
            }
        });
        MenuItem fileItem = new MenuItem();
        fileItem.setText("查看文件");
        fileItem.setOnAction(event -> {
            MP3Info selectedItem = getListView().getSelectionModel().getSelectedItem();
            if (!selectedItem.mp3File.exists()) {
                return;
            }
            File mp3File = selectedItem.mp3File;
            File parentFile = mp3File.getParentFile();

            try {
                java.awt.Desktop.getDesktop().open(new File(parentFile, "/"));
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
        contextMenu.getItems().addAll(playItem, codeItem, deleteItem, fileItem);

        return contextMenu;
    }

    @Override
    protected void updateItem(MP3Info item, boolean empty) {
        if (!empty && item != null) {
            setGraphic(getItemView(item));
        } else {
            setGraphic(null);
        }
        super.updateItem(item, empty);
    }


    public HBox getItemView(final MP3Info info) {

        HBox itemView = new HBox(10);
        itemView.setAlignment(Pos.CENTER_LEFT);

        CheckBox cb = new CheckBox();
        cb.setSelected(info.isChecked);
        cb.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                info.isChecked = newValue;
            }
        });


        Label index = new Label(String.valueOf(getIndex() + 1));
        index.setPrefWidth(25);

        Label name = new Label(info.fileName);
        name.setWrapText(true);
        name.setPrefWidth(200);

        Label title = new Label(info.title);
        title.setWrapText(true);
        title.setPrefWidth(140);
        Label artist = new Label(info.artist);
        artist.setWrapText(true);
        artist.setPrefWidth(80);
        Label album = new Label(info.album);
        album.setWrapText(true);
        album.setPrefWidth(160);
        Label time = new Label(Utils.getMusicTime(info.time));
        time.setWrapText(true);
        time.setPrefWidth(40);

        Separator s1 = new Separator(Orientation.VERTICAL);
        Separator s2 = new Separator(Orientation.VERTICAL);
        Separator s3 = new Separator(Orientation.VERTICAL);
        Separator s4 = new Separator(Orientation.VERTICAL);

        itemView.getChildren().addAll(cb, index, name, s1, title, s2, artist, s3, album, s4, time);

        if (info.isPlaying) {
            setColorPlaying(index);
            setColorPlaying(name);
            setColorPlaying(title);
            setColorPlaying(artist);
            setColorPlaying(album);
            setColorPlaying(time);
        } else {
            setColorDef(index);
            setColorDef(name);
            setColorDef(title);
            setColorDef(artist);
            setColorDef(album);
            setColorDef(time);
        }

        return itemView;

    }

    private void setColorPlaying(Label label) {
        label.setTextFill(Color.LIGHTSKYBLUE);
    }

    private void setColorDef(Label label) {
        label.setTextFill(Color.BLACK);
    }


}
