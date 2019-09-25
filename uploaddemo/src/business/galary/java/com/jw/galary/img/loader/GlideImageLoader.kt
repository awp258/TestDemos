package com.jw.galary.img.loader

import android.app.Activity
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.jw.uploaddemo.R

import java.io.File

class GlideImageLoader : ImageLoader {

    override fun displayImage(
        activity: Activity,
        path: String,
        imageView: ImageView,
        width: Int,
        height: Int
    ) {
        Glide.with(activity).load(Uri.fromFile(File(path))).error(R.drawable.ic_default_image)
            .placeholder(R.drawable.ic_default_image).diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(imageView)
    }

    override fun displayImagePreview(
        activity: Activity,
        path: String,
        imageView: ImageView,
        width: Int,
        height: Int
    ) {
        Glide.with(activity).load(Uri.fromFile(File(path))).diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(imageView)
    }

    override fun clearMemoryCache() {}
}
