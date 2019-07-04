//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.rxxb.imagepicker.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import java.lang.reflect.Method;

public class SystemBarTintManager {
    public static final int DEFAULT_TINT_COLOR = -1728053248;
    private static String sNavBarOverride;
    private final SystemBarTintManager.SystemBarConfig mConfig;
    private boolean mStatusBarAvailable;
    private boolean mNavBarAvailable;
    private boolean mStatusBarTintEnabled;
    private boolean mNavBarTintEnabled;
    private View mStatusBarTintView;
    private View mNavBarTintView;

    @TargetApi(19)
    public SystemBarTintManager(Activity activity) {
        Window win = activity.getWindow();
        ViewGroup decorViewGroup = (ViewGroup)win.getDecorView();
        if (VERSION.SDK_INT >= 19) {
            int[] attrs = new int[]{16843759, 16843760};
            TypedArray a = activity.obtainStyledAttributes(attrs);

            try {
                this.mStatusBarAvailable = a.getBoolean(0, false);
                this.mNavBarAvailable = a.getBoolean(1, false);
            } finally {
                a.recycle();
            }

            LayoutParams winParams = win.getAttributes();
            int bits = 67108864;
            if ((winParams.flags & bits) != 0) {
                this.mStatusBarAvailable = true;
            }

            bits = 134217728;
            if ((winParams.flags & bits) != 0) {
                this.mNavBarAvailable = true;
            }
        }

        this.mConfig = new SystemBarTintManager.SystemBarConfig(activity, this.mStatusBarAvailable, this.mNavBarAvailable);
        if (!this.mConfig.hasNavigtionBar()) {
            this.mNavBarAvailable = false;
        }

        if (this.mStatusBarAvailable) {
            this.setupStatusBarView(activity, decorViewGroup);
        }

        if (this.mNavBarAvailable) {
            this.setupNavBarView(activity, decorViewGroup);
        }

    }

    public void setStatusBarTintEnabled(boolean enabled) {
        this.mStatusBarTintEnabled = enabled;
        if (this.mStatusBarAvailable) {
            this.mStatusBarTintView.setVisibility(enabled ? 0 : 8);
        }

    }

    public void setNavigationBarTintEnabled(boolean enabled) {
        this.mNavBarTintEnabled = enabled;
        if (this.mNavBarAvailable) {
            this.mNavBarTintView.setVisibility(enabled ? 0 : 8);
        }

    }

    public void setTintColor(int color) {
        this.setStatusBarTintColor(color);
        this.setNavigationBarTintColor(color);
    }

    public void setTintResource(int res) {
        this.setStatusBarTintResource(res);
        this.setNavigationBarTintResource(res);
    }

    public void setTintDrawable(Drawable drawable) {
        this.setStatusBarTintDrawable(drawable);
        this.setNavigationBarTintDrawable(drawable);
    }

    public void setTintAlpha(float alpha) {
        this.setStatusBarAlpha(alpha);
        this.setNavigationBarAlpha(alpha);
    }

    public void setStatusBarTintColor(int color) {
        if (this.mStatusBarAvailable) {
            this.mStatusBarTintView.setBackgroundColor(color);
        }

    }

    public void setStatusBarTintResource(int res) {
        if (this.mStatusBarAvailable) {
            this.mStatusBarTintView.setBackgroundResource(res);
        }

    }

    public void setStatusBarTintDrawable(Drawable drawable) {
        if (this.mStatusBarAvailable) {
            this.mStatusBarTintView.setBackgroundDrawable(drawable);
        }

    }

    @TargetApi(11)
    public void setStatusBarAlpha(float alpha) {
        if (this.mStatusBarAvailable && VERSION.SDK_INT >= 11) {
            this.mStatusBarTintView.setAlpha(alpha);
        }

    }

    public void setNavigationBarTintColor(int color) {
        if (this.mNavBarAvailable) {
            this.mNavBarTintView.setBackgroundColor(color);
        }

    }

