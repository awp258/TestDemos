package com.jw.croplibrary.img

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View.OnClickListener
import com.jw.croplibrary.CropLibrary
import com.jw.croplibrary.R
import com.jw.croplibrary.databinding.ActivityCropBinding
import com.jw.croplibrary.img.config.CropIwaSaveConfig.Builder
import com.jw.croplibrary.img.shape.CropIwaOvalShape
import com.jw.library.ColorCofig
import com.jw.library.ui.BaseBindingActivity
import com.jw.library.utils.BitmapUtil
import com.jw.library.utils.FileUtils
import java.io.File

/**
 * 创建时间：
 * 更新时间
 * 版本：
 * 作者：Mr.jin
 * 描述：图片裁剪Activity
 */
class CropActivity : BaseBindingActivity<ActivityCropBinding>(),
    CropIwaView.CropSaveCompleteListener, CropIwaView.ErrorListener {
    private var mProgressDialog: ProgressDialog? = null
    private var dstPath: String? = null
    private var originAngle: Float = 0.toFloat()

    override fun getLayoutId() = R.layout.activity_crop

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        releaseFolder()
        mBinding.apply {
            clickListener = OnClickListener {
                when (it.id) {
                    R.id.tv_rotate -> rotate()
                    R.id.tv_recover -> recover()
                    R.id.btn_back -> finish()
                    R.id.btn_ok -> crop()
                }
            }
            topBar.btnOk.text = getString(R.string.ip_complete)
            setConfirmButtonBg(topBar.btnOk)
        }

        val imageUri = intent.getParcelableExtra<Uri>("CropImage")
        mBinding.cvCropImage.setImageUri(imageUri)
        mBinding.cvCropImage.configureOverlay().setAspectRatio(CropLibrary.aspectRatio)
            .setDynamicCrop(CropLibrary.isDynamicCrop).apply()
        if (CropLibrary.style == CropImageView.Style.CIRCLE) {
            mBinding.cvCropImage.configureOverlay()
                .setCropShape(CropIwaOvalShape(mBinding.cvCropImage.configureOverlay())).apply()
        }
        dstPath =
            File(
                CropLibrary.CACHE_IMG_CROP,
                "IMG_" + System.currentTimeMillis() + ".png"
            ).absolutePath
        mBinding.cvCropImage.setCropSaveCompleteListener(this)
        mBinding.cvCropImage.setErrorListener { this@CropActivity.dismiss() }
        originAngle = mBinding.cvCropImage.matrixAngle
        mBinding.topBar.tvDes.setTextColor(Color.parseColor(ColorCofig.naviTitleColor))
    }

    private fun rotate() {
        mBinding.cvCropImage.rotateImage(90.0f)
    }

    private fun recover() {
        val currentAngle = mBinding.cvCropImage.matrixAngle
        if (originAngle != currentAngle) {
            mBinding.cvCropImage.rotateImage(originAngle - currentAngle)
        }

        mBinding.cvCropImage.initialize()
    }

    private fun crop() {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog(this@CropActivity)
            mProgressDialog!!.setMessage("正在处理中...")
            mProgressDialog!!.setCanceledOnTouchOutside(false)
            mProgressDialog!!.setCancelable(false)
        }

        mProgressDialog!!.show()
        val builder = Builder(Uri.fromFile(File(dstPath)))
/*        if (CropLibrary.outPutX != 0 && CropLibrary.outPutY != 0) {
            builder.setSize(CropLibrary.outPutX, CropLibrary.outPutY)
        }*/

        builder.setQuality(CropLibrary.quality)
        mBinding.cvCropImage.crop(builder.build())
    }

    override fun onCroppedRegionSaved(bitmapUri: Uri?) {
        dismiss()
        if (CropLibrary.isSaveToGalary) {
            val uri = BitmapUtil.saveBitmap2Galary(BitmapFactory.decodeFile(bitmapUri!!.path), this)
            CropLibrary.galleryAddPic(this@CropActivity, uri)
        }
        val intent = Intent()
        intent.putExtra(CropLibrary.EXTRA_CROP_ITEM_OUT_URI, bitmapUri)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onError(var1: Throwable?) {
        dismiss()
    }

    private fun dismiss() {
        if (mProgressDialog != null && mProgressDialog!!.isShowing) {
            mProgressDialog!!.dismiss()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        dismiss()
        mProgressDialog = null
    }

    private fun releaseFolder() {
        FileUtils.releaseFolder(CropLibrary.CACHE_IMG_CROP!!)
    }

    companion object {
        const val REQUEST_CODE_ITEM_CROP = 3001
        const val EXTRA_URI = "CropImage"

        fun start(activity: AppCompatActivity, imageUri: Uri) {
            val intent = Intent(activity, CropActivity::class.java)
            intent.putExtra(EXTRA_URI, imageUri)
            activity.startActivityForResult(intent, REQUEST_CODE_ITEM_CROP)
        }
    }
}
