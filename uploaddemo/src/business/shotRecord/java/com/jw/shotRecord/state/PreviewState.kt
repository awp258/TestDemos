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
 * 描    述：空闲状态
 * =====================================
 */
internal class PreviewState(private val machine: CameraMachine) : State {

    override fun start(holder: SurfaceHolder, screenProp: Float) {
        CameraInterface.getInstance().doStartPreview(holder, screenProp)
    }

    override fun stop() {
        CameraInterface.getInstance().doStopPreview()
    }


    override fun foucs(x: Float, y: Float, callback: CameraInterface.FocusCallback) {
        LogUtil.i("preview state foucs")
        if (machine.view.handlerFoucs(x, y)) {
            CameraInterface.getInstance().handleFocus(machine.context, x, y, callback)
        }
    }

    override fun swtich(holder: SurfaceHolder, screenProp: Float) {
        CameraInterface.getInstance().switchCamera(holder, screenProp)
    }

    override fun restart() {

    }

    override fun capture() {
        CameraInterface.getInstance().takePicture { bitmap, isVertical ->
            machine.view.showPicture(bitmap, isVertical)
            machine.state = machine.borrowPictureState
            LogUtil.i("capture")
        }
    }

    override fun record(surface: Surface, screenProp: Float) {
        CameraInterface.getInstance().startRecord(surface, screenProp, null)
    }

    override fun stopRecord(isShort: Boolean, time: Long) {
        CameraInterface.getInstance().stopRecord(isShort) { url, firstFrame ->
            if (isShort) {
                machine.view.resetState(JCameraView.TYPE_SHORT)
            } else {
                machine.view.playVideo(firstFrame, url)
                machine.state = machine.borrowVideoState
            }
        }
    }

    override fun cancle(holder: SurfaceHolder, screenProp: Float) {
        LogUtil.i("浏览状态下,没有 cancle 事件")
    }

    override fun confirm() {
        LogUtil.i("浏览状态下,没有 confirm 事件")
    }

    override fun zoom(zoom: Float, type: Int) {
        LogUtil.i(TAG, "zoom")
        CameraInterface.getInstance().setZoom(zoom, type)
    }

    override fun flash(mode: String) {
        CameraInterface.getInstance().setFlashMode(mode)
    }

    companion object {
        val TAG = "PreviewState"
    }
}
