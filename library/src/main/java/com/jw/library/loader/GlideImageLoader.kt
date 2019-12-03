package com.jw.library.loader

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.jw.library.utils.RotateTransformation


/**
 * Glide图片加载类
 */
object GlideImageLoader {
    private var options = RequestOptions()
        //.error(R.drawable.ic_default_image)
        //.placeholder(R.drawable.ic_default_image)
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)

    /**
     * 一般加载图片
     * @param context Context
     * @param path String
     * @param imageView ImageView
     */
    fun displayImage(context: Context, path: String, imageView: ImageView) {
        Glide.with(context)
            .load(path)
            .apply(options)
            .into(imageView)
    }

    /**
     * 加载图片并旋转方向
     * @param context Context
     * @param path String
     * @param imageView ImageView
     * @param orientation Int
     */
    fun displayImageRotate(
        context: Context,
        path: String,
        imageView: ImageView,
        orientation: Int = 0
    ) {
        val options = RequestOptions()
            //.error(R.drawable.ic_default_image)
            //.placeholder(R.drawable.ic_default_image)
            .transform(RotateTransformation(context, orientation))
            .diskCacheStrategy(DiskCacheStrategy.ALL)
        Glide.with(context)
            .load(path)
            .apply(options)
            .into(imageView)
    }

    fun clearMemoryCache() {}
}
