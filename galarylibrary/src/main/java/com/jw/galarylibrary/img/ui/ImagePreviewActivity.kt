package com.jw.galarylibrary.img.ui

import android.content.Intent
import android.net.Uri
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import com.jw.croplibrary.img.CropActivity
import com.jw.galarylibrary.base.activity.BasePreviewActivity
import com.jw.galarylibrary.img.ImagePicker
import com.jw.galarylibrary.img.adapter.ImagePageAdapter
import com.jw.library.model.ImageItem
import java.io.File
import java.util.*

class ImagePreviewActivity : BasePreviewActivity<ImageItem>(ImagePicker) {

    override fun initView() {
        mRvAdapter = ImagePageAdapter(this, mItems)
        (mRvAdapter as ImagePageAdapter).setPhotoViewClickListener(this)
        mBinding.topBar.tvDes.text = "图片"
    }

    override fun onEdit(item: ImageItem) {
        CropActivity.start(this, Uri.fromFile(File(item.path)))
    }

    companion object {

        fun start(
            activity: AppCompatActivity,
            position: Int,
            items: ArrayList<ImageItem>?,
            isFromItems: Boolean
        ) {
            val intent = Intent(activity, ImagePreviewActivity::class.java)
            intent.putExtra(EXTRA_SELECTED_ITEM_POSITION, position)
            intent.putExtra(EXTRA_ITEMS, items)
            intent.putExtra(EXTRA_FROM_ITEMS, isFromItems)
            ActivityCompat.startActivityForResult(activity, intent, REQUEST_CODE_ITEM_PREVIEW, null)
        }
    }
}