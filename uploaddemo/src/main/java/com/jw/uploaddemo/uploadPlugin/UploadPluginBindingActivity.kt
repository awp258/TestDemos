package com.jw.uploaddemo.uploadPlugin

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.graphics.Color
import android.os.Bundle
import com.jw.uilibrary.base.activity.BaseActivity
import com.jw.uploaddemo.utils.ThemeUtils

/**
 * 由 jinwangx 创建于 2018/3/5.
 */
abstract class UploadPluginBindingActivity<BINDING : ViewDataBinding> : BaseActivity() {
    protected lateinit var binding: BINDING
        private set

    abstract fun getLayoutId(): Int

    override fun doInflate(activity: BaseActivity, savedInstanceState: Bundle?) {
        ThemeUtils.changeStatusBar(this, Color.parseColor("#424242"))
        binding = DataBindingUtil.setContentView(activity, getLayoutId())
    }
}