package com.jw.galarylibrary.img.ui

import android.net.Uri
import com.jw.croplibrary.CropConfig
import com.jw.croplibrary.img.CropActivity
import com.jw.galarylibrary.base.activity.BasePreviewActivity
import com.jw.galarylibrary.img.ImagePicker
import com.jw.galarylibrary.img.adapter.ImagePageAdapter
import com.jw.library.model.ImageItem
import java.io.File

class ImagePreviewActivity : BasePreviewActivity<ImageItem>(ImagePicker) {

    override fun initView() {
        mRvAdapter = ImagePageAdapter(this, mItems)
        (mRvAdapter as ImagePageAdapter).setPhotoViewClickListener(this)
        mBinding.topBar.tvDes.text = "图片"
    }

    override fun onEdit(item: ImageItem) {
        startActivityForResult(
            CropActivity.callingIntent(this@ImagePreviewActivity, Uri.fromFile(File(item.path))),
            CropConfig.REQUEST_CODE_ITEM_CROP
        )
    }
}