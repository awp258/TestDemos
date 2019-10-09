package com.jw.galary.img

import android.app.Activity
import android.content.Intent
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.support.v4.content.FileProvider
import android.util.Log
import com.jw.galary.base.BasePicker
import com.jw.galary.img.bean.ImageItem
import com.jw.galary.img.crop.AspectRatio
import com.jw.galary.img.util.ProviderUtil
import com.jw.galary.img.util.Utils
import com.jw.galary.img.view.CropImageView.Style
import java.io.File

object ImagePicker : BasePicker<ImageItem>() {
    val TAG = ImagePicker::class.java.simpleName

    var cutType = 2
    var isDynamicCrop = true
    var isSaveRectangle = false
    var outPutX = 0
    var outPutY = 0
    var focusWidth = 280
    var focusHeight = 280
    var quality = 100
    var style: Style = Style.RECTANGLE
    var aspectRatio: AspectRatio = AspectRatio.IMG_SRC

    fun setMultipleModle(
        maxSelectCount: Int = 20,
        cutType: Int = 2,
        outPutX: Int = 0,
        outPutY: Int = 0
    ) {
        this.cutType = cutType
        this.outPutX = outPutX
        this.outPutY = outPutY
        this.style = Style.RECTANGLE
        this.isDynamicCrop = true
        this.isMultiMode = true
        this.selectLimit = maxSelectCount
        this.isCrop = false
    }

    fun setCircleCrop(cutType: Int = 0, outPutX: Int = 1, outPutY: Int = 1) {
        this.cutType = cutType
        this.outPutX = outPutX
        this.outPutY = outPutY
        this.aspectRatio = AspectRatio(outPutX, outPutY)
        this.style = Style.CIRCLE
        this.isDynamicCrop = false
        this.isMultiMode = false
        this.selectLimit = 1
        this.isCrop = true
    }

    fun setRectangleCrop(cutType: Int = 1, outPutX: Int = 1, outPutY: Int = 1) {
        this.cutType = cutType
        this.outPutX = outPutX
        this.outPutY = outPutY
        this.aspectRatio = AspectRatio(outPutX, outPutY)
        this.style = Style.RECTANGLE
        this.isDynamicCrop = false
        this.isMultiMode = false
        this.selectLimit = 1
        this.isCrop = true
    }

    fun restoreInstanceState(savedInstanceState: Bundle) {
        cropCacheFolder = savedInstanceState.getSerializable("cropCacheFolder") as File
        style = savedInstanceState.getSerializable("style") as Style
        isMultiMode = savedInstanceState.getBoolean("multiMode")
        isCrop = savedInstanceState.getBoolean("crop")
        isShowCamera = savedInstanceState.getBoolean("showCamera")
        isSaveRectangle = savedInstanceState.getBoolean("isSaveRectangle")
        selectLimit = savedInstanceState.getInt("selectLimit")
        outPutX = savedInstanceState.getInt("outPutX")
        outPutY = savedInstanceState.getInt("outPutY")
        focusWidth = savedInstanceState.getInt("focusWidth")
        focusHeight = savedInstanceState.getInt("focusHeight")
    }

    fun saveInstanceState(outState: Bundle) {
        outState.putSerializable("cropCacheFolder", cropCacheFolder)
        outState.putSerializable("style", style)
        outState.putBoolean("multiMode", isMultiMode)
        outState.putBoolean("crop", isCrop)
        outState.putBoolean("showCamera", isShowCamera)
        outState.putBoolean("isSaveRectangle", isSaveRectangle)
        outState.putInt("selectLimit", selectLimit)
        outState.putInt("outPutX", outPutX)
        outState.putInt("outPutY", outPutY)
        outState.putInt("focusWidth", focusWidth)
        outState.putInt("focusHeight", focusHeight)
    }

    override fun takeCapture(activity: Activity, requestCode: Int) {
        val takePictureIntent = Intent("android.media.action.IMAGE_CAPTURE")
        takePictureIntent.flags = 67108864
        if (takePictureIntent.resolveActivity(activity.packageManager) != null) {
            if (Utils.existSDCard()) {
                this.takeFile =
                    File(Environment.getExternalStorageDirectory(), "/DCIM/camera/")
            } else {
                this.takeFile = Environment.getDataDirectory()
            }

            this.takeFile = createFile(this.takeFile!!, "IMG_", ".jpg")
            if (this.takeFile != null) {
                val uri: Uri
                if (Build.VERSION.SDK_INT <= 23) {
                    uri = Uri.fromFile(this.takeFile)
                } else {
                    uri = FileProvider.getUriForFile(
                        activity,
                        ProviderUtil.getFileProviderName(activity),
                        this.takeFile!!
                    )
                    val resInfoList =
                        activity.packageManager.queryIntentActivities(takePictureIntent, 65536)
                    val var6 = resInfoList.iterator()

                    while (var6.hasNext()) {
                        val resolveInfo = var6.next() as ResolveInfo
                        val packageName = resolveInfo.activityInfo.packageName
                        activity.grantUriPermission(packageName, uri, 3)
                    }
                }

                Log.e("nanchen", ProviderUtil.getFileProviderName(activity))
                takePictureIntent.putExtra("output", uri)
            }
        }

        activity.startActivityForResult(takePictureIntent, requestCode)
    }
}
