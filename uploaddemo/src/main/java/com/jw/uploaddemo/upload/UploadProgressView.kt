package com.jw.uploaddemo.upload

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
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

    private val type1: Int
    private val progress1: Int
    private lateinit var title: TextView
    private lateinit var pb: ProgressBar
    private lateinit var iv: ImageView
    private lateinit var tvProgress: TextView
    private lateinit var ivSuccess: ImageView
    private lateinit var ivError: ImageView
    private var mUploadItemListener: UploadItemListener? = null
    private var originTitle: String? = null

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.uploadProgressView)
        type1 = ta.getInt(R.styleable.uploadProgressView_type, 0)
        progress1 = ta.getInt(R.styleable.uploadProgressView_progress, 0)
        ta.recycle()
        initView()
    }

    private fun initView() {
        val view = View.inflate(context, R.layout.item_upload_progress, this)
        title = view.findViewById(R.id.title)
        pb = view.findViewById(R.id.pb)
        iv = view.findViewById(R.id.iv)
        ivSuccess = view.findViewById(R.id.iv_success)
        tvProgress = view.findViewById(R.id.tvProgress)
        ivError = view.findViewById(R.id.iv_error)
        ivError.setOnClickListener {
            mUploadItemListener!!.error()
            start()
        }
    }

    fun setType(type: Int, item: Any?) {
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
                if(imageItem.orientation!=0){
                    val bitmap = BitmapUtil.rotateBitmapByDegree(imageItem.path,imageItem.orientation)
                    Glide.with(context)
                        .load(BitmapUtil.Bitmap2Bytes(bitmap))
                        .placeholder(R.drawable.bg_upload_img)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(iv)
                }else{
                    Glide.with(context)
                        .load(Uri.fromFile(File((item as ImageItem).path)))
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
        tvProgress.text = "$progress%"
        if (progress == 100) {
            end()
        }
    }

    fun start(){
        tvProgress.visibility = View.VISIBLE
        ivSuccess.visibility = View.GONE
        ivError.visibility = View.GONE
        title.text = originTitle
        pb.progress = 0
    }

    fun end(){
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
        title.text = "上传失败"
        pb.progress = 0
    }

    interface UploadItemListener {
        fun success()
        fun error()
    }
}