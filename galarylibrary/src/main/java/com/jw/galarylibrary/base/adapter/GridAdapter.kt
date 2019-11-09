package com.jw.galarylibrary.base.adapter

import android.app.Activity
import android.graphics.Color
import android.support.v4.app.ActivityCompat
import android.support.v7.widget.RecyclerView.Adapter
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.jw.galarylibrary.R
import com.jw.galarylibrary.base.BasePicker
import com.jw.galarylibrary.base.activity.BaseGridActivity.Companion.REQUEST_CODE_ITEM_TAKE
import com.jw.galarylibrary.base.bean.Folder
import com.jw.galarylibrary.base.util.Utils
import com.jw.galarylibrary.img.view.SuperCheckBox
import com.jw.galarylibrary.img.view.TextDrawable
import com.jw.library.ColorCofig
import com.jw.library.loader.GlideImageLoader
import com.jw.library.model.BaseItem
import com.jw.library.model.ImageItem
import com.jw.library.model.VideoItem
import com.jw.library.ui.BaseActivity
import com.jw.library.utils.DateUtils
import com.jw.library.utils.ThemeUtils
import java.util.*

/**
 * 创建时间：
 * 更新时间
 * 版本：
 * 作者：Mr.jin
 * 描述：选择页面recyclerView的adapter
 */
