package com.jw.galarylibrary.img

import android.app.Activity
import android.content.Intent
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.support.v4.content.FileProvider
import android.util.Log
import com.jw.croplibrary.CropLibrary
import com.jw.galarylibrary.base.BasePicker
import com.jw.galarylibrary.base.util.ProviderUtil
import com.jw.library.model.ImageItem
import com.jw.library.utils.FileUtils
import com.jw.library.utils.ThemeUtils
import java.io.File

object ImagePicker : BasePicker<ImageItem>() {
    val TAG = ImagePicker::class.java.simpleName

    fun setMultipleModle(
        maxSelectCount: Int = 20,
        cutType: Int = 2,
        cutWidth: Int = 1,
        cutHeight: Int = 1,
        outPutX: Int = 413,
        outPutY: Int = 551
    ) {
        this.isMultiMode = true
        this.selectLimit = maxSelectCount
        CropLibrary.setMultipleModle(cutType, cutWidth, cutHeight, outPutX, outPutY)
    }

    fun setCircleCrop(
        cutType: Int = 0,
        cutWidth: Int = 1,
        cutHeight: Int = 1,
        outPutX: Int = 0,
        outPutY: Int = 0
    ) {
        this.isMultiMode = false
        this.selectLimit = 1
        this.isCrop = true
        CropLibrary.setCircleCrop(cutType, cutWidth, cutHeight, outPutX, outPutY)
    }

    fun setRectangleCrop(
        cutType: Int = 1,
        cutWidth: Int = 1,
        cutHeight: Int = 1,
        outPutX: Int = 0,
        outPutY: Int = 0
    ) {
        this.isMultiMode = false
        this.selectLimit = 1
        this.isCrop = true
        CropLibrary.setRectangleCrop(cutType, cutWidth, cutHeight, outPutX, outPutY)
    }

    override fun takeCapture(activity: Activity, requestCode: Int) {
        val takePictureIntent = Intent("android.media.action.IMAGE_CAPTURE")
        takePictureIntent.flags = 67108864
        if (takePictureIntent.resolveActivity(activity.packageManager) != null) {
            if (ThemeUtils.existSDCard()) {
                this.takeFile =
                    File(Environment.getExternalStorageDirectory(), "/DCIM/camera/")
            } else {
                this.takeFile = Environment.getDataDirectory()
            }

            this.takeFile = FileUtils.createFile(this.takeFile!!, "IMG_", ".jpg")
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
