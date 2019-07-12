//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.jw.galary.img.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.StateListDrawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.widget.Button;
import android.widget.Toast;
import com.jw.uilibrary.base.activity.BaseActivity;
import com.jw.uilibrary.base.utils.ThemeUtils;
import com.jw.galary.img.ImagePicker;
import com.jw.galary.img.util.CornerUtils;
import com.jw.galary.img.util.Utils;

public class ImageBaseActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.changeStatusBar(this, Color.parseColor("#393A3F"));
    }

    @Override
    public void doInflate(BaseActivity activity, Bundle savedInstanceState) {
    }

    @Override
    public void doConfig(Intent arguments) {

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
