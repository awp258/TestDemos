package com.jw.uploadlibrary

import android.content.Context
import android.databinding.DataBindingUtil
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import com.jw.library.ContextUtil
import com.jw.library.loader.GlideImageLoader
import com.jw.library.model.BaseItem
import com.jw.library.model.ImageItem
import com.jw.library.model.VideoItem
import com.jw.uploadlibrary.adapter.ProgressAdapter
import com.jw.uploadlibrary.databinding.ItemUploadProgressBinding

/**
 * 创建时间：2019/5/2020:04
 * 更新时间 2019/5/2020:04
 * 版本：
 * 作者：Mr.jin
 * 描述：
 */
class UploadProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var mPosition: Int = 0
    private lateinit var mItem: BaseItem
    private var mUploadItemListener: UploadItemListener? = null
    private var originTitle: String? = null
    private var binding: ItemUploadProgressBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.item_upload_progress,
        this,
        true
    )

    fun setItem(position: Int, type: Int, item: BaseItem) {
        mPosition = position
        mItem = item
        initView(type, item)
    }

    private fun initView(type: Int, item: Any) {

        when (type) {
            UploadLibrary.TYPE_UPLOAD_VIDEO -> {
                originTitle = "视频文件上传中"
                val videoItem = item as VideoItem
                GlideImageLoader.displayImage(
                    ContextUtil.context!!,
                    videoItem.thumbPath!!,
                    binding.iv
                )
            }
            UploadLibrary.TYPE_UPLOAD_IMG -> {
                originTitle = "图片文件上传中"
                val imageItem = item as ImageItem
                GlideImageLoader.displayImage(
                    ContextUtil.context!!,
                    imageItem.path!!,
                    binding.iv
                )
            }
            UploadLibrary.TYPE_UPLOAD_VOICE -> {
                originTitle = "音频文件上传中"
                binding.iv.setImageResource(R.drawable.bg_upload_voice)
                binding.iv.scaleType = ImageView.ScaleType.CENTER_INSIDE
            }
        }
        binding.title = originTitle
        binding.ivError.setOnClickListener {
            mUploadItemListener!!.reUpload(mPosition, mItem)
            binding.apply {
                state = ProgressAdapter.STATE_START
                progress = 0
                title = originTitle
            }
        }
    }

    fun refresh(state1: Int, progress1: Int?, des: String?) {
        when (state1) {
            ProgressAdapter.STATE_START -> {
                binding.apply {
                    state = ProgressAdapter.STATE_START
                    progress = 0
                    title = originTitle
                }
            }
            ProgressAdapter.STATE_PROGRESS -> {
                binding.apply {
                    state = ProgressAdapter.STATE_PROGRESS
                    progress = if (progress1!! > 100)
                        99
                    else
                        progress1
                    title = originTitle?.substring(0, 4) + "上传中"
                    tvProgress.text = "$progress%"
                }
                if (progress1 == 100) {
                    binding.apply {
                        state = ProgressAdapter.STATE_END
                        title = "上传成功！"
                    }
                }
            }
            ProgressAdapter.STATE_END -> {
                binding.apply {
                    state = ProgressAdapter.STATE_END
                    title = "上传成功！"
                }
            }
            ProgressAdapter.STATE_ERROR -> {
                binding.apply {
                    state = ProgressAdapter.STATE_ERROR
                    progress = 0
                    title = originTitle?.substring(0, 4) + "上传失败,请刷新"
                }
            }
            ProgressAdapter.STATE_COMPRESSING -> {
                binding.apply {
                    state = ProgressAdapter.STATE_COMPRESSING
                    title = des
                    progress = 0
                }
            }
        }
    }

    fun setUploadItemListener(uploadItemListener: UploadItemListener) {
        mUploadItemListener = uploadItemListener
    }

    companion object {
        const val STATE_START = 0
        const val STATE_PROGRESS = 1
        const val STATE_END = 2
        const val STATE_ERROR = 3
        const val STATE_COMPRESSING = 4
    }

    interface UploadItemListener {
        fun reUpload(position: Int, item: BaseItem)
    }
}