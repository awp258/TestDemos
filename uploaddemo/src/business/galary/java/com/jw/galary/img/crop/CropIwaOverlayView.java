

package com.jw.galary.img.crop;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import com.jw.galary.img.crop.config.ConfigChangeListener;
import com.jw.galary.img.crop.config.CropIwaOverlayConfig;
import com.jw.galary.img.crop.shape.CropIwaShape;

@SuppressLint({"ViewConstructor"})
class CropIwaOverlayView extends View implements ConfigChangeListener, OnImagePositionedListener {
    private Paint overlayPaint;
    private OnNewBoundsListener newBoundsListener;
    private CropIwaShape cropShape;
    private float cropScale;
    private RectF imageBounds;
    protected RectF cropRect;
    protected CropIwaOverlayConfig config;
    protected boolean shouldDrawOverlay;

    public CropIwaOverlayView(Context context, CropIwaOverlayConfig config) {
        super(context);
        this.initWith(config);
    }

    protected void initWith(CropIwaOverlayConfig c) {
        this.config = c;
        this.config.addConfigChangeListener(this);
        this.imageBounds = new RectF();
        this.cropScale = this.config.getCropScale();
        this.cropShape = c.getCropShape();
        this.cropRect = new RectF();
        this.overlayPaint = new Paint();
        this.overlayPaint.setStyle(Style.FILL);
        this.overlayPaint.setColor(c.getOverlayColor());
        this.setLayerType(1, (Paint)null);
    }

    public void onImagePositioned(RectF imageRect) {
        this.imageBounds.set(imageRect);
        this.setCropRectAccordingToAspectRatio();
        this.notifyNewBounds();
        this.invalidate();
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    public boolean onTouchEvent(MotionEvent ev) {
        return true;
    }

    protected void onDraw(Canvas canvas) {
        if (this.shouldDrawOverlay) {
            canvas.drawRect(0.0F, 0.0F, (float)this.getWidth(), (float)this.getHeight(), this.overlayPaint);
            if (this.isValidCrop()) {
                this.cropShape.draw(canvas, this.cropRect);
            }
        }

    }

    protected void notifyNewBounds() {
        if (this.newBoundsListener != null) {
            RectF rect = new RectF(this.cropRect);
            this.newBoundsListener.onNewBounds(rect);
        }

    }

    private boolean isValidCrop() {
        return this.cropRect.width() >= (float)this.config.getMinWidth() && this.cropRect.height() >= (float)this.config.getMinHeight();
    }

    public boolean isResizing() {
        return false;
    }

    public boolean isDraggingCropArea() {
        return false;
    }

    public RectF getCropRect() {
        return new RectF(this.cropRect);
    }

    public void setDrawOverlay(boolean shouldDraw) {
        this.shouldDrawOverlay = shouldDraw;
        this.invalidate();
    }

    public boolean isDrawn() {
        return this.shouldDrawOverlay;
    }

    public void setNewBoundsListener(OnNewBoundsListener newBoundsListener) {
        this.newBoundsListener = newBoundsListener;
    }

    public void onConfigChanged() {
        this.overlayPaint.setColor(this.config.getOverlayColor());
        this.cropShape = this.config.getCropShape();
        this.cropScale = this.config.getCropScale();
        this.cropShape.onConfigChanged();
        this.setCropRectAccordingToAspectRatio();
        this.notifyNewBounds();
        this.invalidate();
    }

    private void setCropRectAccordingToAspectRatio() {
        float viewWidth = (float)this.getMeasuredWidth();
        float viewHeight = (float)this.getMeasuredHeight();
        if (viewWidth != 0.0F && viewHeight != 0.0F) {
            AspectRatio aspectRatio = this.getAspectRatio();
            if (aspectRatio != null) {
                float centerX;
                if (this.cropRect.width() != 0.0F && this.cropRect.height() != 0.0F) {
                    centerX = this.cropRect.width() / this.cropRect.height();
                    if ((double)Math.abs(centerX - aspectRatio.getRatio()) < 0.001D) {
                        return;
                    }
                }

                centerX = viewWidth * 0.5F;
                float centerY = viewHeight * 0.5F;
                boolean calculateFromWidth = aspectRatio.getHeight() < aspectRatio.getWidth() || aspectRatio.isSquare() && viewWidth < viewHeight;
                float halfWidth;
                float halfHeight;
                if (calculateFromWidth) {
                    halfWidth = viewWidth * this.cropScale * 0.5F;
                    halfHeight = halfWidth / aspectRatio.getRatio();
                } else {
                    halfHeight = viewHeight * this.cropScale * 0.5F;
                    halfWidth = halfHeight * aspectRatio.getRatio();
                }

                this.cropRect.set(centerX - halfWidth + 10.0F, centerY - halfHeight + 10.0F, centerX + halfWidth - 10.0F, centerY + halfHeight - 10.0F);
            }
        }
    }

    @Nullable
    private AspectRatio getAspectRatio() {
        AspectRatio aspectRatio = this.config.getAspectRatio();
        if (aspectRatio == AspectRatio.IMG_SRC) {
            if (this.imageBounds.width() == 0.0F || this.imageBounds.height() == 0.0F) {
                return null;
            }

            aspectRatio = new AspectRatio(Math.round(this.imageBounds.width()), Math.round(this.imageBounds.height()));
        }

        return aspectRatio;
    }
}
