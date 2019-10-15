

package com.jw.croplibrary.img.util;

import android.graphics.RectF;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

public class TensionInterpolator {
    private static final float TENSION_FACTOR = 10.0F;
    private float tensionZone;
    private float tensionZonePull;
    private TensionInterpolator.TensionBorder yTensionBounds;
    private TensionInterpolator.TensionBorder xTensionBounds;
    private Interpolator interpolator = new DecelerateInterpolator(2.0F);
    private float downX;
    private float downY;

    public TensionInterpolator() {
    }

    public void onDown(float x, float y, RectF draggedObj, RectF tensionStartBorder) {
        this.downX = x;
        this.downY = y;
        this.tensionZone = Math.min(draggedObj.width(), draggedObj.height()) * 0.2F;
        this.tensionZonePull = this.tensionZone * 10.0F;
        this.xTensionBounds = new TensionInterpolator.TensionBorder(draggedObj.right - tensionStartBorder.right, tensionStartBorder.left - draggedObj.left);
        this.yTensionBounds = new TensionInterpolator.TensionBorder(draggedObj.bottom - tensionStartBorder.bottom, tensionStartBorder.top - draggedObj.top);
    }

    public float interpolateX(float x) {
        return this.downX + this.interpolateDistance(x - this.downX, this.xTensionBounds);
    }

    public float interpolateY(float y) {
        return this.downY + this.interpolateDistance(y - this.downY, this.yTensionBounds);
    }

    private float interpolateDistance(float delta, TensionInterpolator.TensionBorder tensionBorder) {
        float distance = Math.abs(delta);
        float direction = delta >= 0.0F ? 1.0F : -1.0F;
        float tensionStart = direction == 1.0F ? tensionBorder.getPositiveTensionStart() : tensionBorder.getNegativeTensionStart();
        if (distance < tensionStart) {
            return delta;
        } else {
            float tensionDiff = distance - tensionStart;
            float tensionEnd = tensionStart + this.tensionZone;
            if (distance >= this.tensionZonePull + tensionStart) {
                return tensionEnd * direction;
            } else {
                float realProgress = tensionDiff / this.tensionZonePull;
                float progress = this.interpolator.getInterpolation(realProgress);
                return (tensionStart + progress * this.tensionZone) * direction;
            }
        }
    }

    private static class TensionBorder {
        private float negativeTensionStart;
        private float positiveTensionStart;

        private TensionBorder(float negativeTensionStart, float positiveTensionStart) {
            this.negativeTensionStart = Math.max(negativeTensionStart, 0.0F);
            this.positiveTensionStart = Math.max(positiveTensionStart, 0.0F);
        }

        public float getNegativeTensionStart() {
            return this.negativeTensionStart;
        }

        public float getPositiveTensionStart() {
            return this.positiveTensionStart;
        }

        public String toString() {
            return "TensionBorder{negativeTensionStart=" + this.negativeTensionStart + ", positiveTensionStart=" + this.positiveTensionStart + '}';
        }
    }
}
