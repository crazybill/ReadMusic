package com.music.read;

import javafx.application.Application;
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
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by xupanpan on 12/10/2017.
 */
public class Main extends Application {
    private ListView<MP3Info> listView;
    private Executor executor = Executors.newSingleThreadExecutor();
    public ObservableList<MP3Info> list = FXCollections.observableArrayList();

    public static void main(String[] args) {
        launch(args);
    }

    private boolean isSelectAll = true;
    private static final double WIDTH = 1000d;
    private static final double HIGTH = 700d;
    private Stage primaryStage;
    private VBox rootView;
    private static final String DEFULT_PRE = "-";
    private boolean nameFirst = true;
    private TextField splitText;
    private TextField deleteBeforText;
    private TextField deleteAfterText;
    private TextField text1;
    private TextField text2;
    private BorderPane borderPane;

    private boolean isShowErrorCodeSettingView = false;
    private boolean isShowReplaceSettingView = false;
    private boolean isShowAddSettingView = false;

    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("MP3信息助手");

        borderPane = new BorderPane();
        Scene scene = new Scene(borderPane, WIDTH, HIGTH);
        primaryStage.setScene(scene);
        primaryStage.show();

        initMenuBar();
        initView();
        borderPane.setCenter(rootView);

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent event) {
                System.exit(0);
            }
        });
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
    }


    private void initMenuBar() {
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("文件");
        MenuItem openItem = new MenuItem("打开");
        openItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                FileChooser chooser = new FileChooser();
                chooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("All Images", "*.*"),
                        new FileChooser.ExtensionFilter("MP3", "*.mp3")
                );
                List<File> files = chooser.showOpenMultipleDialog(primaryStage);
                if (files != null) {
                    loadMp3Data(files);
                }
            }
        });

        MenuItem clearItem = new MenuItem("清除");
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

        MenuItem clearAllItem = new MenuItem("清空");
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
        borderPane.setTop(menuBar);
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
                executList();
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
                renameList();
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
                addStrList();
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

                        MusicPlayer.getInstans().play(selectedItem.mp3File);
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
        title.setPrefWidth(160);
        Label artist = new Label("歌手");
        artist.setPrefWidth(80);
        Label album = new Label("专辑名称");
        album.setPrefWidth(180);
        Label time = new Label("时长");
        time.setPrefWidth(50);

        Separator s2 = new Separator(Orientation.VERTICAL);
        Separator s3 = new Separator(Orientation.VERTICAL);
        Separator s4 = new Separator(Orientation.VERTICAL);
        Separator s5 = new Separator(Orientation.VERTICAL);
        itemView.getChildren().addAll(cb, index, name, s2, title, s3, artist, s4, album, s5, time);

        return itemView;
    }

    private Alert alert;

    private void loadMp3Data(final List<File> files) {

        alert = new Alert(Alert.AlertType.NONE);
        alert.setHeaderText("加载中，请稍等...");
        alert.initOwner(primaryStage);
        executor.execute(new Runnable() {
            public void run() {
                for (int i = 0; i < files.size(); i++) {
                    File f = files.get(i);
                    String name = f.getName();
                    if (name.endsWith(".mp3") || name.endsWith(".MP3")) {
                        MP3Info bean = new MP3Info();
                        bean.isChecked = isSelectAll;
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


    private void updateLoad(final MP3Info bean, final String msg) {
        Platform.runLater(new Runnable() {
            public void run() {
                list.add(bean);
                alert.setContentText(msg);
            }
        });

    }

    private void addStrList() {

        String textWei = text1.getText();
        String textStr = text2.getText();
        if (textWei == null || textWei.length() == 0 || textStr == null || textStr.length() == 0) {
            return;
        }

        int wei = 1;
        try {
            wei = Integer.parseInt(textWei);
            if (wei < 1) {
                wei = 1;
            }
        } catch (Exception e) {
            wei = 1;
        }

        for (MP3Info mp3Info : list) {
            if (mp3Info.isChecked) {
                String fileName = mp3Info.fileName;
                if (wei == 1) {
                    fileName = textStr + fileName;
                } else if (wei > fileName.length()) {
                    fileName = fileName + textStr;
                } else {
                    String substring = fileName.substring(0, wei - 1);
                    String substring2 = fileName.substring(wei - 1, fileName.length());
                    fileName = substring + textStr + substring2;
                }

                mp3Info.fileName = fileName;

                File newNameFile = new File(mp3Info.mp3File.getParent(), fileName);
                if (!newNameFile.exists()) {
                    mp3Info.mp3File.renameTo(newNameFile);
                    mp3Info.mp3File = newNameFile;
                }


            }
        }
        listView.setItems(null);
        listView.setItems(list);

    }

    private void renameList() {

        String textBefor = deleteBeforText.getText();
        if (textBefor == null || textBefor.length() == 0) {
            return;
        }
        String textAfter = deleteAfterText.getText();
        if (textAfter == null) {
            textAfter = "";
        }

        for (MP3Info mp3Info : list) {
            if (mp3Info.isChecked) {
                String fileName = mp3Info.fileName;
                if (fileName.contains(textBefor)) {

                    String replace = fileName.replace(textBefor, textAfter);
                    mp3Info.fileName = replace;

                    File newNameFile = new File(mp3Info.mp3File.getParent(), replace);
                    if (!newNameFile.exists()) {
                        mp3Info.mp3File.renameTo(newNameFile);
                        mp3Info.mp3File = newNameFile;

                    }
                }
            }
        }

        listView.setItems(null);
        listView.setItems(list);
    }

    private void executList() {

        for (MP3Info mp3Info : list) {
            if (mp3Info.isChecked) {
                String fileName = mp3Info.fileName.replaceAll(".mp3", "");
                String title = mp3Info.title;
                String artist = mp3Info.artist;

                String text = splitText.getText();
                if (text == null || text.length() == 0) {
                    text = DEFULT_PRE;
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
        }

        listView.setItems(null);
        listView.setItems(list);

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

    //MIMEType="image/jpeg"; Description="UE;]CC_CCA&PBJ@<M_0"; PictureData="12460 bytes";
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


}
