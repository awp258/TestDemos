

package com.jw.croplibrary.img.shape;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;

import com.jw.croplibrary.img.config.CropIwaOverlayConfig;

public class CropIwaOvalShape extends com.jw.croplibrary.img.shape.CropIwaShape {
    private Path clipPath = new Path();

    public CropIwaOvalShape(CropIwaOverlayConfig config) {
        super(config);
    }

    protected void clearArea(Canvas canvas, RectF cropBounds, Paint clearPaint) {
        canvas.drawOval(cropBounds, clearPaint);
    }

    protected void drawBorders(Canvas canvas, RectF cropBounds, Paint paint) {
        canvas.drawOval(cropBounds, paint);
        if (this.overlayConfig.isDynamicCrop()) {
            canvas.drawRect(cropBounds, paint);
        }

    }

    @SuppressLint({"WrongConstant"})
    protected void drawGrid(Canvas canvas, RectF cropBounds, Paint paint) {
        this.clipPath.rewind();
        this.clipPath.addOval(cropBounds, Direction.CW);
        canvas.save();
        canvas.clipPath(this.clipPath);
        super.drawGrid(canvas, cropBounds, paint);
        canvas.restore();
    }

    public CropIwaShapeMask getMask() {
        return new CropIwaOvalShape.OvalShapeMask();
    }

    private static class OvalShapeMask implements CropIwaShapeMask {
        private OvalShapeMask() {
        }

        public Bitmap applyMaskTo(Bitmap croppedRegion) {
            croppedRegion.setHasAlpha(true);
            Paint maskPaint = new Paint(1);
            maskPaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
            RectF ovalRect = new RectF(0.0F, 0.0F, (float)croppedRegion.getWidth(), (float)croppedRegion.getHeight());
            Path maskShape = new Path();
            maskShape.addRect(ovalRect, Direction.CW);
            maskShape.addOval(ovalRect, Direction.CCW);
            Canvas canvas = new Canvas(croppedRegion);
            canvas.drawPath(maskShape, maskPaint);
            return croppedRegion;
        }
    }
}
