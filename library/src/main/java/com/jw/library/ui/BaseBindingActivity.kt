package com.jw.library.ui

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import com.jw.uilibrary.base.activity.BaseActivity

/**
 * 由 jinwangx 创建于 2018/3/5.
 */
abstract class BaseBindingActivity<BINDING : ViewDataBinding> : com.jw.library.ui.BaseActivity() {
    protected lateinit var mBinding
            : BINDING
        private set

    abstract fun getLayoutId(): Int

    override fun doInflate(activity: BaseActivity, savedInstanceState: Bundle?) {
        mBinding = DataBindingUtil.setContentView(activity, getLayoutId())
    }
}