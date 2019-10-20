package com.jw.library.utils

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable

object CornerUtils {

    fun cornerDrawable(bgColor: Int, cornerradius: Float): Drawable {
        val bg = GradientDrawable()
        bg.cornerRadius = cornerradius
        bg.setColor(bgColor)
        return bg
    }

    fun cornerDrawable(bgColor: Int, cornerradius: FloatArray): Drawable {
        val bg = GradientDrawable()
        bg.cornerRadii = cornerradius
        bg.setColor(bgColor)
        return bg
    }

    fun cornerDrawable(
        bgColor: Int,
        cornerradius: FloatArray,
        borderwidth: Int,
        bordercolor: Int
    ): Drawable {
        val bg = GradientDrawable()
        bg.cornerRadii = cornerradius
        bg.setStroke(borderwidth, bordercolor)
        bg.setColor(bgColor)
        return bg
    }

    fun btnSelector(
        radius: Float,
        normalColor: Int,
        pressColor: Int,
        enableColor: Int,
        position: Int
    ): StateListDrawable {
        val bg = StateListDrawable()
        var normal: Drawable? = null
        var pressed: Drawable? = null
        var enabled: Drawable? = null
        if (position == 0) {
            normal = cornerDrawable(
                normalColor,
                floatArrayOf(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, radius, radius)
            )
            pressed = cornerDrawable(
                pressColor,
                floatArrayOf(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, radius, radius)
            )
            enabled = cornerDrawable(
                enableColor,
                floatArrayOf(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, radius, radius)
            )
        } else if (position == 1) {
            normal = cornerDrawable(
                normalColor,
                floatArrayOf(0.0f, 0.0f, 0.0f, 0.0f, radius, radius, 0.0f, 0.0f)
            )
            pressed = cornerDrawable(
                pressColor,
                floatArrayOf(0.0f, 0.0f, 0.0f, 0.0f, radius, radius, 0.0f, 0.0f)
            )
            enabled = cornerDrawable(
                enableColor,
                floatArrayOf(0.0f, 0.0f, 0.0f, 0.0f, radius, radius, 0.0f, 0.0f)
            )
        } else if (position == -1) {
            normal = cornerDrawable(
                normalColor,
                floatArrayOf(0.0f, 0.0f, 0.0f, 0.0f, radius, radius, radius, radius)
            )
            pressed = cornerDrawable(
                pressColor,
                floatArrayOf(0.0f, 0.0f, 0.0f, 0.0f, radius, radius, radius, radius)
            )
            enabled = cornerDrawable(
                enableColor,
                floatArrayOf(0.0f, 0.0f, 0.0f, 0.0f, radius, radius, radius, radius)
            )
        } else if (position == -2) {
            normal = cornerDrawable(normalColor, radius)
            pressed = cornerDrawable(pressColor, radius)
            enabled = cornerDrawable(enableColor, radius)
        }

        bg.addState(intArrayOf(-16842919), normal)
        bg.addState(intArrayOf(16842919), pressed)
        bg.addState(intArrayOf(16842910), enabled)
        return bg
    }

    fun listItemSelector(
        radius: Float,
        normalColor: Int,
        pressColor: Int,
        isLastPostion: Boolean
    ): StateListDrawable {
        val bg = StateListDrawable()
        var normal: Drawable? = null
        var pressed: Drawable? = null
        if (!isLastPostion) {
            normal = ColorDrawable(normalColor)
            pressed = ColorDrawable(pressColor)
        } else {
            normal = cornerDrawable(
                normalColor,
                floatArrayOf(0.0f, 0.0f, 0.0f, 0.0f, radius, radius, radius, radius)
            )
            pressed = cornerDrawable(
                pressColor,
                floatArrayOf(0.0f, 0.0f, 0.0f, 0.0f, radius, radius, radius, radius)
            )
        }

        bg.addState(intArrayOf(-16842919), normal as Drawable?)
        bg.addState(intArrayOf(16842919), pressed as Drawable?)
        return bg
    }

    fun listItemSelector(
        radius: Float,
        normalColor: Int,
        pressColor: Int,
        itemTotalSize: Int,
        itemPosition: Int
    ): StateListDrawable {
        val bg = StateListDrawable()
        var normal: Drawable? = null
        var pressed: Drawable? = null
        if (itemPosition == 0 && itemPosition == itemTotalSize - 1) {
            normal = cornerDrawable(
                normalColor,
                floatArrayOf(radius, radius, radius, radius, radius, radius, radius, radius)
            )
            pressed = cornerDrawable(
                pressColor,
                floatArrayOf(radius, radius, radius, radius, radius, radius, radius, radius)
            )
        } else if (itemPosition == 0) {
            normal = cornerDrawable(
                normalColor,
                floatArrayOf(radius, radius, radius, radius, 0.0f, 0.0f, 0.0f, 0.0f)
            )
            pressed = cornerDrawable(
                pressColor,
                floatArrayOf(radius, radius, radius, radius, 0.0f, 0.0f, 0.0f, 0.0f)
            )
        } else if (itemPosition == itemTotalSize - 1) {
            normal = cornerDrawable(
                normalColor,
                floatArrayOf(0.0f, 0.0f, 0.0f, 0.0f, radius, radius, radius, radius)
            )
            pressed = cornerDrawable(
                pressColor,
                floatArrayOf(0.0f, 0.0f, 0.0f, 0.0f, radius, radius, radius, radius)
            )
        } else {
            normal = ColorDrawable(normalColor)
            pressed = ColorDrawable(pressColor)
        }

        bg.addState(intArrayOf(-16842919), normal as Drawable?)
        bg.addState(intArrayOf(16842919), pressed as Drawable?)
        return bg
    }
}
