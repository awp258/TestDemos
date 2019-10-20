package com.jw.library.utils

import android.databinding.BindingAdapter
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.widget.ImageView

/**
 * Created by liyuan on 16/12/6.
 */

object ImageViewBinding {

    val FLAG_NONE = 1 shl 0
    val FLAG_ROUND = 1 shl 1
    val FLAG_ANIM = 1 shl 2
    val FLAG_FIT_CENTER = 1 shl 3

    /**
     * 根据资源设置图片
     */
    @BindingAdapter("android:src")
    fun setImageResource(imageView: ImageView, resource: Int) {
        imageView.setImageResource(resource)
    }

    /**
     * 异步加载图片, 使用默认策略
     */
    @BindingAdapter("uri")
    fun setImageUri(iv: ImageView, oldRoundUri: String, newRoundUri: String) {
        ImageViewUtil.setImageUriAsync(iv, newRoundUri)
    }

    @BindingAdapter("uri", "default")
    fun setImageUri(
        iv: ImageView, oldRoundUri: String, oldRes: Drawable,
        newRoundUri: String, newRes: Drawable
    ) {
        if (TextUtils.isEmpty(newRoundUri)) {
            iv.setImageDrawable(newRes)
        } else {
            ImageViewUtil.setImageUriAsync(iv, newRoundUri)
        }
    }

    /**
     * 异步加载图片 圆形
     */
    @BindingAdapter("round_uri")
    fun setImageRoundUriOld(iv: ImageView, oldRoundUri: String, newRoundUri: String) {
        setImageRoundUri(iv, oldRoundUri, newRoundUri)
    }


    @BindingAdapter("roundUri")
    fun setImageRoundUri(iv: ImageView, oldRoundUri: String, newRoundUri: String) {
        ImageViewUtil.setImageUriRoundAsync(iv, newRoundUri)
    }

}
