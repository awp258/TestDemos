package com.jw.galarylibrary.img.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.jw.croplibrary.CropConfig
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
        val intent = Intent(this, ImagePreviewActivity::class.java)
        if (position != null) {
            intent.putExtra(ImagePicker.EXTRA_SELECTED_ITEM_POSITION, position)
            ImagePicker.data[DH_CURRENT_ITEM_FOLDER_ITEMS] = ImagePicker.currentItemFolderItems
        } else {
            intent.putExtra(ImagePicker.EXTRA_SELECTED_ITEM_POSITION, 0)
            intent.putExtra(ImagePicker.EXTRA_ITEMS, ImagePicker.selectedItems)
            intent.putExtra(ImagePicker.EXTRA_FROM_ITEMS, true)
        }
        startActivityForResult(intent, ImagePicker.REQUEST_CODE_ITEM_PREVIEW)

    }

    override fun onEdit(imageItem: ImageItem) {
        startActivityForResult(
            CropActivity.callingIntent(this, Uri.fromFile(File(imageItem.path))),
            CropConfig.REQUEST_CODE_ITEM_CROP
        )
    }

    companion object {
        val REQUEST_PERMISSION_STORAGE = 1
        val REQUEST_PERMISSION_CAMERA = 2
        val EXTRAS_TAKE_PICKERS = "TAKE"
        val EXTRAS_IMAGES = "IMAGES"
        val SPAN_COUNT = 4
    }
}
