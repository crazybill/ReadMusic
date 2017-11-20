package com.music.read

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXCheckBox
import com.jfoenix.controls.JFXListView
import com.jfoenix.controls.JFXTextField
import com.sun.javafx.scene.control.skin.ListViewSkin
import com.sun.javafx.scene.control.skin.VirtualFlow
import com.sun.org.apache.bcel.internal.generic.DADD
import javafx.application.Platform
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.event.EventType
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.*
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.stage.FileChooser
import javafx.stage.Stage
import javafx.stage.WindowEvent
import javafx.util.Callback

import java.io.File
import java.io.InputStream
import java.util.Timer
import java.util.TimerTask

/**
 * Created by xupanpan on 03/11/2017.
 */
class HomeView(var primaryStage: Stage) {

    var listView: JFXListView<MP3Info>

    var isSelectAll = true
    var rootView: VBox
    var nameFirst = true
    var splitText: javafx.scene.control.TextField
    private var deleteBeforText: javafx.scene.control.TextField? = null
    private var deleteAfterText: javafx.scene.control.TextField? = null
    private var text1: javafx.scene.control.TextField? = null
    private var text2: javafx.scene.control.TextField? = null
    private var borderPane: BorderPane? = null

    private var isShowErrorCodeSettingView = false
    private var isShowReplaceSettingView = false
    private var isShowAddSettingView = false
    private var menuBar: MenuBar? = null

    var musicParser: MusicFileParser
    var playManager: PlayManager
    var fileNameEditer: FileNameEditer
    private var playConfig: Config? = null
    private var image: Image? = null


    private var errorCodeSetting: HBox? = null
    private var replaceSetting: HBox? = null
    private var addSetting: HBox? = null
    private var vSeparator: Separator? = null
    private var vSeparator1: Separator? = null
    private var hSeparator: Separator? = null
    private var headSettingView: HBox? = null


    private var first = 0
    private var last = 0


    private val headTitle: HBox
        get() {
            val itemView = HBox(10.0)
            itemView.alignment = Pos.CENTER_LEFT
            itemView.padding = Insets(5.0, 9.0, 5.0, 9.0)
            val cb = JFXCheckBox()
            cb.isSelected = isSelectAll
            cb.selectedProperty().addListener { observable, oldValue, newValue ->
                isSelectAll = newValue!!
                DataManager.instans.setAllCheckStatus(newValue)
                listView.setItems(null)
                listView.setItems(DataManager.instans.list)
            }

            val name = Label("文件名称")
            name.prefWidth = 200.0
            val index = Label("#")
            index.prefWidth = 25.0

            val title = Label("标题")
            title.prefWidth = 140.0
            val artist = Label("歌手")
            artist.prefWidth = 80.0
            val album = Label("专辑名称")
            album.prefWidth = 160.0
            val time = Label("时长")
            time.prefWidth = 40.0

            val s2 = Separator(Orientation.VERTICAL)
            val s3 = Separator(Orientation.VERTICAL)
            val s4 = Separator(Orientation.VERTICAL)
            val s5 = Separator(Orientation.VERTICAL)
            itemView.children.addAll(cb, index, name, s2, title, s3, artist, s4, album, s5, time)
            initPlayControlView(itemView)
            return itemView
        }

    var playTimeLabel: Label
    var playStop: Button

    private var imageView: ImageView? = null

    private val startImage: ImageView?
        get() {
            if (imageView == null) {
                try {
                    val resourceAsStream = HomeView::class.java!!.javaClass.getResourceAsStream("/res/ic_live_play.png")
                    val im = Image(resourceAsStream)
                    imageView = ImageView(im)
                } catch (e: Exception) {
                }

            }
            return imageView
        }

    private var imageView1: ImageView? = null

    private val stopImage: ImageView?
        get() {
            if (imageView1 == null) {
                try {
                    val resourceAsStream = HomeView::class.java!!.javaClass.getResourceAsStream("/res/ic_live_suspend.png")
                    val im = Image(resourceAsStream)
                    imageView1 = ImageView(im)
                } catch (e: Exception) {
                }

            }

            return imageView1
        }

    init {

        try {
            image = Image(this.javaClass.getResourceAsStream("/res/icon.png"))
            primaryStage.icons.add(image)
        } catch (e: Exception) {
        }

        primaryStage.onCloseRequest = EventHandler { exitApp() }

    }


    fun exitApp() {

        val config = Config()
        config.isCheckedAll = isSelectAll
        config.playType = playManager.playType

        PlayListManager.savePlayConfig(config)
        PlayListManager.savePlayList(DataManager.instans.list)
        System.exit(0)
    }


