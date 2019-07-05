//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.rxxb.imagepicker.crop;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import com.rxxb.imagepicker.crop.CropIwaImageView.GestureProcessor;
import com.rxxb.imagepicker.crop.config.ConfigChangeListener;
import com.rxxb.imagepicker.crop.config.CropIwaImageViewConfig;
import com.rxxb.imagepicker.crop.config.CropIwaOverlayConfig;
import com.rxxb.imagepicker.crop.config.CropIwaSaveConfig;
import com.rxxb.imagepicker.crop.image.CropArea;
import com.rxxb.imagepicker.crop.image.CropIwaBitmapManager;
import com.rxxb.imagepicker.crop.image.CropIwaResultReceiver;
import com.rxxb.imagepicker.crop.image.CropIwaResultReceiver.Listener;
import com.rxxb.imagepicker.crop.shape.CropIwaShapeMask;
import com.rxxb.imagepicker.crop.util.CropIwaLog;
import com.rxxb.imagepicker.crop.util.LoadBitmapCommand;

public class CropIwaView extends FrameLayout {
    private CropIwaImageView imageView;
    private CropIwaOverlayView overlayView;
    private CropIwaOverlayConfig overlayConfig;
    private CropIwaImageViewConfig imageConfig;
    private GestureProcessor gestureDetector;
    private Uri imageUri;
    private LoadBitmapCommand loadBitmapCommand;
    private CropIwaView.ErrorListener errorListener;
    private CropIwaView.CropSaveCompleteListener cropSaveCompleteListener;
    private CropIwaResultReceiver cropIwaResultReceiver;

    public CropIwaView(Context context) {
        super(context);
        this.init((AttributeSet)null);
    }

