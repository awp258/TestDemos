package com.jw.uploaddemo.activity

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.jw.galary.img.bean.ImageItem
import com.jw.galary.img.util.BitmapUtil
import com.jw.galary.video.VideoItem
import com.jw.uploaddemo.R
import com.jw.uploaddemo.UploadConfig.TYPE_UPLOAD_IMG
import com.jw.uploaddemo.UploadConfig.TYPE_UPLOAD_VIDEO
import com.jw.uploaddemo.UploadConfig.TYPE_UPLOAD_VOICE
import java.io.File

class ProgressAdapter(val context: Context, lists: List<Any>?) :
    DefaultAdapter<Any>(context, lists) {
    var holders = ArrayList<BaseHolder>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.item_upload_progress, parent, false)
        return BaseHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holders.add(holder as BaseHolder)
        val item = lists[position]
        val type = when (item) {
            is ImageItem -> TYPE_UPLOAD_IMG
            is VideoItem -> TYPE_UPLOAD_VIDEO
            else -> TYPE_UPLOAD_VOICE
        }
        holder.bind(type, item)
    }

    fun getHolder(position: Int): BaseHolder {
        return holders[position]
    }

    override fun getItemCount(): Int {
        return if (lists == null) 0 else lists.size
    }

    open inner class BaseHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var title: TextView = itemView.findViewById(R.id.title)
        private var pb: ProgressBar = itemView.findViewById(R.id.pb)
        private var iv: ImageView = itemView.findViewById(R.id.iv)
        private var tvProgress: TextView = itemView.findViewById(R.id.tvProgress)
        private var ivSuccess: ImageView = itemView.findViewById(R.id.iv_success)
        private var ivError: ImageView = itemView.findViewById(R.id.iv_error)
        private var mUploadItemListener: UploadItemListener? = null
        private var originTitle: String? = null

        fun bind(type: Int, item: Any?) {
            when (type) {
                TYPE_UPLOAD_VIDEO -> {
                    originTitle = "视频文件上传中"
                    title.text = "视频文件上传中"
                    Glide.with(context)
                        .load(Uri.fromFile(File((item as VideoItem).thumbPath)))
                        .placeholder(R.drawable.bg_upload_video)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(iv)
                }
                TYPE_UPLOAD_IMG -> {
                    originTitle = "图片文件上传中"
                    title.text = "图片文件上传中"
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
                    title.text = "音频文件上传中"
                    iv.setImageResource(R.drawable.bg_upload_voice)
                    iv.scaleType = ImageView.ScaleType.CENTER_INSIDE
                }
            }
        }

        @SuppressLint("SetTextI18n")
        fun setProgress(progress: Int) {
            pb.progress = progress
            title.text = originTitle?.substring(0, 4) + "上传中"
            tvProgress.visibility = View.VISIBLE
            tvProgress.text = "$progress%"
            if (progress == 100) {
                end()
            }
        }

        fun start() {
            tvProgress.visibility = View.VISIBLE
            ivSuccess.visibility = View.GONE
            ivError.visibility = View.GONE
            title.text = originTitle
            pb.progress = 0
        }

        fun end() {
            tvProgress.visibility = View.GONE
            ivSuccess.visibility = View.VISIBLE
            ivError.visibility = View.GONE
            title.text = "上传成功！"
        }

        fun setUploadItemListener(uploadItemListener: UploadItemListener) {
            mUploadItemListener = uploadItemListener
        }


        fun setError() {
            tvProgress.visibility = View.GONE
            ivSuccess.visibility = View.GONE
            ivError.visibility = View.VISIBLE
            title.text = originTitle?.substring(0, 4) + "上传失败,请刷新"
            title.setTextColor(Color.RED)
            pb.progress = 0
        }

        fun setCompressing(des: String) {
            tvProgress.visibility = View.GONE
            ivSuccess.visibility = View.GONE
            ivError.visibility = View.GONE
            title.text = des
            pb.progress = 0
        }
    }

    interface UploadItemListener {
        fun success()
        fun error()
    }
}