package com.jw.cameralibrary.listener

import android.graphics.Bitmap

/**
 * =====================================
 * 作    者: 陈嘉桐
 * 版    本：1.1.4
 * 创建日期：2017/4/26
 * 描    述：
 * =====================================
 */
interface JCameraListener {

    fun captureSuccess(bitmap: Bitmap)

    fun recordSuccess(url: String, firstFrame: Bitmap, duration: Long)

    fun captureEdit(bitmap: Bitmap)

    fun recordEdit(url: String, firstFrame: Bitmap, duration: Long)
}
