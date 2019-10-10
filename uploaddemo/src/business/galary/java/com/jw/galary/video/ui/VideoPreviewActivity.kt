package com.jw.galary.video.ui

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.StrictMode
import android.view.View
import com.jw.galary.base.activity.BasePreviewActivity
import com.jw.galary.img.util.Utils
import com.jw.galary.video.VideoPicker
import com.jw.galary.video.adapter.VideoPageAdapter
import com.jw.galary.video.bean.VideoItem
import com.jw.galary.video.trim.VideoTrimmerActivity
import java.io.File

class VideoPreviewActivity : BasePreviewActivity<VideoItem>(VideoPicker),
    VideoPageAdapter.PhotoViewClickListener {

    override fun initView() {
        mRvAdapter = VideoPageAdapter(this, mItems)
        (mRvAdapter as VideoPageAdapter).setPhotoViewClickListener(this)
    }

    override fun OnImageClickListener(videoItem: VideoItem) {
        OnPhotoTapListener(View(this), 0F, 0F)
    }

    override fun OnStartClickListener(videoItem: VideoItem) {
        openFile(File(videoItem.path))
    }

    override fun onEdit(item: VideoItem) {
        VideoTrimmerActivity.call(
            this,
            item.path!!,
            item.name!!
        )
    }

    /**
     * 打开文件
     *
     * @param file
     */
    private fun openFile(file: File) {

        val intent = Intent()
        // 这是比较流氓的方法，绕过7.0的文件权限检查
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val builder = StrictMode.VmPolicy.Builder()
            StrictMode.setVmPolicy(builder.build())
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        //设置intent的Action属性
        intent.action = Intent.ACTION_VIEW
        //获取文件file的MIME类型
        val type = Utils.getMIMEType(file)
        //设置intent的data和Type属性。
        intent.setDataAndType(/*uri*/Uri.fromFile(file), type)
        //跳转
        startActivity(intent)

    }
}