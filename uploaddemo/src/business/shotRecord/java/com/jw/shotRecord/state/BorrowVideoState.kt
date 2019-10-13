package com.jw.shotRecord.state

import android.view.Surface
import android.view.SurfaceHolder

import com.jw.shotRecord.CameraInterface
import com.jw.shotRecord.JCameraView
import com.jw.shotRecord.util.LogUtil

/**
 * =====================================
 * 作    者: 陈嘉桐
 * 版    本：1.1.4
 * 创建日期：2017/9/8
 * 描    述：
 * =====================================
 */
class BorrowVideoState(private val machine: CameraMachine) : State {
    private val TAG = "BorrowVideoState"

    override fun start(holder: SurfaceHolder, screenProp: Float) {
        CameraInterface.getInstance().doStartPreview(holder, screenProp)
        machine.state = machine.previewState
    }

    override fun stop() {

    }

    override fun foucs(x: Float, y: Float, callback: CameraInterface.FocusCallback) {

    }


    override fun swtich(holder: SurfaceHolder, screenProp: Float) {

    }

    override fun restart() {

    }

    override fun capture() {

    }

    override fun record(surface: Surface, screenProp: Float) {

    }

    override fun stopRecord(isShort: Boolean, time: Long) {

    }

    override fun cancle(holder: SurfaceHolder, screenProp: Float) {
        machine.view.resetState(JCameraView.TYPE_VIDEO)
        machine.state = machine.previewState
    }

    override fun confirm() {
        machine.view.confirmState(JCameraView.TYPE_VIDEO)
        machine.state = machine.previewState
    }

    override fun zoom(zoom: Float, type: Int) {
        LogUtil.i(TAG, "zoom")
    }

    override fun flash(mode: String) {

    }
}
