

package com.rxxb.imagepicker.crop.util;

import android.graphics.Matrix;
import android.graphics.RectF;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

public class MatrixUtils {
    private float[] outValues = new float[9];

    public MatrixUtils() {
    }

    public float getScaleX(Matrix mat) {
        return (float)Math.sqrt(Math.pow((double)this.getMatrixValue(mat, 0), 2.0D) + Math.pow((double)this.getMatrixValue(mat, 3), 2.0D));
    }

    public float getXTranslation(Matrix mat) {
        mat.getValues(this.outValues);
        return this.outValues[2];
    }

    public float getYTranslation(Matrix mat) {
        mat.getValues(this.outValues);
        return this.outValues[5];
    }

    private float getMatrixValue(@NonNull Matrix mat, @IntRange(from = 0L,to = 9L) int valueIndex) {
        mat.getValues(this.outValues);
        return this.outValues[valueIndex];
    }

    public float getMatrixAngle(@NonNull Matrix matrix) {
        return (float)(-(Math.atan2((double)this.getMatrixValue(matrix, 1), (double)this.getMatrixValue(matrix, 0)) * 57.29577951308232D));
    }

    public static Matrix findTransformToAllowedBounds(RectF initial, Matrix initialTransform, RectF allowedBounds) {
        RectF initialBounds = new RectF();
        initialBounds.set(initial);
        Matrix transform = new Matrix();
        transform.set(initialTransform);
        RectF current = new RectF(initial);
        transform.mapRect(current);
        float var6;
        if (current.width() < allowedBounds.width()) {
            var6 = allowedBounds.width() / current.width();
        }

        if (current.height() < allowedBounds.height()) {
            var6 = allowedBounds.height() / current.height();
        }

        if (!RectF.intersects(current, allowedBounds)) {
            if (current.left > allowedBounds.left) {
                translate(initialBounds, allowedBounds.left - current.left, 0.0F, transform, current);
            }

            if (current.right < allowedBounds.right) {
                translate(initialBounds, allowedBounds.right - current.right, 0.0F, transform, current);
            }

            if (current.top > allowedBounds.top) {
                translate(initialBounds, 0.0F, allowedBounds.top - current.top, transform, current);
            }

            if (current.bottom < allowedBounds.bottom) {
                translate(initialBounds, 0.0F, allowedBounds.bottom - current.bottom, transform, current);
            }
        }

        return transform;
    }

    private static void scale(RectF initial, float scale, Matrix transform, RectF outRect) {
        transform.postScale(scale, scale, outRect.centerX(), outRect.centerY());
        transformInitial(initial, transform, outRect);
    }

    private static void translate(RectF initial, float dx, float dy, Matrix transform, RectF outRect) {
        transform.postTranslate(dx, dy);
        transformInitial(initial, transform, outRect);
    }

    private static void transformInitial(RectF initial, Matrix transform, RectF outRect) {
        outRect.set(initial);
        transform.mapRect(outRect);
    }
}
