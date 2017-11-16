package com.music.read;

import com.jfoenix.controls.JFXCheckBox;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
        emptyProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    setContextMenu(null);
                } else {
                    setContextMenu(getCM());
                }
            }
        });
    }

    private ContextMenu getCM() {

        ContextMenu contextMenu = new ContextMenu();
        MenuItem playItem = new MenuItem();
        playItem.setText("播放");

        playItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                MP3Info selectedItem = getListView().getSelectionModel().getSelectedItem();
                homeView.playManager.play(selectedItem);
            }
        });

        MenuItem codeItem = new MenuItem();
        codeItem.setText("乱码处理");
        codeItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                MP3Info selectedItem = getListView().getSelectionModel().getSelectedItem();
                homeView.musicParser.executList(selectedItem, true);
            }
        });
        MenuItem deleteItem = new MenuItem();
        deleteItem.setText("删除");
        deleteItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                MP3Info selectedItem = getListView().getSelectionModel().getSelectedItem();
                if (selectedItem.isPlaying) {
                    homeView.playManager.closePlay();
                    homeView.setCurrentPlayTitle(Main.APP_NAME);
                }
                DataManager.getInstans().removeMP3Info(selectedItem);
            }
        });
        MenuItem fileItem = new MenuItem();
        fileItem.setText("查看文件");
        fileItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                MP3Info selectedItem = getListView().getSelectionModel().getSelectedItem();

                File musicFile = selectedItem.getMusicFile();
                if (musicFile == null) {
                    return;
                }
                File parentFile = musicFile.getParentFile();

                try {
                    java.awt.Desktop.getDesktop().open(new File(parentFile, "/"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        contextMenu.getItems().addAll(playItem, codeItem, deleteItem, fileItem);

        return contextMenu;
    }

    @Override
    protected void updateItem(MP3Info item, boolean empty) {
        super.updateItem(item, empty);

        if (!empty && item != null) {
            updateView(item);
            setGraphic(hBox);
        } else {
            setGraphic(null);
        }

    }

    private final HBox hBox = createView();

    private JFXCheckBox cb;
    private Label index, name, title, artist, album, time;


    public HBox createView() {
        HBox itemView = new HBox(10);
        itemView.setAlignment(Pos.CENTER_LEFT);
        cb = new JFXCheckBox();
        index = new Label();
        index.setPrefWidth(25);

        name = new Label();
        name.setWrapText(true);
        name.setPrefWidth(200);

        title = new Label();
        title.setWrapText(true);
        title.setPrefWidth(140);
        artist = new Label();
        artist.setWrapText(true);
        artist.setPrefWidth(80);
        album = new Label();
        album.setWrapText(true);
        album.setPrefWidth(160);
        time = new Label();
        time.setWrapText(true);
        time.setPrefWidth(40);
        Separator s1 = new Separator(Orientation.VERTICAL);
        Separator s2 = new Separator(Orientation.VERTICAL);
        Separator s3 = new Separator(Orientation.VERTICAL);
        Separator s4 = new Separator(Orientation.VERTICAL);
        itemView.getChildren().addAll(cb, index, name, s1, title, s2, artist, s3, album, s4, time);
        return itemView;

    }

    private void updateView(final MP3Info info) {

        cb.setSelected(info.isChecked);
        cb.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                info.isChecked = newValue;
            }
        });
        index.setText(String.valueOf(getIndex() + 1));
        name.setText(info.fileName);
        title.setText(info.title);
        artist.setText(info.artist);
        album.setText(info.album);
        time.setText(Utils.getMusicTime(info.time));

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
    }


    private static void setColorPlaying(Label label) {
        label.setTextFill(Color.LIGHTSKYBLUE);
    }

    private static void setColorDef(Label label) {
        label.setTextFill(Color.BLACK);
    }


}
