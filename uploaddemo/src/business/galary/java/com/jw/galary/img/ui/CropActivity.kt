package com.jw.galary.img.ui

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.View.OnClickListener
import android.widget.TextView
import com.jw.galary.img.ImagePicker
import com.jw.galary.img.crop.CropIwaView
import com.jw.galary.img.crop.config.CropIwaSaveConfig.Builder
import com.jw.galary.img.crop.shape.CropIwaOvalShape
import com.jw.galary.img.view.CropImageView.Style
import com.jw.uploaddemo.ColorCofig
import com.jw.uploaddemo.R
import com.jw.uploaddemo.databinding.ActivityCropBinding
import com.jw.uploaddemo.uploadPlugin.UploadPluginBindingActivity
import kotlinx.android.synthetic.main.activity_crop.*
import kotlinx.android.synthetic.main.include_top_bar.view.*
import java.io.File

class CropActivity : UploadPluginBindingActivity<ActivityCropBinding>(),
    CropIwaView.CropSaveCompleteListener, CropIwaView.ErrorListener {
    private var mProgressDialog: ProgressDialog? = null
    private var dstPath: String? = null
    private var originAngle: Float = 0.toFloat()

    override fun getLayoutId() = R.layout.activity_crop

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.apply {
            clickListener = OnClickListener {
                when (it.id) {
                    R.id.tv_rotate -> rotate()
                    R.id.tv_recover -> recover()
                    R.id.btn_back -> back()
                    R.id.btn_ok -> crop()
                }
            }
            top_bar.btn_ok.text = getString(R.string.ip_complete)
        }

        val imageUri = intent.getParcelableExtra<Uri>("CropImage")
        mBinding.cvCropImage.setImageUri(imageUri)
        mBinding.cvCropImage.configureOverlay().setAspectRatio(ImagePicker.aspectRatio)
            .setDynamicCrop(ImagePicker.isDynamicCrop).apply()
        if (ImagePicker.style == Style.CIRCLE) {
            mBinding.cvCropImage.configureOverlay()
                .setCropShape(CropIwaOvalShape(mBinding.cvCropImage.configureOverlay())).apply()
        }

        val cropCacheFolder = if (ImagePicker.cutType == 2) {
            File(Environment.getExternalStorageDirectory().toString() + "/RXImagePicker/")
        } else {
            ImagePicker.cropCacheFolder
        }

        if (!cropCacheFolder!!.exists() || !cropCacheFolder.isDirectory) {
            cropCacheFolder.mkdirs()
        }

        dstPath =
            File(cropCacheFolder, "IMG_" + System.currentTimeMillis() + ".png").absolutePath
        mBinding.cvCropImage.setCropSaveCompleteListener(this)
        mBinding.cvCropImage.setErrorListener { this@CropActivity.dismiss() }
        originAngle = mBinding.cvCropImage.matrixAngle
        setConfirmButtonBg(top_bar.btn_ok)
        top_bar.setBackgroundColor(Color.parseColor(ColorCofig.naviBgColor))
        (findViewById<View>(R.id.tv_des) as TextView).setTextColor(Color.parseColor(ColorCofig.naviTitleColor))
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
        if (ImagePicker.outPutX != 0 && ImagePicker.outPutY != 0 && !ImagePicker.isOrigin) {
            builder.setSize(ImagePicker.outPutX, ImagePicker.outPutY)
        }

        builder.setQuality(ImagePicker.quality)
        mBinding.cvCropImage.crop(builder.build())
    }

    private fun back() {
        setResult(0)
        finish()
    }

    override fun onCroppedRegionSaved(bitmapUri: Uri?) {
        this@CropActivity.dismiss()
        ImagePicker.galleryAddPic(this@CropActivity.applicationContext, bitmapUri!!)
        val intent = Intent()
        intent.putExtra(ImagePicker.EXTRA_CROP_ITEM_OUT_URI, bitmapUri)
        this@CropActivity.setResult(ImagePicker.RESULT_CODE_ITEM_CROP, intent)
        this@CropActivity.finish()
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

    companion object {
        private val EXTRA_URI = "CropImage"

        fun callingIntent(context: Context, imageUri: Uri): Intent {
            val intent = Intent(context, CropActivity::class.java)
            intent.putExtra("CropImage", imageUri)
            return intent
        }
    }
}
