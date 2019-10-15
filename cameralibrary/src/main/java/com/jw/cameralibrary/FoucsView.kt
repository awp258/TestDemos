package com.jw.cameralibrary

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.jw.library.utils.ThemeUtils


/**
 * =====================================
 * 作    者: 陈嘉桐
 * 版    本：1.1.4
 * 创建日期：2017/4/26
 * 描    述：对焦框
 * =====================================
 */
class FoucsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val size: Int = ThemeUtils.getWindowWidth(context) / 3
    private var centerX: Int = 0
    private var centerY: Int = 0
    private var length: Int = 0
    private val mPaint: Paint

    init {
        mPaint = Paint()
        mPaint.isAntiAlias = true
        mPaint.isDither = true
        mPaint.color = -0x11e951ea
        mPaint.strokeWidth = 4f
        mPaint.style = Paint.Style.STROKE
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        centerX = (size / 2.0).toInt()
        centerY = (size / 2.0).toInt()
        length = (size / 2.0).toInt() - 2
        setMeasuredDimension(size, size)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRect(
            (centerX - length).toFloat(),
            (centerY - length).toFloat(),
            (centerX + length).toFloat(),
            (centerY + length).toFloat(),
            mPaint
        )
        canvas.drawLine(
            2f,
            (height / 2).toFloat(),
            (size / 10).toFloat(),
            (height / 2).toFloat(),
            mPaint
        )
        canvas.drawLine(
            (width - 2).toFloat(),
            (height / 2).toFloat(),
            (width - size / 10).toFloat(),
            (height / 2).toFloat(),
            mPaint
        )
        canvas.drawLine(
            (width / 2).toFloat(),
            2f,
            (width / 2).toFloat(),
            (size / 10).toFloat(),
            mPaint
        )
        canvas.drawLine(
            (width / 2).toFloat(),
            (height - 2).toFloat(),
            (width / 2).toFloat(),
            (height - size / 10).toFloat(),
            mPaint
        )
    }
}
