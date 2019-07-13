package com.jw.uploaddemo.base.binding

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import com.jw.uploaddemo.base.activity.BaseActivity

/**
 * 由 jinwangx 创建于 2018/3/5.
 */
abstract class BaseBindingActivity<BINDING : ViewDataBinding> : BaseActivity() {
    protected lateinit var binding: BINDING
        private set

    abstract fun getLayoutId(): Int

    override fun doInflate(activity: BaseActivity, savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(activity, getLayoutId())
    }
}