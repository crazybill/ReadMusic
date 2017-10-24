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
import java.io.PrintWriter;
import java.io.StringWriter;
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

    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("MP3信息助手");
        rootView = new VBox();
        rootView.setPadding(new Insets(10, 10, 10, 10));
        rootView.setSpacing(10);
        rootView.setAlignment(Pos.TOP_CENTER);
        rootView.setBackground(Background.EMPTY);
        Scene scene = new Scene(rootView, WIDTH, HIGTH);
        primaryStage.setScene(scene);
        primaryStage.show();
        initView();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent event) {
                System.exit(0);
            }
        });

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
    }


    class ExceptionHandler implements Thread.UncaughtExceptionHandler {
        public void uncaughtException(Thread t, Throwable e) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("程序异常！");
            alert.setContentText(e.toString());

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String exceptionText = sw.toString();
            Label label = new Label("The exception stacktrace was:");

            TextArea textArea = new TextArea(exceptionText);
            textArea.setEditable(false);
            textArea.setWrapText(true);

            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            GridPane.setVgrow(textArea, Priority.ALWAYS);
            GridPane.setHgrow(textArea, Priority.ALWAYS);

            GridPane expContent = new GridPane();
            expContent.setMaxWidth(Double.MAX_VALUE);
            expContent.add(label, 0, 0);
            expContent.add(textArea, 0, 1);
            alert.getDialogPane().setExpandableContent(expContent);

            e.printStackTrace();
            alert.showAndWait();
            //System.exit(0);
        }
    }

    private void initView() {
        HBox headView = new HBox(5);
        headView.setAlignment(Pos.CENTER_LEFT);

        Button btn_selecte = new Button("加载mp3");
        btn_selecte.setOnAction(new EventHandler<ActionEvent>() {
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
        Separator separatorH = new Separator(Orientation.HORIZONTAL);

        Separator separator = new Separator(Orientation.VERTICAL);

        Label splitLabel = new Label("分隔线:");

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

        Separator separatorH3 = new Separator(Orientation.VERTICAL);

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

        Separator separatorH2 = new Separator(Orientation.VERTICAL);

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

        Separator separatorH1 = new Separator(Orientation.VERTICAL);

        Button btn_clear = new Button("清除");
        btn_clear.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                list.clear();
            }
        });

        headView.getChildren().addAll(btn_selecte, separator, splitLabel, splitText,
                headBox, btn_ana, separatorH3, petchDeletLabel, deleteBeforText, deleteAfterText, btn_delete, separatorH2, label1, text1, label2, text2, btn_add, separatorH1, btn_clear);

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
        VBox.setVgrow(listView, Priority.ALWAYS);
        rootView.getChildren().addAll(headView, separatorH, getHeadTitle(), listView);

    }

    private boolean isSelectAll = false;

    private HBox getHeadTitle() {
        HBox itemView = new HBox(10);
        itemView.setAlignment(Pos.CENTER_LEFT);
        itemView.setPadding(new Insets(0, 9, 0, 9));
        CheckBox cb = new CheckBox();
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
        itemView.getChildren().addAll(cb, name, s2, title, s3, artist, s4, album, s5, time);

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
