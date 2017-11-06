package com.music.read;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xupanpan on 03/11/2017.
 */
public class HomeView {

    public ListView<MP3Info> listView;
    public ObservableList<MP3Info> list = FXCollections.observableArrayList();
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
    private BorderPane borderPane;

    private boolean isShowErrorCodeSettingView = false;
    private boolean isShowReplaceSettingView = false;
    private boolean isShowAddSettingView = false;
    private MenuBar menuBar;

    private MusicParser musicParser;
    private PlayManager playManager;
    private FileNameEditer fileNameEditer;

    public HomeView(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }


    public void init() {
        musicParser = new MusicParser(this);
        playManager = new PlayManager(this);
        fileNameEditer = new FileNameEditer(this);
        primaryStage.setTitle("MP3信息助手");
        borderPane = new BorderPane();
        Scene scene = new Scene(borderPane, WIDTH, HIGTH);
        primaryStage.setScene(scene);
        primaryStage.show();

        initMenuBar();
        initView();
        borderPane.setCenter(rootView);
    }


    private void initMenuBar() {

        menuBar = new MenuBar();
        menuBar.setUseSystemMenuBar(true);
        Menu fileMenu = new Menu("文件");

        MenuItem openItem = new MenuItem("加载音乐");
        openItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                FileChooser chooser = new FileChooser();
                chooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("All Images", "*.*"),
                        new FileChooser.ExtensionFilter("MP3", "*.mp3")
                );
                List<File> files = chooser.showOpenMultipleDialog(primaryStage);
                if (files != null) {
                    musicParser.loadMp3Data(files);
                }
            }
        });

        MenuItem clearItem = new MenuItem("删除选中");
        clearItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                List<MP3Info> rList = new ArrayList<MP3Info>();
                for (int i = 0; i < list.size(); i++) {
                    MP3Info mp3Info = list.get(i);
                    if (mp3Info.isChecked) {
                        rList.add(mp3Info);
                    }
                }
                for (MP3Info info : rList) {
                    list.remove(info);
                }
            }
        });

        MenuItem clearAllItem = new MenuItem("清空列表");
        clearAllItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                list.clear();
            }
        });

        MenuItem exitItem = new MenuItem("退出");
        exitItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                Platform.exit();
            }
        });

        fileMenu.getItems().addAll(openItem, clearItem, clearAllItem, exitItem);


        Menu editMenu = new Menu("编辑");

        CheckMenuItem codeErrorItem = new CheckMenuItem("乱码分析");
        codeErrorItem.setSelected(isShowErrorCodeSettingView);
        codeErrorItem.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                isShowErrorCodeSettingView = newValue;
                checkSettingView();
            }
        });


        CheckMenuItem replaceNameItem = new CheckMenuItem("批量替换名称字符");
        replaceNameItem.setSelected(isShowReplaceSettingView);
        replaceNameItem.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                isShowReplaceSettingView = newValue;
                checkSettingView();
            }
        });

        CheckMenuItem addNameItem = new CheckMenuItem("批量添加名称字符");
        addNameItem.setSelected(isShowAddSettingView);
        addNameItem.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                isShowAddSettingView = newValue;
                checkSettingView();

            }
        });


        editMenu.getItems().addAll(codeErrorItem, replaceNameItem, addNameItem);


        Menu helpMenu = new Menu("帮助");
        MenuItem useInfoItem = new MenuItem("使用说明");
        useInfoItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
            }
        });
        MenuItem aboutItem = new MenuItem("关于");
        aboutItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
            }
        });

        helpMenu.getItems().addAll(useInfoItem, aboutItem);

        menuBar.getMenus().addAll(fileMenu, editMenu, helpMenu);

        initPlayerView();

        borderPane.setTop(menuBar);
    }


    private void initPlayerView() {

        Menu playMenu = new Menu("play");
        menuBar.getMenus().addAll(playMenu);

    }


    private HBox getErrorCodeSetting() {

        HBox mHBox = new HBox(5);
        mHBox.setAlignment(Pos.CENTER_LEFT);
        Label splitLabel = new Label("文件分隔线:");

        splitText = new TextField(DEFULT_PRE);
        splitText.setAlignment(Pos.CENTER);
        splitText.setPrefWidth(40);


        CheckBox headBox = new CheckBox("歌手名在前");
        headBox.setSelected(nameFirst);
        headBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                nameFirst = newValue;
            }
        });


        Button btn_ana = new Button("开始分析");
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

        deleteBeforText = new TextField();
        deleteBeforText.setPromptText("改前");
        deleteBeforText.setPrefWidth(50);

        deleteAfterText = new TextField();
        deleteAfterText.setPromptText("改后");
        deleteAfterText.setPrefWidth(50);

        Button btn_delete = new Button("开始更改");
        btn_delete.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {

                String textBefor = deleteBeforText.getText();
                String textAfter = deleteAfterText.getText();

                fileNameEditer.renameList(textBefor,textAfter);
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
        text1 = new TextField();
        text1.setPrefWidth(30);
        Label label2 = new Label("位前，添加");
        text2 = new TextField();
        text2.setPrefWidth(50);

        Button btn_add = new Button("开始添加");
        btn_add.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                String textWei = text1.getText();
                String textStr = text2.getText();
                fileNameEditer.addStrList(textWei,textStr);
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
        headSettingView = new HBox(5);
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

    private void checkSettingView() {

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


    private void initView() {

        rootView = new VBox();
        rootView.setPadding(new Insets(10, 10, 10, 10));
        rootView.setSpacing(10);
        rootView.setAlignment(Pos.TOP_CENTER);
        rootView.setBackground(Background.EMPTY);

        initSettingView();

        listView = new ListView<MP3Info>();
        listView.setItems(list);
        listView.setEditable(true);
        listView.setOrientation(Orientation.VERTICAL);
        listView.setCellFactory(new Callback<ListView<MP3Info>, ListCell<MP3Info>>() {
            public ListCell<MP3Info> call(ListView<MP3Info> param) {
                return new MP3ListCell();
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
        VBox.setVgrow(listView, Priority.ALWAYS);
        rootView.getChildren().addAll(getHeadTitle(), listView);
    }

    private HBox getHeadTitle() {
        HBox itemView = new HBox(10);
        itemView.setAlignment(Pos.CENTER_LEFT);
        itemView.setPadding(new Insets(0, 9, 0, 9));
        CheckBox cb = new CheckBox();
        cb.setSelected(isSelectAll);
        cb.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                isSelectAll = newValue;
                if (!list.isEmpty()) {
                    for (MP3Info mp3Info : list) {
                        mp3Info.isChecked = newValue;
                    }
                    listView.setItems(null);
                    listView.setItems(list);
                }
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
        Label album = new Label("专辑名称");
        album.setPrefWidth(160);
        Label time = new Label("时长");
        time.setPrefWidth(40);

        Separator s2 = new Separator(Orientation.VERTICAL);
        Separator s3 = new Separator(Orientation.VERTICAL);
        Separator s4 = new Separator(Orientation.VERTICAL);
        Separator s5 = new Separator(Orientation.VERTICAL);
        itemView.getChildren().addAll(cb, index, name, s2, title, s3, artist, s4, album, s5, time);
        initPlayControlView(itemView);
        return itemView;
    }

    public Label playTimeLabel;
    public Button playStop;

    private void initPlayControlView(HBox itemView) {
        Separator s6 = new Separator(Orientation.VERTICAL);
        playTimeLabel = new Label();
        playTimeLabel.setPrefWidth(45);
        playTimeLabel.setTextFill(Color.LIGHTSKYBLUE);

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
            } catch (Exception e) {
            }
        }
        return imageView1;
    }

    private void onPlayClick() {

        if (list.isEmpty()) return;

        if (playManager.currentPosition == -1) {
            playManager.play(0);
        } else {
            playManager.stopAndStart();
        }
    }



}
