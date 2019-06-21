package com.jw.uploaddemo

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.jw.uploaddemo.UploadConfig.TYPE_UPLOAD_IMG
import com.jw.uploaddemo.UploadConfig.TYPE_UPLOAD_VIDEO
import com.jw.uploaddemo.UploadConfig.TYPE_UPLOAD_VOICE

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
        tvProgress = view.findViewById(R.id.tvProgress)
    }

    fun setType(type: Int) {
        when(type){
            TYPE_UPLOAD_VIDEO->{
                title.text = "视频文件上传中"
                iv.setImageResource(R.drawable.bg_upload_video)
            }
            TYPE_UPLOAD_IMG->{
                title.text = "图片文件上传中"
                iv.setImageResource(R.drawable.bg_upload_img)
            }
            TYPE_UPLOAD_VOICE->{
                title.text = "音频文件上传中"
                iv.setImageResource(R.drawable.bg_upload_voice)
            }
        }
    }

    fun setProgress(progress: Int) {
        pb.progress = progress
        tvProgress.text = progress.toString()
    }
}