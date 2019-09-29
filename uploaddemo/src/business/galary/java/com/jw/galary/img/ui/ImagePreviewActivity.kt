package com.jw.galary.img.ui

import android.net.Uri
import com.jw.galary.base.activity.BasePreviewActivity
import com.jw.galary.img.ImagePicker
import com.jw.galary.img.adapter.ImagePageAdapter
import com.jw.galary.img.bean.ImageItem
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
            ImagePicker.REQUEST_CODE_ITEM_CROP
        )
    }
}