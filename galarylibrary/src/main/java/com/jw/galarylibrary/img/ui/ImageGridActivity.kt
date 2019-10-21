package com.jw.galarylibrary.img.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat.startActivityForResult
import android.support.v7.app.AppCompatActivity
import com.jw.croplibrary.img.CropActivity
import com.jw.galarylibrary.R
import com.jw.galarylibrary.base.activity.BaseGridActivity
import com.jw.galarylibrary.img.ImageDataSource
import com.jw.galarylibrary.img.ImagePicker
import com.jw.galarylibrary.img.ImagePicker.DH_CURRENT_ITEM_FOLDER_ITEMS
import com.jw.library.model.ImageItem
import kotlinx.android.synthetic.main.activity_grid.view.*
import java.io.File

class ImageGridActivity : BaseGridActivity<ImageItem>(ImagePicker) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ImageDataSource(this, null as String?, this)
        onItemSelected(0, null, false)
        mBinding.topBar.tvDes.text = "图片"
        mBinding.footerBar.tv_dir.text = getString(R.string.ip_all_images)
    }

    override fun onPreview(position: Int?) {
        if (position != null) {
            ImagePicker.data[DH_CURRENT_ITEM_FOLDER_ITEMS] = ImagePicker.currentItemFolderItems
            ImagePreviewActivity.start(this, position, null, false)
        } else {
            ImagePreviewActivity.start(this, 0, ImagePicker.selectedItems, true)
        }
    }

    override fun onEdit(imageItem: ImageItem) {
        CropActivity.start(this, Uri.fromFile(File(imageItem.path)))
    }

    companion object {

        fun start(activity: AppCompatActivity) {
            val intent = Intent(activity, ImageGridActivity::class.java)
            startActivityForResult(activity, intent, REQUEST_CODE_GRID, null)
        }
    }
}
