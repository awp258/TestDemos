package com.jw.galary.video

import android.app.Activity
import android.content.Intent
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.support.v4.content.FileProvider
import android.util.Log
import com.jw.galary.base.BasePicker
import com.jw.galary.base.util.ProviderUtil
import com.jw.galary.base.util.Utils
import com.jw.galary.video.bean.VideoItem
import java.io.File

object VideoPicker : BasePicker<VideoItem>() {
    val TAG = VideoPicker::class.java.simpleName


    override fun takeCapture(activity: Activity, requestCode: Int) {
        val takePictureIntent = Intent("android.media.action.VIDEO_CAPTURE")
        takePictureIntent.flags = 67108864
        if (takePictureIntent.resolveActivity(activity.packageManager) != null) {
            if (Utils.existSDCard()) {
                this.takeFile =
                    File(Environment.getExternalStorageDirectory(), "/DCIM/camera/")
            } else {
                this.takeFile = Environment.getDataDirectory()
            }

            this.takeFile = createFile(this.takeFile!!, "IMG_", ".mp4")
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