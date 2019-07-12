//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.jw.galary.img.view;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.graphics.Paint.Align;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Checkable;
import com.jw.uploaddemo.R;

public class SmoothCheckBox extends View implements Checkable {
    private static final String KEY_INSTANCE_STATE = "InstanceState";
    private static final int COLOR_TICK = -1;
    private static final int COLOR_UNCHECKED = -1;
    private static final int COLOR_CHECKED = Color.parseColor("#FB4846");
    private static final int COLOR_FLOOR_UNCHECKED = Color.parseColor("#DFDFDF");
    private static final int DEF_DRAW_SIZE = 25;
    private static final int DEF_ANIM_DURATION = 300;
    private Paint mPaint;
    private Paint mTickPaint;
    private Paint mFloorPaint;
    private Paint mTextPaint;
    private Point[] mTickPoints;
    private Point mCenterPoint;
    private Path mTickPath;
    private float mLeftLineDistance;
    private float mRightLineDistance;
    private float mDrewDistance;
    private float mScaleVal;
    private float mFloorScale;
    private int mWidth;
    private int mAnimDuration;
    private int mStrokeWidth;
    private int mCheckedColor;
    private int mUnCheckedColor;
    private int mFloorColor;
    private int mFloorUnCheckedColor;
    private boolean mChecked;
    private boolean mTickDrawing;
    private int number;
    private SmoothCheckBox.OnCheckedChangeListener mListener;

    public SmoothCheckBox(Context context) {
        this(context, (AttributeSet)null);
    }

    public SmoothCheckBox(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SmoothCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mScaleVal = 1.0F;
        this.mFloorScale = 1.0F;
        this.number = -1;
        this.init(attrs);
    }

