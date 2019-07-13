package com.jw.uploaddemo.uploadPlugin

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.widget.Button
import android.widget.Toast
import com.jw.galary.img.util.CornerUtils
import com.jw.galary.img.util.Utils
import com.jw.uploaddemo.ColorCofig
import com.jw.uploaddemo.base.activity.BaseActivity
import com.jw.uploaddemo.base.utils.ThemeUtils

/**
 * 由 jinwangx 创建于 2018/3/5.
 */
abstract class UploadPluginActivity : BaseActivity() {

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
            Utils.dp2px(this, 3.0f).toFloat(),
            Color.parseColor(ColorCofig.oKButtonTitleColorNormal),
            Color.parseColor(ColorCofig.oKButtonTitleColorNormal),
            Color.parseColor(ColorCofig.oKButtonTitleColorDisabled),
            -2
        )
        mBtnOk.background = btnOkDrawable
        mBtnOk.setPadding(Utils.dp2px(this, 12.0f), 0, Utils.dp2px(this, 12.0f), 0)
        mBtnOk.setTextColor(Color.parseColor(ColorCofig.barItemTextColor))
    }
}