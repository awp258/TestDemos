

package com.jw.galary.img.crop;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.FloatRange;
import android.support.v7.widget.AppCompatImageView;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import com.jw.galary.img.crop.config.ConfigChangeListener;
import com.jw.galary.img.crop.config.CropIwaImageViewConfig;
import com.jw.galary.img.crop.util.CropIwaUtils;
import com.jw.galary.img.crop.util.MatrixUtils;
import com.jw.galary.img.crop.util.TensionInterpolator;

@SuppressLint({"ViewConstructor"})
class CropIwaImageView extends AppCompatImageView implements OnNewBoundsListener, ConfigChangeListener {
    private Matrix imageMatrix;
    private MatrixUtils matrixUtils;
    private CropIwaImageView.GestureProcessor gestureDetector;
    private RectF allowedBounds;
    private RectF imageBounds;
    private RectF realImageBounds;
    private OnImagePositionedListener imagePositionedListener;
    private CropIwaImageViewConfig config;

    public CropIwaImageView(Context context, CropIwaImageViewConfig config) {
        super(context);
        this.initWith(config);
    }

    private void initWith(CropIwaImageViewConfig c) {
        this.config = c;
        this.config.addConfigChangeListener(this);
        this.imageBounds = new RectF();
        this.allowedBounds = new RectF();
        this.realImageBounds = new RectF();
        this.matrixUtils = new MatrixUtils();
        this.imageMatrix = new Matrix();
        this.setScaleType(ScaleType.MATRIX);
        this.gestureDetector = new CropIwaImageView.GestureProcessor();
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (this.hasImageSize()) {
            this.placeImageToInitialPosition();
        }

    }

    @SuppressLint({"Range"})
    public void initialize() {
        this.config.setScale(-1.0F);
        this.placeImageToInitialPosition();
    }

    private void placeImageToInitialPosition() {
        this.updateImageBounds();
        this.moveImageToTheCenter();
        if (this.config.getScale() == -1.0F) {
            switch(this.config.getImageInitialPosition()) {
                case CENTER_CROP:
                    this.resizeImageToFillTheView();
                    break;
                case CENTER_INSIDE:
                    this.resizeImageToBeInsideTheView();
            }

            this.config.setScale(this.getCurrentScalePercent()).apply();
        } else {
            this.setScalePercent(this.config.getScale());
        }

        this.notifyImagePositioned();
    }

    private void resizeImageToFillTheView() {
        float scale;
        if (this.getWidth() < this.getHeight()) {
            scale = (float)this.getHeight() / (float)this.getImageHeight();
        } else {
            scale = (float)this.getWidth() / (float)this.getImageWidth();
        }

        this.scaleImage(scale);
    }

    private void resizeImageToBeInsideTheView() {
        float scale;
        if (this.getImageWidth() < this.getImageHeight()) {
            scale = (float)this.getHeight() / (float)this.getImageHeight();
        } else {
            scale = (float)this.getWidth() / (float)this.getImageWidth();
        }

        this.scaleImage(scale);
    }

    private void moveImageToTheCenter() {
        this.updateImageBounds();
        float deltaX = (float)this.getWidth() / 2.0F - this.imageBounds.centerX();
        float deltaY = (float)this.getHeight() / 2.0F - this.imageBounds.centerY();
        this.translateImage(deltaX, deltaY);
    }

    private float calculateMinScale() {
        float viewWidth = (float)this.getWidth();
        float viewHeight = (float)this.getHeight();
        if ((float)this.getRealImageWidth() <= viewWidth && (float)this.getRealImageHeight() <= viewHeight) {
            return this.config.getMinScale();
        } else {
            float scaleFactor = viewWidth < viewHeight ? viewWidth / (float)this.getRealImageWidth() : viewHeight / (float)this.getRealImageHeight();
            return scaleFactor * 0.8F;
        }
    }

    private int getRealImageWidth() {
        Drawable image = this.getDrawable();
        return image != null ? image.getIntrinsicWidth() : -1;
    }

