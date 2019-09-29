package com.jw.uploaddemo.uploadPlugin

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import com.jw.uploaddemo.base.activity.BaseActivity

/**
 * 由 jinwangx 创建于 2018/3/5.
 */
abstract class UploadPluginBindingActivity<BINDING : ViewDataBinding> : UploadPluginActivity() {
    protected lateinit var mBinding
            : BINDING
        private set

    abstract fun getLayoutId(): Int

    override fun doInflate(activity: BaseActivity, savedInstanceState: Bundle?) {
        mBinding = DataBindingUtil.setContentView(activity, getLayoutId())
    }
}