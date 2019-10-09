package com.jw.uploaddemo.activity

import android.content.Context
import android.databinding.DataBindingUtil
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.jw.galary.img.bean.ImageItem
import com.jw.galary.img.util.BitmapUtil
import com.jw.galary.video.VideoItem
import com.jw.uploaddemo.R
import com.jw.uploaddemo.UploadConfig.TYPE_UPLOAD_IMG
import com.jw.uploaddemo.UploadConfig.TYPE_UPLOAD_VIDEO
import com.jw.uploaddemo.UploadConfig.TYPE_UPLOAD_VOICE
import com.jw.uploaddemo.databinding.ItemUploadProgressBinding
import java.io.File

class ProgressAdapter(val context: Context, lists: List<Any>?) :
    DefaultAdapter<Any>(context, lists) {
    var holders = ArrayList<BaseHolder>()
    val STATE_START = 0
    val STATE_PROGRESS = 1
    val STATE_END = 2
    val STATE_ERROR = 3
    val STATE_COMPRESSING = 4

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.item_upload_progress, parent, false)
        val holder = BaseHolder(view)
        holders.add(holder)
        return holder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = lists[position]
        val type = when (item) {
            is ImageItem -> TYPE_UPLOAD_IMG
            is VideoItem -> TYPE_UPLOAD_VIDEO
            else -> TYPE_UPLOAD_VOICE
        }
        (holder as BaseHolder).bind(type, item)
    }

    fun getHolder(position: Int): BaseHolder {
        return holders[position]
    }

    override fun getItemCount(): Int {
        return if (lists == null) 0 else lists.size
    }

    open inner class BaseHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var iv: ImageView = itemView.findViewById(R.id.iv)
        private var mUploadItemListener: UploadItemListener? = null
        var originTitle: String? = null
        val binding = DataBindingUtil.bind<ItemUploadProgressBinding>(itemView)!!

        fun bind(type: Int, item: Any?) {
            when (type) {
                TYPE_UPLOAD_VIDEO -> {
                    originTitle = "视频文件上传中"
                    Glide.with(context)
                        .load(Uri.fromFile(File((item as VideoItem).thumbPath)))
                        .placeholder(R.drawable.bg_upload_video)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(iv)
                }
                TYPE_UPLOAD_IMG -> {
                    originTitle = "图片文件上传中"
                    val imageItem = item as ImageItem
                    if (imageItem.orientation != 0) {
                        val bitmap =
                            BitmapUtil.rotateBitmapByDegree(imageItem.path, imageItem.orientation)
                        Glide.with(context)
                            .load(BitmapUtil.Bitmap2Bytes(bitmap))
                            .placeholder(R.drawable.bg_upload_img)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(iv)
                    } else {
                        Glide.with(context)
                            .load(Uri.fromFile(File(item.path)))
                            .placeholder(R.drawable.bg_upload_img)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(iv)
                    }
                }
                TYPE_UPLOAD_VOICE -> {
                    originTitle = "音频文件上传中"
                    iv.setImageResource(R.drawable.bg_upload_voice)
                    iv.scaleType = ImageView.ScaleType.CENTER_INSIDE
                }
            }
            binding.title = originTitle
            binding.ivError.setOnClickListener {
                mUploadItemListener!!.error()
                binding.apply {
                    state = STATE_START
                    progress = 0
                    title = originTitle
                }
            }
        }

        fun setUploadItemListener(uploadItemListener: UploadItemListener) {
            mUploadItemListener = uploadItemListener
        }
    }

    fun refresh(position: Int, state1: Int, progress1: Int?, des: String?) {
        val holder = holders[position]
        val binding = holder.binding
        when (state1) {
            STATE_START -> {
                binding.apply {
                    state = STATE_START
                    progress = 0
                    title = holder.originTitle
                }
            }
            STATE_PROGRESS -> {
                binding.apply {
                    state = STATE_PROGRESS
                    progress = if (progress1!! > 100)
                        99
                    else
                        progress1
                    title = holder.originTitle?.substring(0, 4) + "上传中"
                    tvProgress.text = "$progress1%"
                }
                if (progress1 == 100) {
                    binding.apply {
                        state = STATE_END
                        title = "上传成功！"
                    }
                }
            }
            STATE_END -> {
                binding.apply {
                    state = STATE_END
                    title = "上传成功！"
                }
            }
            STATE_ERROR -> {
                binding.apply {
                    state = STATE_ERROR
                    progress = 0
                    title = holder.originTitle?.substring(0, 4) + "上传失败,请刷新"
                }
            }
            STATE_COMPRESSING -> {
                binding.apply {
                    state = STATE_COMPRESSING
                    title = des
                    progress = 0
                }
            }
        }
    }


    interface UploadItemListener {
        fun success()
        fun error()
    }
}