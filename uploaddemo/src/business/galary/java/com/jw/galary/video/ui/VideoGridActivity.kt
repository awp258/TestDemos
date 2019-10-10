package com.jw.galary.video.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.jw.galary.base.activity.BaseGridActivity
import com.jw.galary.video.VideoDataSource
import com.jw.galary.video.VideoPicker
import com.jw.galary.video.VideoPicker.DH_CURRENT_ITEM_FOLDER_ITEMS
import com.jw.galary.video.VideoPicker.EXTRA_FROM_ITEMS
import com.jw.galary.video.VideoPicker.EXTRA_ITEMS
import com.jw.galary.video.VideoPicker.EXTRA_SELECTED_ITEM_POSITION
import com.jw.galary.video.VideoPicker.REQUEST_CODE_ITEM_PREVIEW
import com.jw.galary.video.bean.VideoItem
import com.jw.galary.video.trim.VideoTrimmerActivity
import com.jw.uploaddemo.R
import com.jw.uploaddemo.UploadConfig
import kotlinx.android.synthetic.main.activity_grid.view.*
import java.io.File

class VideoGridActivity : BaseGridActivity<VideoItem>(VideoPicker) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        releaseFolder()
        VideoDataSource(this, null as String?, this)
        onItemSelected(0, null, false)
        mBinding.apply {
            topBar.tvDes.text = "视频"
            footerBar.tv_dir.text = getString(R.string.ip_all_video)
            cbOrigin.visibility = View.GONE
        }
    }

    override fun onEdit(item: VideoItem) {
        VideoTrimmerActivity.call(
            this,
            item.path!!,
            item.name!!
        )
    }

    override fun onPreview(position: Int?) {
        val intent = Intent(this, VideoPreviewActivity::class.java)
        if (position != null) {
            intent.putExtra(EXTRA_SELECTED_ITEM_POSITION, position)
            VideoPicker.data[DH_CURRENT_ITEM_FOLDER_ITEMS] = VideoPicker.currentItemFolderItems
        } else {
            intent.putExtra(EXTRA_SELECTED_ITEM_POSITION, 0)
            intent.putExtra(EXTRA_ITEMS, VideoPicker.selectedItems)
            intent.putExtra(EXTRA_FROM_ITEMS, true)
        }
        startActivityForResult(intent, REQUEST_CODE_ITEM_PREVIEW)
    }

    private fun releaseFolder() {
        val folder = File(UploadConfig.CACHE_VIDEO_CROP)
        if (!folder.exists()) {
            folder.mkdir()
        }
        val folder2 = File(UploadConfig.CACHE_VIDEO_PATH_COVER)
        if (!folder2.exists()) {
            folder2.mkdir()
        }
    }

    companion object {
        val REQUEST_PERMISSION_STORAGE = 1
        val REQUEST_PERMISSION_CAMERA = 2
        val EXTRAS_TAKE_PICKERS = "TAKE"
        val EXTRAS_VIDEOS = "VIDEOS"
        val SPAN_COUNT = 4
    }
}
