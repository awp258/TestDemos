package com.jw.videopicker.trim;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.Color;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import com.rxxb.imagepicker.ImagePicker;
import com.rxxb.imagepicker.util.CornerUtils;
import com.rxxb.imagepicker.util.Utils;
import com.rxxb.imagepicker.view.SystemBarTintManager;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2019/02/22 4:38 PM
 * version: 1.0
 * description:模板设计模式：
 * 定义算法骨架，将一些步骤延时到子类，可定义钩子函数。
 */
@SuppressLint("Registered")
public abstract class BaseActivity extends AppCompatActivity {
  protected SystemBarTintManager tintManager;
  protected abstract void initUI();
  protected void loadData() {
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    try {
      render();
      //this.setTranslucentStatus(true);
      this.tintManager = new SystemBarTintManager(this);
      this.tintManager.setStatusBarTintEnabled(true);
      this.tintManager.setStatusBarTintResource(com.rxxb.imagepicker.R.color.cropiwa_default_overlay_color);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void render() {
    initUI();
    loadData();
  }

  @TargetApi(19)
  private void setTranslucentStatus(boolean on) {
    Window win = this.getWindow();
    WindowManager.LayoutParams winParams = win.getAttributes();
    int bits = 67108864;
    if (on) {
      winParams.flags |= 67108864;
    } else {
      winParams.flags &= -67108865;
    }

    win.setAttributes(winParams);
  }

  protected void setConfirmButtonBg(Button mBtnOk) {
    ImagePicker imagePicker = ImagePicker.getInstance();
    StateListDrawable btnOkDrawable = CornerUtils.btnSelector((float) Utils.dp2px(this, 3.0F), Color.parseColor(imagePicker.getViewColor().getoKButtonTitleColorNormal()), Color.parseColor(imagePicker.getViewColor().getoKButtonTitleColorNormal()), Color.parseColor(imagePicker.getViewColor().getoKButtonTitleColorDisabled()), -2);
    if (Build.VERSION.SDK_INT >= 16) {
      mBtnOk.setBackground(btnOkDrawable);
    } else {
      mBtnOk.setBackgroundDrawable(btnOkDrawable);
    }

    mBtnOk.setPadding(Utils.dp2px(this, 12.0F), 0, Utils.dp2px(this, 12.0F), 0);
    mBtnOk.setTextColor(Color.parseColor(imagePicker.getViewColor().getBarItemTextColor()));
  }
}
