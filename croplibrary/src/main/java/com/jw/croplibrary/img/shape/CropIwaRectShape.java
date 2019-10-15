

package com.jw.croplibrary.img.shape;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.jw.croplibrary.img.config.CropIwaOverlayConfig;

public class CropIwaRectShape extends com.jw.croplibrary.img.shape.CropIwaShape {
    public CropIwaRectShape(CropIwaOverlayConfig config) {
        super(config);
    }

    protected void clearArea(Canvas canvas, RectF cropBounds, Paint clearPaint) {
        canvas.drawRect(cropBounds, clearPaint);
    }

    protected void drawBorders(Canvas canvas, RectF cropBounds, Paint paint) {
        canvas.drawRect(cropBounds, paint);
    }

    public com.jw.croplibrary.img.shape.CropIwaShapeMask getMask() {
        return new CropIwaRectShape.RectShapeMask();
    }

    private static class RectShapeMask implements com.jw.croplibrary.img.shape.CropIwaShapeMask {
        private RectShapeMask() {
        }

        public Bitmap applyMaskTo(Bitmap croppedRegion) {
            return croppedRegion;
        }
    }
}