    public void setNavigationBarTintResource(int res) {
        if (this.mNavBarAvailable) {
            this.mNavBarTintView.setBackgroundResource(res);
        }

    }

    public void setNavigationBarTintDrawable(Drawable drawable) {
        if (this.mNavBarAvailable) {
            this.mNavBarTintView.setBackgroundDrawable(drawable);
        }

    }

    @TargetApi(11)
    public void setNavigationBarAlpha(float alpha) {
        if (this.mNavBarAvailable && VERSION.SDK_INT >= 11) {
            this.mNavBarTintView.setAlpha(alpha);
        }

    }

    public SystemBarTintManager.SystemBarConfig getConfig() {
        return this.mConfig;
    }

    public boolean isStatusBarTintEnabled() {
        return this.mStatusBarTintEnabled;
    }

    public boolean isNavBarTintEnabled() {
        return this.mNavBarTintEnabled;
    }

    private void setupStatusBarView(Context context, ViewGroup decorViewGroup) {
        this.mStatusBarTintView = new View(context);
        android.widget.FrameLayout.LayoutParams params = new android.widget.FrameLayout.LayoutParams(-1, this.mConfig.getStatusBarHeight());
        params.gravity = 48;
        if (this.mNavBarAvailable && !this.mConfig.isNavigationAtBottom()) {
            params.rightMargin = this.mConfig.getNavigationBarWidth();
        }

        this.mStatusBarTintView.setLayoutParams(params);
        this.mStatusBarTintView.setBackgroundColor(-1728053248);
        this.mStatusBarTintView.setVisibility(8);
        decorViewGroup.addView(this.mStatusBarTintView);
    }

    private void setupNavBarView(Context context, ViewGroup decorViewGroup) {
        this.mNavBarTintView = new View(context);
        android.widget.FrameLayout.LayoutParams params;
        if (this.mConfig.isNavigationAtBottom()) {
            params = new android.widget.FrameLayout.LayoutParams(-1, this.mConfig.getNavigationBarHeight());
            params.gravity = 80;
        } else {
            params = new android.widget.FrameLayout.LayoutParams(this.mConfig.getNavigationBarWidth(), -1);
            params.gravity = 5;
        }

        this.mNavBarTintView.setLayoutParams(params);
        this.mNavBarTintView.setBackgroundColor(-1728053248);
        this.mNavBarTintView.setVisibility(8);
        decorViewGroup.addView(this.mNavBarTintView);
    }

    static {
        if (VERSION.SDK_INT >= 19) {
            try {
                Class c = Class.forName("android.os.SystemProperties");
                Method m = c.getDeclaredMethod("get", String.class);
                m.setAccessible(true);
                sNavBarOverride = (String)m.invoke((Object)null, "qemu.hw.mainkeys");
            } catch (Throwable var2) {
                sNavBarOverride = null;
            }
        }

    }

    public static class SystemBarConfig {
        private static final String STATUS_BAR_HEIGHT_RES_NAME = "status_bar_height";
        private static final String NAV_BAR_HEIGHT_RES_NAME = "navigation_bar_height";
        private static final String NAV_BAR_HEIGHT_LANDSCAPE_RES_NAME = "navigation_bar_height_landscape";
        private static final String NAV_BAR_WIDTH_RES_NAME = "navigation_bar_width";
        private static final String SHOW_NAV_BAR_RES_NAME = "config_showNavigationBar";
        private final boolean mTranslucentStatusBar;
        private final boolean mTranslucentNavBar;
        private final int mStatusBarHeight;
        private final int mActionBarHeight;
        private final boolean mHasNavigationBar;
        private final int mNavigationBarHeight;
        private final int mNavigationBarWidth;
        private final boolean mInPortrait;
        private final float mSmallestWidthDp;

