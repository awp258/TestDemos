package com.sencent.library.base.dialog

import android.app.Dialog
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog

/**
 * 由 Harreke 创建于 2017/9/14.
 */
abstract class BaseBottomDialog : BaseDialog() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = BottomSheetDialog(context!!, theme)
}