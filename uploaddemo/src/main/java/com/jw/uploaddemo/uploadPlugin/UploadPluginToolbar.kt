package com.jw.uploaddemo.uploadPlugin

import android.content.Context
import android.os.Build
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.util.AttributeSet
import android.widget.TextView
import skin.support.widget.SkinCompatToolbar

/**
 * 由 Harreke 创建于 2017/12/12.
 */
class UploadPluginToolbar(context: Context, attrs: AttributeSet) : SkinCompatToolbar(context, attrs) {
    private var mTitleTextView: TextView? = null
    private var mSubTitleTextView: TextView? = null

    init {
        if (Build.VERSION.SDK_INT >= 19) {
            //ViewUtil.patchTopPadding(this)
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        val titleTextView = mTitleTextView
        if (titleTextView != null) {
            val tw = titleTextView.measuredWidth
            val tl = (measuredWidth - tw) / 2
            titleTextView.layout(tl, titleTextView.top, tl + tw, titleTextView.bottom)
        }

        val subtitleTextView = mSubTitleTextView
        if (subtitleTextView != null) {
            val stw = subtitleTextView.measuredWidth
            val stl = (measuredWidth - stw) / 2
            subtitleTextView.layout(stl, subtitleTextView.top, stl + stw, subtitleTextView.bottom)
        }
    }

    override fun setTitle(title: CharSequence?) {
        super.setTitle(title)
        if (mTitleTextView == null) {
            mTitleTextView = tryGetTitleTextView()
        }
        requestLayout()
    }

    override fun setSubtitle(subtitle: CharSequence?) {
        super.setSubtitle(subtitle)
        if (mSubTitleTextView == null) {
            mSubTitleTextView = tryGetSubTitleTextView()
        }
        requestLayout()
    }

    private fun tryGetSubTitleTextView(): TextView? {
        try {
            val field = Toolbar::class.java.getDeclaredField("mSubtitleTextView")
            if (field != null) {
                field.isAccessible = true
                val titleTextView = field.get(this) as? TextView
                if (titleTextView != null) {
                    titleTextView.setLines(1)
                    titleTextView.ellipsize = TextUtils.TruncateAt.END
                    return titleTextView
                }
            }
        } catch (e: NoSuchFieldException) {
        } catch (e: IllegalAccessException) {
        }
        return null
    }

    private fun tryGetTitleTextView(): TextView? {
        try {
            val field = Toolbar::class.java.getDeclaredField("mTitleTextView")
            if (field != null) {
                field.isAccessible = true
                val titleTextView = field.get(this) as? TextView
                if (titleTextView != null) {
                    titleTextView.setLines(1)
                    titleTextView.ellipsize = TextUtils.TruncateAt.END
                    return titleTextView
                }
            }
        } catch (e: NoSuchFieldException) {
        } catch (e: IllegalAccessException) {
        }
        return null
    }
}