class GridAdapter<ITEM : BaseItem>(
    var mActivity: Activity,
    items: ArrayList<ITEM>?,
    picker: BasePicker<ITEM>
) :
    Adapter<ViewHolder>() {
    private var mItems: ArrayList<ITEM>? = null
    private var mSelectedVideos: ArrayList<ITEM>? = null
    private var mIsShowCamera: Boolean = false
    private var mImageSize: Int = 0
    private var mInflater: LayoutInflater
    private var mListener: OnItemClickListener<ITEM>? = null
    private var mDrawableBuilder: TextDrawable.IBuilder
    private var mAalreadyChecked: ArrayList<Int>? = null
    private var mSelectLimit: Int = 0
    private var mPicker = picker

    init {
        mItems = if (mItems != null && mItems!!.size != 0) {
            items
        } else {
            ArrayList()
        }

        mImageSize = Utils.getImageItemWidth(mActivity)

        mInflater = LayoutInflater.from(mActivity)
        mDrawableBuilder =
            TextDrawable.builder().beginConfig().width(ThemeUtils.dip2px(mActivity, 18.0f))
                .height(ThemeUtils.dip2px(mActivity, 18.0f)).endConfig()
                .roundRect(ThemeUtils.dip2px(mActivity, 3.0f))

        mIsShowCamera = mPicker.isShowCamera
        mSelectedVideos = mPicker.selectedItems
        mAalreadyChecked = ArrayList(mPicker.selectLimit)
        mSelectLimit = mPicker.selectLimit
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var holder: ViewHolder? = null
        when (viewType) {
            0 -> {
                val view = mInflater.inflate(R.layout.adapter_camera_item, parent, false)
                holder = CameraViewHolder(view)
            }
            1 -> {
                val view = mInflater.inflate(R.layout.adapter_image_list_item, parent, false)
                holder = ImageViewHolder(view)
            }
            2 -> {
                val view = mInflater.inflate(R.layout.adapter_video_list_item, parent, false)
                holder = VideoViewHolder(view)
            }
        }
        return holder!!
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is GridAdapter<*>.CameraViewHolder -> holder.bindCamera()
            is GridAdapter<*>.ImageViewHolder -> holder.bind(position)
            is GridAdapter<*>.VideoViewHolder -> holder.bind(position)
        }

    }

    internal open inner class BaseHolder(private val rootView: View) : ViewHolder(rootView) {
        val ivThumb: ImageView = rootView.findViewById(R.id.iv_thumb)
        private val mask: View = rootView.findViewById(R.id.mask)
        private val checkView: View = rootView.findViewById(R.id.checkView)
        private val cbCheck: SuperCheckBox = rootView.findViewById(R.id.cb_check)

        init {
            rootView.layoutParams = AbsListView.LayoutParams(-1, mImageSize)
        }

        open fun bind(position: Int) {
            val item = getItem(position)
            ivThumb.setOnClickListener {
                if (mListener != null) {
                    val position = if (mPicker.isShowCamera) position - 1 else position
                    mListener!!.onItemClick(rootView, item!!, position)
                }

            }
            checkView.setOnClickListener {
                cbCheck.isChecked = !cbCheck.isChecked
                val selectLimit = mPicker.selectLimit
                if (cbCheck.isChecked && mSelectedVideos!!.size >= selectLimit) {
                    var limitStr = ""
                    if (item is ImageItem)
                        limitStr = mActivity.getString(R.string.ip_select_limit, selectLimit)
                    else if (item is VideoItem)
                        limitStr = mActivity.getString(R.string.ip_select_video_limit, selectLimit)
                    Toast.makeText(
                        mActivity,
                        limitStr,
                        Toast.LENGTH_SHORT
                    ).show()
                    cbCheck.isChecked = false
                } else {
                    mPicker.addSelectedItem(
                        position, item!!, cbCheck.isChecked
                    )
                }

            }
            if (mPicker.isMultiMode) {
                checkView.visibility = View.VISIBLE
                val index = mSelectedVideos!!.indexOf(item)
                if (index >= 0) {
                    if (!mAalreadyChecked!!.contains(position)) {
                        mAalreadyChecked!!.add(position)
                    }

                    cbCheck.isChecked = true
                    cbCheck.buttonDrawable =
                        mDrawableBuilder.build(
                            (index + 1).toString(),
                            Color.parseColor(ColorCofig.oKButtonTitleColorNormal)
                        )
                } else {
                    mAalreadyChecked!!.remove(position as Any)
                    cbCheck.isChecked = false
                    cbCheck.setButtonDrawable(R.drawable.checkbox_normal)
                }

                val selectLimit = mPicker.selectLimit
                if (mSelectedVideos!!.size >= selectLimit) {
                    mask.visibility = if (index < View.VISIBLE) View.VISIBLE else View.GONE
                } else {
                    mask.visibility = View.GONE
                }
            } else {
                checkView.visibility = View.GONE
            }
        }
    }

    internal inner class CameraViewHolder(var mItemView: View) : ViewHolder(mItemView) {

        fun bindCamera() {
            mItemView.layoutParams = AbsListView.LayoutParams(-1, mImageSize)
            mItemView.tag = null
            mItemView.setOnClickListener { v ->
                run {
                    if (!(mActivity as BaseActivity).checkPermission("android.permission.CAMERA")) {
                        ActivityCompat.requestPermissions(
                            mActivity,
                            arrayOf("android.permission.CAMERA"),
                            2
                        )
                    } else {
                        mPicker.takeCapture(
                            mActivity,
                            REQUEST_CODE_ITEM_TAKE
                        )
                    }
                }
            }
        }
    }

    internal inner class ImageViewHolder(rootView: View) : BaseHolder(rootView) {

        override fun bind(position: Int) {
            super.bind(position)
            val item = getItem(position) as ImageItem
            GlideImageLoader.displayImage(
                mActivity,
                item.path!!,
                ivThumb
            )
        }
    }

    internal inner class VideoViewHolder(rootView: View) : BaseHolder(rootView) {

        val tvDuration: TextView = rootView.findViewById(R.id.tv_duration)

        override fun bind(position: Int) {
            super.bind(position)
            val videoItem = getItem(position) as VideoItem
            tvDuration.text = DateUtils.getDuration(videoItem.duration, "mm:ss")
            GlideImageLoader.displayImage(
                mActivity,
                videoItem.thumbPath!!,
                ivThumb
            )
        }
    }

    fun setOnItemClickListener(listener: OnItemClickListener<ITEM>) {
        mListener = listener
    }

    fun refreshData(items: ArrayList<ITEM>?) {
        mItems = if (items != null && items.size != 0) {
            items
        } else {
            ArrayList()
        }

        notifyDataSetChanged()
    }

    fun refreshCheckedData(position: Int) {
        val checked = ArrayList<Int>(mSelectLimit)
        if (mAalreadyChecked != null) {
            checked.addAll(mAalreadyChecked!!)
        }

        var payload = "saveToGalary"
        if (!checked.contains(position)) {
            checked.add(position)
        } else {
            payload = "remove"
        }

        if (checked.size == mSelectLimit) {
            notifyItemRangeChanged(
                if (mIsShowCamera) ITEM_TYPE_NORMAL else ITEM_TYPE_CAMERA,
                mItems!!.size,
                payload
            )
        } else if (checked.isNotEmpty()) {

            for (check in checked) {
                notifyItemChanged(check, payload)
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        if (mIsShowCamera && position == 0) {
            return 0
        } else if (getItem(position) is ImageItem) {
            return 1
        } else if (getItem(position) is VideoItem) {
            return 2
        }
        return 1
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return if (mIsShowCamera) mItems!!.size + 1 else mItems!!.size
    }

    fun getItem(position: Int): ITEM? {
        return if (mPicker.isShowCamera) {
            if (position == 0)
                null
            else
                mItems!![position - 1]
        } else {
            mItems?.get(position)
        }
    }

    interface OnItemClickListener<ITEM> {
        fun onItemClick(view: View, item: ITEM, position: Int)
    }

    interface OnItemsLoadedListener<Data> {
        fun onItemsLoaded(folders: MutableList<Folder<Data>>)
    }

    companion object {
        private val ITEM_TYPE_CAMERA = 0
        private val ITEM_TYPE_NORMAL = 1
    }
}