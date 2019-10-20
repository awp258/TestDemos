package com.jw.library.utils

import android.content.Context
import android.graphics.Bitmap

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation

class RotateTransformation(context: Context, private val rotateRotationAngle: Int) :
    BitmapTransformation(context) {

    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap {
        return BitmapUtil.rotateBitmapByDegree(toTransform, rotateRotationAngle)
    }

    override fun getId(): String {
        return "rotate$rotateRotationAngle"
    }
}