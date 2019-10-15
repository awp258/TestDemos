package com.jw.galary.img.adapter

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import com.jw.galary.base.adapter.BasePageAdapter
import com.jw.galary.base.loader.GlideImageLoader
import com.jw.library.model.ImageItem
import uk.co.senab.photoview.PhotoView
import java.util.*

class ImagePageAdapter(activity: Activity, images: ArrayList<ImageItem>) :
    BasePageAdapter<ImageItem>(activity, images) {
    var mListener: PhotoViewClickListener? = null

    fun setPhotoViewClickListener(listener: PhotoViewClickListener) {
        this.mListener = listener
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val photoView = PhotoView(this.mActivity)
        val imageItem = this.mItems[position]
        GlideImageLoader.displayImagePreview(
            this.mActivity,
            imageItem.path!!,
            photoView,
            this.mScreenWidth,
            this.mScreenHeight
        )
        photoView.setOnPhotoTapListener { view, x, y ->
            if (mListener != null) {
                mListener!!.OnPhotoTapListener(view, x, y)
            }
        }
        container.addView(photoView)
        return photoView
    }

    interface PhotoViewClickListener {
        fun OnPhotoTapListener(view: View, x: Float, y: Float)
    }
}
