package com.jw.galary.base.adapter

import android.app.Activity
import android.support.v7.widget.RecyclerView.Adapter
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.FrameLayout
import android.widget.ImageView
import com.jw.galary.base.loader.GlideImageLoader
import com.jw.galary.base.util.Utils
import com.jw.library.model.ImageItem
import com.jw.library.model.VideoItem
import com.jw.uploaddemo.R
import java.util.*

class ThumbPreviewAdapter<ITEM>(
    var mContext: Activity,
    var mItems: ArrayList<ITEM>
) : Adapter<ViewHolder>() {


    var mItemsSize: Int = 0
    var mSelectedPosition: Int = 0
    var mListener: OnThumbItemClickListener<ITEM>? = null

    init {
        mItemsSize = Utils.getImageItemWidth(mContext, 6, 5)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        when (viewType) {
            0 -> {
                return ThumbViewHolder(
                    LayoutInflater.from(mContext).inflate(
                        R.layout.adapter_thumb_image_preview_list_item,
                        parent,
                        false
                    )
                )
            }
            1 -> {
                return VideoThumbViewHolder(
                    LayoutInflater.from(mContext).inflate(
                        R.layout.adapter_thumb_preview_list_item,
                        parent,
                        false
                    )
                )
            }
        }
        return return ThumbViewHolder(
            LayoutInflater.from(mContext).inflate(
                R.layout.adapter_thumb_image_preview_list_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder is ThumbPreviewAdapter<*>.ThumbViewHolder) {
            holder.bind(position)
        } else if (holder is ThumbPreviewAdapter<*>.VideoThumbViewHolder) {
            holder.bind(position)
        }
    }

    fun getItem(position: Int) = mItems[position]

    override fun getItemViewType(position: Int): Int {
        if (getItem(position) is ImageItem) {
            return 0
        } else if (getItem(position) is VideoItem) {
            return 1
        }
        return 1
    }

    open inner class BaseHolder(itemView: View) : ViewHolder(itemView) {
        var mFrameLayout: FrameLayout = itemView.findViewById(R.id.frame_thumb_preview)
        var mItemView: ImageView = itemView.findViewById(R.id.iv_thumb_preview)
        var thumbView: View = itemView.findViewById(R.id.view_thumb_preview)

        init {
            this.mFrameLayout.layoutParams = AbsListView.LayoutParams(mItemsSize, mItemsSize)
        }

        open fun bind(position: Int) {
            val imageItem = mItems[position]
            if (mSelectedPosition == position) {
                this.thumbView.setBackgroundResource(R.drawable.bg_thumb_selceted_shape)
            } else {
                this.thumbView.setBackgroundDrawable(null)
            }

            this.mFrameLayout.setOnClickListener { v ->
                if (mListener != null) {
                    mListener!!.onThumbItemClick(imageItem)
                }

            }
        }
    }

    inner class ThumbViewHolder(itemView: View) : BaseHolder(itemView) {

        override fun bind(position: Int) {
            super.bind(position)
            val item = mItems.get(position) as ImageItem
            GlideImageLoader.displayImage(
                mContext,
                item.path!!,
                this.mItemView,
                mItemsSize,
                mItemsSize
            )
        }
    }

    inner class VideoThumbViewHolder(itemView: View) : BaseHolder(itemView) {

        override fun bind(position: Int) {
            super.bind(position)
            val item = mItems.get(position) as VideoItem
            GlideImageLoader.displayImage(
                mContext,
                item.thumbPath,
                this.mItemView,
                mItemsSize,
                mItemsSize
            )
        }
    }

    fun setSelected(item: ITEM?) {
        if (mSelectedPosition != -1) {
            notifyItemChanged(mSelectedPosition)
        }

        if (item == null) {
            mSelectedPosition = -1
        } else {
            mSelectedPosition = mItems.indexOf(item)
        }

        if (mSelectedPosition != -1) {
            notifyItemChanged(mSelectedPosition)
        }

    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    fun setOnThumbItemClickListener(listener: OnThumbItemClickListener<ITEM>) {
        mListener = listener
    }

    interface OnThumbItemClickListener<ITEM> {
        fun onThumbItemClick(item: ITEM)
    }
}