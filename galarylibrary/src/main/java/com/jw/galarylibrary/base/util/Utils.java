

package com.jw.galarylibrary.base.util;

import android.app.Activity;

public class Utils {
    public Utils() {
    }

    public static int getImageItemWidth(Activity activity) {
        int screenWidth = activity.getResources().getDisplayMetrics().widthPixels;
        int densityDpi = activity.getResources().getDisplayMetrics().densityDpi;
        int cols = 4;
        cols = cols < 3 ? 3 : cols;
        int columnSpace = (int) (2.0F * activity.getResources().getDisplayMetrics().density);
        return (screenWidth - columnSpace * (cols - 1)) / cols;
    }

    public static int getImageItemWidth(Activity activity, int cols, int space) {
        int screenWidth = activity.getResources().getDisplayMetrics().widthPixels;
        cols = cols < 3 ? 3 : cols;
        int columnSpace = (int) ((float) space * activity.getResources().getDisplayMetrics().density);
        return (screenWidth - columnSpace * (cols - 1)) / cols;
    }
}
