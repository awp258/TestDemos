package com.sencent.library.base.dialog

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatDialogFragment
import android.support.v7.widget.Toolbar
import android.view.*
import com.jw.uilibrary.FragmentFrameworkHelper
import com.jw.uilibrary.IBaseFramework
import com.jw.uilibrary.RefreshHelper
import com.jw.uilibrary.StarterHelper
import com.jw.uilibrary.base.fragment.IFragmentData
import com.jw.uilibrary.base.fragment.IOtherFragmentData

/**
 * 由 Harreke 创建于 2017/9/14.
 */
abstract class BaseDialog : AppCompatDialogFragment(), IBaseFramework, IFragmentData,
    IOtherFragmentData {
    private val mRefreshHelper: RefreshHelper = RefreshHelper()
    private val mFragmentFrameworkHelper = FragmentFrameworkHelper()

    open fun doConfig(arguments: Bundle?) {
    }

    override fun isActivity(): Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        doInflate(inflater, container, savedInstanceState)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : View> findViewById(viewId: Int): T? {
        return try {
            view?.findViewById(viewId)
        } catch (e: ClassCastException) {
            null
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        doConfig(arguments)
    }

    override fun onDestroyView() {
        mRefreshHelper.destroy()
        super.onDestroyView()
    }

    abstract fun doInflate(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?

    override fun start(intent: Intent) {
        start(intent, -1, null)
    }

    override fun start(intent: Intent, requestCode: Int) {
        start(intent, requestCode, null)
    }

    override fun start(intent: Intent, bundle: Bundle?) {
        start(intent, -1, bundle)
    }

    override fun start(intent: Intent, requestCode: Int, bundle: Bundle?) {
        StarterHelper.start(this, intent, requestCode, bundle)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mFragmentFrameworkHelper.onAttach(context)
    }

    override fun onDetach() {
        mFragmentFrameworkHelper.onDetach()
        super.onDetach()
    }

    override fun onPause() {
        mRefreshHelper.pause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        if (mRefreshHelper.firstEnter()) {
            doLaunch()
        } else if (mRefreshHelper.shouldRefresh()) {
            doRefresh()
        }
    }

    override final fun setRefreshThreshold(refreshThreshold: Long) {
        mRefreshHelper.setRefreshThreshold(refreshThreshold)
    }

    override fun setToolbar(toolbar: Toolbar) {
        toolbar.setNavigationOnClickListener {
            onToolbarNavigationClick()
        }
        toolbar.setOnMenuItemClickListener {
            onToolbarMenuItemClick(it)
        }
    }

    override fun onToolbarNavigationClick() {
    }

    override fun onToolbarMenuItemClick(menuItem: MenuItem) = false

    override fun sendDataToActivity(name: String, data: Any?) {
        mFragmentFrameworkHelper.sendDataToActivity(this, name, data)
    }

    override fun sendDataToOtherFragment(receiverTag: String, name: String, data: Any?) {
        mFragmentFrameworkHelper.sendDataToOtherFragment(this, receiverTag, name, data)
    }

    override fun onReceiveDataFromActivity(name: String, data: Any?) {
    }

    override fun onReceiveDataFromOtherFragment(senderTag: String, name: String, data: Any?) {
    }

    override fun onQueryDataFromActivity(name: String) = null

    override fun queryActivityData(name: String) = mFragmentFrameworkHelper.queryActivityData(this, name)
}