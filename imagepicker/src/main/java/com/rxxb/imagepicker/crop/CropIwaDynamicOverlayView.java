//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.rxxb.imagepicker.crop;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.SparseArray;
import android.view.MotionEvent;
import com.rxxb.imagepicker.crop.config.CropIwaOverlayConfig;
import com.rxxb.imagepicker.crop.shape.CropIwaShape;
import com.rxxb.imagepicker.crop.util.CropIwaUtils;
import java.util.Arrays;

@SuppressLint({"ViewConstructor"})
class CropIwaDynamicOverlayView extends CropIwaOverlayView {
    private static final float CLICK_AREA_CORNER_POINT = (float)CropIwaUtils.dpToPx(24);
    private static final int LEFT_TOP = 0;
    private static final int RIGHT_TOP = 1;
    private static final int LEFT_BOTTOM = 2;
    private static final int RIGHT_BOTTOM = 3;
    private float[][] cornerSides;
    private CropIwaDynamicOverlayView.CornerPoint[] cornerPoints;
    private SparseArray<CropIwaDynamicOverlayView.CornerPoint> fingerToCornerMapping;
    private PointF cropDragStartPoint;
    private RectF cropRectBeforeDrag;

    public CropIwaDynamicOverlayView(Context context, CropIwaOverlayConfig config) {
        super(context, config);
    }

    protected void initWith(CropIwaOverlayConfig config) {
        super.initWith(config);
        this.fingerToCornerMapping = new SparseArray();
        this.cornerPoints = new CropIwaDynamicOverlayView.CornerPoint[4];
        float cornerCathetusLength = (float)Math.min(config.getMinWidth(), config.getMinHeight()) * 0.3F;
        this.cornerSides = this.generateCornerSides(cornerCathetusLength);
    }

    public void onImagePositioned(RectF imageRect) {
        super.onImagePositioned(imageRect);
        this.initCornerPoints();
        this.invalidate();
    }

    private void initCornerPoints() {
        if (this.cropRect.width() > 0.0F && this.cropRect.height() > 0.0F) {
            if (CropIwaUtils.isAnyNull(Arrays.asList(this.cornerPoints))) {
                PointF leftTop = new PointF(this.cropRect.left, this.cropRect.top);
                PointF leftBot = new PointF(this.cropRect.left, this.cropRect.bottom);
                PointF rightTop = new PointF(this.cropRect.right, this.cropRect.top);
                PointF rightBot = new PointF(this.cropRect.right, this.cropRect.bottom);
                this.cornerPoints[0] = new CropIwaDynamicOverlayView.CornerPoint(leftTop, rightTop, leftBot);
                this.cornerPoints[2] = new CropIwaDynamicOverlayView.CornerPoint(leftBot, rightBot, leftTop);
                this.cornerPoints[1] = new CropIwaDynamicOverlayView.CornerPoint(rightTop, leftTop, rightBot);
                this.cornerPoints[3] = new CropIwaDynamicOverlayView.CornerPoint(rightBot, leftBot, rightTop);
            } else {
                this.updateCornerPointsCoordinates();
            }
        }

    }

    public boolean onTouchEvent(MotionEvent ev) {
        if (!this.shouldDrawOverlay) {
            return false;
        } else {
            switch(ev.getActionMasked()) {
                case 0:
                    this.onStartGesture(ev);
                    break;
                case 1:
                case 3:
                    this.onEndGesture();
                    break;
                case 2:
                    this.onPointerMove(ev);
                    break;
                case 4:
                default:
                    return false;
                case 5:
                    this.onPointerDown(ev);
                    break;
                case 6:
                    this.onPointerUp(ev);
            }

            this.invalidate();
            return true;
        }
    }

    private void onStartGesture(MotionEvent ev) {
        if (!this.tryAssociateWithCorner(ev)) {
            int index = ev.getActionIndex();
            if (this.cropRect.contains(ev.getX(index), ev.getY(index))) {
                this.cropDragStartPoint = new PointF(ev.getX(index), ev.getY(index));
                this.cropRectBeforeDrag = new RectF(this.cropRect);
            }

        }
    }

    private void onPointerDown(MotionEvent ev) {
        if (this.isResizing()) {
            this.tryAssociateWithCorner(ev);
        }

    }

    private void onPointerUp(MotionEvent ev) {
        int id = ev.getPointerId(ev.getActionIndex());
        this.fingerToCornerMapping.remove(id);
    }

    private void onPointerMove(MotionEvent ev) {
        if (this.isResizing()) {
            for(int i = 0; i < ev.getPointerCount(); ++i) {
                int id = ev.getPointerId(i);
                CropIwaDynamicOverlayView.CornerPoint point = (CropIwaDynamicOverlayView.CornerPoint)this.fingerToCornerMapping.get(id);
                if (point != null) {
                    point.processDrag(CropIwaUtils.boundValue(ev.getX(i), 0.0F, (float)this.getWidth()), CropIwaUtils.boundValue(ev.getY(i), 0.0F, (float)this.getHeight()));
                }
            }

            this.updateCropAreaCoordinates();
        } else if (this.isDraggingCropArea()) {
            float deltaX = ev.getX() - this.cropDragStartPoint.x;
            float deltaY = ev.getY() - this.cropDragStartPoint.y;
            this.cropRect = CropIwaUtils.moveRectBounded(this.cropRectBeforeDrag, deltaX, deltaY, this.getWidth(), this.getHeight(), this.cropRect);
            this.updateCornerPointsCoordinates();
        }

    }

