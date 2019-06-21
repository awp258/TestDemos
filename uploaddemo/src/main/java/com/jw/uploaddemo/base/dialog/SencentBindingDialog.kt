package com.jw.uploaddemo.base.dialog

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * 由 Harreke 创建于 2017/10/11.
 */
abstract class SencentBindingDialog<BINDING : ViewDataBinding> : SencentDialog() {
    protected var binding: BINDING? = null

    abstract fun getLayoutId(): Int

    final override fun doInflate(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        return binding?.root
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}