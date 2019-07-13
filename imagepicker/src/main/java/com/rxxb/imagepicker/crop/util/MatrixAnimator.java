

package com.rxxb.imagepicker.crop.util;

import android.animation.FloatEvaluator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.graphics.Matrix;
import android.view.animation.AccelerateDecelerateInterpolator;
import java.lang.ref.WeakReference;

public class MatrixAnimator {
    public MatrixAnimator() {
    }

    public void animate(Matrix initial, Matrix target, AnimatorUpdateListener listener) {
        ValueAnimator animator = ValueAnimator.ofObject(new MatrixAnimator.MatrixEvaluator(), new Object[]{initial, target});
        animator.addUpdateListener(new MatrixAnimator.SafeListener(listener));
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(200L);
        animator.start();
    }

    private static class SafeListener implements AnimatorUpdateListener {
        private WeakReference<AnimatorUpdateListener> wrapped;

        private SafeListener(AnimatorUpdateListener wrapped) {
            this.wrapped = new WeakReference(wrapped);
        }

        public void onAnimationUpdate(ValueAnimator animation) {
            AnimatorUpdateListener listener = (AnimatorUpdateListener)this.wrapped.get();
            if (listener != null) {
                listener.onAnimationUpdate(animation);
            }

        }
    }

    private static class MatrixEvaluator implements TypeEvaluator<Matrix> {
        private Matrix current = new Matrix();
        private Matrix lastStart;
        private Matrix lastEnd;
        private FloatEvaluator floatEvaluator = new FloatEvaluator();
        private float initialTranslationX;
        private float initialTranslationY;
        private float initialScale;
        private float endTranslationX;
        private float endTranslationY;
        private float endScale;

        public MatrixEvaluator() {
        }

        public Matrix evaluate(float fraction, Matrix startValue, Matrix endValue) {
            if (this.shouldReinitialize(startValue, endValue)) {
                this.collectValues(startValue, endValue);
            }

            float translationX = this.floatEvaluator.evaluate(fraction, this.initialTranslationX, this.endTranslationX);
            float translationY = this.floatEvaluator.evaluate(fraction, this.initialTranslationY, this.endTranslationY);
            float scale = this.floatEvaluator.evaluate(fraction, this.initialScale, this.endScale);
            this.current.reset();
            this.current.postScale(scale, scale);
            this.current.postTranslate(translationX, translationY);
            return this.current;
        }

        private boolean shouldReinitialize(Matrix start, Matrix end) {
            return this.lastStart != start || this.lastEnd != end;
        }

        private void collectValues(Matrix start, Matrix end) {
            MatrixUtils matrixUtils = new MatrixUtils();
            this.initialTranslationX = matrixUtils.getXTranslation(start);
            this.initialTranslationY = matrixUtils.getYTranslation(start);
            this.initialScale = matrixUtils.getScaleX(start);
            this.endTranslationX = matrixUtils.getXTranslation(end);
            this.endTranslationY = matrixUtils.getYTranslation(end);
            this.endScale = matrixUtils.getScaleX(end);
            this.lastStart = start;
            this.lastEnd = end;
        }
    }
}
