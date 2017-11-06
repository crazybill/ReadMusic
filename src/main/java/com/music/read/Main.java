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
    private TrayIcon trayIcon;

    public static void main(String[] args) {
        launch(args);
    }

    public void start(final Stage primaryStage) throws Exception {
        enableTray(primaryStage);


        HomeView homeView = new HomeView(primaryStage);
        homeView.init();
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
    }


    private void enableTray(final Stage stage) {
        PopupMenu popupMenu = new PopupMenu();
        java.awt.MenuItem openItem = new java.awt.MenuItem("显示");
        java.awt.MenuItem hideItem = new java.awt.MenuItem("最小化");
        java.awt.MenuItem quitItem = new java.awt.MenuItem("退出");

        ActionListener acl = new ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                java.awt.MenuItem item = (java.awt.MenuItem) e.getSource();
                Platform.setImplicitExit(false); //多次使用显示和隐藏设置false

                if (item.getLabel().equals("退出")) {
                    SystemTray.getSystemTray().remove(trayIcon);
                    Platform.exit();
                    return;
                }
                if (item.getLabel().equals("显示")) {
                    Platform.runLater(new Runnable() {
                        public void run() {
                            stage.show();
                        }
                    });
                }
                if (item.getLabel().equals("最小化")) {
                    Platform.runLater(new Runnable() {
                        public void run() {
                            stage.hide();
                        }
                    });
                }
            }
        };
        openItem.addActionListener(acl);
        quitItem.addActionListener(acl);
        hideItem.addActionListener(acl);
        popupMenu.add(openItem);
        popupMenu.add(hideItem);
        popupMenu.add(quitItem);
        try {
            SystemTray tray = SystemTray.getSystemTray();
            BufferedImage image = ImageIO.read(Main.class.getClass()
                    .getResourceAsStream("/res/icon.png"));
            trayIcon = new TrayIcon(image, "Music助手", popupMenu);
            trayIcon.setToolTip("Music助手");
            tray.add(trayIcon);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