    public CropIwaView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(attrs);
    }

    public CropIwaView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init(attrs);
    }

    @TargetApi(21)
    public CropIwaView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.init(attrs);
    }

    private void init(AttributeSet attrs) {
        this.imageConfig = CropIwaImageViewConfig.createFromAttributes(this.getContext(), attrs);
        this.initImageView();
        this.overlayConfig = CropIwaOverlayConfig.createFromAttributes(this.getContext(), attrs);
        this.overlayConfig.addConfigChangeListener(new CropIwaView.ReInitOverlayOnResizeModeChange());
        this.initOverlayView();
        this.cropIwaResultReceiver = new CropIwaResultReceiver();
        this.cropIwaResultReceiver.register(this.getContext());
        this.cropIwaResultReceiver.setListener(new CropIwaView.CropResultRouter());
    }

    private void initImageView() {
        if (this.imageConfig == null) {
            throw new IllegalStateException("imageConfig must be initialized before calling this method");
        } else {
            this.imageView = new CropIwaImageView(this.getContext(), this.imageConfig);
            this.imageView.setBackgroundColor(-16777216);
            this.gestureDetector = this.imageView.getImageTransformGestureDetector();
            this.addView(this.imageView);
        }
    }

    private void initOverlayView() {
        if (this.imageView != null && this.overlayConfig != null) {
            this.overlayView = (CropIwaOverlayView)(this.overlayConfig.isDynamicCrop() ? new CropIwaDynamicOverlayView(this.getContext(), this.overlayConfig) : new CropIwaOverlayView(this.getContext(), this.overlayConfig));
            this.overlayView.setNewBoundsListener(this.imageView);
            this.imageView.setImagePositionedListener(this.overlayView);
            this.addView(this.overlayView);
        } else {
            throw new IllegalStateException("imageView and overlayConfig must be initialized before calling this method");
        }
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (this.loadBitmapCommand != null) {
            this.loadBitmapCommand.setDimensions(w, h);
            this.loadBitmapCommand.tryExecute(this.getContext());
        }

    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            if (ev.getAction() == 0) {
                this.gestureDetector.onDown(ev);
                return false;
            } else {
                return !this.overlayView.isResizing();
            }
        } catch (IllegalArgumentException var3) {
            return false;
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        try {
            this.gestureDetector.onTouchEvent(event);
            return super.onTouchEvent(event);
        } catch (IllegalArgumentException var3) {
            return false;
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.imageView.measure(widthMeasureSpec, heightMeasureSpec);
        this.overlayView.measure(this.imageView.getMeasuredWidthAndState(), this.imageView.getMeasuredHeightAndState());
        this.imageView.notifyImagePositioned();
        this.setMeasuredDimension(this.imageView.getMeasuredWidthAndState(), this.imageView.getMeasuredHeightAndState());
    }

    public void invalidate() {
        this.imageView.invalidate();
        this.overlayView.invalidate();
    }

    public CropIwaOverlayConfig configureOverlay() {
        return this.overlayConfig;
    }

    public void rotateImage(float deltaAngle) {
        this.imageView.rotateImage(deltaAngle, this.overlayView.getCropRect().centerX(), this.overlayView.getCropRect().centerY());
    }

    public float getMatrixAngle() {
        return this.imageView.getMatrixAngle();
    }

    public void initialize() {
        this.imageView.initialize();
    }

    public CropIwaImageViewConfig configureImage() {
        return this.imageConfig;
    }

    public void setImageUri(Uri uri) {
        this.imageUri = uri;
        this.loadBitmapCommand = new LoadBitmapCommand(uri, this.getWidth(), this.getHeight(), new CropIwaView.BitmapLoadListener());
        this.loadBitmapCommand.tryExecute(this.getContext());
    }

    public void setImage(Bitmap bitmap) {
        this.imageView.setImageBitmap(bitmap);
        this.overlayView.setDrawOverlay(true);
    }

    public void crop(CropIwaSaveConfig saveConfig) {
        CropArea cropArea = CropArea.create(this.imageView.getImageRect(), this.imageView.getImageRect(), this.overlayView.getCropRect());
        CropIwaShapeMask mask = this.overlayConfig.getCropShape().getMask();
        CropIwaBitmapManager.get().crop(this.getContext(), cropArea, mask, this.imageUri, saveConfig, this.imageView.getMatrixAngle());
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.imageUri != null) {
            CropIwaBitmapManager loader = CropIwaBitmapManager.get();
            loader.unregisterLoadListenerFor(this.imageUri);
            loader.removeIfCached(this.imageUri);
        }

        if (this.cropIwaResultReceiver != null) {
            this.cropIwaResultReceiver.unregister(this.getContext());
        }

    }

    public void setErrorListener(CropIwaView.ErrorListener errorListener) {
        this.errorListener = errorListener;
    }

    public void setCropSaveCompleteListener(CropIwaView.CropSaveCompleteListener cropSaveCompleteListener) {
        this.cropSaveCompleteListener = cropSaveCompleteListener;
    }

    public interface ErrorListener {
        void onError(Throwable var1);
    }

    public interface CropSaveCompleteListener {
        void onCroppedRegionSaved(Uri var1);
    }

    private class ReInitOverlayOnResizeModeChange implements ConfigChangeListener {
        private ReInitOverlayOnResizeModeChange() {
        }

        public void onConfigChanged() {
            if (this.shouldReInit()) {
                CropIwaView.this.overlayConfig.removeConfigChangeListener(CropIwaView.this.overlayView);
                boolean shouldDrawOverlay = CropIwaView.this.overlayView.isDrawn();
                CropIwaView.this.removeView(CropIwaView.this.overlayView);
                CropIwaView.this.initOverlayView();
                CropIwaView.this.overlayView.setDrawOverlay(shouldDrawOverlay);
                CropIwaView.this.invalidate();
            }

        }

        private boolean shouldReInit() {
            return CropIwaView.this.overlayConfig.isDynamicCrop() != (CropIwaView.this.overlayView instanceof CropIwaDynamicOverlayView);
        }
    }

    private class CropResultRouter implements Listener {
        private CropResultRouter() {
        }

        public void onCropSuccess(Uri croppedUri) {
            if (CropIwaView.this.cropSaveCompleteListener != null) {
                CropIwaView.this.cropSaveCompleteListener.onCroppedRegionSaved(croppedUri);
            }

        }

        public void onCropFailed(Throwable e) {
            if (CropIwaView.this.errorListener != null) {
                CropIwaView.this.errorListener.onError(e);
            }

        }
    }

    private class BitmapLoadListener implements com.rxxb.imagepicker.crop.image.CropIwaBitmapManager.BitmapLoadListener {
        private BitmapLoadListener() {
        }

        public void onBitmapLoaded(Uri imageUri, Bitmap bitmap) {
            CropIwaView.this.setImage(bitmap);
        }

        public void onLoadFailed(Throwable e) {
            CropIwaLog.e("CropIwa Image loading from [" + CropIwaView.this.imageUri + "] failed", e);
            CropIwaView.this.overlayView.setDrawOverlay(false);
            if (CropIwaView.this.errorListener != null) {
                CropIwaView.this.errorListener.onError(e);
            }

        }
    }
}
