package com.jw.cameralibrary

import android.content.Context
import android.content.Intent
import android.net.Uri
import java.io.File

/**
 * 创建时间：2019/5/2318:07
 * 更新时间 2019/5/2318:07
 * 版本：
 * 作者：Mr.jin
 * 描述：
 */
object CameraConfig {
    var EXTRA_ITEMS = "extra_items"
    var RESULT_CODE_ITEMS = 1004

    var VIDEO_RECORD_LENGTH: Int = 60 * 1000   //视频最大录制时长默认1min
    var CACHE_IMG_PATH: String? = null   //拍照缓存路径
    var CACHE_VIDEO_PATH: String? = null   //视频录制缓存路径
    var CACHE_VIDEO_PATH_COVER: String? = null   //视频录制封面缓存路径
    var SHOT_TYPE = 4   //相机模式 4:拍照、摄像都可 5：仅拍照 6:仅录制
    var SHOT_MODEL = 2   //相机样式 1：短视频 2：长视频

    fun galleryAddPic(context: Context, file: File) {
        val mediaScanIntent = Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE")
        val contentUri = Uri.fromFile(file)
        mediaScanIntent.data = contentUri
        context.sendBroadcast(mediaScanIntent)
    }

    fun galleryAddPic(context: Context, contentUri: Uri) {
        val mediaScanIntent = Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE")
        mediaScanIntent.data = contentUri
        context.sendBroadcast(mediaScanIntent)
    }
}