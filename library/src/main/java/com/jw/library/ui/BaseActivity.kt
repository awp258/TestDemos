package com.jw.library.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.widget.Button
import android.widget.Toast
import com.jw.library.utils.CornerUtils
import com.jw.library.utils.ThemeUtils
import com.jw.uilibrary.base.activity.BaseActivity

/**
 * 由 jinwangx 创建于 2018/3/5.
 */
abstract class BaseActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeUtils.changeStatusBar(this, Color.parseColor("#393A3F"))
    }

    override fun doInflate(activity: BaseActivity, savedInstanceState: Bundle?) {

    }

    override fun doConfig(arguments: Intent) {

    }

    fun checkPermission(permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(this, permission) == 0
    }

    fun showToast(toastText: String) {
        Toast.makeText(this.applicationContext, toastText, Toast.LENGTH_SHORT).show()
    }

    protected fun setConfirmButtonBg(mBtnOk: Button) {
        val btnOkDrawable = CornerUtils.btnSelector(
            ThemeUtils.dip2px(this, 3.0f).toFloat(),
            Color.parseColor(com.jw.library.ColorCofig.oKButtonTitleColorNormal),
            Color.parseColor(com.jw.library.ColorCofig.oKButtonTitleColorNormal),
            Color.parseColor(com.jw.library.ColorCofig.oKButtonTitleColorDisabled),
            -2
        )
        mBtnOk.background = btnOkDrawable
        mBtnOk.setPadding(ThemeUtils.dip2px(this, 12.0f), 0, ThemeUtils.dip2px(this, 12.0f), 0)
        mBtnOk.setTextColor(Color.parseColor(com.jw.library.ColorCofig.barItemTextColor))
    }
}