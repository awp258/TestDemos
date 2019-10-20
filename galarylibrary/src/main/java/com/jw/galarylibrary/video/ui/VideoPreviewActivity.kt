package com.jw.galarylibrary.video.ui

import android.content.Intent
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.jw.croplibrary.video.VideoTrimmerActivity
import com.jw.galarylibrary.base.activity.BasePreviewActivity
import com.jw.galarylibrary.video.VideoPicker
import com.jw.galarylibrary.video.adapter.VideoPageAdapter
import com.jw.library.model.VideoItem
import com.jw.library.utils.ThemeUtils
import java.io.File
import java.util.*

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
        ThemeUtils.openFile(this, File(videoItem.path))
    }

    override fun onEdit(item: VideoItem) {
        VideoTrimmerActivity.start(this, item.path!!, item.name!!)
    }

    companion object {

        fun start(
            activity: AppCompatActivity,
            position: Int,
            items: ArrayList<VideoItem>?,
            isFromItems: Boolean
        ) {
            val intent = Intent(activity, VideoPreviewActivity::class.java)
            intent.putExtra(EXTRA_SELECTED_ITEM_POSITION, position)
            intent.putExtra(EXTRA_ITEMS, items)
            intent.putExtra(EXTRA_FROM_ITEMS, isFromItems)
            ActivityCompat.startActivityForResult(activity, intent, REQUEST_CODE_ITEM_PREVIEW, null)
        }
    }
}