    private int getRealImageHeight() {
        Drawable image = this.getDrawable();
        return image != null ? image.getIntrinsicHeight() : -1;
    }

    public int getImageWidth() {
        return (int)this.imageBounds.width();
    }

    public int getImageHeight() {
        return (int)this.imageBounds.height();
    }

    public boolean hasImageSize() {
        return this.getRealImageWidth() != -1 && this.getRealImageHeight() != -1;
    }

    public CropIwaImageView.GestureProcessor getImageTransformGestureDetector() {
        return this.gestureDetector;
    }

    public void onNewBounds(RectF bounds) {
        this.updateImageBounds();
        this.allowedBounds.set(bounds);
        if (this.hasImageSize()) {
            this.post(new Runnable() {
                public void run() {
                    CropIwaImageView.this.animateToAllowedBounds();
                }
            });
            this.updateImageBounds();
            this.invalidate();
        }

    }

    private void animateToAllowedBounds() {
        this.updateImageBounds();
        Matrix endMatrix = MatrixUtils.findTransformToAllowedBounds(this.realImageBounds, this.imageMatrix, this.allowedBounds);
        this.imageMatrix.set(endMatrix);
        this.setImageMatrix(this.imageMatrix);
        this.updateImageBounds();
        this.invalidate();
    }

    private void setScalePercent(@FloatRange(from = 0.009999999776482582D,to = 1.0D) float percent) {
        percent = Math.min(Math.max(0.01F, percent), 1.0F);
        float desiredScale = this.config.getMinScale() + this.config.getMaxScale() * percent;
        float currentScale = this.matrixUtils.getScaleX(this.imageMatrix);
        float factor = desiredScale / currentScale;
        this.scaleImage(factor);
        this.invalidate();
    }

    private void scaleImage(float factor) {
        this.updateImageBounds();
        this.scaleImage(factor, this.imageBounds.centerX(), this.imageBounds.centerY());
    }

    private void scaleImage(float factor, float pivotX, float pivotY) {
        this.imageMatrix.postScale(factor, factor, pivotX, pivotY);
        this.setImageMatrix(this.imageMatrix);
        this.updateImageBounds();
    }

    private void translateImage(float deltaX, float deltaY) {
        this.imageMatrix.postTranslate(deltaX, deltaY);
        this.setImageMatrix(this.imageMatrix);
        if (deltaX > 0.01F || deltaY > 0.01F) {
            this.updateImageBounds();
        }

    }

    public void rotateImage(float deltaAngle, float px, float py) {
        this.imageMatrix.postRotate(deltaAngle, px, py);
        this.setImageMatrix(this.imageMatrix);
        this.updateImageBounds();
    }

    public float getMatrixAngle() {
        return this.matrixUtils.getMatrixAngle(this.imageMatrix);
    }

    private void updateImageBounds() {
        this.realImageBounds.set(0.0F, 0.0F, (float)this.getRealImageWidth(), (float)this.getRealImageHeight());
        this.imageBounds.set(this.realImageBounds);
        this.imageMatrix.mapRect(this.imageBounds);
    }

    public void onConfigChanged() {
        if (Math.abs(this.getCurrentScalePercent() - this.config.getScale()) > 0.001F) {
            this.setScalePercent(this.config.getScale());
            this.animateToAllowedBounds();
        }

    }

    public void setImagePositionedListener(OnImagePositionedListener imagePositionedListener) {
        this.imagePositionedListener = imagePositionedListener;
        if (this.hasImageSize()) {
            this.updateImageBounds();
            this.notifyImagePositioned();
        }

    }

    public RectF getImageRect() {
        this.updateImageBounds();
        return new RectF(this.imageBounds);
    }

    public void notifyImagePositioned() {
        if (this.imagePositionedListener != null) {
            RectF imageRect = new RectF(this.imageBounds);
            CropIwaUtils.constrainRectTo(0, 0, this.getWidth(), this.getHeight(), imageRect);
            this.imagePositionedListener.onImagePositioned(imageRect);
        }

    }

