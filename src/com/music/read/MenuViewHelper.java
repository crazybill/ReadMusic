package com.music.read;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class MenuViewHelper {
    private HomeView homeView;
    private MenuBar menuBar;
    private Stage primaryStage;
    private MusicFileParser musicParser;
    private PlayManager playManager;

    public MenuViewHelper(HomeView homeView) {
        this.homeView = homeView;
        this.primaryStage = homeView.primaryStage;
        this.musicParser = homeView.musicParser;
        this.playManager = homeView.playManager;
    }

    public void initMenuBar() {
        menuBar = new MenuBar();
        menuBar.setUseSystemMenuBar(true);

        initFileMenu();
        initEditMenu();
        initViewMenu();
        initHelpMenu();
        initPlayerMenu();

        homeView.borderPane.setTop(menuBar);
    }

    private void initFileMenu() {

        Menu fileMenu = new Menu("文件");

        MenuItem openItem = new MenuItem("加载音乐");
        openItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                FileChooser chooser = new FileChooser();
                chooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("All Images", "*.*")
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
                if (DataManager.getInstans().removeSelected()) {
                    homeView.setCurrentPlayTitle(Main.APP_NAME);
                    playManager.closePlay();
                }
            }
        });

        MenuItem clearAllItem = new MenuItem("清空列表");
        clearAllItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                DataManager.getInstans().clearList();
                homeView.setCurrentPlayTitle(Main.APP_NAME);
                playManager.closePlay();
                homeView.listView.setUserData(null);
            }
        });

        MenuItem exitItem = new MenuItem("退出");
        exitItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                homeView.exitApp();
            }
        });

        fileMenu.getItems().addAll(openItem, clearItem, clearAllItem, new SeparatorMenuItem(), exitItem);

        menuBar.getMenus().add(fileMenu);
    }

    private void initEditMenu() {

        Menu editMenu = new Menu("编辑");

        CheckMenuItem codeErrorItem = new CheckMenuItem("乱码分析");
        codeErrorItem.setSelected(homeView.isShowErrorCodeSettingView);
        codeErrorItem.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                homeView.isShowErrorCodeSettingView = newValue;
                homeView.checkSettingView();
            }
        });


        CheckMenuItem replaceNameItem = new CheckMenuItem("批量替换名称字符");
        replaceNameItem.setSelected(homeView.isShowReplaceSettingView);
        replaceNameItem.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                homeView.isShowReplaceSettingView = newValue;
                homeView.checkSettingView();
            }
        });

        CheckMenuItem addNameItem = new CheckMenuItem("批量添加名称字符");
        addNameItem.setSelected(homeView.isShowAddSettingView);
        addNameItem.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                homeView.isShowAddSettingView = newValue;
                homeView.checkSettingView();

            }
        });


        editMenu.getItems().addAll(codeErrorItem, replaceNameItem, addNameItem);
        menuBar.getMenus().add(editMenu);

    }

    private void initHelpMenu() {

        Menu helpMenu = new Menu("帮助");
        MenuItem useInfoItem = new MenuItem("使用说明");
        useInfoItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
            }
        });
        MenuItem aboutItem = new MenuItem("关于");
        aboutItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("关于" + Main.APP_NAME);
                alert.setContentText("当前版本：" + Main.APP_VERSION);
                alert.show();
            }
        });

        helpMenu.getItems().addAll(useInfoItem, aboutItem);
        menuBar.getMenus().add(helpMenu);
    }


    private void initPlayerMenu() {

        Menu playMenu = new Menu("play");

        MenuItem lastItem = new MenuItem("上一曲");
        lastItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                playManager.playLast();
            }
        });

        MenuItem playCloseItem = new MenuItem("播放/停止");
        playCloseItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                homeView.onPlayClick();
            }
        });

        MenuItem nextItem = new MenuItem("下一曲");
        nextItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                playManager.playNext();
            }
        });

        ToggleGroup toggleGroup = new ToggleGroup();

        RadioMenuItem singleItem = new RadioMenuItem("单曲循环");
        singleItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                playManager.setPlayType(PlayManager.PlayType.SINGLE);

            }
        });

        singleItem.setToggleGroup(toggleGroup);

        RadioMenuItem shunItem = new RadioMenuItem("顺序播放");
        shunItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                playManager.setPlayType(PlayManager.PlayType.RECYCLE);
            }
        });
        shunItem.setToggleGroup(toggleGroup);

        RadioMenuItem suijiItem = new RadioMenuItem("随机播放");
        suijiItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                playManager.setPlayType(PlayManager.PlayType.RANDOM);
            }
        });
        suijiItem.setToggleGroup(toggleGroup);

        if (homeView.playConfig != null) {
            switch (homeView.playConfig.playType) {
                case SINGLE:
                    singleItem.setSelected(true);
                    break;
                case RECYCLE:
                    shunItem.setSelected(true);
                    break;
                case RANDOM:
                    suijiItem.setSelected(true);
                    break;
            }
        }

        playMenu.getItems().addAll(lastItem, playCloseItem, nextItem, new SeparatorMenuItem(), singleItem, shunItem, suijiItem);
        menuBar.getMenus().addAll(playMenu);

    }

    private void initViewMenu() {

        Menu viewMenu = new Menu("视图");
        ToggleGroup toggleGroup = new ToggleGroup();

        RadioMenuItem pathItem = new RadioMenuItem("路径排序（默认）");
        pathItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                homeView.sortType = SortType.PATH;

            }
        });
        pathItem.setToggleGroup(toggleGroup);

        RadioMenuItem musicNameItem = new RadioMenuItem("标题排序");
        musicNameItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                DataManager.getInstans().sortByMusicName();
                homeView.notifyListViewDataSetChange();
                homeView.sortType = SortType.MUSIC_NAME;
                if (!DataManager.getInstans().isListEmpty()) {
                    homeView.listView.scrollTo(0);
                }
            }
        });

        musicNameItem.setToggleGroup(toggleGroup);

        RadioMenuItem singerItem = new RadioMenuItem("歌手排序");
        singerItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                DataManager.getInstans().sortBySingerName();
                homeView.notifyListViewDataSetChange();
                homeView.sortType = SortType.SINGER;
                if (!DataManager.getInstans().isListEmpty()) {
                    homeView.listView.scrollTo(0);
                }
            }
        });
        singerItem.setToggleGroup(toggleGroup);

        RadioMenuItem fileNameItem = new RadioMenuItem("文件名排序");
        fileNameItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {

                DataManager.getInstans().sortByFileName();
                homeView.notifyListViewDataSetChange();
                homeView.sortType = SortType.FILE_NAME;
                if (!DataManager.getInstans().isListEmpty()) {
                    homeView.listView.scrollTo(0);
                }
            }
        });
        fileNameItem.setToggleGroup(toggleGroup);

        RadioMenuItem timeItem = new RadioMenuItem("时间排序");
        timeItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                DataManager.getInstans().sortByTime();
                homeView.notifyListViewDataSetChange();
                homeView.sortType = SortType.TIME;
                if (!DataManager.getInstans().isListEmpty()) {
                    homeView.listView.scrollTo(0);
                }
            }
        });
        timeItem.setToggleGroup(toggleGroup);

        switch (homeView.sortType) {
            case PATH:
                pathItem.setSelected(true);
                break;
            case TIME:
                timeItem.setSelected(true);
                break;
            case SINGER:
                singerItem.setSelected(true);
                break;
            case FILE_NAME:
                fileNameItem.setSelected(true);
                break;
            case MUSIC_NAME:
                musicNameItem.setSelected(true);
                break;
        }

        CheckMenuItem albumItem = new CheckMenuItem("显示专辑名");
        albumItem.setSelected(homeView.isShowAlbum);
        albumItem.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                homeView.setShowZhuanji(newValue);
            }
        });

        CheckMenuItem showSizeItem = new CheckMenuItem("显示文件大小");
        showSizeItem.setSelected(homeView.isShowSize);
        showSizeItem.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                homeView.isShowSize = newValue;
                homeView.notifyListViewDataSetChange();
            }
        });
        CheckMenuItem showbitItem = new CheckMenuItem("显示比特率");
        showbitItem.setSelected(homeView.isShowBit);
        showbitItem.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                homeView.isShowBit = newValue;
                homeView.notifyListViewDataSetChange();
            }
        });
        viewMenu.getItems().addAll(pathItem, musicNameItem, singerItem, fileNameItem, timeItem, new SeparatorMenuItem(), albumItem, showSizeItem, showbitItem);

        menuBar.getMenus().add(viewMenu);

    }


}
