package com.music.read

import javafx.geometry.Insets
import javafx.scene.control.Button
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.paint.Paint

import java.awt.*
import java.net.URI

/**
 * Created by xupanpan on 3/17/17.
 */
object FxViewUtil {


    val defBackground: Background
        get() = getBackground(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)


    val defBorder: Border
        get() = getBorder(Color.WHITE, 5.0, 1.0)

    fun getBackground(color: Paint, corner: Int): Background {

        return getBackground(color, CornerRadii(corner.toDouble()), Insets.EMPTY)
    }


    @JvmOverloads
    fun getBackground(color: Paint, corner: CornerRadii = CornerRadii.EMPTY, insets: Insets = Insets.EMPTY): Background {
        return Background(BackgroundFill(color, corner, insets))
    }

    @JvmOverloads
    fun getBorder(color: Paint, corner: Double = 5.0, borderWidths: Double = 1.0): Border {

        return getBorder(color, BorderStrokeStyle.SOLID, CornerRadii(corner), BorderWidths(borderWidths))
    }


    fun getBorder(color: Paint, style: BorderStrokeStyle, corner: CornerRadii, borderWidths: BorderWidths): Border {
        return Border(BorderStroke(color, style, corner, borderWidths))
    }


    fun setDefTextFileStyle(textField: Region) {
        textField.background = FxViewUtil.defBackground
        textField.border = FxViewUtil.getBorder(Color.WHITE, 3.0, 1.0)
    }

    fun setDefButtonStyle(btn: Button) {
        setDefTextFileStyle(btn)
        btn.textFill = Color.WHITE
    }

    fun setButtonBlueStyle(btn: Button) {
        btn.background = FxViewUtil.defBackground
        btn.border = FxViewUtil.getBorder(Color.LIGHTSKYBLUE, 3.0, 1.0)
        btn.textFill = Color.LIGHTSKYBLUE
    }


    fun openSystemBrowser(url: String) {

        if (Desktop.isDesktopSupported()) {
            try {
                val uri = URI.create(url)
                val desktop = Desktop.getDesktop()
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    desktop.browse(uri)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

}
