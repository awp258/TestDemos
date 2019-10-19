package com.jw.croplibrary.img

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.TextView
import com.jw.croplibrary.CropLibrary
import com.jw.croplibrary.R
import com.jw.croplibrary.databinding.ActivityCropBinding
import com.jw.croplibrary.img.config.CropIwaSaveConfig.Builder
import com.jw.croplibrary.img.shape.CropIwaOvalShape
import com.jw.library.ColorCofig
import com.jw.library.ui.BaseBindingActivity
import com.jw.library.utils.BitmapUtil
import kotlinx.android.synthetic.main.activity_crop.*
import java.io.File

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
            top_bar.setBackgroundColor(Color.parseColor(ColorCofig.naviBgColor))
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
/*        if (CropLibrary.outPutX != 0 && CropLibrary.outPutY != 0) {
            builder.setSize(CropLibrary.outPutX, CropLibrary.outPutY)
        }*/

        builder.setQuality(CropLibrary.quality)
        mBinding.cvCropImage.crop(builder.build())
    }

    override fun onCroppedRegionSaved(bitmapUri: Uri?) {
        this@CropActivity.dismiss()
        val uri = BitmapUtil.saveBitmap2Galary(BitmapFactory.decodeFile(bitmapUri!!.path), this)
        CropLibrary.galleryAddPic(this@CropActivity.applicationContext, uri)
        val intent = Intent()
        intent.putExtra(CropLibrary.EXTRA_CROP_ITEM_OUT_URI, bitmapUri)
        this@CropActivity.setResult(CropLibrary.RESULT_CODE_ITEM_CROP, intent)
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

    fun releaseFolder() {
        val folder = File(CropLibrary.CACHE_IMG_CROP)
        if (!folder.exists()) {
            folder.mkdirs()
        }
        CropLibrary.cropImageCacheFolder = folder
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
