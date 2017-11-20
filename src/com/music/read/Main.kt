package com.music.read

import javafx.application.Application
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.stage.Stage
import javafx.stage.WindowEvent

import javax.imageio.ImageIO
import java.awt.*
import java.awt.event.ActionListener
import java.awt.image.BufferedImage

/**
 * Created by xupanpan on 12/10/2017.
 */
class Main : Application() {
    private var trayIcon: TrayIcon? = null
    private var homeView: HomeView? = null

    @Throws(Exception::class)
    override fun start(primaryStage: Stage) {
        enableTray(primaryStage)
        homeView = HomeView(primaryStage)
        homeView!!.init()
        Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler())
    }


    private fun enableTray(stage: Stage) {
        val popupMenu = PopupMenu()
        val openItem = java.awt.MenuItem("显示")
        val hideItem = java.awt.MenuItem("最小化")
        val quitItem = java.awt.MenuItem("退出")
        val last = MenuItem("上一首")
        val stop = MenuItem("暂停/播放")
        val next = MenuItem("下一首")

        val acl = ActionListener { e ->
            val item = e.source as java.awt.MenuItem
            Platform.setImplicitExit(false) //多次使用显示和隐藏设置false

            if (item.label == "退出") {
                SystemTray.getSystemTray().remove(trayIcon)
                homeView!!.exitApp()
                return@ActionListener
            } else if (item.label == "显示") {
                Platform.runLater { stage.show() }
            } else if (item.label == "最小化") {
                Platform.runLater { stage.hide() }
            } else if (item.label == "上一首") {
                homeView!!.playManager.playLast()

            } else if (item.label == "暂停/播放") {
                homeView!!.onPlayClick()

            } else if (item.label == "下一首") {
                homeView!!.playManager.playNext()
            }
        }
        openItem.addActionListener(acl)
        quitItem.addActionListener(acl)
        hideItem.addActionListener(acl)
        last.addActionListener(acl)
        stop.addActionListener(acl)
        next.addActionListener(acl)
        popupMenu.add(last)
        popupMenu.add(stop)
        popupMenu.add(next)
        popupMenu.addSeparator()
        popupMenu.add(openItem)
        popupMenu.add(hideItem)
        popupMenu.add(quitItem)

        try {
            val tray = SystemTray.getSystemTray()
            val image = ImageIO.read(Main::class.java!!.javaClass
                    .getResourceAsStream("/res/icon.png"))
            trayIcon = TrayIcon(image, APP_NAME, popupMenu)
            trayIcon!!.toolTip = APP_NAME
            tray.add(trayIcon!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    companion object {
        val APP_NAME = "MusicOcean"
        val APP_VERSION = "1.5"

        @JvmStatic
        fun main(args: Array<String>) {
            Application.launch(*args)
        }
    }


}
