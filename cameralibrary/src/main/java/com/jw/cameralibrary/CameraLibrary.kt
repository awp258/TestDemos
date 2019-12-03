package com.jw.cameralibrary

import android.content.Context
import android.content.Intent
import com.jw.library.utils.BitmapUtil

/**
 * 创建时间：2019/5/2318:07
 * 更新时间 2019/5/2318:07
 * 版本：
 * 作者：Mr.jin
 * 描述：
 */
object CameraLibrary {
    var EXTRA_ITEMS = "extra_items"

    var VIDEO_RECORD_LENGTH: Int = 60 * 1000   //视频最大录制时长默认1min
    var CACHE_IMG_PATH: String? = null   //拍照缓存路径
    var CACHE_VIDEO_PATH: String? = null   //视频录制缓存路径
    var CACHE_VIDEO_PATH_COVER: String? = null   //视频录制封面缓存路径
    var SHOT_TYPE = 4   //相机模式 4:拍照、摄像都可 5：仅拍照 6:仅录制
    var SHOT_MODEL = 2   //相机样式 1：长视频 2：短视频
    var isCrop = false

    fun galleryAddPic(context: Context, path: String) {
        val uri = BitmapUtil.saveMedia2Galary(context, path)
        val mediaScanIntent = Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE")
        mediaScanIntent.data = uri
        context.sendBroadcast(mediaScanIntent)
    }

    fun init(baseCachePath: String) {
        CACHE_IMG_PATH = "$baseCachePath/雷小锋"
        CACHE_VIDEO_PATH = "$baseCachePath/雷小锋"
        CACHE_VIDEO_PATH_COVER = "$baseCachePath/shot/video/cover"
    }

}