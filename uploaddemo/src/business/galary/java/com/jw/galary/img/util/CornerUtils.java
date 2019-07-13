

package com.jw.galary.img.util;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;

public class CornerUtils {
    public CornerUtils() {
    }

    public static Drawable cornerDrawable(int bgColor, float cornerradius) {
        GradientDrawable bg = new GradientDrawable();
        bg.setCornerRadius(cornerradius);
        bg.setColor(bgColor);
        return bg;
    }

    public static Drawable cornerDrawable(int bgColor, float[] cornerradius) {
        GradientDrawable bg = new GradientDrawable();
        bg.setCornerRadii(cornerradius);
        bg.setColor(bgColor);
        return bg;
    }

    public static Drawable cornerDrawable(int bgColor, float[] cornerradius, int borderwidth, int bordercolor) {
        GradientDrawable bg = new GradientDrawable();
        bg.setCornerRadii(cornerradius);
        bg.setStroke(borderwidth, bordercolor);
        bg.setColor(bgColor);
        return bg;
    }

    public static StateListDrawable btnSelector(float radius, int normalColor, int pressColor, int enableColor, int position) {
        StateListDrawable bg = new StateListDrawable();
        Drawable normal = null;
        Drawable pressed = null;
        Drawable enabled = null;
        if (position == 0) {
            normal = cornerDrawable(normalColor, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, radius, radius});
            pressed = cornerDrawable(pressColor, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, radius, radius});
            enabled = cornerDrawable(enableColor, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, radius, radius});
        } else if (position == 1) {
            normal = cornerDrawable(normalColor, new float[]{0.0F, 0.0F, 0.0F, 0.0F, radius, radius, 0.0F, 0.0F});
            pressed = cornerDrawable(pressColor, new float[]{0.0F, 0.0F, 0.0F, 0.0F, radius, radius, 0.0F, 0.0F});
            enabled = cornerDrawable(enableColor, new float[]{0.0F, 0.0F, 0.0F, 0.0F, radius, radius, 0.0F, 0.0F});
        } else if (position == -1) {
            normal = cornerDrawable(normalColor, new float[]{0.0F, 0.0F, 0.0F, 0.0F, radius, radius, radius, radius});
            pressed = cornerDrawable(pressColor, new float[]{0.0F, 0.0F, 0.0F, 0.0F, radius, radius, radius, radius});
            enabled = cornerDrawable(enableColor, new float[]{0.0F, 0.0F, 0.0F, 0.0F, radius, radius, radius, radius});
        } else if (position == -2) {
            normal = cornerDrawable(normalColor, radius);
            pressed = cornerDrawable(pressColor, radius);
            enabled = cornerDrawable(enableColor, radius);
        }

        bg.addState(new int[]{-16842919}, normal);
        bg.addState(new int[]{16842919}, pressed);
        bg.addState(new int[]{16842910}, enabled);
        return bg;
    }

    public static StateListDrawable listItemSelector(float radius, int normalColor, int pressColor, boolean isLastPostion) {
        StateListDrawable bg = new StateListDrawable();
        Drawable normal = null;
        Drawable pressed = null;
        if (!isLastPostion) {
            normal = new ColorDrawable(normalColor);
            pressed = new ColorDrawable(pressColor);
        } else {
            normal = cornerDrawable(normalColor, new float[]{0.0F, 0.0F, 0.0F, 0.0F, radius, radius, radius, radius});
            pressed = cornerDrawable(pressColor, new float[]{0.0F, 0.0F, 0.0F, 0.0F, radius, radius, radius, radius});
        }

        bg.addState(new int[]{-16842919}, (Drawable)normal);
        bg.addState(new int[]{16842919}, (Drawable)pressed);
        return bg;
    }

    public static StateListDrawable listItemSelector(float radius, int normalColor, int pressColor, int itemTotalSize, int itemPosition) {
        StateListDrawable bg = new StateListDrawable();
        Drawable normal = null;
        Drawable pressed = null;
        if (itemPosition == 0 && itemPosition == itemTotalSize - 1) {
            normal = cornerDrawable(normalColor, new float[]{radius, radius, radius, radius, radius, radius, radius, radius});
            pressed = cornerDrawable(pressColor, new float[]{radius, radius, radius, radius, radius, radius, radius, radius});
        } else if (itemPosition == 0) {
            normal = cornerDrawable(normalColor, new float[]{radius, radius, radius, radius, 0.0F, 0.0F, 0.0F, 0.0F});
            pressed = cornerDrawable(pressColor, new float[]{radius, radius, radius, radius, 0.0F, 0.0F, 0.0F, 0.0F});
        } else if (itemPosition == itemTotalSize - 1) {
            normal = cornerDrawable(normalColor, new float[]{0.0F, 0.0F, 0.0F, 0.0F, radius, radius, radius, radius});
            pressed = cornerDrawable(pressColor, new float[]{0.0F, 0.0F, 0.0F, 0.0F, radius, radius, radius, radius});
        } else {
            normal = new ColorDrawable(normalColor);
            pressed = new ColorDrawable(pressColor);
        }

        bg.addState(new int[]{-16842919}, (Drawable)normal);
        bg.addState(new int[]{16842919}, (Drawable)pressed);
        return bg;
    }
}
