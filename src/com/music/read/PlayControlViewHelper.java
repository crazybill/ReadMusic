package com.music.read;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class PlayControlViewHelper {
    private HomeView homeView;
    private MusicFileParser musicParser;
    private PlayManager playManager;

    private ImageView musicImageView;
    private JFXButton startBtn;

    private int icon_width = 30;
    private int icon_height = 30;
    private HBox bottomContentView;
    private JFXSlider playSlider;
    private Label musicName, musicSinger;

    public PlayControlViewHelper(HomeView homeView) {
        this.homeView = homeView;
        this.musicParser = homeView.musicParser;
        this.playManager = homeView.playManager;

    }

    public void initBottonView() {


        VBox bottomView = new VBox();

        playSlider = new JFXSlider(0.3, 1, 1);
        playSlider.setBackground(FxViewUtil.getBackground(Color.WHITE));
        playSlider.setShowTickLabels(false);
        playSlider.setShowTickMarks(false);
        playSlider.setMajorTickUnit(1);
        playSlider.setMax(100);

        playSlider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {

            }
        });


        bottomContentView = new HBox();
        bottomContentView.setAlignment(Pos.CENTER_LEFT);
        bottomContentView.setBackground(FxViewUtil.getBackground(Color.WHITE));

        musicImageView = new ImageView();
        musicImageView.setFitHeight(80);
        musicImageView.setFitWidth(80);
        updateMusicInfo(null);

        HBox.setMargin(musicImageView, new Insets(8, 10, 10, 10));

        VBox vBox1 = new VBox();
        vBox1.setAlignment(Pos.CENTER_LEFT);

        musicName = new Label();
        musicName.setMaxWidth(300);
        musicName.setPrefWidth(300);
        musicName.setWrapText(true);
        musicName.setFont(Font.font(18));
        musicName.setTextFill(Main.blueColor);

        musicSinger = new Label();
        musicSinger.setMaxWidth(300);
        musicSinger.setPrefWidth(300);
        musicSinger.setWrapText(true);
        musicSinger.setFont(Font.font(12));
        vBox1.getChildren().addAll(musicName, musicSinger);

        VBox.setMargin(musicSinger, new Insets(5, 0, 0, 0));


        VBox vBox2 = new VBox();
        vBox2.setAlignment(Pos.CENTER);

        HBox hBox1 = new HBox();
        JFXButton lastBtn = new JFXButton();
        lastBtn.setRipplerFill(Color.GREEN);
        lastBtn.setFocusTraversable(false);
        lastBtn.setButtonType(JFXButton.ButtonType.FLAT);
        ImageView lastBgView = new ImageView(new Image(HomeView.class.getClass().getResourceAsStream("/res/icon_last.png")));
        lastBgView.setFitHeight(icon_height);
        lastBgView.setFitWidth(icon_width);
        lastBtn.setGraphic(lastBgView);
        lastBtn.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                playManager.playLast();
            }
        });

        startBtn = new JFXButton();
        startBtn.setRipplerFill(Color.GREEN);
        startBtn.setFocusTraversable(false);
        startBtn.setButtonType(JFXButton.ButtonType.FLAT);
        setButtonPlay();
        startBtn.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                homeView.onPlayClick();
            }
        });

        JFXButton nextBtn = new JFXButton();
        nextBtn.setRipplerFill(Color.GREEN);
        nextBtn.setFocusTraversable(false);
        nextBtn.setButtonType(JFXButton.ButtonType.FLAT);
        ImageView nextBgView = new ImageView(new Image(HomeView.class.getClass().getResourceAsStream("/res/icon_next.png")));
        nextBgView.setFitHeight(icon_height);
        nextBgView.setFitWidth(icon_width);
        nextBtn.setGraphic(nextBgView);
        nextBtn.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                playManager.playNext();
            }
        });
        hBox1.getChildren().addAll(lastBtn, startBtn, nextBtn);

        HBox.setMargin(hBox1, new Insets(8, 0, 0, 0));

        playTimeLabel = new Label();
        playTimeLabel.setTextFill(Main.blueColor);

        vBox2.getChildren().addAll(playTimeLabel, hBox1);

        VBox.setVgrow(vBox2, Priority.ALWAYS);

        bottomContentView.getChildren().addAll(musicImageView, vBox1, vBox2);

        bottomView.getChildren().addAll(playSlider, bottomContentView);


        homeView.borderPane.setBottom(bottomView);

    }

    private String totalTime = "";

    public void updateMusicInfo(MP3Info currentPlayInfo) {
        if (currentPlayInfo != null) {
            totalTime = currentPlayInfo.timeShow;
            setButtonStop();
            musicName.setText(TextUtils.isEmpty(currentPlayInfo.title) ? currentPlayInfo.fileName : currentPlayInfo.title);
            playSlider.setMax(currentPlayInfo.time);
            musicSinger.setText(currentPlayInfo.artist);
            if (currentPlayInfo.hasImage) {
                byte[] musicImage = musicParser.getMusicImage(currentPlayInfo.getMusicFile());
                Image image = new Image(new ByteArrayInputStream(musicImage));
                musicImageView.setImage(image);
            } else {
                musicImageView.setImage(getDefImageBg());
            }
        } else {
            totalTime = "";
            musicImageView.setImage(getDefImageBg());
        }
    }


    public Label playTimeLabel;


    public void updatePlayTime(int position) {
        playTimeLabel.setText(Utils.getMusicTime(position) + " / " + totalTime);
        playSlider.setIndicatorPosition(JFXSlider.IndicatorPosition.LEFT);
        playSlider.setValue(position);

    }

    private Image musicDefImg;

    private Image getDefImageBg() {
        if (musicDefImg == null) {
            musicDefImg = new Image(HomeView.class.getClass().getResourceAsStream("/res/img_music_def.png"));
        }
        return musicDefImg;
    }


    public void setButtonPlay() {
        ImageView startImage = getStartImage();
        if (startImage != null) {
            startBtn.setGraphic(startImage);
        }
    }

    public void setButtonStop() {
        ImageView stopImage = getStopImage();
        if (stopImage != null) {
            startBtn.setGraphic(stopImage);
        }
    }

    private ImageView imageView = null;

    private ImageView getStartImage() {
        if (imageView == null) {
            try {
                InputStream resourceAsStream = HomeView.class.getClass().getResourceAsStream("/res/icon_start.png");
                Image im = new Image(resourceAsStream);
                imageView = new ImageView(im);
                imageView.setFitHeight(icon_height);
                imageView.setFitWidth(icon_width);
            } catch (Exception e) {
            }
        }
        return imageView;
    }

    private ImageView imageView1 = null;

    private ImageView getStopImage() {
        if (imageView1 == null) {
            try {
                InputStream resourceAsStream = HomeView.class.getClass().getResourceAsStream("/res/icon_pause.png");
                Image im = new Image(resourceAsStream);
                imageView1 = new ImageView(im);
                imageView1.setFitHeight(icon_height);
                imageView1.setFitWidth(icon_width);
            } catch (Exception e) {
            }
        }

        return imageView1;
    }

}
