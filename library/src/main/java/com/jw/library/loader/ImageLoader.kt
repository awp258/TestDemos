package com.jw.library.loader

import android.content.Context
import android.widget.ImageView
import java.io.Serializable

interface ImageLoader : Serializable {
    fun displayImage(
        context: Context,
        path: String,
        imageView: ImageView
    )

    fun displayImageRotate(
        context: Context,
        path: String,
        imageView: ImageView,
        orientation: Int
    )

    fun clearMemoryCache()
}
