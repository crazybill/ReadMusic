package com.music.read;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;

/**
 * Created by xupanpan on 09/08/2017.
 */
public class MP3ListCell extends ListCell<MP3Info> {

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


        Label name = new Label(info.fileName);
        name.setWrapText(true);
        name.setPrefWidth(200);

        Label title = new Label(info.title);
        title.setWrapText(true);
        title.setPrefWidth(160);
        Label artist = new Label(info.artist);
        artist.setWrapText(true);
        artist.setPrefWidth(80);
        Label album = new Label(info.album);
        album.setWrapText(true);
        album.setPrefWidth(180);
        Label time = new Label(info.time + "");
        time.setWrapText(true);
        time.setPrefWidth(50);

        Separator s1 = new Separator(Orientation.VERTICAL);
        Separator s2 = new Separator(Orientation.VERTICAL);
        Separator s3 = new Separator(Orientation.VERTICAL);
        Separator s4 = new Separator(Orientation.VERTICAL);

        itemView.getChildren().addAll(cb,name, s1, title, s2, artist, s3, album, s4, time);

        return itemView;

    }
}
