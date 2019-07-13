

package com.rxxb.imagepicker.util;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

public class NavigationBarChangeListener implements OnGlobalLayoutListener {
    public static final int ORIENTATION_VERTICAL = 1;
    public static final int ORIENTATION_HORIZONTAL = 2;
    private Rect rect;
    private View rootView;
    private boolean isShowNavigationBar = false;
    private int orientation;
    private NavigationBarChangeListener.OnSoftInputStateChangeListener listener;

    public NavigationBarChangeListener(View rootView, int orientation) {
        this.rootView = rootView;
        this.orientation = orientation;
        this.rect = new Rect();
    }

    public void onGlobalLayout() {
        this.rect.setEmpty();
        this.rootView.getWindowVisibleDisplayFrame(this.rect);
        int heightDiff = 0;
        if (this.orientation == 1) {
            heightDiff = this.rootView.getHeight() - (this.rect.bottom - this.rect.top);
        } else if (this.orientation == 2) {
            heightDiff = this.rootView.getWidth() - (this.rect.right - this.rect.left);
        }

        int navigationBarHeight = Utils.hasVirtualNavigationBar(this.rootView.getContext()) ? Utils.getNavigationBarHeight(this.rootView.getContext()) : 0;
        if (heightDiff >= navigationBarHeight && heightDiff < navigationBarHeight * 2) {
            if (!this.isShowNavigationBar && this.listener != null) {
                this.listener.onNavigationBarShow(this.orientation, heightDiff);
            }

            this.isShowNavigationBar = true;
        } else {
            if (this.isShowNavigationBar && this.listener != null) {
                this.listener.onNavigationBarHide(this.orientation);
            }

            this.isShowNavigationBar = false;
        }

    }

    public void setListener(NavigationBarChangeListener.OnSoftInputStateChangeListener listener) {
        this.listener = listener;
    }

    public static NavigationBarChangeListener with(View rootView) {
        return with((View)rootView, 1);
    }

    public static NavigationBarChangeListener with(Activity activity) {
        return with((View)activity.findViewById(16908290), 1);
    }

    public static NavigationBarChangeListener with(View rootView, int orientation) {
        NavigationBarChangeListener changeListener = new NavigationBarChangeListener(rootView, orientation);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(changeListener);
        return changeListener;
    }

    public static NavigationBarChangeListener with(Activity activity, int orientation) {
        return with(activity.findViewById(16908290), orientation);
    }

    public interface OnSoftInputStateChangeListener {
        void onNavigationBarShow(int var1, int var2);

        void onNavigationBarHide(int var1);
    }
}
