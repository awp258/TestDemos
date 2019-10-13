package com.jw.shotRecord.listener

/**
 * create by CJT2325
 * 445263848@qq.com.
 */

interface CaptureListener {
    fun takePictures()

    fun recordShort(time: Long)

    fun recordStart()

    fun recordEnd(time: Long)

    fun recordZoom(zoom: Float)

    fun recordError()

    fun takeTypeChange(takeType: Int)
}
