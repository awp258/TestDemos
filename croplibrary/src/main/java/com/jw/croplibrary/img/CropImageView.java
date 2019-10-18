

package com.jw.croplibrary.img;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Region.Op;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;

import com.jw.croplibrary.CropLibrary;
import com.jw.croplibrary.R;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CropImageView extends AppCompatImageView {
    private CropImageView.Style[] styles;
    private int mMaskColor;
    private int mBorderColor;
    private int mBorderWidth;
    private int mFocusWidth;
    private int mFocusHeight;
    private int mDefaultStyleIndex;
    private CropImageView.Style mStyle;
    private Paint mBorderPaint;
    private Path mFocusPath;
    private RectF mFocusRect;
    private static final float MAX_SCALE = 4.0F;
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private static final int ROTATE = 3;
    private static final int ZOOM_OR_ROTATE = 4;
    private int mImageWidth;
    private int mImageHeight;
    private int mRotatedImageWidth;
    private int mRotatedImageHeight;
    private Matrix matrix;
    private Matrix savedMatrix;
    private PointF pA;
    private PointF pB;
    private PointF midPoint;
    private PointF doubleClickPos;
    private PointF mFocusMidPoint;
    private int mode;
    private long doubleClickTime;
    private double rotation;
    private float oldDist;
    private int sumRotateLevel;
    private float mMaxScale;
    private boolean isInited;
    private boolean mSaving;
    private static Handler mHandler = new CropImageView.InnerHandler();
    private static CropImageView.OnBitmapSaveCompleteListener mListener;

    public CropImageView(Context context) {
        this(context, null);
    }

    public CropImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CropImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.styles = new CropImageView.Style[]{CropImageView.Style.RECTANGLE, CropImageView.Style.CIRCLE};
        this.mMaskColor = -1358954496;
        this.mBorderColor = -1434419072;
        this.mBorderWidth = 1;
        this.mFocusWidth = 250;
        this.mFocusHeight = 250;
        this.mDefaultStyleIndex = 0;
        this.mStyle = this.styles[this.mDefaultStyleIndex];
        this.mBorderPaint = new Paint();
        this.mFocusPath = new Path();
        this.mFocusRect = new RectF();
        this.matrix = new Matrix();
        this.savedMatrix = new Matrix();
        this.pA = new PointF();
        this.pB = new PointF();
        this.midPoint = new PointF();
        this.doubleClickPos = new PointF();
        this.mFocusMidPoint = new PointF();
        this.mode = 0;
        this.doubleClickTime = 0L;
        this.rotation = 0.0D;
        this.oldDist = 1.0F;
        this.sumRotateLevel = 0;
        this.mMaxScale = 4.0F;
        this.isInited = false;
        this.mSaving = false;
        this.mFocusWidth = (int)TypedValue.applyDimension(1, (float)this.mFocusWidth, this.getResources().getDisplayMetrics());
        this.mFocusHeight = (int)TypedValue.applyDimension(1, (float)this.mFocusHeight, this.getResources().getDisplayMetrics());
        this.mBorderWidth = (int)TypedValue.applyDimension(1, (float)this.mBorderWidth, this.getResources().getDisplayMetrics());
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CropImageView);
        this.mMaskColor = a.getColor(R.styleable.CropImageView_cropMaskColor, this.mMaskColor);
        this.mBorderColor = a.getColor(R.styleable.CropImageView_cropBorderColor, this.mBorderColor);
        this.mBorderWidth = a.getDimensionPixelSize(R.styleable.CropImageView_cropBorderWidth, this.mBorderWidth);
        this.mFocusWidth = a.getDimensionPixelSize(R.styleable.CropImageView_cropFocusWidth, this.mFocusWidth);
        this.mFocusHeight = a.getDimensionPixelSize(R.styleable.CropImageView_cropFocusHeight, this.mFocusHeight);
        this.mDefaultStyleIndex = a.getInteger(R.styleable.CropImageView_cropStyle, this.mDefaultStyleIndex);
        this.mStyle = this.styles[this.mDefaultStyleIndex];
        a.recycle();
        this.setScaleType(ScaleType.MATRIX);
    }

    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        this.initImage();
    }

    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        this.initImage();
    }

    public void setImageResource(int resId) {
        super.setImageResource(resId);
        this.initImage();
    }

    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        this.initImage();
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.isInited = true;
        this.initImage();
    }

    private void initImage() {
        Drawable d = this.getDrawable();
        if (this.isInited && d != null) {
            this.mode = 0;
            this.matrix = this.getImageMatrix();
            this.mImageWidth = this.mRotatedImageWidth = d.getIntrinsicWidth();
            this.mImageHeight = this.mRotatedImageHeight = d.getIntrinsicHeight();
            int viewWidth = this.getWidth();
            int viewHeight = this.getHeight();
            float midPointX = (float)(viewWidth / 2);
            float midPointY = (float)(viewHeight / 2);
            this.mFocusMidPoint = new PointF(midPointX, midPointY);
            if (this.mStyle == CropImageView.Style.CIRCLE) {
                int focusSize = Math.min(this.mFocusWidth, this.mFocusHeight);
                this.mFocusWidth = focusSize;
                this.mFocusHeight = focusSize;
            }

            this.mFocusRect.left = this.mFocusMidPoint.x - (float)(this.mFocusWidth / 2);
            this.mFocusRect.right = this.mFocusMidPoint.x + (float)(this.mFocusWidth / 2);
            this.mFocusRect.top = this.mFocusMidPoint.y - (float)(this.mFocusHeight / 2);
            this.mFocusRect.bottom = this.mFocusMidPoint.y + (float)(this.mFocusHeight / 2);
            float fitFocusScale = this.getScale(this.mImageWidth, this.mImageHeight, this.mFocusWidth, this.mFocusHeight, true);
            this.mMaxScale = fitFocusScale * 4.0F;
            float fitViewScale = this.getScale(this.mImageWidth, this.mImageHeight, viewWidth, viewHeight, false);
            float scale = fitViewScale > fitFocusScale ? fitViewScale : fitFocusScale;
            this.matrix.setScale(scale, scale, (float)(this.mImageWidth / 2), (float)(this.mImageHeight / 2));
            float[] mImageMatrixValues = new float[9];
            this.matrix.getValues(mImageMatrixValues);
            float transX = this.mFocusMidPoint.x - (mImageMatrixValues[2] + (float)this.mImageWidth * mImageMatrixValues[0] / 2.0F);
            float transY = this.mFocusMidPoint.y - (mImageMatrixValues[5] + (float)this.mImageHeight * mImageMatrixValues[4] / 2.0F);
            this.matrix.postTranslate(transX, transY);
            this.setImageMatrix(this.matrix);
            this.invalidate();
        }
    }

    private float getScale(int bitmapWidth, int bitmapHeight, int minWidth, int minHeight, boolean isMinScale) {
        float scaleX = (float)minWidth / (float)bitmapWidth;
        float scaleY = (float)minHeight / (float)bitmapHeight;
        float scale;
        if (isMinScale) {
            scale = scaleX > scaleY ? scaleX : scaleY;
        } else {
            scale = scaleX < scaleY ? scaleX : scaleY;
        }

        return scale;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (CropImageView.Style.RECTANGLE == this.mStyle) {
            this.mFocusPath.addRect(this.mFocusRect, Direction.CCW);
            canvas.save();
            canvas.clipRect(0, 0, this.getWidth(), this.getHeight());
            canvas.clipPath(this.mFocusPath, Op.DIFFERENCE);
            canvas.drawColor(this.mMaskColor);
            canvas.restore();
        } else if (CropImageView.Style.CIRCLE == this.mStyle) {
            float radius = Math.min((this.mFocusRect.right - this.mFocusRect.left) / 2.0F, (this.mFocusRect.bottom - this.mFocusRect.top) / 2.0F);
            this.mFocusPath.addCircle(this.mFocusMidPoint.x, this.mFocusMidPoint.y, radius, Direction.CCW);
            canvas.save();
            canvas.clipRect(0, 0, this.getWidth(), this.getHeight());
            canvas.clipPath(this.mFocusPath, Op.DIFFERENCE);
            canvas.drawColor(this.mMaskColor);
            canvas.restore();
        }

        this.mBorderPaint.setColor(this.mBorderColor);
        this.mBorderPaint.setStyle(android.graphics.Paint.Style.STROKE);
        this.mBorderPaint.setStrokeWidth((float)this.mBorderWidth);
        this.mBorderPaint.setAntiAlias(true);
        canvas.drawPath(this.mFocusPath, this.mBorderPaint);
        this.mFocusPath.reset();
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (!this.mSaving && null != this.getDrawable()) {
            switch(event.getAction() & 255) {
                case 0:
                    this.savedMatrix.set(this.matrix);
                    this.pA.set(event.getX(), event.getY());
                    this.pB.set(event.getX(), event.getY());
                    this.mode = 1;
                    break;
                case 1:
                case 6:
                    if (this.mode == 1) {
                        if (this.spacing(this.pA, this.pB) < 50.0F) {
                            long now = System.currentTimeMillis();
                            if (now - this.doubleClickTime < 500L && this.spacing(this.pA, this.doubleClickPos) < 50.0F) {
                                this.doubleClick(this.pA.x, this.pA.y);
                                now = 0L;
                            }

                            this.doubleClickPos.set(this.pA);
                            this.doubleClickTime = now;
                        }
                    } else if (this.mode == 3) {
                        int rotateLevel = (int)Math.floor((this.rotation + 0.7853981633974483D) / 1.5707963267948966D);
                        if (rotateLevel == 4) {
                            rotateLevel = 0;
                        }

                        this.matrix.set(this.savedMatrix);
                        this.matrix.postRotate((float)(90 * rotateLevel), this.midPoint.x, this.midPoint.y);
                        if (rotateLevel == 1 || rotateLevel == 3) {
                            int tmp = this.mRotatedImageWidth;
                            this.mRotatedImageWidth = this.mRotatedImageHeight;
                            this.mRotatedImageHeight = tmp;
                        }

                        this.fixScale();
                        this.fixTranslation();
                        this.setImageMatrix(this.matrix);
                        this.sumRotateLevel += rotateLevel;
                    }

                    this.mode = 0;
                    break;
                case 2:
                    PointF pC;
                    double a;
                    double b;
                    double c;
                    double cosA;
                    double angleA;
                    double ta;
                    if (this.mode == 4) {
                        pC = new PointF(event.getX(1) - event.getX(0) + this.pA.x, event.getY(1) - event.getY(0) + this.pA.y);
                        a = (double)this.spacing(this.pB.x, this.pB.y, pC.x, pC.y);
                        b = (double)this.spacing(this.pA.x, this.pA.y, pC.x, pC.y);
                        c = (double)this.spacing(this.pA.x, this.pA.y, this.pB.x, this.pB.y);
                        if (a >= 10.0D) {
                            cosA = (a * a + c * c - b * b) / (2.0D * a * c);
                            angleA = Math.acos(cosA);
                            ta = 0.7853981633974483D;
                            if (angleA > ta && angleA < 3.0D * ta) {
                                this.mode = 3;
                            } else {
                                this.mode = 2;
                            }
                        }
                    }

                    if (this.mode == 1) {
                        this.matrix.set(this.savedMatrix);
                        this.matrix.postTranslate(event.getX() - this.pA.x, event.getY() - this.pA.y);
                        this.fixTranslation();
                        this.setImageMatrix(this.matrix);
                    } else if (this.mode == 2) {
                        float newDist = this.spacing(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
                        if (newDist > 10.0F) {
                            this.matrix.set(this.savedMatrix);
                            float tScale = Math.min(newDist / this.oldDist, this.maxPostScale());
                            if (tScale != 0.0F) {
                                this.matrix.postScale(tScale, tScale, this.midPoint.x, this.midPoint.y);
                                this.fixScale();
                                this.fixTranslation();
                                this.setImageMatrix(this.matrix);
                            }
                        }
                    } else if (this.mode == 3) {
                        pC = new PointF(event.getX(1) - event.getX(0) + this.pA.x, event.getY(1) - event.getY(0) + this.pA.y);
                        a = (double)this.spacing(this.pB.x, this.pB.y, pC.x, pC.y);
                        b = (double)this.spacing(this.pA.x, this.pA.y, pC.x, pC.y);
                        c = (double)this.spacing(this.pA.x, this.pA.y, this.pB.x, this.pB.y);
                        if (b > 10.0D) {
                            cosA = (b * b + c * c - a * a) / (2.0D * b * c);
                            angleA = Math.acos(cosA);
                            ta = (double)(this.pB.y - this.pA.y);
                            double tb = (double)(this.pA.x - this.pB.x);
                            double tc = (double)(this.pB.x * this.pA.y - this.pA.x * this.pB.y);
                            double td = ta * (double)pC.x + tb * (double)pC.y + tc;
                            if (td > 0.0D) {
                                angleA = 6.283185307179586D - angleA;
                            }

                            this.rotation = angleA;
                            this.matrix.set(this.savedMatrix);
                            this.matrix.postRotate((float)(this.rotation * 180.0D / 3.141592653589793D), this.midPoint.x, this.midPoint.y);
                            this.setImageMatrix(this.matrix);
                        }
                    }
                case 3:
                case 4:
                default:
                    break;
                case 5:
                    if (event.getActionIndex() <= 1) {
                        this.pA.set(event.getX(0), event.getY(0));
                        this.pB.set(event.getX(1), event.getY(1));
                        this.midPoint.set((this.pA.x + this.pB.x) / 2.0F, (this.pA.y + this.pB.y) / 2.0F);
                        this.oldDist = this.spacing(this.pA, this.pB);
                        this.savedMatrix.set(this.matrix);
                        if (this.oldDist > 10.0F) {
                            this.mode = 4;
                        }
                    }
            }

            ViewCompat.postInvalidateOnAnimation(this);
            return true;
        } else {
            return super.onTouchEvent(event);
        }
    }

    private void fixScale() {
        float[] imageMatrixValues = new float[9];
        this.matrix.getValues(imageMatrixValues);
        float currentScale = Math.abs(imageMatrixValues[0]) + Math.abs(imageMatrixValues[1]);
        float minScale = this.getScale(this.mRotatedImageWidth, this.mRotatedImageHeight, this.mFocusWidth, this.mFocusHeight, true);
        this.mMaxScale = minScale * 4.0F;
        float scale;
        if (currentScale < minScale) {
            scale = minScale / currentScale;
            this.matrix.postScale(scale, scale);
        } else if (currentScale > this.mMaxScale) {
            scale = this.mMaxScale / currentScale;
            this.matrix.postScale(scale, scale);
        }

    }

    private void fixTranslation() {
        RectF imageRect = new RectF(0.0F, 0.0F, (float)this.mImageWidth, (float)this.mImageHeight);
        this.matrix.mapRect(imageRect);
        float deltaX = 0.0F;
        float deltaY = 0.0F;
        if (imageRect.left > this.mFocusRect.left) {
            deltaX = -imageRect.left + this.mFocusRect.left;
        } else if (imageRect.right < this.mFocusRect.right) {
            deltaX = -imageRect.right + this.mFocusRect.right;
        }

        if (imageRect.top > this.mFocusRect.top) {
            deltaY = -imageRect.top + this.mFocusRect.top;
        } else if (imageRect.bottom < this.mFocusRect.bottom) {
            deltaY = -imageRect.bottom + this.mFocusRect.bottom;
        }

        this.matrix.postTranslate(deltaX, deltaY);
    }

    private float maxPostScale() {
        float[] imageMatrixValues = new float[9];
        this.matrix.getValues(imageMatrixValues);
        float curScale = Math.abs(imageMatrixValues[0]) + Math.abs(imageMatrixValues[1]);
        return this.mMaxScale / curScale;
    }

    private float spacing(float x1, float y1, float x2, float y2) {
        float x = x1 - x2;
        float y = y1 - y2;
        return (float)Math.sqrt((double)(x * x + y * y));
    }

    private float spacing(PointF pA, PointF pB) {
        return this.spacing(pA.x, pA.y, pB.x, pB.y);
    }

    private void doubleClick(float x, float y) {
        float[] p = new float[9];
        this.matrix.getValues(p);
        float curScale = Math.abs(p[0]) + Math.abs(p[1]);
        float minScale = this.getScale(this.mRotatedImageWidth, this.mRotatedImageHeight, this.mFocusWidth, this.mFocusHeight, true);
        float toScale;
        if (curScale < this.mMaxScale) {
            toScale = Math.min(curScale + minScale, this.mMaxScale) / curScale;
            this.matrix.postScale(toScale, toScale, x, y);
        } else {
            toScale = minScale / curScale;
            this.matrix.postScale(toScale, toScale, x, y);
            this.fixTranslation();
        }

        this.setImageMatrix(this.matrix);
    }

    public Bitmap getCropBitmap(int expectWidth, int exceptHeight, boolean isSaveRectangle) {
        if (expectWidth > 0 && exceptHeight >= 0) {
            Bitmap srcBitmap = ((BitmapDrawable)this.getDrawable()).getBitmap();
            srcBitmap = this.rotate(srcBitmap, this.sumRotateLevel * 90);
            return this.makeCropBitmap(srcBitmap, this.mFocusRect, this.getImageMatrixRect(), expectWidth, exceptHeight, isSaveRectangle);
        } else {
            return null;
        }
    }

    public Bitmap rotate(Bitmap bitmap, int degrees) {
        if (degrees != 0 && bitmap != null) {
            Matrix matrix = new Matrix();
            matrix.setRotate((float)degrees, (float)bitmap.getWidth() / 2.0F, (float)bitmap.getHeight() / 2.0F);

            try {
                Bitmap rotateBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                if (bitmap != rotateBitmap) {
                    return rotateBitmap;
                }
            } catch (OutOfMemoryError var5) {
                var5.printStackTrace();
            }
        }

        return bitmap;
    }

    private RectF getImageMatrixRect() {
        RectF rectF = new RectF();
        rectF.set(0.0F, 0.0F, (float)this.getDrawable().getIntrinsicWidth(), (float)this.getDrawable().getIntrinsicHeight());
        this.matrix.mapRect(rectF);
        return rectF;
    }

    private Bitmap makeCropBitmap(Bitmap bitmap, RectF focusRect, RectF imageMatrixRect, int expectWidth, int exceptHeight, boolean isSaveRectangle) {
        if (imageMatrixRect != null && bitmap != null) {
            float scale = imageMatrixRect.width() / (float)bitmap.getWidth();
            int left = (int)((focusRect.left - imageMatrixRect.left) / scale);
            int top = (int)((focusRect.top - imageMatrixRect.top) / scale);
            int width = (int)(focusRect.width() / scale);
            int height = (int)(focusRect.height() / scale);
            if (left < 0) {
                left = 0;
            }

            if (top < 0) {
                top = 0;
            }

            if (left + width > bitmap.getWidth()) {
                width = bitmap.getWidth() - left;
            }

            if (top + height > bitmap.getHeight()) {
                height = bitmap.getHeight() - top;
            }

            try {
                bitmap = Bitmap.createBitmap(bitmap, left, top, width, height);
                if (expectWidth != width || exceptHeight != height) {
                    bitmap = Bitmap.createScaledBitmap(bitmap, expectWidth, exceptHeight, true);
                    if (this.mStyle == CropImageView.Style.CIRCLE && !isSaveRectangle) {
                        int length = Math.min(expectWidth, exceptHeight);
                        int radius = length / 2;
                        Bitmap circleBitmap = Bitmap.createBitmap(length, length, Config.ARGB_8888);
                        Canvas canvas = new Canvas(circleBitmap);
                        BitmapShader bitmapShader = new BitmapShader(bitmap, TileMode.CLAMP, TileMode.CLAMP);
                        Paint paint = new Paint();
                        paint.setShader(bitmapShader);
                        canvas.drawCircle((float)expectWidth / 2.0F, (float)exceptHeight / 2.0F, (float)radius, paint);
                        bitmap = circleBitmap;
                    }
                }
            } catch (OutOfMemoryError var18) {
                var18.printStackTrace();
            }

            return bitmap;
        } else {
            return null;
        }
    }

    public void saveBitmapToFile(File folder, int expectWidth, int exceptHeight, boolean isSaveRectangle) {
        if (!this.mSaving) {
            this.mSaving = true;
            final Bitmap croppedImage = this.getCropBitmap(expectWidth, exceptHeight, isSaveRectangle);
            CompressFormat outputFormat = CompressFormat.JPEG;
            File saveFile = this.createFile(folder, "IMG_", ".jpg");
            if (this.mStyle == CropImageView.Style.CIRCLE && !isSaveRectangle) {
                outputFormat = CompressFormat.PNG;
                saveFile = this.createFile(folder, "IMG_", ".png");
            }

            final CompressFormat finalOutputFormat = outputFormat;
            final File finalSaveFile = saveFile;
            (new Thread() {
                public void run() {
                    CropImageView.this.saveOutput(croppedImage, finalOutputFormat, finalSaveFile);
                }
            }).start();
        }
    }

    private File createFile(File folder, String prefix, String suffix) {
        if (!folder.exists() || !folder.isDirectory()) {
            folder.mkdirs();
        }

        try {
            File nomedia = new File(folder, ".nomedia");
            if (!nomedia.exists()) {
                nomedia.createNewFile();
            }
        } catch (IOException var6) {
            var6.printStackTrace();
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA);
        String filename = prefix + dateFormat.format(new Date(System.currentTimeMillis())) + suffix;
        return new File(folder, filename);
    }

    private void saveOutput(Bitmap croppedImage, CompressFormat outputFormat, File saveFile) {
        OutputStream outputStream = null;

        try {
            outputStream = this.getContext().getContentResolver().openOutputStream(Uri.fromFile(saveFile));
            if (outputStream != null) {
                croppedImage.compress(outputFormat, 90, outputStream);
            }

            Message.obtain(mHandler, 4001, saveFile).sendToTarget();
        } catch (IOException var14) {
            var14.printStackTrace();
            Message.obtain(mHandler, CropLibrary.INSTANCE.getREQUEST_CODE_ITEM_CROP(), saveFile).sendToTarget();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException var13) {
                    var13.printStackTrace();
                }
            }

        }

        this.mSaving = false;
        croppedImage.recycle();
    }

    public void setOnBitmapSaveCompleteListener(CropImageView.OnBitmapSaveCompleteListener listener) {
        mListener = listener;
    }

    public int getFocusWidth() {
        return this.mFocusWidth;
    }

    public void setFocusWidth(int width) {
        this.mFocusWidth = width;
        this.initImage();
    }

    public int getFocusHeight() {
        return this.mFocusHeight;
    }

    public void setFocusHeight(int height) {
        this.mFocusHeight = height;
        this.initImage();
    }

    public int getMaskColor() {
        return this.mMaskColor;
    }

    public void setMaskColor(int color) {
        this.mMaskColor = color;
        this.invalidate();
    }

    public int getFocusColor() {
        return this.mBorderColor;
    }

    public void setBorderColor(int color) {
        this.mBorderColor = color;
        this.invalidate();
    }

    public float getBorderWidth() {
        return (float)this.mBorderWidth;
    }

    public void setBorderWidth(int width) {
        this.mBorderWidth = width;
        this.invalidate();
    }

    public void setFocusStyle(CropImageView.Style style) {
        this.mStyle = style;
        this.invalidate();
    }

    public CropImageView.Style getFocusStyle() {
        return this.mStyle;
    }

    public interface OnBitmapSaveCompleteListener {
        void onBitmapSaveSuccess(File var1);

        void onBitmapSaveError(File var1);
    }

    public enum Style {
        RECTANGLE,
        CIRCLE;

        Style() {
        }
    }

    private static class InnerHandler extends Handler {
        public InnerHandler() {
            super(Looper.getMainLooper());
        }

        public void handleMessage(Message msg) {
            File saveFile = (File)msg.obj;
            if (msg.what == 4001) {
                if (CropImageView.mListener != null) {
                    CropImageView.mListener.onBitmapSaveSuccess(saveFile);
                }
            } else if (msg.what == CropLibrary.INSTANCE.getREQUEST_CODE_ITEM_CROP()) {
                if (CropImageView.mListener != null) {
                    CropImageView.mListener.onBitmapSaveError(saveFile);
                }
            }

        }
    }
}
