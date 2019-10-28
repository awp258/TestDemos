package com.jw.galarylibrary.base.adapter

import android.app.Activity
import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup
import com.jw.library.utils.ThemeUtils
import java.util.*

/**
 * 创建时间：
 * 更新时间
 * 版本：
 * 作者：Mr.jin
 * 描述：预览页面viewPager的adapter
 */
open class BasePageAdapter<ITEM>(var mActivity: Activity, var mItems: ArrayList<ITEM>) :
    PagerAdapter() {
    private var mScreenWidth: Int = 0
    private var mScreenHeight: Int = 0

    init {
        this.mScreenWidth = ThemeUtils.getWindowWidth(mActivity)
        this.mScreenHeight = ThemeUtils.getWindowHeight(mActivity)
    }

    fun setData(images: ArrayList<ITEM>) {
        this.mItems = images
    }

    override fun getCount(): Int {
        return this.mItems.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getItemPosition(`object`: Any): Int {
        return -2
    }
}