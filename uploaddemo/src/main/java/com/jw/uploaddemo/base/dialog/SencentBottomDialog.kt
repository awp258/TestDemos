package com.jw.uploaddemo.base.dialog

import android.support.v4.app.FragmentManager

/**
 * 由 Harreke 创建于 2017/10/11.
 */
abstract class SencentBottomDialog : BaseBottomDialog() {
    override fun show(manager: FragmentManager, tag: String) {
        try {
            super.show(manager, tag)
        } catch (e: Exception) {
        }
    }
}