    @TargetApi(21)
    public SmoothCheckBox(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mScaleVal = 1.0F;
        this.mFloorScale = 1.0F;
        this.number = -1;
        this.init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray ta = this.getContext().obtainStyledAttributes(attrs, R.styleable.SmoothCheckBox);
        int tickColor = ta.getColor(R.styleable.SmoothCheckBox_color_tick, -1);
        this.mAnimDuration = ta.getInt(R.styleable.SmoothCheckBox_duration, 300);
        this.mFloorColor = ta.getColor(R.styleable.SmoothCheckBox_color_unchecked_stroke, COLOR_FLOOR_UNCHECKED);
        this.mCheckedColor = ta.getColor(R.styleable.SmoothCheckBox_color_checked, COLOR_CHECKED);
        this.mUnCheckedColor = ta.getColor(R.styleable.SmoothCheckBox_color_unchecked, -1);
        this.mStrokeWidth = ta.getDimensionPixelSize(R.styleable.SmoothCheckBox_stroke_width, this.dp2px(this.getContext(), 0.0F));
        ta.recycle();
        this.mFloorUnCheckedColor = this.mFloorColor;
        this.mTickPaint = new Paint(1);
        this.mTickPaint.setStyle(Style.STROKE);
        this.mTickPaint.setStrokeCap(Cap.ROUND);
        this.mTickPaint.setColor(tickColor);
        this.mFloorPaint = new Paint(1);
        this.mFloorPaint.setStyle(Style.FILL);
        this.mFloorPaint.setColor(this.mFloorColor);
        this.mTextPaint = new Paint();
        this.mTextPaint.setColor(-1);
        this.mTextPaint.setAntiAlias(true);
        this.mTextPaint.setStyle(Style.FILL);
        this.mTextPaint.setTextAlign(Align.CENTER);
        this.mPaint = new Paint(1);
        this.mPaint.setStyle(Style.FILL);
        this.mPaint.setColor(this.mCheckedColor);
        this.mTickPath = new Path();
        this.mCenterPoint = new Point();
        this.mTickPoints = new Point[3];
        this.mTickPoints[0] = new Point();
        this.mTickPoints[1] = new Point();
        this.mTickPoints[2] = new Point();
        this.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                SmoothCheckBox.this.toggle();
                SmoothCheckBox.this.mTickDrawing = false;
                SmoothCheckBox.this.mDrewDistance = 0.0F;
                if (SmoothCheckBox.this.isChecked()) {
                    SmoothCheckBox.this.startCheckedAnimation();
                } else {
                    SmoothCheckBox.this.startUnCheckedAnimation();
                }

            }
        });
    }

    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("InstanceState", super.onSaveInstanceState());
        bundle.putBoolean("InstanceState", this.isChecked());
        return bundle;
    }

    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle)state;
            boolean isChecked = bundle.getBoolean("InstanceState");
            this.setChecked(isChecked);
            super.onRestoreInstanceState(bundle.getParcelable("InstanceState"));
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    public boolean isChecked() {
        return this.mChecked;
    }

    public void toggle() {
        this.setChecked(!this.isChecked());
    }

    public void setChecked(boolean checked) {
        this.mChecked = checked;
        this.reset();
        this.invalidate();
        if (this.mListener != null) {
            this.mListener.onCheckedChanged(this, this.mChecked);
        }

    }

    public void setChecked(boolean checked, boolean animate) {
        if (animate) {
            this.mTickDrawing = false;
            this.mChecked = checked;
            this.mDrewDistance = 0.0F;
            if (checked) {
                this.startCheckedAnimation();
            } else {
                this.startUnCheckedAnimation();
            }

            if (this.mListener != null) {
                this.mListener.onCheckedChanged(this, this.mChecked);
            }
        } else {
            this.setChecked(checked);
        }

    }

    private void reset() {
        this.mTickDrawing = true;
        this.mFloorScale = 1.0F;
        this.mScaleVal = this.isChecked() ? 0.0F : 1.0F;
        this.mFloorColor = this.isChecked() ? this.mCheckedColor : this.mFloorUnCheckedColor;
        this.mDrewDistance = this.isChecked() ? this.mLeftLineDistance + this.mRightLineDistance : 0.0F;
    }

    private int measureSize(int measureSpec) {
        int defSize = this.dp2px(this.getContext(), 25.0F);
        int specSize = MeasureSpec.getSize(measureSpec);
        int specMode = MeasureSpec.getMode(measureSpec);
        int result = 0;
        switch(specMode) {
            case -2147483648:
            case 0:
                result = Math.min(defSize, specSize);
                break;
            case 1073741824:
                result = specSize;
        }

        return result;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.setMeasuredDimension(this.measureSize(widthMeasureSpec), this.measureSize(heightMeasureSpec));
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        this.mWidth = this.getMeasuredWidth();
        this.mStrokeWidth = this.mStrokeWidth == 0 ? this.getMeasuredWidth() / 10 : this.mStrokeWidth;
        this.mStrokeWidth = this.mStrokeWidth > this.getMeasuredWidth() / 5 ? this.getMeasuredWidth() / 5 : this.mStrokeWidth;
        this.mStrokeWidth = this.mStrokeWidth < 3 ? 3 : this.mStrokeWidth;
        this.mCenterPoint.x = this.mWidth / 2;
        this.mCenterPoint.y = this.getMeasuredHeight() / 2;
        this.mTickPoints[0].x = Math.round((float)this.getMeasuredWidth() / 30.0F * 7.0F);
        this.mTickPoints[0].y = Math.round((float)this.getMeasuredHeight() / 30.0F * 14.0F);
        this.mTickPoints[1].x = Math.round((float)this.getMeasuredWidth() / 30.0F * 13.0F);
        this.mTickPoints[1].y = Math.round((float)this.getMeasuredHeight() / 30.0F * 20.0F);
        this.mTickPoints[2].x = Math.round((float)this.getMeasuredWidth() / 30.0F * 22.0F);
        this.mTickPoints[2].y = Math.round((float)this.getMeasuredHeight() / 30.0F * 10.0F);
        this.mLeftLineDistance = (float)Math.sqrt(Math.pow((double)(this.mTickPoints[1].x - this.mTickPoints[0].x), 2.0D) + Math.pow((double)(this.mTickPoints[1].y - this.mTickPoints[0].y), 2.0D));
        this.mRightLineDistance = (float)Math.sqrt(Math.pow((double)(this.mTickPoints[2].x - this.mTickPoints[1].x), 2.0D) + Math.pow((double)(this.mTickPoints[2].y - this.mTickPoints[1].y), 2.0D));
        this.mTickPaint.setStrokeWidth((float)this.mStrokeWidth);
    }

    public void setShowNumber(int number) {
        this.number = number;
    }

    protected void onDraw(Canvas canvas) {
        this.drawBorder(canvas);
        this.drawCenter(canvas);
        if (this.number >= 0) {
            this.drawText(canvas);
        } else {
            this.drawTick(canvas);
        }

    }

    private void drawCenter(Canvas canvas) {
        this.mPaint.setColor(this.mUnCheckedColor);
        float radius = (float)(this.mCenterPoint.x - this.mStrokeWidth) * this.mScaleVal;
        canvas.drawCircle((float)this.mCenterPoint.x, (float)this.mCenterPoint.y, radius, this.mPaint);
    }

    private void drawBorder(Canvas canvas) {
        this.mFloorPaint.setColor(this.mFloorColor);
        int radius = this.mCenterPoint.x;
        canvas.drawCircle((float)this.mCenterPoint.x, (float)this.mCenterPoint.y, (float)radius * this.mFloorScale, this.mFloorPaint);
    }

    private void drawTick(Canvas canvas) {
        if (this.mTickDrawing && this.isChecked()) {
            this.drawTickPath(canvas);
        }

    }

    private void drawText(Canvas canvas) {
        int fontSize = this.mCenterPoint.x;
        this.mTextPaint.setTextSize((float)fontSize);
        canvas.drawText("23", (float)this.mCenterPoint.x, (float)this.mCenterPoint.y - (this.mTextPaint.descent() + this.mTextPaint.ascent()) / 2.0F, this.mTextPaint);
    }

    private void drawTickPath(Canvas canvas) {
        this.mTickPath.reset();
        float stopX;
        float stopY;
        float step;
        if (this.mDrewDistance < this.mLeftLineDistance) {
            stopX = (float)this.mWidth / 20.0F < 3.0F ? 3.0F : (float)this.mWidth / 20.0F;
            this.mDrewDistance += stopX;
            stopY = (float)this.mTickPoints[0].x + (float)(this.mTickPoints[1].x - this.mTickPoints[0].x) * this.mDrewDistance / this.mLeftLineDistance;
            step = (float)this.mTickPoints[0].y + (float)(this.mTickPoints[1].y - this.mTickPoints[0].y) * this.mDrewDistance / this.mLeftLineDistance;
            this.mTickPath.moveTo((float)this.mTickPoints[0].x, (float)this.mTickPoints[0].y);
            this.mTickPath.lineTo(stopY, step);
            canvas.drawPath(this.mTickPath, this.mTickPaint);
            if (this.mDrewDistance > this.mLeftLineDistance) {
                this.mDrewDistance = this.mLeftLineDistance;
            }
        } else {
            this.mTickPath.moveTo((float)this.mTickPoints[0].x, (float)this.mTickPoints[0].y);
            this.mTickPath.lineTo((float)this.mTickPoints[1].x, (float)this.mTickPoints[1].y);
            canvas.drawPath(this.mTickPath, this.mTickPaint);
            if (this.mDrewDistance < this.mLeftLineDistance + this.mRightLineDistance) {
                stopX = (float)this.mTickPoints[1].x + (float)(this.mTickPoints[2].x - this.mTickPoints[1].x) * (this.mDrewDistance - this.mLeftLineDistance) / this.mRightLineDistance;
                stopY = (float)this.mTickPoints[1].y - (float)(this.mTickPoints[1].y - this.mTickPoints[2].y) * (this.mDrewDistance - this.mLeftLineDistance) / this.mRightLineDistance;
                this.mTickPath.reset();
                this.mTickPath.moveTo((float)this.mTickPoints[1].x, (float)this.mTickPoints[1].y);
                this.mTickPath.lineTo(stopX, stopY);
                canvas.drawPath(this.mTickPath, this.mTickPaint);
                step = this.mWidth / 20 < 3 ? 3.0F : (float)(this.mWidth / 20);
                this.mDrewDistance += step;
            } else {
                this.mTickPath.reset();
                this.mTickPath.moveTo((float)this.mTickPoints[1].x, (float)this.mTickPoints[1].y);
                this.mTickPath.lineTo((float)this.mTickPoints[2].x, (float)this.mTickPoints[2].y);
                canvas.drawPath(this.mTickPath, this.mTickPaint);
            }
        }

        if (this.mDrewDistance < this.mLeftLineDistance + this.mRightLineDistance) {
            this.postDelayed(new Runnable() {
                public void run() {
                    SmoothCheckBox.this.postInvalidate();
                }
            }, 10L);
        }

    }

    private void startCheckedAnimation() {
        ValueAnimator animator = ValueAnimator.ofFloat(new float[]{1.0F, 0.0F});
        animator.setDuration((long)(this.mAnimDuration / 3 * 2));
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                SmoothCheckBox.this.mScaleVal = (Float)animation.getAnimatedValue();
                SmoothCheckBox.this.mFloorColor = SmoothCheckBox.getGradientColor(SmoothCheckBox.this.mUnCheckedColor, SmoothCheckBox.this.mCheckedColor, 1.0F - SmoothCheckBox.this.mScaleVal);
                SmoothCheckBox.this.postInvalidate();
            }
        });
        animator.start();
        ValueAnimator floorAnimator = ValueAnimator.ofFloat(new float[]{1.0F, 0.8F, 1.0F});
        floorAnimator.setDuration((long)this.mAnimDuration);
        floorAnimator.setInterpolator(new LinearInterpolator());
        floorAnimator.addUpdateListener(new AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                SmoothCheckBox.this.mFloorScale = (Float)animation.getAnimatedValue();
                SmoothCheckBox.this.postInvalidate();
            }
        });
        floorAnimator.start();
        this.drawTickDelayed();
    }

    private void startUnCheckedAnimation() {
        ValueAnimator animator = ValueAnimator.ofFloat(new float[]{0.0F, 1.0F});
        animator.setDuration((long)this.mAnimDuration);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                SmoothCheckBox.this.mScaleVal = (Float)animation.getAnimatedValue();
                SmoothCheckBox.this.mFloorColor = SmoothCheckBox.getGradientColor(SmoothCheckBox.this.mCheckedColor, SmoothCheckBox.this.mFloorUnCheckedColor, SmoothCheckBox.this.mScaleVal);
                SmoothCheckBox.this.postInvalidate();
            }
        });
        animator.start();
        ValueAnimator floorAnimator = ValueAnimator.ofFloat(new float[]{1.0F, 0.8F, 1.0F});
        floorAnimator.setDuration((long)this.mAnimDuration);
        floorAnimator.setInterpolator(new LinearInterpolator());
        floorAnimator.addUpdateListener(new AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                SmoothCheckBox.this.mFloorScale = (Float)animation.getAnimatedValue();
                SmoothCheckBox.this.postInvalidate();
            }
        });
        floorAnimator.start();
    }

    private void drawTickDelayed() {
        this.postDelayed(new Runnable() {
            public void run() {
                SmoothCheckBox.this.mTickDrawing = true;
                SmoothCheckBox.this.postInvalidate();
            }
        }, (long)this.mAnimDuration);
    }

    private static int getGradientColor(int startColor, int endColor, float percent) {
        int startA = Color.alpha(startColor);
        int startR = Color.red(startColor);
        int startG = Color.green(startColor);
        int startB = Color.blue(startColor);
        int endA = Color.alpha(endColor);
        int endR = Color.red(endColor);
        int endG = Color.green(endColor);
        int endB = Color.blue(endColor);
        int currentA = (int)((float)startA * (1.0F - percent) + (float)endA * percent);
        int currentR = (int)((float)startR * (1.0F - percent) + (float)endR * percent);
        int currentG = (int)((float)startG * (1.0F - percent) + (float)endG * percent);
        int currentB = (int)((float)startB * (1.0F - percent) + (float)endB * percent);
        return Color.argb(currentA, currentR, currentG, currentB);
    }

    public void setOnCheckedChangeListener(SmoothCheckBox.OnCheckedChangeListener l) {
        this.mListener = l;
    }

    private int dp2px(Context context, float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dipValue * scale + 0.5F);
    }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(SmoothCheckBox var1, boolean var2);
    }
}
