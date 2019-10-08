package com.jw.galary.base.adapter

import android.app.Activity
import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup
import com.jw.galary.img.util.Utils
import java.util.*

open class BasePageAdapter<ITEM>(var mActivity: Activity, var mItems: ArrayList<ITEM>) :
    PagerAdapter() {
    var mScreenWidth: Int = 0
    var mScreenHeight: Int = 0

    init {
        val dm = Utils.getScreenPix(mActivity)
        this.mScreenWidth = dm.widthPixels
        this.mScreenHeight = dm.heightPixels
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