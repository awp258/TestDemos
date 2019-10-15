package com.jw.cameralibrary

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.os.CountDownTimer
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.jw.cameralibrary.JCameraView.shotModel
import com.jw.cameralibrary.listener.CaptureListener
import com.jw.cameralibrary.util.CheckPermission
import com.jw.cameralibrary.util.LogUtil
import com.jw.library.utils.ThemeUtils


/**
 * =====================================
 * 作    者: 陈嘉桐 445263848@qq.com
 * 版    本：1.1.4
 * 创建日期：2017/4/25
 * 描    述：拍照按钮
 * =====================================
 */
class CaptureButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var state: Int = 0              //当前按钮状态
    private var button_state: Int = 0       //按钮可执行的功能状态（拍照,录制,两者）

    private val progress_color = -0x11e951ea            //进度条颜色
    private val outside_color = -0x11232324             //外圆背景色
    private val inside_color = -0x1              //内圆背景色


    private var event_Y: Float = 0.toFloat()  //Touch_Event_Down时候记录的Y值


    private var mPaint: Paint? = null

    private var strokeWidth: Float = 0.toFloat()          //进度条宽度
    private var outside_add_size: Int = 0       //长按外圆半径变大的Size
    private var inside_reduce_size: Int = 0     //长安内圆缩小的Size

    //中心坐标
    private var center_X: Float = 0.toFloat()
    private var center_Y: Float = 0.toFloat()

    private var button_radius: Float = 0.toFloat()            //按钮半径
    private var button_outside_radius: Float = 0.toFloat()    //外圆半径
    private var button_inside_radius: Float = 0.toFloat()     //内圆半径
    private var button_size: Int = 0                //按钮大小

    private var progress: Float = 0.toFloat()         //录制视频的进度
    private var duration: Int = 0           //录制视频最大时间长度
    private var min_duration: Int = 0       //最短录制时间限制
    private var recorded_time: Int = 0      //记录当前录制的时间

    private var rectF: RectF? = null

    private var longPressRunnable: LongPressRunnable? = null    //长按后处理的逻辑Runnable
    private var captureLisenter: CaptureListener? = null        //按钮回调接口
    private var timer: RecordCountDownTimer? = null             //计时器
    private var currentTakeType =
        JCameraView.BUTTON_STATE_ONLY_CAPTURE             //计时器

    //是否空闲状态
    val isIdle: Boolean
        get() = if (state == STATE_IDLE) true else false

    init {
        val size = ThemeUtils.dip2px(context, 80f)
        this.button_size = size
        button_radius = size / 2.0f

        button_outside_radius = button_radius
        button_inside_radius = button_radius * 0.75f

        strokeWidth = (size / 15).toFloat()
        outside_add_size = 0
        inside_reduce_size = size / 8

        mPaint = Paint()
        mPaint!!.isAntiAlias = true

        progress = 0f
        longPressRunnable = LongPressRunnable()

        state =
            STATE_IDLE                //初始化为空闲状态
        button_state = JCameraView.BUTTON_STATE_BOTH  //初始化按钮为可录制可拍照
        LogUtil.i("CaptureButtom start")
        duration = 10 * 1000              //默认最长录制时间为10s
        LogUtil.i("CaptureButtom end")
        min_duration = 1500              //默认最短录制时间为1.5s

        center_X = ((button_size + outside_add_size * 2) / 2).toFloat()
        center_Y = ((button_size + outside_add_size * 2) / 2).toFloat()

        rectF = RectF(
            center_X - (button_radius + outside_add_size - strokeWidth / 2),
            center_Y - (button_radius + outside_add_size - strokeWidth / 2),
            center_X + (button_radius + outside_add_size - strokeWidth / 2),
            center_Y + (button_radius + outside_add_size - strokeWidth / 2)
        )

        timer = RecordCountDownTimer(duration.toLong(), (duration / 360).toLong())    //录制定时器
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(button_size + outside_add_size * 2, button_size + outside_add_size * 2)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mPaint!!.style = Paint.Style.FILL

        mPaint!!.color = outside_color //外圆（半透明灰色）
        canvas.drawCircle(center_X, center_Y, button_outside_radius, mPaint!!)

        mPaint!!.color = inside_color  //内圆（白色）
        canvas.drawCircle(center_X, center_Y, button_inside_radius, mPaint!!)

        //如果状态为录制状态，则绘制录制进度条
        if (state == STATE_RECORDERING) {
            mPaint!!.color = progress_color
            mPaint!!.style = Paint.Style.STROKE
            mPaint!!.strokeWidth = strokeWidth
            canvas.drawArc(rectF!!, -90f, progress, false, mPaint!!)
        }
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                LogUtil.i("state = $state")
                if (!(event.pointerCount > 1 || state != STATE_IDLE)) {
                    event_Y = event.y     //记录Y值
                    state =
                        STATE_PRESS        //修改当前状态为点击按下
                    if (shotModel == 2) {
                        //判断按钮状态是否为可录制状态
                        if (button_state == JCameraView.BUTTON_STATE_ONLY_RECORDER || button_state == JCameraView.BUTTON_STATE_BOTH)
                            postDelayed(longPressRunnable, 500)    //同时延长500启动长按后处理的逻辑Runnable
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> if (captureLisenter != null
                && state == STATE_RECORDERING
                && (button_state == JCameraView.BUTTON_STATE_ONLY_RECORDER || button_state == JCameraView.BUTTON_STATE_BOTH)
            ) {
                //记录当前Y值与按下时候Y值的差值，调用缩放回调接口
                captureLisenter!!.recordZoom(event_Y - event.y)
            }
            MotionEvent.ACTION_UP ->
                //根据当前按钮的状态进行相应的处理
                handlerUnpressByState()
        }
        return true
    }

    //当手指松开按钮时候处理的逻辑
    private fun handlerUnpressByState() {
        removeCallbacks(longPressRunnable) //移除长按逻辑的Runnable
        //根据当前状态处理
        when (state) {
            //当前是点击按下
            STATE_PRESS -> if (shotModel == 1) {
                if (currentTakeType == JCameraView.BUTTON_STATE_ONLY_CAPTURE) {
                    if (captureLisenter != null && (button_state == JCameraView.BUTTON_STATE_ONLY_CAPTURE || button_state == JCameraView.BUTTON_STATE_BOTH)) {
                        startCaptureAnimation(button_inside_radius)
                    } else {
                        state = STATE_IDLE
                    }
                } else {
                    post(longPressRunnable)
                }
            } else {
                if (captureLisenter != null && (button_state == JCameraView.BUTTON_STATE_ONLY_CAPTURE || button_state == JCameraView.BUTTON_STATE_BOTH)) {
                    startCaptureAnimation(button_inside_radius)
                } else {
                    state = STATE_IDLE
                }
            }
            //当前是长按状态
            STATE_RECORDERING -> if (shotModel == 2) {
                timer!!.cancel() //停止计时器
                recordEnd()    //录制结束
            }
        }
    }

    //录制结束
    fun recordEnd() {
        if (captureLisenter != null) {
            if (recorded_time < min_duration)
                captureLisenter!!.recordShort(recorded_time.toLong())//回调录制时间过短
            else
                captureLisenter!!.recordEnd(recorded_time.toLong())  //回调录制结束
        }
        resetRecordAnim()  //重制按钮状态
    }

    //重制状态
    private fun resetRecordAnim() {
        state = STATE_BAN
        progress = 0f       //重制进度
        invalidate()
        //还原按钮初始状态动画
        startRecordAnimation(
            button_outside_radius,
            button_radius,
            button_inside_radius,
            button_radius * 0.75f
        )
    }

    //内圆动画
    private fun startCaptureAnimation(inside_start: Float) {
        val inside_anim = ValueAnimator.ofFloat(inside_start, inside_start * 0.75f, inside_start)
        inside_anim.addUpdateListener { animation ->
            button_inside_radius = animation.animatedValue as Float
            invalidate()
        }
        inside_anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                //回调拍照接口
                captureLisenter!!.takePictures()
                state = STATE_BAN
            }
        })
        inside_anim.duration = 100
        inside_anim.start()
    }

    //内外圆动画
    private fun startRecordAnimation(
        outside_start: Float,
        outside_end: Float,
        inside_start: Float,
        inside_end: Float
    ) {
        val outside_anim = ValueAnimator.ofFloat(outside_start, outside_end)
        val inside_anim = ValueAnimator.ofFloat(inside_start, inside_end)
        //外圆动画监听
        outside_anim.addUpdateListener { animation ->
            button_outside_radius = animation.animatedValue as Float
            invalidate()
        }
        //内圆动画监听
        inside_anim.addUpdateListener { animation ->
            button_inside_radius = animation.animatedValue as Float
            invalidate()
        }
        val set = AnimatorSet()
        //当动画结束后启动录像Runnable并且回调录像开始接口
        set.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                //设置为录制状态
                if (state == STATE_LONG_PRESS) {
                    if (captureLisenter != null)
                        captureLisenter!!.recordStart()
                    state = STATE_RECORDERING
                    timer!!.start()
                }
            }
        })
        set.playTogether(outside_anim, inside_anim)
        set.duration = 100
        set.start()
    }


    //更新进度条
    private fun updateProgress(millisUntilFinished: Long) {
        recorded_time = (duration - millisUntilFinished).toInt()
        progress = 360f - millisUntilFinished / duration.toFloat() * 360f
        invalidate()
    }

    //录制视频计时器
    private inner class RecordCountDownTimer internal constructor(
        millisInFuture: Long,
        countDownInterval: Long
    ) : CountDownTimer(millisInFuture, countDownInterval) {

        override fun onTick(millisUntilFinished: Long) {
            updateProgress(millisUntilFinished)
        }

        override fun onFinish() {
            updateProgress(0)
            recordEnd()
        }
    }

    //长按线程
    private inner class LongPressRunnable : Runnable {
        override fun run() {
            state =
                STATE_LONG_PRESS   //如果按下后经过500毫秒则会修改当前状态为长按状态
            //没有录制权限
            if (CheckPermission.getRecordState() != CheckPermission.STATE_SUCCESS) {
                state = STATE_IDLE
                if (captureLisenter != null) {
                    captureLisenter!!.recordError()
                    return
                }
            }
            if (shotModel == 1) {
                if (captureLisenter != null)
                    captureLisenter!!.recordStart()
                state = STATE_RECORDERING
                timer!!.start()
            } else {
                //启动按钮动画，外圆变大，内圆缩小
                startRecordAnimation(
                    button_outside_radius,
                    button_outside_radius + outside_add_size,
                    button_inside_radius,
                    button_inside_radius - inside_reduce_size
                )
            }
        }
    }

    /**************************************************
     * 对外提供的API                     *
     */

    //设置最长录制时间
    fun setDuration(duration: Int) {
        this.duration = duration
        timer = RecordCountDownTimer(duration.toLong(), (duration / 360).toLong())    //录制定时器
    }

    //设置最短录制时间
    fun setMinDuration(duration: Int) {
        this.min_duration = duration
    }

    //设置回调接口
    fun setCaptureLisenter(captureLisenter: CaptureListener) {
        this.captureLisenter = captureLisenter
    }

    fun setCurrentTakeType(takeType: Int) {
        currentTakeType = takeType
    }

    //设置按钮功能（拍照和录像）
    fun setButtonFeatures(state: Int) {
        this.button_state = state
    }

    //设置状态
    fun resetState() {
        state = STATE_IDLE
    }

    companion object {

        val STATE_IDLE = 0x001        //空闲状态
        val STATE_PRESS = 0x002       //按下状态
        val STATE_LONG_PRESS = 0x003  //长按状态
        val STATE_RECORDERING = 0x004 //录制状态
        val STATE_BAN = 0x005         //禁止状态
    }
}
