

package com.rxxb.imagepicker.crop.image;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;

public class CropArea {
    private final Rect imageRect;
    private final Rect cropRect;

    public static CropArea create(RectF coordinateSystem, RectF imageRect, RectF cropRect) {
        return new CropArea(moveRectToCoordinateSystem(coordinateSystem, imageRect), moveRectToCoordinateSystem(coordinateSystem, cropRect));
    }

    private static Rect moveRectToCoordinateSystem(RectF system, RectF rect) {
        float originX = system.left;
        float originY = system.top;
        return new Rect(Math.round(rect.left - originX), Math.round(rect.top - originY), Math.round(rect.right - originX), Math.round(rect.bottom - originY));
    }

    public CropArea(Rect imageRect, Rect cropRect) {
        this.imageRect = imageRect;
        this.cropRect = cropRect;
    }

    public Bitmap applyCropTo(Bitmap bitmap) {
        int x = this.findRealCoordinate(bitmap.getWidth(), this.cropRect.left, (float)this.imageRect.width());
        int y = this.findRealCoordinate(bitmap.getHeight(), this.cropRect.top, (float)this.imageRect.height());
        int width = Math.abs(this.findRealCoordinate(bitmap.getWidth(), this.cropRect.width(), (float)this.imageRect.width()));
        int height = Math.abs(this.findRealCoordinate(bitmap.getHeight(), this.cropRect.height(), (float)this.imageRect.height()));
        Bitmap immutableCropped = null;
        if (this.imageRect.contains(this.cropRect)) {
            System.out.println("完全包含....");
            immutableCropped = Bitmap.createBitmap(bitmap, x, y, width, height);
        } else if (Rect.intersects(this.imageRect, this.cropRect)) {
            System.out.println("有相交的部分");
            immutableCropped = Bitmap.createBitmap(width, height, Config.ARGB_8888);
            immutableCropped.eraseColor(-16777216);
            Canvas canvas = new Canvas(immutableCropped);
            Rect intersectRect = new Rect();
            int cropX;
            if (x < 0) {
                intersectRect.left = -x;
                cropX = 0;
            } else {
                intersectRect.left = 0;
                cropX = x;
            }

            int cropY;
            if (y < 0) {
                intersectRect.top = -y;
                cropY = 0;
            } else {
                intersectRect.top = 0;
                cropY = y;
            }

            int cropWidth = this.findRealCoordinate(bitmap.getWidth(), Math.min(this.cropRect.right, this.imageRect.right) - Math.max(this.cropRect.left, this.imageRect.left), (float)this.imageRect.width());
            int cropHeight = this.findRealCoordinate(bitmap.getHeight(), Math.min(this.cropRect.bottom, this.imageRect.bottom) - Math.max(this.cropRect.top, this.imageRect.top), (float)this.imageRect.height());
            Bitmap tempCropped = Bitmap.createBitmap(bitmap, cropX, cropY, cropWidth, cropHeight);
            intersectRect.right = intersectRect.left + cropWidth;
            intersectRect.bottom = intersectRect.top + cropHeight;
            canvas.drawBitmap(tempCropped, new Rect(0, 0, cropWidth, cropHeight), intersectRect, (Paint)null);
            tempCropped.recycle();
        } else {
            System.out.println("完全没相交的部分");
        }

        return immutableCropped.copy(immutableCropped.getConfig(), true);
    }

    private int findRealCoordinate(int imageRealSize, int cropCoordinate, float cropImageSize) {
        return Math.round((float)(imageRealSize * cropCoordinate) / cropImageSize);
    }
}
