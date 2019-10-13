package com.jw.shotRecord.state

import android.view.Surface
import android.view.SurfaceHolder

import com.jw.shotRecord.CameraInterface

/**
 * =====================================
 * 作    者: 陈嘉桐
 * 版    本：1.1.4
 * 创建日期：2017/9/8
 * 描    述：
 * =====================================
 */
interface State {

    fun start(holder: SurfaceHolder, screenProp: Float)

    fun stop()

    fun foucs(x: Float, y: Float, callback: CameraInterface.FocusCallback)

    fun swtich(holder: SurfaceHolder, screenProp: Float)

    fun restart()

    fun capture()

    fun record(surface: Surface, screenProp: Float)

    fun stopRecord(isShort: Boolean, time: Long)

    fun cancle(holder: SurfaceHolder, screenProp: Float)

    fun confirm()

    fun zoom(zoom: Float, type: Int)

    fun flash(mode: String)
}
