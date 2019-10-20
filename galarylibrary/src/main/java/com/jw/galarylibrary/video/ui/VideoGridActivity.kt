package com.jw.galarylibrary.video.ui

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityCompat.startActivityForResult
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.jw.croplibrary.video.VideoTrimmerActivity
import com.jw.galarylibrary.R
import com.jw.galarylibrary.base.activity.BaseGridActivity
import com.jw.galarylibrary.video.VideoDataSource
import com.jw.galarylibrary.video.VideoPicker
import com.jw.galarylibrary.video.VideoPicker.DH_CURRENT_ITEM_FOLDER_ITEMS
import com.jw.library.model.VideoItem
import kotlinx.android.synthetic.main.activity_grid.view.*

class VideoGridActivity : BaseGridActivity<VideoItem>(VideoPicker) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        VideoDataSource(this, null as String?, this)
        onItemSelected(0, null, false)
        mBinding.apply {
            topBar.tvDes.text = "视频"
            footerBar.tv_dir.text = getString(R.string.ip_all_video)
            cbOrigin.visibility = View.GONE
        }
    }

    override fun onEdit(item: VideoItem) {
        VideoTrimmerActivity.start(
            this,
            item.path!!,
            item.name!!
        )
    }

    override fun onPreview(position: Int?) {
        if (position != null) {
            VideoPicker.data[DH_CURRENT_ITEM_FOLDER_ITEMS] = VideoPicker.currentItemFolderItems
            VideoPreviewActivity.start(this, position, null, false)
        } else {
            VideoPreviewActivity.start(this, 0, VideoPicker.selectedItems, true)
        }
    }

    companion object {
        const val REQUEST_CODE_IMAGE_GRID = 1002

        fun start(activity: AppCompatActivity) {
            val intent = Intent(activity, VideoGridActivity::class.java)
            startActivityForResult(activity, intent, REQUEST_CODE_IMAGE_GRID, null)
        }
    }
}
