package com.jw.shotRecord

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

/**
 * =====================================
 * 作    者: 陈嘉桐 445263848@qq.com
 * 版    本：1.0.4
 * 创建日期：2017/4/26
 * 描    述：向下箭头的退出按钮
 * =====================================
 */
class ReturnButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var size: Int = 240
    private var centerX: Int
    private var centerY: Int
    private var strokeWidth: Float
    private var paint: Paint
    internal var path: Path

    init {
        centerX = size / 2
        centerY = size / 2

        strokeWidth = size / 15f

        paint = Paint()
        paint.isAntiAlias = true
        paint.color = Color.WHITE
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = strokeWidth

        path = Path()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(size, size / 2)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        path.moveTo(strokeWidth, strokeWidth / 2)
        path.lineTo(centerX.toFloat(), centerY - strokeWidth / 2)
        path.lineTo(size - strokeWidth, strokeWidth / 2)
        canvas.drawPath(path, paint)
    }
}
