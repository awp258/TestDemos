

package com.jw.galary.img.crop.shape;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import com.jw.galary.img.crop.CropIwaView;
import com.jw.galary.img.crop.config.ConfigChangeListener;
import com.jw.galary.img.crop.config.CropIwaOverlayConfig;

public abstract class CropIwaShape implements ConfigChangeListener {
    private Paint clearPaint;
    private Paint cornerPaint;
    private Paint gridPaint;
    private Paint borderPaint;
    protected CropIwaOverlayConfig overlayConfig;

    public CropIwaShape(CropIwaView cropIwaView) {
        this(cropIwaView.configureOverlay());
    }

    public CropIwaShape(CropIwaOverlayConfig config) {
        this.overlayConfig = config;
        this.clearPaint = new Paint(1);
        this.clearPaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
        this.gridPaint = new Paint(1);
        this.gridPaint.setStyle(Style.STROKE);
        this.gridPaint.setStrokeCap(Cap.SQUARE);
        this.borderPaint = new Paint(this.gridPaint);
        this.cornerPaint = new Paint(1);
        this.cornerPaint.setStyle(Style.STROKE);
        this.cornerPaint.setStrokeCap(Cap.ROUND);
        this.updatePaintObjectsFromConfig();
    }

    public final void draw(Canvas canvas, RectF cropBounds) {
        this.clearArea(canvas, cropBounds, this.clearPaint);
        if (this.overlayConfig.shouldDrawGrid()) {
            this.drawGrid(canvas, cropBounds, this.gridPaint);
        }

        this.drawBorders(canvas, cropBounds, this.borderPaint);
    }

    public void drawCorner(Canvas canvas, float x, float y, float deltaX, float deltaY) {
        canvas.drawLine(x, y, x + deltaX, y, this.cornerPaint);
        canvas.drawLine(x, y, x, y + deltaY, this.cornerPaint);
    }

    public Paint getCornerPaint() {
        return this.cornerPaint;
    }

    public Paint getGridPaint() {
        return this.gridPaint;
    }

    public Paint getBorderPaint() {
        return this.borderPaint;
    }

    public abstract CropIwaShapeMask getMask();

    protected abstract void clearArea(Canvas var1, RectF var2, Paint var3);

    protected abstract void drawBorders(Canvas var1, RectF var2, Paint var3);

    protected void drawGrid(Canvas canvas, RectF cropBounds, Paint paint) {
        float stepX = cropBounds.width() * 0.333F;
        float stepY = cropBounds.height() * 0.333F;
        float x = cropBounds.left;
        float y = cropBounds.top;

        for(int i = 0; i < 2; ++i) {
            x += stepX;
            y += stepY;
            canvas.drawLine(x, cropBounds.top, x, cropBounds.bottom, paint);
            canvas.drawLine(cropBounds.left, y, cropBounds.right, y, paint);
        }

    }

    public void onConfigChanged() {
        this.updatePaintObjectsFromConfig();
    }

    private void updatePaintObjectsFromConfig() {
        this.cornerPaint.setStrokeWidth((float)this.overlayConfig.getCornerStrokeWidth());
        this.cornerPaint.setColor(this.overlayConfig.getCornerColor());
        this.gridPaint.setColor(this.overlayConfig.getGridColor());
        this.gridPaint.setStrokeWidth((float)this.overlayConfig.getGridStrokeWidth());
        this.borderPaint.setColor(this.overlayConfig.getBorderColor());
        this.borderPaint.setStrokeWidth((float)this.overlayConfig.getBorderStrokeWidth());
    }
}
