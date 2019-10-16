package com.jw.galarylibrary.video.adapter

import android.app.Activity
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.jw.galarylibrary.R
import com.jw.galarylibrary.base.adapter.BasePageAdapter
import com.jw.library.model.VideoItem
import java.util.*

class VideoPageAdapter(activity: Activity, videos: ArrayList<VideoItem>) :
    BasePageAdapter<VideoItem>(activity, videos) {
    var mListener: PhotoViewClickListener? = null

    fun setPhotoViewClickListener(listener: PhotoViewClickListener) {
        this.mListener = listener
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val videoItem = this.mItems[position]
        val view = View.inflate(this.mActivity, R.layout.pager_preview, null)
        val iv = view.findViewById<ImageView>(R.id.iv1)
        val ivStart = view.findViewById<ImageView>(R.id.iv_start)
        iv.setImageURI(Uri.parse(videoItem.thumbPath))
        iv.setOnClickListener { v ->
            run {
                if (mListener != null)
                    mListener?.OnImageClickListener(videoItem)
            }
        }
        ivStart.setOnClickListener { v ->
            run {
                if (mListener != null)
                    mListener?.OnStartClickListener(videoItem)
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