        private SystemBarConfig(Activity activity, boolean translucentStatusBar, boolean traslucentNavBar) {
            Resources res = activity.getResources();
            this.mInPortrait = res.getConfiguration().orientation == 1;
            this.mSmallestWidthDp = this.getSmallestWidthDp(activity);
            this.mStatusBarHeight = this.getInternalDimensionSize(res, "status_bar_height");
            this.mActionBarHeight = this.getActionBarHeight(activity);
            this.mNavigationBarHeight = this.getNavigationBarHeight(activity);
            this.mNavigationBarWidth = this.getNavigationBarWidth(activity);
            this.mHasNavigationBar = this.mNavigationBarHeight > 0;
            this.mTranslucentStatusBar = translucentStatusBar;
            this.mTranslucentNavBar = traslucentNavBar;
        }

        @TargetApi(14)
        private int getActionBarHeight(Context context) {
            int result = 0;
            if (VERSION.SDK_INT >= 14) {
                TypedValue tv = new TypedValue();
                context.getTheme().resolveAttribute(16843499, tv, true);
                result = TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
            }

            return result;
        }

        @TargetApi(14)
        private int getNavigationBarHeight(Context context) {
            Resources res = context.getResources();
            int result = 0;
            if (VERSION.SDK_INT >= 14 && this.hasNavBar(context)) {
                String key;
                if (this.mInPortrait) {
                    key = "navigation_bar_height";
                } else {
                    key = "navigation_bar_height_landscape";
                }

                return this.getInternalDimensionSize(res, key);
            } else {
                return result;
            }
        }

        @TargetApi(14)
        private int getNavigationBarWidth(Context context) {
            Resources res = context.getResources();
            int result = 0;
            return VERSION.SDK_INT >= 14 && this.hasNavBar(context) ? this.getInternalDimensionSize(res, "navigation_bar_width") : result;
        }

        @TargetApi(14)
        private boolean hasNavBar(Context context) {
            Resources res = context.getResources();
            int resourceId = res.getIdentifier("config_showNavigationBar", "bool", "android");
            if (resourceId != 0) {
                boolean hasNav = res.getBoolean(resourceId);
                if ("1".equals(SystemBarTintManager.sNavBarOverride)) {
                    hasNav = false;
                } else if ("0".equals(SystemBarTintManager.sNavBarOverride)) {
                    hasNav = true;
                }

                return hasNav;
            } else {
                return !ViewConfiguration.get(context).hasPermanentMenuKey();
            }
        }

        private int getInternalDimensionSize(Resources res, String key) {
            int result = 0;
            int resourceId = res.getIdentifier(key, "dimen", "android");
            if (resourceId > 0) {
                result = res.getDimensionPixelSize(resourceId);
            }

            return result;
        }

        @SuppressLint({"NewApi"})
        private float getSmallestWidthDp(Activity activity) {
            DisplayMetrics metrics = new DisplayMetrics();
            if (VERSION.SDK_INT >= 16) {
                activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
            } else {
                activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            }

            float widthDp = (float)metrics.widthPixels / metrics.density;
            float heightDp = (float)metrics.heightPixels / metrics.density;
            return Math.min(widthDp, heightDp);
        }

        public boolean isNavigationAtBottom() {
            return this.mSmallestWidthDp >= 600.0F || this.mInPortrait;
        }

        public int getStatusBarHeight() {
            return this.mStatusBarHeight;
        }

        public int getActionBarHeight() {
            return this.mActionBarHeight;
        }

        public boolean hasNavigtionBar() {
            return this.mHasNavigationBar;
        }

        public int getNavigationBarHeight() {
            return this.mNavigationBarHeight;
        }

        public int getNavigationBarWidth() {
            return this.mNavigationBarWidth;
        }

        public int getPixelInsetTop(boolean withActionBar) {
            return (this.mTranslucentStatusBar ? this.mStatusBarHeight : 0) + (withActionBar ? this.mActionBarHeight : 0);
        }

        public int getPixelInsetBottom() {
            return this.mTranslucentNavBar && this.isNavigationAtBottom() ? this.mNavigationBarHeight : 0;
        }

        public int getPixelInsetRight() {
            return this.mTranslucentNavBar && !this.isNavigationAtBottom() ? this.mNavigationBarWidth : 0;
        }
    }
}
