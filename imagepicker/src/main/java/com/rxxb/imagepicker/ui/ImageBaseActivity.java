//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.rxxb.imagepicker.ui;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.graphics.drawable.StateListDrawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.Toast;
import com.rxxb.imagepicker.ImagePicker;
import com.rxxb.imagepicker.R.color;
import com.rxxb.imagepicker.util.CornerUtils;
import com.rxxb.imagepicker.util.Utils;
import com.rxxb.imagepicker.view.SystemBarTintManager;

public class ImageBaseActivity extends AppCompatActivity {
    protected SystemBarTintManager tintManager;

    public ImageBaseActivity() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTranslucentStatus(true);
        this.tintManager = new SystemBarTintManager(this);
        this.tintManager.setStatusBarTintEnabled(true);
        this.tintManager.setStatusBarTintResource(color.ip_color_primary_dark);
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = this.getWindow();
        LayoutParams winParams = win.getAttributes();
        int bits = 67108864;
        if (on) {
            winParams.flags |= 67108864;
        } else {
            winParams.flags &= -67108865;
        }

        win.setAttributes(winParams);
    }

    public boolean checkPermission(@NonNull String permission) {
        return ActivityCompat.checkSelfPermission(this, permission) == 0;
    }

    public void showToast(String toastText) {
        Toast.makeText(this.getApplicationContext(), toastText, 0).show();
    }

    protected void setConfirmButtonBg(Button mBtnOk) {
        ImagePicker imagePicker = ImagePicker.getInstance();
        StateListDrawable btnOkDrawable = CornerUtils.btnSelector((float) Utils.dp2px(this, 3.0F), Color.parseColor(imagePicker.getViewColor().getoKButtonTitleColorNormal()), Color.parseColor(imagePicker.getViewColor().getoKButtonTitleColorNormal()), Color.parseColor(imagePicker.getViewColor().getoKButtonTitleColorDisabled()), -2);
        if (VERSION.SDK_INT >= 16) {
            mBtnOk.setBackground(btnOkDrawable);
        } else {
            mBtnOk.setBackgroundDrawable(btnOkDrawable);
        }

        mBtnOk.setPadding(Utils.dp2px(this, 12.0F), 0, Utils.dp2px(this, 12.0F), 0);
        mBtnOk.setTextColor(Color.parseColor(imagePicker.getViewColor().getBarItemTextColor()));
    }
}