    private float getCurrentScalePercent() {
        return CropIwaUtils.boundValue(0.01F + (this.matrixUtils.getScaleX(this.imageMatrix) - this.config.getMinScale()) / this.config.getMaxScale(), 0.01F, 1.0F);
    }

    public class GestureProcessor {
        private ScaleGestureDetector scaleDetector = new ScaleGestureDetector(CropIwaImageView.this.getContext(), CropIwaImageView.this.new ScaleGestureListener());
        private CropIwaImageView.TranslationGestureListener translationGestureListener = CropIwaImageView.this.new TranslationGestureListener();

        public GestureProcessor() {
        }

        public void onDown(MotionEvent event) {
            this.translationGestureListener.onDown(event);
        }

        public void onTouchEvent(MotionEvent event) {
            switch(event.getAction()) {
                case 0:
                    return;
                case 1:
                case 3:
                    CropIwaImageView.this.animateToAllowedBounds();
                    return;
                case 2:
                default:
                    if (CropIwaImageView.this.config.isImageScaleEnabled()) {
                        this.scaleDetector.onTouchEvent(event);
                    }

                    if (CropIwaImageView.this.config.isImageTranslationEnabled()) {
                        this.translationGestureListener.onTouchEvent(event, !this.scaleDetector.isInProgress());
                    }

            }
        }
    }

    private class TranslationGestureListener {
        private float prevX;
        private float prevY;
        private int id;
        private TensionInterpolator interpolator;

        private TranslationGestureListener() {
            this.interpolator = new TensionInterpolator();
        }

        public void onDown(MotionEvent e) {
            this.onDown(e.getX(), e.getY(), e.getPointerId(0));
        }

        private void onDown(float x, float y, int id) {
            CropIwaImageView.this.updateImageBounds();
            this.interpolator.onDown(x, y, CropIwaImageView.this.imageBounds, CropIwaImageView.this.allowedBounds);
            this.saveCoordinates(x, y, id);
        }

        public void onTouchEvent(MotionEvent e, boolean canHandle) {
            switch(e.getActionMasked()) {
                case 2:
                    int index = e.findPointerIndex(this.id);
                    CropIwaImageView.this.updateImageBounds();
                    float currentX = this.interpolator.interpolateX(e.getX(index));
                    float currentY = this.interpolator.interpolateY(e.getY(index));
                    if (canHandle) {
                        CropIwaImageView.this.translateImage(currentX - this.prevX, currentY - this.prevY);
                    }

                    this.saveCoordinates(currentX, currentY);
                    return;
                case 6:
                    this.onPointerUp(e);
                    return;
                default:
            }
        }

        private void onPointerUp(MotionEvent e) {
            if (e.getPointerId(e.getActionIndex()) == this.id) {
                int index;
                for(index = 0; index < e.getPointerCount() && index == e.getActionIndex(); ++index) {
                }

                this.onDown(e.getX(index), e.getY(index), e.getPointerId(index));
            }

        }

        private void saveCoordinates(float x, float y) {
            this.saveCoordinates(x, y, this.id);
        }

        private void saveCoordinates(float x, float y, int id) {
            this.prevX = x;
            this.prevY = y;
            this.id = id;
        }
    }

    private class ScaleGestureListener extends SimpleOnScaleGestureListener {
        private ScaleGestureListener() {
        }

        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();
            float newScale = CropIwaImageView.this.matrixUtils.getScaleX(CropIwaImageView.this.imageMatrix) * scaleFactor;
            if (this.isValidScale(newScale)) {
                CropIwaImageView.this.scaleImage(scaleFactor, detector.getFocusX(), detector.getFocusY());
                CropIwaImageView.this.config.setScale(CropIwaImageView.this.getCurrentScalePercent()).apply();
            }

            return true;
        }

        private boolean isValidScale(float newScale) {
            return newScale >= CropIwaImageView.this.config.getMinScale() && newScale <= CropIwaImageView.this.config.getMinScale() + CropIwaImageView.this.config.getMaxScale();
        }
    }
}
