package com.music.read;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

/**
 * Created by xupanpan on 12/10/2017.
 */
public class Main extends Application {
    public static final String APP_NAME = "MusicOcean";
    public static final String APP_VERSION = "1.0";
    private TrayIcon trayIcon;
    private HomeView homeView;

    public static void main(String[] args) {
        launch(args);
    }

    public void start(final Stage primaryStage) throws Exception {
        enableTray(primaryStage);
        homeView = new HomeView(primaryStage);
        homeView.init();
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
    }


    private void enableTray(final Stage stage) {
        PopupMenu popupMenu = new PopupMenu();
        java.awt.MenuItem openItem = new java.awt.MenuItem("显示");
        java.awt.MenuItem hideItem = new java.awt.MenuItem("最小化");
        java.awt.MenuItem quitItem = new java.awt.MenuItem("退出");
        MenuItem last = new MenuItem("上一首");
        MenuItem stop = new MenuItem("暂停/播放");
        MenuItem next = new MenuItem("下一首");

        ActionListener acl = new ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                java.awt.MenuItem item = (java.awt.MenuItem) e.getSource();
                Platform.setImplicitExit(false); //多次使用显示和隐藏设置false

                if (item.getLabel().equals("退出")) {
                    SystemTray.getSystemTray().remove(trayIcon);
                    homeView.exitApp();
                    return;
                } else if (item.getLabel().equals("显示")) {
                    Platform.runLater(new Runnable() {
                        public void run() {
                            stage.show();
                        }
                    });
                } else if (item.getLabel().equals("最小化")) {
                    Platform.runLater(new Runnable() {
                        public void run() {
                            stage.hide();
                        }
                    });
                } else if (item.getLabel().equals("上一首")) {
                    homeView.playManager.playLast();

                } else if (item.getLabel().equals("暂停/播放")) {
                    homeView.onPlayClick();

                } else if (item.getLabel().equals("下一首")) {
                    homeView.playManager.playNext();
                }
            }
        };
        openItem.addActionListener(acl);
        quitItem.addActionListener(acl);
        hideItem.addActionListener(acl);
        last.addActionListener(acl);
        stop.addActionListener(acl);
        next.addActionListener(acl);
        popupMenu.add(last);
        popupMenu.add(stop);
        popupMenu.add(next);
        popupMenu.addSeparator();
        popupMenu.add(openItem);
        popupMenu.add(hideItem);
        popupMenu.add(quitItem);

        try {
            SystemTray tray = SystemTray.getSystemTray();
            BufferedImage image = ImageIO.read(Main.class.getClass()
                    .getResourceAsStream("/res/icon.png"));
            trayIcon = new TrayIcon(image, APP_NAME, popupMenu);
            trayIcon.setToolTip(APP_NAME);
            tray.add(trayIcon);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
