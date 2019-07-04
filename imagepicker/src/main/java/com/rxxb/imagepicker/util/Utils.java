//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.rxxb.imagepicker.util;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.os.Build.VERSION;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.ViewConfiguration;
import android.view.WindowManager;

public class Utils {
    public Utils() {
    }

    public static int getStatusHeight(Context context) {
        int statusHeight = -1;

        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height").get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception var5) {
            var5.printStackTrace();
        }

        return statusHeight;
    }

    public static int getImageItemWidth(Activity activity) {
        int screenWidth = activity.getResources().getDisplayMetrics().widthPixels;
        int densityDpi = activity.getResources().getDisplayMetrics().densityDpi;
        int cols = 4;
        cols = cols < 3 ? 3 : cols;
        int columnSpace = (int)(2.0F * activity.getResources().getDisplayMetrics().density);
        return (screenWidth - columnSpace * (cols - 1)) / cols;
    }

    public static int getImageItemWidth(Activity activity, int cols, int space) {
        int screenWidth = activity.getResources().getDisplayMetrics().widthPixels;
        cols = cols < 3 ? 3 : cols;
        int columnSpace = (int)((float)space * activity.getResources().getDisplayMetrics().density);
        return (screenWidth - columnSpace * (cols - 1)) / cols;
    }

    public static boolean existSDCard() {
        return Environment.getExternalStorageState().equals("mounted");
    }

    public static DisplayMetrics getScreenPix(Activity activity) {
        DisplayMetrics displaysMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaysMetrics);
        return displaysMetrics;
    }

    public static int dp2px(Context context, float dpVal) {
        return (int)TypedValue.applyDimension(1, dpVal, context.getResources().getDisplayMetrics());
    }

    public static boolean hasVirtualNavigationBar(Context context) {
        boolean hasSoftwareKeys = true;
        if (VERSION.SDK_INT >= 17) {
            Display d = ((WindowManager)context.getSystemService("window")).getDefaultDisplay();
            DisplayMetrics realDisplayMetrics = new DisplayMetrics();
            d.getRealMetrics(realDisplayMetrics);
            int realHeight = realDisplayMetrics.heightPixels;
            int realWidth = realDisplayMetrics.widthPixels;
            DisplayMetrics displayMetrics = new DisplayMetrics();
            d.getMetrics(displayMetrics);
            int displayHeight = displayMetrics.heightPixels;
            int displayWidth = displayMetrics.widthPixels;
            hasSoftwareKeys = realWidth - displayWidth > 0 || realHeight - displayHeight > 0;
        } else if (VERSION.SDK_INT >= 14) {
            boolean hasMenuKey = ViewConfiguration.get(context).hasPermanentMenuKey();
            boolean hasBackKey = KeyCharacterMap.deviceHasKey(4);
            hasSoftwareKeys = !hasMenuKey && !hasBackKey;
        }

        return hasSoftwareKeys;
    }

    public static int getNavigationBarHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        return resourceId > 0 ? context.getResources().getDimensionPixelSize(resourceId) : 0;
    }
}
