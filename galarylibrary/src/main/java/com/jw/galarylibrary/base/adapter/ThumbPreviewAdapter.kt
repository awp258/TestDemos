package com.jw.galarylibrary.base.adapter

import android.app.Activity
import android.support.v7.widget.RecyclerView.Adapter
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.FrameLayout
import android.widget.ImageView
import com.jw.galarylibrary.R
import com.jw.galarylibrary.base.util.Utils
import com.jw.library.loader.GlideImageLoader
import com.jw.library.model.ImageItem
import com.jw.library.model.VideoItem
import java.util.*

/**
 * 创建时间：
 * 更新时间
 * 版本：
 * 作者：Mr.jin
 * 描述：预览页面已选择条目recycleView的adapter
 */
class ThumbPreviewAdapter<ITEM>(
    var mContext: Activity,
    var mItems: ArrayList<ITEM>
) : Adapter<ViewHolder>() {


    private var mItemsSize: Int = 0
    private var mSelectedPosition: Int = 0
    private var mListener: OnThumbItemClickListener<ITEM>? = null

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

    internal open inner class BaseHolder(itemView: View) : ViewHolder(itemView) {
        private var mFrameLayout: FrameLayout = itemView.findViewById(R.id.frame_thumb_preview)
        protected var mItemView: ImageView = itemView.findViewById(R.id.iv_thumb_preview)
        private var thumbView: View = itemView.findViewById(R.id.view_thumb_preview)

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

    internal inner class ThumbViewHolder(itemView: View) : BaseHolder(itemView) {

        override fun bind(position: Int) {
            super.bind(position)
            val item = mItems.get(position) as ImageItem
            GlideImageLoader.displayImage(
                mContext,
                item.path!!,
                this.mItemView
            )
        }
    }

    internal inner class VideoThumbViewHolder(itemView: View) : BaseHolder(itemView) {

        override fun bind(position: Int) {
            super.bind(position)
            val item = mItems.get(position) as VideoItem
            GlideImageLoader.displayImage(
                mContext,
                item.thumbPath!!,
                this.mItemView
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