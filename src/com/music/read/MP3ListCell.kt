package com.music.read

import com.jfoenix.controls.JFXCheckBox
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.layout.HBox
import javafx.scene.paint.Color

import java.io.File
import java.io.IOException

/**
 * Created by xupanpan on 09/08/2017.
 */
class MP3ListCell(private val homeView: HomeView) : ListCell<MP3Info>() {
    private val hBox = createView()
    private var cb: JFXCheckBox? = null
    private var index: Label? = null
    private var name: Label? = null
    private var title: Label? = null
    private var artist: Label? = null
    private var album: Label? = null
    private var time: Label? = null
    private val contextMenu = cm

    private val listener = ChangeListener<Boolean> { observable, oldValue, newValue ->
        if (newValue) {
            setContextMenu(null)
        } else {
            setContextMenu(contextMenu)
        }
    }

    private val cm: ContextMenu
        get() {
            val contextMenu = ContextMenu()
            val playItem = MenuItem()
            playItem.text = "播放"

            playItem.onAction = EventHandler {
                val selectedItem = listView.selectionModel.selectedItem
                homeView.playManager.play(selectedItem)
            }

            val codeItem = MenuItem()
            codeItem.text = "乱码处理"
            codeItem.onAction = EventHandler {
                val selectedItem = listView.selectionModel.selectedItem
                homeView.musicParser.executList(selectedItem, true)
            }
            val deleteItem = MenuItem()
            deleteItem.text = "删除"
            deleteItem.onAction = EventHandler {
                val selectedItem = listView.selectionModel.selectedItem
                if (selectedItem.isPlaying) {
                    homeView.playManager.closePlay()
                    homeView.setCurrentPlayTitle(Main.APP_NAME)
                }
                DataManager.instans.removeMP3Info(selectedItem)
            }
            val fileItem = MenuItem()
            fileItem.text = "查看文件"
            fileItem.onAction = EventHandler {
                val selectedItem = listView.selectionModel.selectedItem

                val musicFile = selectedItem.musicFile ?: return@EventHandler
                val parentFile = musicFile.parentFile

                try {
                    java.awt.Desktop.getDesktop().open(File(parentFile, "/"))
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            contextMenu.items.addAll(playItem, codeItem, deleteItem, fileItem)

            return contextMenu
        }

    init {
        emptyProperty().addListener(listener)
    }

    override fun updateItem(item: MP3Info?, empty: Boolean) {
        super.updateItem(item, empty)

        if (!empty && item != null) {
            updateView(item)
            graphic = hBox
        } else {
            graphic = null
        }

    }


    fun createView(): HBox {
        val itemView = HBox(10.0)
        itemView.alignment = Pos.CENTER_LEFT
        cb = JFXCheckBox()
        index = Label()
        index!!.prefWidth = 25.0

        name = Label()
        name!!.isWrapText = true
        name!!.prefWidth = 200.0

        title = Label()
        title!!.isWrapText = true
        title!!.prefWidth = 140.0
        artist = Label()
        artist!!.isWrapText = true
        artist!!.prefWidth = 80.0
        album = Label()
        album!!.isWrapText = true
        album!!.prefWidth = 160.0
        time = Label()
        time!!.isWrapText = true
        time!!.prefWidth = 40.0
        val s1 = Separator(Orientation.VERTICAL)
        val s2 = Separator(Orientation.VERTICAL)
        val s3 = Separator(Orientation.VERTICAL)
        val s4 = Separator(Orientation.VERTICAL)
        itemView.children.addAll(cb, index, name, s1, title, s2, artist, s3, album, s4, time)
        return itemView

    }

    private fun updateView(info: MP3Info) {

        cb!!.isSelected = info.isChecked
        cb!!.selectedProperty().addListener { observable, oldValue, newValue -> info.isChecked = newValue!! }
        index!!.text = (getIndex() + 1).toString()
        name!!.text = info.fileName
        title!!.text = info.title
        artist!!.text = info.artist
        album!!.text = info.album
        time!!.text = Utils.getMusicTime(info.time)

        if (info.isPlaying) {
            setColorPlaying(index!!)
            setColorPlaying(name!!)
            setColorPlaying(title!!)
            setColorPlaying(artist!!)
            setColorPlaying(album!!)
            setColorPlaying(time!!)
        } else {
            setColorDef(index!!)
            setColorDef(name!!)
            setColorDef(title!!)
            setColorDef(artist!!)
            setColorDef(album!!)
            setColorDef(time!!)
        }
    }


    private fun setColorPlaying(label: Label) {
        label.textFill = Color.LIGHTSKYBLUE
    }

    private fun setColorDef(label: Label) {
        label.textFill = Color.BLACK
    }


}
