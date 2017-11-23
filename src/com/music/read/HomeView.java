package com.music.read;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import com.sun.javafx.scene.control.skin.ListViewSkin;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by xupanpan on 03/11/2017.
 */
public class HomeView {

    public JFXListView<MP3Info> listView;

    public boolean isSelectAll = true;
    private static final double WIDTH = 1000d;
    private static final double HIGTH = 700d;
    public Stage primaryStage;
    public VBox rootView;
    public static final String DEFULT_PRE = "-";
    public boolean nameFirst = true;
    public javafx.scene.control.TextField splitText;
    private javafx.scene.control.TextField deleteBeforText;
    private javafx.scene.control.TextField deleteAfterText;
    private javafx.scene.control.TextField text1;
    private javafx.scene.control.TextField text2;
    public BorderPane borderPane;
    public boolean isShowErrorCodeSettingView = false;
    public boolean isShowReplaceSettingView = false;
    public boolean isShowAddSettingView = false;

    public MusicFileParser musicParser;
    public PlayManager playManager;
    public FileNameEditer fileNameEditer;
    public Config playConfig;

    public boolean isShowAlbum = true;
    public boolean isShowSize = true;
    public boolean isShowBit = true;
    public SortType sortType = SortType.PATH;

    public HomeView(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent event) {
                exitApp();
            }
        });
    }


    public void init() {
        musicParser = new MusicFileParser(this);
        playManager = new PlayManager(this);
        fileNameEditer = new FileNameEditer(this);
        setCurrentPlayTitle(Main.APP_NAME);

        playConfig = PlayListManager.getPlayConfig();
        if (playConfig != null) {
            isSelectAll = playConfig.isCheckedAll;
            playManager.setPlayType(playConfig.playType);
            isShowAlbum = playConfig.isCheckAlbum;
            isShowSize = playConfig.isCheckSize;
            isShowBit = playConfig.isCheckBit;
            sortType = playConfig.sortType == null ? SortType.PATH : playConfig.sortType;
        }

        borderPane = new BorderPane();
        Scene scene = new Scene(borderPane, WIDTH, HIGTH);
        scene.getStylesheets().add("/res/lisStyles.css");

        primaryStage.setScene(scene);
        primaryStage.show();

        new MenuViewHelper(this).initMenuBar();

        initView();
        borderPane.setCenter(rootView);

        initData();
    }

    private void initView() {

        rootView = new VBox();
        rootView.setAlignment(Pos.TOP_CENTER);
        rootView.setBackground(Background.EMPTY);

        initSettingView();

        StackPane stackPane = new StackPane();

        listView = new JFXListView<MP3Info>();
        listView.setItems(DataManager.getInstans().getList());
        listView.setEditable(true);
        listView.setBorder(FxViewUtil.getBorder(Color.TRANSPARENT, 0, 0));
        listView.setBackground(FxViewUtil.getBackground(Color.TRANSPARENT, 0));
        listView.setOrientation(Orientation.VERTICAL);
        listView.setCellFactory(new Callback<ListView<MP3Info>, ListCell<MP3Info>>() {
            public ListCell<MP3Info> call(ListView<MP3Info> param) {
                return new MP3ListCell(HomeView.this);
            }
        });
        listView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<MP3Info>() {
            public void changed(ObservableValue<? extends MP3Info> observable, MP3Info oldValue, MP3Info newValue) {
            }
        });

        listView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.PRIMARY) {
                    if (event.getClickCount() == 2) {
                        MP3Info selectedItem = listView.getSelectionModel().getSelectedItem();
                        playManager.play(selectedItem);
                    }
                }
            }
        });

        VBox.setVgrow(stackPane, Priority.ALWAYS);

        listView.setOnDragOver(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                if (event.getGestureSource() != listView) {
                    event.acceptTransferModes(TransferMode.ANY);
                }
            }
        });

        listView.setOnDragDropped(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                Dragboard dragboard = event.getDragboard();
                List<File> files = dragboard.getFiles();
                if (files.size() > 0) {
                    musicParser.loadMp3Data(files);
                }
            }
        });

        listView.setOnKeyReleased(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {
                KeyCode code = event.getCode();
                if (code == KeyCode.ENTER) {
                    playManager.play(listView.getSelectionModel().getSelectedIndex());
                } else if (code == KeyCode.SPACE) {
                    onPlayClick();
                } else if (code == KeyCode.RIGHT) {
                    playManager.playNext();
                } else if (code == KeyCode.LEFT) {
                    playManager.playLast();
                }
            }
        });

        musicBg = new ImageView();
        musicBg.setBlendMode(BlendMode.MULTIPLY);
        musicBg.setFitWidth(700);
        musicBg.setFitHeight(700);
        musicBg.setSmooth(true);
        StackPane.setAlignment(musicBg, Pos.TOP_RIGHT);
        stackPane.getChildren().addAll(listView, musicBg);
        rootView.getChildren().addAll(getHeadTitle(), stackPane);
    }

    public ImageView musicBg;

    private void initData() {
        new Thread(new Runnable() {
            public void run() {
                List<MP3Info> historyPlayList = PlayListManager.getHistoryPlayList();
                if (historyPlayList != null && !historyPlayList.isEmpty()) {
                    DataManager.getInstans().add2List(historyPlayList);
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            Platform.runLater(new Runnable() {
                                public void run() {
                                    playManager.stopAndStart();
                                }
                            });
                        }
                    }, 3000);
                }
            }
        }).start();
    }

    public void setCurrentPlayTitle(String title) {
        primaryStage.setTitle(title);
    }

    public void exitApp() {

        Config config = new Config();
        config.isCheckedAll = isSelectAll;
        config.playType = playManager.getPlayType();
        config.isCheckAlbum = isShowAlbum;
        config.isCheckSize = isShowSize;
        config.isCheckBit = isShowBit;
        config.sortType = sortType;

        PlayListManager.savePlayConfig(config);
        PlayListManager.savePlayList(DataManager.getInstans().getList());
        System.exit(0);
    }


    private HBox getErrorCodeSetting() {

        HBox mHBox = new HBox(5);
        mHBox.setAlignment(Pos.CENTER_LEFT);
        Label splitLabel = new Label("文件分隔线:");

        splitText = new JFXTextField(DEFULT_PRE);
        splitText.setAlignment(Pos.CENTER);
        splitText.setPrefWidth(40);


        JFXCheckBox headBox = new JFXCheckBox("歌手名在前");
        headBox.setSelected(nameFirst);
        headBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                nameFirst = newValue;
            }
        });


        JFXButton btn_ana = new JFXButton("开始分析");
        btn_ana.setButtonType(JFXButton.ButtonType.RAISED);
        btn_ana.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                musicParser.executList();
            }
        });

        mHBox.getChildren().addAll(splitLabel, splitText,
                headBox, btn_ana);

        return mHBox;
    }


    private HBox getReplaceSetting() {

        HBox mHBox = new HBox(5);
        mHBox.setAlignment(Pos.CENTER_LEFT);
        Label petchDeletLabel = new Label("批量改文件名:");

        deleteBeforText = new JFXTextField();
        deleteBeforText.setPromptText("改前");
        deleteBeforText.setPrefWidth(40);

        deleteAfterText = new JFXTextField();
        deleteAfterText.setPromptText("改后");
        deleteAfterText.setPrefWidth(40);

        JFXButton btn_delete = new JFXButton("开始更改");
        btn_delete.setButtonType(JFXButton.ButtonType.RAISED);
        btn_delete.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {

                String textBefor = deleteBeforText.getText();
                String textAfter = deleteAfterText.getText();

                fileNameEditer.renameList(textBefor, textAfter);
            }
        });


        mHBox.getChildren().addAll(petchDeletLabel, deleteBeforText,
                deleteAfterText, btn_delete);

        return mHBox;
    }


    private HBox getAddSetting() {
        HBox mHBox = new HBox(5);
        mHBox.setAlignment(Pos.CENTER_LEFT);
        Label label1 = new Label("在第");
        text1 = new JFXTextField();
        text1.setPrefWidth(40);
        Label label2 = new Label("位前，添加");
        text2 = new JFXTextField();
        text2.setPrefWidth(40);

        JFXButton btn_add = new JFXButton("开始添加");
        btn_add.setButtonType(JFXButton.ButtonType.RAISED);
        btn_add.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                String textWei = text1.getText();
                String textStr = text2.getText();
                fileNameEditer.addStrList(textWei, textStr);
            }
        });

        mHBox.getChildren().addAll(label1, text1,
                label2, text2, btn_add);

        return mHBox;
    }

    private Separator getVSeparator() {
        return new Separator(Orientation.VERTICAL);
    }

    private Separator getHSeparator() {
        return new Separator(Orientation.HORIZONTAL);
    }


    private HBox errorCodeSetting, replaceSetting, addSetting;
    private Separator vSeparator, vSeparator1, hSeparator;
    private HBox headSettingView;

    private void initSettingView() {
        headSettingView = new HBox();
        headSettingView.setPadding(new Insets(8, 8, 8, 8));
        headSettingView.setSpacing(8);
        headSettingView.setAlignment(Pos.CENTER_LEFT);

        errorCodeSetting = getErrorCodeSetting();
        replaceSetting = getReplaceSetting();
        addSetting = getAddSetting();

        vSeparator = getVSeparator();
        vSeparator1 = getVSeparator();
        hSeparator = getHSeparator();

        headSettingView.getChildren().addAll(errorCodeSetting, vSeparator, replaceSetting, vSeparator1, addSetting);
        rootView.getChildren().addAll(headSettingView, hSeparator);
        checkSettingView();
    }

    public void checkSettingView() {

        errorCodeSetting.setVisible(isShowErrorCodeSettingView);
        errorCodeSetting.setManaged(isShowErrorCodeSettingView);

        replaceSetting.setVisible(isShowReplaceSettingView);
        replaceSetting.setManaged(isShowReplaceSettingView);

        addSetting.setVisible(isShowAddSettingView);
        addSetting.setManaged(isShowAddSettingView);

        if ((isShowErrorCodeSettingView && isShowReplaceSettingView) || (isShowErrorCodeSettingView && isShowAddSettingView)) {
            vSeparator.setVisible(true);
            vSeparator.setManaged(true);
        } else {
            vSeparator.setVisible(false);
            vSeparator.setManaged(false);
        }

        if (isShowReplaceSettingView && isShowAddSettingView) {
            vSeparator1.setVisible(true);
            vSeparator1.setManaged(true);
        } else {
            vSeparator1.setVisible(false);
            vSeparator1.setManaged(false);
        }

        if (isShowErrorCodeSettingView || isShowReplaceSettingView || isShowAddSettingView) {
            headSettingView.setVisible(true);
            headSettingView.setManaged(true);
            hSeparator.setVisible(true);
            hSeparator.setManaged(true);
        } else {
            headSettingView.setVisible(false);
            headSettingView.setManaged(false);
            hSeparator.setVisible(false);
            hSeparator.setManaged(false);
        }

    }


    private int first = 0;
    private int last = 0;

    private void getFirstAndLast() {
        if (DataManager.getInstans().isListEmpty()) {
            return;
        }
        try {
            ListViewSkin<?> ts = (ListViewSkin<?>) listView.getSkin();
            VirtualFlow<?> vf = (VirtualFlow<?>) ts.getChildren().get(0);
            first = vf.getFirstVisibleCell().getIndex();
            last = vf.getLastVisibleCell().getIndex();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void scrollToShow() {
        getFirstAndLast();
        int currentPlayPosition = DataManager.getInstans().getCurrentPlayPosition();
        if (currentPlayPosition < first || currentPlayPosition > last) {
            listView.scrollTo(currentPlayPosition);
        }
    }


    public void notifyListViewDataSetChange() {
        listView.setItems(null);
        listView.setItems(DataManager.getInstans().getList());

    }

    private Label album;
    private Separator s5;

    private HBox getHeadTitle() {
        HBox itemView = new HBox(10);
        itemView.setAlignment(Pos.CENTER_LEFT);
        itemView.setPadding(new Insets(5, 9, 5, 9));
        JFXCheckBox cb = new JFXCheckBox();
        cb.setSelected(isSelectAll);
        cb.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                isSelectAll = newValue;
                DataManager.getInstans().setAllCheckStatus(newValue);
                notifyListViewDataSetChange();
            }
        });

        Label name = new Label("文件名称");
        name.setPrefWidth(200);
        Label index = new Label("#");
        index.setPrefWidth(25);

        Label title = new Label("标题");
        title.setPrefWidth(140);
        Label artist = new Label("歌手");
        artist.setPrefWidth(80);
        album = new Label("专辑名称");
        album.setPrefWidth(160);
        Label time = new Label("时长");
        time.setPrefWidth(40);

        Separator s2 = new Separator(Orientation.VERTICAL);
        Separator s3 = new Separator(Orientation.VERTICAL);
        Separator s4 = new Separator(Orientation.VERTICAL);
        s5 = new Separator(Orientation.VERTICAL);
        itemView.getChildren().addAll(cb, index, name, s2, title, s3, artist, s4, album, s5, time);
        initPlayControlView(itemView);
        setShowZhuanji(isShowAlbum);
        return itemView;
    }


    public void setShowZhuanji(boolean isShow) {
        isShowAlbum = isShow;
        notifyListViewDataSetChange();
        if (isShowAlbum) {
            album.setManaged(true);
            album.setVisible(true);
            s5.setManaged(true);
            s5.setVisible(true);
        } else {
            album.setManaged(false);
            album.setVisible(false);
            s5.setManaged(false);
            s5.setVisible(false);
        }
    }


    public Label playTimeLabel;
    public Button playStop;

    private void initPlayControlView(HBox itemView) {
        Separator s6 = new Separator(Orientation.VERTICAL);
        playTimeLabel = new Label();
        playTimeLabel.setPrefWidth(45);
        playTimeLabel.setTextFill(Main.blueColor);
        playTimeLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.PRIMARY) {
                    if (event.getClickCount() == 2) {

                        scrollToShow();
                    }
                }
            }
        });

        playStop = new Button();
        setButtonPlay();
        playStop.setBackground(null);
        playStop.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                onPlayClick();
            }
        });

        itemView.getChildren().addAll(s6, playStop, playTimeLabel);
    }


    public void setButtonPlay() {
        ImageView startImage = getStartImage();
        if (startImage != null) {
            playStop.setGraphic(startImage);
        }
    }

    public void setButtonStop() {
        ImageView stopImage = getStopImage();
        if (stopImage != null) {
            playStop.setGraphic(stopImage);
        }
    }

    private ImageView imageView = null;

    private ImageView getStartImage() {
        if (imageView == null) {
            try {
                InputStream resourceAsStream = HomeView.class.getClass().getResourceAsStream("/res/ic_live_play.png");
                Image im = new Image(resourceAsStream);
                imageView = new ImageView(im);
                imageView.setFitHeight(20);
                imageView.setFitWidth(20);
            } catch (Exception e) {
            }
        }
        return imageView;
    }

    private ImageView imageView1 = null;

    private ImageView getStopImage() {
        if (imageView1 == null) {
            try {
                InputStream resourceAsStream = HomeView.class.getClass().getResourceAsStream("/res/ic_live_suspend.png");
                Image im = new Image(resourceAsStream);
                imageView1 = new ImageView(im);
                imageView1.setFitHeight(20);
                imageView1.setFitWidth(20);
            } catch (Exception e) {
            }
        }

        return imageView1;
    }

    public void onPlayClick() {

        if (DataManager.getInstans().isListEmpty()) return;

        if (DataManager.getInstans().getCurrentPlayPosition() == -1) {
            playManager.play(0);
        } else {
            playManager.stopAndStart();
        }
    }


}
