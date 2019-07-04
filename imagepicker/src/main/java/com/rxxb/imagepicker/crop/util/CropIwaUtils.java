//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.rxxb.imagepicker.crop.util;

import android.content.res.Resources;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import java.io.Closeable;
import java.io.File;
import java.util.Iterator;

public abstract class CropIwaUtils {
    public CropIwaUtils() {
    }

    public static void delete(@Nullable File file) {
        if (file != null) {
            file.delete();
        }

    }

    public static boolean isAnyNull(Iterable<?> iterable) {
        Iterator var1 = iterable.iterator();

        Object o;
        do {
            if (!var1.hasNext()) {
                return false;
            }

            o = var1.next();
        } while(o != null);

        return true;
    }

    public static void constrainRectTo(int minLeft, int minTop, int maxRight, int maxBottom, RectF rect) {
        rect.set(Math.max(rect.left, (float)minLeft), Math.max(rect.top, (float)minTop), Math.min(rect.right, (float)maxRight), Math.min(rect.bottom, (float)maxBottom));
    }

    public static void closeSilently(@Nullable Closeable c) {
        try {
            if (c != null) {
                c.close();
            }
        } catch (Exception var2) {
        }

    }

    public static RectF enlargeRectBy(float value, @NonNull RectF outRect) {
        outRect.top -= value;
        outRect.bottom += value;
        outRect.left -= value;
        outRect.right += value;
        return outRect;
    }

    public static float boundValue(float value, float lowBound, float highBound) {
        return Math.max(Math.min(value, highBound), lowBound);
    }

    public static RectF moveRectBounded(@NonNull RectF initial, float deltaX, float deltaY, int horizontalBound, int verticalBound, @NonNull RectF outRect) {
        float newLeft = boundValue(initial.left + deltaX, 0.0F, (float)horizontalBound - initial.width());
        float newRight = newLeft + initial.width();
        float newTop = boundValue(initial.top + deltaY, 0.0F, (float)verticalBound - initial.height());
        float newBottom = newTop + initial.height();
        outRect.set(newLeft, newTop, newRight, newBottom);
        return outRect;
    }

    public static int dpToPx(int dp) {
        DisplayMetrics dm = Resources.getSystem().getDisplayMetrics();
        return Math.round(dm.density * (float)dp);
    }
}
