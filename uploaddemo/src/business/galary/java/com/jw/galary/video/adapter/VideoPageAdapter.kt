package com.jw.galary.video.adapter

import android.app.Activity
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.jw.galary.base.adapter.BasePageAdapter
import com.jw.galary.video.bean.VideoItem
import com.jw.uploaddemo.R
import java.util.*

class VideoPageAdapter(activity: Activity, images: ArrayList<VideoItem>) :
    BasePageAdapter<VideoItem>(activity, images) {
    var mListener: PhotoViewClickListener? = null

    fun setPhotoViewClickListener(listener: PhotoViewClickListener) {
        this.mListener = listener
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val imageItem = this.mItems[position]
        val view = View.inflate(this.mActivity, R.layout.pager_preview, null)
        val iv = view.findViewById<ImageView>(R.id.iv1)
        val ivStart = view.findViewById<ImageView>(R.id.iv_start)
        iv.setImageURI(Uri.parse(imageItem.thumbPath))
        iv.setOnClickListener { v ->
            run {
                if (mListener != null)
                    mListener?.OnImageClickListener(imageItem)
            }
        }
        ivStart.setOnClickListener { v ->
            run {
                if (mListener != null)
                    mListener?.OnStartClickListener(imageItem)
            }
        }
        container.addView(view)
        return view
    }

    override fun getItemPosition(`object`: Any): Int {
        return -2
    }

    interface PhotoViewClickListener {
        fun OnStartClickListener(videoItem: VideoItem)

        fun OnImageClickListener(videoItem: VideoItem)
    }
}
