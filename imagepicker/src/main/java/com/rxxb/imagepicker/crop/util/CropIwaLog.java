//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.rxxb.imagepicker.crop.util;

import android.util.Log;

public class CropIwaLog {
    private static final String LOG_TAG = CropIwaLog.class.getSimpleName();
    private static boolean LOG_ON = true;

    public CropIwaLog() {
    }

    public static void d(String formatStr, Object... args) {
        if (LOG_ON) {
            Log.d(LOG_TAG, String.format(formatStr, args));
        }

    }

    public static void e(String message, Throwable e) {
        if (LOG_ON) {
            Log.e(LOG_TAG, message, e);
        }

    }

    public static void setEnabled(boolean enabled) {
        LOG_ON = enabled;
    }
}
