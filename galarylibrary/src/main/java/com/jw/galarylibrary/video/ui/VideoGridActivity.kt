package com.jw.galarylibrary.video.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.jw.croplibrary.video.VideoTrimmerActivity
import com.jw.galarylibrary.R
import com.jw.galarylibrary.base.activity.BaseGridActivity
import com.jw.galarylibrary.video.VideoDataSource
import com.jw.galarylibrary.video.VideoPicker
import com.jw.galarylibrary.video.VideoPicker.DH_CURRENT_ITEM_FOLDER_ITEMS
import com.jw.galarylibrary.video.VideoPicker.EXTRA_FROM_ITEMS
import com.jw.galarylibrary.video.VideoPicker.EXTRA_ITEMS
import com.jw.galarylibrary.video.VideoPicker.EXTRA_SELECTED_ITEM_POSITION
import com.jw.galarylibrary.video.VideoPicker.REQUEST_CODE_ITEM_PREVIEW
import com.jw.library.model.VideoItem
import kotlinx.android.synthetic.main.activity_grid.*
import kotlinx.android.synthetic.main.activity_grid.view.*
import kotlinx.android.synthetic.main.include_top_bar.view.*

class VideoGridActivity : BaseGridActivity<VideoItem>(VideoPicker) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        VideoDataSource(this, null as String?, this)
        onItemSelected(0, null, false)
        mBinding.apply {
            top_bar.tv_des.text = "视频"
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

    companion object {
        val REQUEST_PERMISSION_STORAGE = 1
        val REQUEST_PERMISSION_CAMERA = 2
        val EXTRAS_TAKE_PICKERS = "TAKE"
        val EXTRAS_VIDEOS = "VIDEOS"
        val SPAN_COUNT = 4
    }
}
