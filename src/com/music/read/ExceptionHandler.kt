package com.music.read

import javafx.scene.control.Alert
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority

import java.io.PrintWriter
import java.io.StringWriter

/**
 * Created by xupanpan on 31/10/2017.
 */
class ExceptionHandler : Thread.UncaughtExceptionHandler {
    override fun uncaughtException(t: Thread, e: Throwable) {
        val alert = Alert(Alert.AlertType.ERROR)
        alert.headerText = "程序异常！"
        alert.contentText = e.toString()

        val sw = StringWriter()
        val pw = PrintWriter(sw)
        e.printStackTrace(pw)
        val exceptionText = sw.toString()
        val label = Label("The exception stacktrace was:")

        val textArea = TextArea(exceptionText)
        textArea.isEditable = false
        textArea.isWrapText = true

        textArea.maxWidth = java.lang.Double.MAX_VALUE
        textArea.maxHeight = java.lang.Double.MAX_VALUE
        GridPane.setVgrow(textArea, Priority.ALWAYS)
        GridPane.setHgrow(textArea, Priority.ALWAYS)

        val expContent = GridPane()
        expContent.maxWidth = java.lang.Double.MAX_VALUE
        expContent.add(label, 0, 0)
        expContent.add(textArea, 0, 1)
        alert.dialogPane.expandableContent = expContent

        e.printStackTrace()
        alert.showAndWait()
        //System.exit(0);
    }
}
