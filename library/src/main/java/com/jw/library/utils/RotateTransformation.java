package com.jw.library.utils;

import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

public class RotateTransformation extends BitmapTransformation {

    private int rotateRotationAngle = 0;

    public RotateTransformation(Context context, int rotateRotationAngle) {
        super(context);

        this.rotateRotationAngle = rotateRotationAngle;
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        return BitmapUtil.rotateBitmapByDegree(toTransform, rotateRotationAngle);
    }

    @Override
    public String getId() {
        return "rotate" + rotateRotationAngle;
    }
}