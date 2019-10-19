package com.jw.library.loader

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.jw.library.R

object GlideImageLoader : ImageLoader {

    override fun displayImage(context: Context, path: String, imageView: ImageView) {
        Glide.with(context)
            .load(path)
            .error(R.drawable.ic_default_image)
            .placeholder(R.drawable.ic_default_image).diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(imageView)
    }

    override fun displayImageRotate(
        context: Context,
        path: String,
        imageView: ImageView,
        orientation: Int
    ) {
        Glide.with(context)
            .load(path)
            //.transform( RotateTransformation( context, orientation))
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(imageView)
    }

    override fun clearMemoryCache() {}
}
