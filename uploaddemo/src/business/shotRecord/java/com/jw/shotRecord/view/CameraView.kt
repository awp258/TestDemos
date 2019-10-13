package com.jw.shotRecord.view

import android.graphics.Bitmap

/**
 * =====================================
 * 作    者: 陈嘉桐
 * 版    本：1.1.4
 * 创建日期：2017/9/8
 * 描    述：
 * =====================================
 */
interface CameraView {
    fun resetState(type: Int)

    fun confirmState(type: Int)

    fun showPicture(bitmap: Bitmap, isVertical: Boolean)

    fun playVideo(firstFrame: Bitmap, url: String)

    fun stopVideo()

    fun setTip(tip: String)

    fun startPreviewCallback()

    fun handlerFoucs(x: Float, y: Float): Boolean
}
