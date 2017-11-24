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
import javafx.scene.text.Font;

import java.io.File;
import java.io.IOException;

/**
 * Created by xupanpan on 09/08/2017.
 */
public class MP3ListCell extends ListCell<MP3Info> {
    private HomeView homeView;
    private HBox hBox = createView();
    private JFXCheckBox cb;
    private Label index, name, title, artist, album, time, size, bitRate;
    private ContextMenu contextMenu = getCM();

    private ChangeListener<Boolean> listener = new ChangeListener<Boolean>() {
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if (newValue) {
                setContextMenu(null);
            } else {
                setContextMenu(contextMenu);
            }
        }
    };

    public MP3ListCell(HomeView homeView) {
        this.homeView = homeView;
        emptyProperty().addListener(listener);
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
        MenuItem removeItem = new MenuItem();
        removeItem.setText("移除");
        removeItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                MP3Info selectedItem = getListView().getSelectionModel().getSelectedItem();
                DataManager.getInstans().removeMP3Info(selectedItem);
                if (selectedItem.isPlaying) {
                    homeView.playManager.closePlay();
                    homeView.updateMusicPlayState();
                }
            }
        });

        MenuItem deleteItem = new MenuItem();
        deleteItem.setText("彻底删除！");
        deleteItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                MP3Info selectedItem = getListView().getSelectionModel().getSelectedItem();
                DataManager.getInstans().deleteMP3Info(selectedItem);
                if (selectedItem.isPlaying) {
                    homeView.playManager.closePlay();
                    homeView.updateMusicPlayState();
                }
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
        contextMenu.getItems().addAll(playItem, codeItem, removeItem, deleteItem, fileItem);

        return contextMenu;
    }


    private Separator s1, s2, s3, s4, s5;

    public HBox createView() {
        HBox itemView = new HBox(10);
        itemView.setAlignment(Pos.CENTER_LEFT);
        cb = new JFXCheckBox();
        cb.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                mMP3Info.isChecked = newValue;
            }
        });
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
        size = new Label();
        size.setWrapText(true);
        size.setPrefWidth(50);
        size.setFont(Font.font(11));
        bitRate = new Label();
        bitRate.setWrapText(true);
        bitRate.setPrefWidth(40);
        bitRate.setFont(Font.font(11));
        s1 = new Separator(Orientation.VERTICAL);
        s2 = new Separator(Orientation.VERTICAL);
        s3 = new Separator(Orientation.VERTICAL);
        s4 = new Separator(Orientation.VERTICAL);
        s5 = new Separator(Orientation.VERTICAL);
        itemView.getChildren().addAll(cb, index, name, s1, title, s2, artist, s3, album, s4, time, s5, size, bitRate);
        return itemView;

    }


    private MP3Info mMP3Info;
    private boolean iscPlay;

    private void updateView(MP3Info info) {
        if (mMP3Info != null && mMP3Info == info && iscPlay == info.isPlaying) {
            return;
        }
        iscPlay = info.isPlaying;
        mMP3Info = info;
        cb.setSelected(info.isChecked);
        index.setText(String.valueOf(getIndex() + 1));
        name.setText(info.fileName);
        title.setText(info.title);
        artist.setText(info.artist);
        album.setText(info.album);
        time.setText(info.timeShow);
        size.setText(info.size);
        bitRate.setText(info.bitRate);

        if (homeView.isShowAlbum) {
            album.setManaged(true);
            album.setVisible(true);
            s4.setManaged(true);
            s4.setVisible(true);
        } else {
            album.setManaged(false);
            album.setVisible(false);
            s4.setManaged(false);
            s4.setVisible(false);
        }

        if (homeView.isShowSize) {
            size.setManaged(true);
            size.setVisible(true);
        } else {
            size.setManaged(false);
            size.setVisible(false);
        }
        if (homeView.isShowBit) {
            bitRate.setManaged(true);
            bitRate.setVisible(true);
        } else {
            bitRate.setManaged(false);
            bitRate.setVisible(false);
        }
        if (info.isPlaying) {
            setColorPlaying(name);
            setColorPlaying(title);
            setColorPlaying(artist);
            setColorPlaying(album);
            setColorPlaying(time);
            setColorPlaying(size);
            setColorPlaying(bitRate);
        } else {
            setColorDef(name);
            setColorDef(title);
            setColorDef(artist);
            setColorDef(album);
            setColorDef(time);
            setColorDef(size);
            setColorDef(bitRate);
        }

        if (info.hasImage) {
            index.setTextFill(Color.RED);
        } else {
            setColorDef(index);
        }
    }


    private static void setColorPlaying(Label label) {
        label.setTextFill(Main.blueColor);
    }

    private static void setColorDef(Label label) {
        label.setTextFill(Color.BLACK);
    }


}