    private void onEndGesture() {
        if (this.cropRectBeforeDrag != null && !this.cropRectBeforeDrag.equals(this.cropRect)) {
            this.notifyNewBounds();
        }

        if (this.fingerToCornerMapping.size() > 0) {
            this.notifyNewBounds();
        }

        this.fingerToCornerMapping.clear();
        this.cropDragStartPoint = null;
        this.cropRectBeforeDrag = null;
    }

    private void updateCornerPointsCoordinates() {
        this.cornerPoints[0].processDrag(this.cropRect.left, this.cropRect.top);
        this.cornerPoints[3].processDrag(this.cropRect.right, this.cropRect.bottom);
    }

    private void updateCropAreaCoordinates() {
        this.cropRect.set(this.cornerPoints[0].x(), this.cornerPoints[0].y(), this.cornerPoints[3].x(), this.cornerPoints[3].y());
    }

    protected void onDraw(Canvas canvas) {
        if (this.shouldDrawOverlay) {
            super.onDraw(canvas);
            if (this.areCornersInitialized()) {
                CropIwaShape shape = this.config.getCropShape();

                for(int i = 0; i < this.cornerPoints.length; ++i) {
                    shape.drawCorner(canvas, this.cornerPoints[i].x(), this.cornerPoints[i].y(), this.cornerSides[i][0], this.cornerSides[i][1]);
                }
            }
        }

    }

    public boolean isResizing() {
        return this.fingerToCornerMapping.size() != 0;
    }

    public boolean isDraggingCropArea() {
        return this.cropDragStartPoint != null;
    }

    private boolean tryAssociateWithCorner(MotionEvent ev) {
        int index = ev.getActionIndex();
        return this.tryAssociateWithCorner(ev.getPointerId(index), ev.getX(index), ev.getY(index));
    }

    private boolean tryAssociateWithCorner(int id, float x, float y) {
        CropIwaDynamicOverlayView.CornerPoint[] var4 = this.cornerPoints;
        int var5 = var4.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            CropIwaDynamicOverlayView.CornerPoint cornerPoint = var4[var6];
            if (cornerPoint.isClicked(x, y)) {
                this.fingerToCornerMapping.put(id, cornerPoint);
                return true;
            }
        }

        return false;
    }

    private boolean areCornersInitialized() {
        return this.cornerPoints[0] != null && this.cornerPoints[0].isValid();
    }

    public void onConfigChanged() {
        super.onConfigChanged();
        this.initCornerPoints();
    }

    private float[][] generateCornerSides(float length) {
        float[][] result = new float[4][2];
        result[0] = new float[]{length, length};
        result[2] = new float[]{length, -length};
        result[1] = new float[]{-length, length};
        result[3] = new float[]{-length, -length};
        return result;
    }

    private class CornerPoint {
        private RectF clickableArea;
        private PointF thisPoint;
        private PointF horizontalNeighbourPoint;
        private PointF verticalNeighbourPoint;

        public CornerPoint(PointF thisPoint, PointF horizontalNeighbourPoint, PointF verticalNeighbourPoint) {
            this.thisPoint = thisPoint;
            this.horizontalNeighbourPoint = horizontalNeighbourPoint;
            this.verticalNeighbourPoint = verticalNeighbourPoint;
            this.clickableArea = new RectF();
        }

        public void processDrag(float x, float y) {
            float newX = this.computeCoordinate(this.thisPoint.x, x, this.horizontalNeighbourPoint.x, CropIwaDynamicOverlayView.this.config.getMinWidth());
            this.thisPoint.x = newX;
            this.verticalNeighbourPoint.x = newX;
            float newY = this.computeCoordinate(this.thisPoint.y, y, this.verticalNeighbourPoint.y, CropIwaDynamicOverlayView.this.config.getMinHeight());
            this.thisPoint.y = newY;
            this.horizontalNeighbourPoint.y = newY;
        }

        private float computeCoordinate(float old, float candidate, float opposite, int min) {
            boolean isCandidateAllowed = Math.abs(candidate - opposite) > (float)min;
            boolean isDraggingFromLeftOrTop = opposite > old;
            float minAllowedPosition;
            if (isDraggingFromLeftOrTop) {
                minAllowedPosition = opposite - (float)min;
                isCandidateAllowed &= candidate < opposite;
            } else {
                minAllowedPosition = opposite + (float)min;
                isCandidateAllowed &= candidate > opposite;
            }

            return isCandidateAllowed ? candidate : minAllowedPosition;
        }

        public boolean isClicked(float x, float y) {
            this.clickableArea.set(this.thisPoint.x, this.thisPoint.y, this.thisPoint.x, this.thisPoint.y);
            CropIwaUtils.enlargeRectBy(CropIwaDynamicOverlayView.CLICK_AREA_CORNER_POINT, this.clickableArea);
            return this.clickableArea.contains(x, y);
        }

        public float x() {
            return this.thisPoint.x;
        }

        public float y() {
            return this.thisPoint.y;
        }

        public String toString() {
            return this.thisPoint.toString();
        }

        public boolean isValid() {
            return Math.abs(this.thisPoint.x - this.horizontalNeighbourPoint.x) >= (float)CropIwaDynamicOverlayView.this.config.getMinWidth();
        }
    }
}