    fun init() {
        musicParser = MusicFileParser(this)
        playManager = PlayManager(this)
        fileNameEditer = FileNameEditer(this)
        setCurrentPlayTitle(Main.APP_NAME)

        playConfig = PlayListManager.playConfig
        if (playConfig != null) {
            isSelectAll = playConfig!!.isCheckedAll
            playManager.playType = playConfig!!.playType
        }

        borderPane = BorderPane()
        val scene = Scene(borderPane!!, WIDTH, HIGTH)
        primaryStage.scene = scene
        primaryStage.show()

        initMenuBar()
        initView()
        borderPane!!.center = rootView

        initData()
    }

    private fun initData() {
        Thread(Runnable {
            val historyPlayList = PlayListManager.historyPlayList
            if (historyPlayList != null && !historyPlayList!!.isEmpty()) {
                DataManager.instans.add2List(historyPlayList)
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        Platform.runLater { playManager.stopAndStart() }
                    }
                }, 3000)
            }
        }).start()
    }

    fun setCurrentPlayTitle(title: String) {
        primaryStage.title = title
    }

    private fun initMenuBar() {

        menuBar = MenuBar()
        menuBar!!.isUseSystemMenuBar = true
        val fileMenu = Menu("文件")

        val openItem = MenuItem("加载音乐")
        openItem.onAction = EventHandler {
            val chooser = FileChooser()
            chooser.extensionFilters.addAll(
                    FileChooser.ExtensionFilter("All Images", "*.*")
            )
            val files = chooser.showOpenMultipleDialog(primaryStage)
            if (files != null) {
                musicParser.loadMp3Data(files)
            }
        }

        val clearItem = MenuItem("删除选中")
        clearItem.onAction = EventHandler {
            if (DataManager.instans.removeSelected()) {
                setCurrentPlayTitle(Main.APP_NAME)
                playManager.closePlay()
            }
        }

        val clearAllItem = MenuItem("清空列表")
        clearAllItem.onAction = EventHandler {
            DataManager.instans.clearList()
            setCurrentPlayTitle(Main.APP_NAME)
            playManager.closePlay()
            listView.userData = null
        }

        val exitItem = MenuItem("退出")
        exitItem.onAction = EventHandler { exitApp() }

        fileMenu.items.addAll(openItem, clearItem, clearAllItem, exitItem)


        val editMenu = Menu("编辑")

        val codeErrorItem = CheckMenuItem("乱码分析")
        codeErrorItem.isSelected = isShowErrorCodeSettingView
        codeErrorItem.selectedProperty().addListener { observable, oldValue, newValue ->
            isShowErrorCodeSettingView = newValue!!
            checkSettingView()
        }


        val replaceNameItem = CheckMenuItem("批量替换名称字符")
        replaceNameItem.isSelected = isShowReplaceSettingView
        replaceNameItem.selectedProperty().addListener { observable, oldValue, newValue ->
            isShowReplaceSettingView = newValue!!
            checkSettingView()
        }

        val addNameItem = CheckMenuItem("批量添加名称字符")
        addNameItem.isSelected = isShowAddSettingView
        addNameItem.selectedProperty().addListener { observable, oldValue, newValue ->
            isShowAddSettingView = newValue!!
            checkSettingView()
        }


        editMenu.items.addAll(codeErrorItem, replaceNameItem, addNameItem)


        val helpMenu = Menu("帮助")
        val useInfoItem = MenuItem("使用说明")
        useInfoItem.onAction = EventHandler { }
        val aboutItem = MenuItem("关于")
        aboutItem.onAction = EventHandler {
            val alert = Alert(Alert.AlertType.INFORMATION)
            alert.headerText = "关于" + Main.APP_NAME
            alert.contentText = "当前版本：" + Main.APP_VERSION
            alert.show()
        }

        helpMenu.items.addAll(useInfoItem, aboutItem)

        menuBar!!.menus.addAll(fileMenu, editMenu, helpMenu)

        initPlayerView()

        borderPane!!.top = menuBar
    }


    private fun initPlayerView() {

        val playMenu = Menu("play")

        val lastItem = MenuItem("上一曲")
        lastItem.onAction = EventHandler { playManager.playLast() }

        val playCloseItem = MenuItem("播放/停止")
        playCloseItem.onAction = EventHandler { onPlayClick() }

        val nextItem = MenuItem("下一曲")
        nextItem.onAction = EventHandler { playManager.playNext() }

        val toggleGroup = ToggleGroup()

        val singleItem = RadioMenuItem("单曲循环")
        singleItem.onAction = EventHandler { playManager.playType = PlayManager.PlayType.SINGLE }

        singleItem.toggleGroup = toggleGroup

        val shunItem = RadioMenuItem("顺序播放")
        shunItem.onAction = EventHandler { playManager.playType = PlayManager.PlayType.RECYCLE }
        shunItem.toggleGroup = toggleGroup

        val suijiItem = RadioMenuItem("随机播放")
        suijiItem.onAction = EventHandler { playManager.playType = PlayManager.PlayType.RADOM }
        suijiItem.toggleGroup = toggleGroup

        if (playConfig != null) {
            when (playConfig!!.playType) {
                PlayManager.PlayType.SINGLE -> singleItem.isSelected = true
                PlayManager.PlayType.RECYCLE -> shunItem.isSelected = true
                PlayManager.PlayType.RADOM -> suijiItem.isSelected = true
            }
        }

        playMenu.items.addAll(lastItem, playCloseItem, nextItem, SeparatorMenuItem(), singleItem, shunItem, suijiItem)
        menuBar!!.menus.addAll(playMenu)

    }


    private fun getErrorCodeSetting(): HBox {

        val mHBox = HBox(5.0)
        mHBox.alignment = Pos.CENTER_LEFT
        val splitLabel = Label("文件分隔线:")

        splitText = JFXTextField(DEFULT_PRE)
        splitText.alignment = Pos.CENTER
        splitText.prefWidth = 40.0


        val headBox = JFXCheckBox("歌手名在前")
        headBox.isSelected = nameFirst
        headBox.selectedProperty().addListener { observable, oldValue, newValue -> nameFirst = newValue!! }


        val btn_ana = JFXButton("开始分析")
        btn_ana.buttonType = JFXButton.ButtonType.RAISED
        btn_ana.onAction = EventHandler { musicParser.executList() }

        mHBox.children.addAll(splitLabel, splitText,
                headBox, btn_ana)

        return mHBox
    }


    private fun getReplaceSetting(): HBox {

        val mHBox = HBox(5.0)
        mHBox.alignment = Pos.CENTER_LEFT
        val petchDeletLabel = Label("批量改文件名:")

        deleteBeforText = JFXTextField()
        deleteBeforText!!.promptText = "改前"
        deleteBeforText!!.prefWidth = 40.0

        deleteAfterText = JFXTextField()
        deleteAfterText!!.promptText = "改后"
        deleteAfterText!!.prefWidth = 40.0

        val btn_delete = JFXButton("开始更改")
        btn_delete.buttonType = JFXButton.ButtonType.RAISED
        btn_delete.onAction = EventHandler {
            val textBefor = deleteBeforText!!.text
            val textAfter = deleteAfterText!!.text

            fileNameEditer.renameList(textBefor, textAfter)
        }


        mHBox.children.addAll(petchDeletLabel, deleteBeforText,
                deleteAfterText, btn_delete)

        return mHBox
    }


    private fun getAddSetting(): HBox {
        val mHBox = HBox(5.0)
        mHBox.alignment = Pos.CENTER_LEFT
        val label1 = Label("在第")
        text1 = JFXTextField()
        text1!!.prefWidth = 40.0
        val label2 = Label("位前，添加")
        text2 = JFXTextField()
        text2!!.prefWidth = 40.0

        val btn_add = JFXButton("开始添加")
        btn_add.buttonType = JFXButton.ButtonType.RAISED
        btn_add.onAction = EventHandler {
            val textWei = text1!!.text
            val textStr = text2!!.text
            fileNameEditer.addStrList(textWei, textStr)
        }

        mHBox.children.addAll(label1, text1,
                label2, text2, btn_add)

        return mHBox
    }

    private fun getVSeparator(): Separator {
        return Separator(Orientation.VERTICAL)
    }

    private fun getHSeparator(): Separator {
        return Separator(Orientation.HORIZONTAL)
    }

    private fun initSettingView() {
        headSettingView = HBox()
        headSettingView!!.padding = Insets(8.0, 8.0, 8.0, 8.0)
        headSettingView!!.spacing = 8.0
        headSettingView!!.alignment = Pos.CENTER_LEFT

        errorCodeSetting = getErrorCodeSetting()
        replaceSetting = getReplaceSetting()
        addSetting = getAddSetting()

        vSeparator = getVSeparator()
        vSeparator1 = getVSeparator()
        hSeparator = getHSeparator()

        headSettingView!!.children.addAll(errorCodeSetting, vSeparator, replaceSetting, vSeparator1, addSetting)
        rootView.children.addAll(headSettingView, hSeparator)
        checkSettingView()
    }

    private fun checkSettingView() {

        errorCodeSetting!!.isVisible = isShowErrorCodeSettingView
        errorCodeSetting!!.isManaged = isShowErrorCodeSettingView

        replaceSetting!!.isVisible = isShowReplaceSettingView
        replaceSetting!!.isManaged = isShowReplaceSettingView

        addSetting!!.isVisible = isShowAddSettingView
        addSetting!!.isManaged = isShowAddSettingView

        if (isShowErrorCodeSettingView && isShowReplaceSettingView || isShowErrorCodeSettingView && isShowAddSettingView) {
            vSeparator!!.isVisible = true
            vSeparator!!.isManaged = true
        } else {
            vSeparator!!.isVisible = false
            vSeparator!!.isManaged = false
        }

        if (isShowReplaceSettingView && isShowAddSettingView) {
            vSeparator1!!.isVisible = true
            vSeparator1!!.isManaged = true
        } else {
            vSeparator1!!.isVisible = false
            vSeparator1!!.isManaged = false
        }

        if (isShowErrorCodeSettingView || isShowReplaceSettingView || isShowAddSettingView) {
            headSettingView!!.isVisible = true
            headSettingView!!.isManaged = true
            hSeparator!!.isVisible = true
            hSeparator!!.isManaged = true
        } else {
            headSettingView!!.isVisible = false
            headSettingView!!.isManaged = false
            hSeparator!!.isVisible = false
            hSeparator!!.isManaged = false
        }

    }

    private fun initView() {

        rootView = VBox()
        rootView.alignment = Pos.TOP_CENTER
        rootView.background = Background.EMPTY

        initSettingView()

        listView = JFXListView()
        listView.setItems(DataManager.instans.list)
        listView.isEditable = true
        listView.border = FxViewUtil.getBorder(Color.WHITE, 0.0, 0.0)
        listView.background = FxViewUtil.getBackground(Color.WHITE, 0)
        listView.orientation = Orientation.VERTICAL
        listView.setCellFactory { MP3ListCell(this@HomeView) }
        listView.selectionModel.selectedItemProperty().addListener { observable, oldValue, newValue -> }

        listView.onMouseClicked = EventHandler { event ->
            if (event.button == MouseButton.PRIMARY) {
                if (event.clickCount == 2) {
                    val selectedItem = listView.selectionModel.selectedItem
                    playManager.play(selectedItem)
                }
            }
        }

        VBox.setVgrow(listView, Priority.ALWAYS)

        listView.onDragOver = EventHandler { event ->
            if (event.gestureSource !== listView) {
                event.acceptTransferModes(*TransferMode.ANY)
            }
        }

        listView.onDragDropped = EventHandler { event ->
            val dragboard = event.dragboard
            val files = dragboard.files
            if (files.size > 0) {
                musicParser.loadMp3Data(files)
            }
        }

        listView.onKeyReleased = EventHandler { event ->
            val code = event.code
            if (code == KeyCode.ENTER) {
                playManager.play(listView.selectionModel.selectedIndex)
            } else if (code == KeyCode.SPACE) {
                onPlayClick()
            } else if (code == KeyCode.RIGHT) {
                playManager.playNext()
            } else if (code == KeyCode.LEFT) {
                playManager.playLast()
            }
        }

        rootView.children.addAll(headTitle, listView)
    }

    private fun getFirstAndLast() {
        if (DataManager.instans.isListEmpty) {
            return
        }
        try {
            val ts = listView.skin as ListViewSkin<*>
            val vf = ts.children[0] as VirtualFlow<*>
            first = vf.firstVisibleCell.index
            last = vf.lastVisibleCell.index
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }

    fun scrollToShow() {
        getFirstAndLast()
        val currentPlayPosition = DataManager.instans.currentPlayPosition
        if (currentPlayPosition < first || currentPlayPosition > last) {
            listView.scrollTo(currentPlayPosition)
        }
    }


    private fun showNotification() {

    }

    private fun initPlayControlView(itemView: HBox) {
        val s6 = Separator(Orientation.VERTICAL)
        playTimeLabel = Label()
        playTimeLabel.prefWidth = 45.0
        playTimeLabel.textFill = Color.LIGHTSKYBLUE
        playTimeLabel.onMouseClicked = EventHandler { event ->
            if (event.button == MouseButton.PRIMARY) {
                if (event.clickCount == 2) {

                    scrollToShow()
                }
            }
        }

        playStop = Button()
        setButtonPlay()
        playStop.background = null
        playStop.onAction = EventHandler { onPlayClick() }

        itemView.children.addAll(s6, playStop, playTimeLabel)
    }


    fun setButtonPlay() {
        val startImage = startImage
        if (startImage != null) {
            playStop.graphic = startImage
        }
    }

    fun setButtonStop() {
        val stopImage = stopImage
        if (stopImage != null) {
            playStop.graphic = stopImage
        }
    }

    fun onPlayClick() {

        if (DataManager.instans.isListEmpty) return

        if (DataManager.instans.currentPlayPosition == -1) {
            playManager.play(0)
        } else {
            playManager.stopAndStart()
        }
    }

    companion object {
        private val WIDTH = 1000.0
        private val HIGTH = 700.0
        val DEFULT_PRE = "-"
    }


}
