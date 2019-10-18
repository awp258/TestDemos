package com.jw.croplibrary

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.jw.croplibrary.img.AspectRatio
import com.jw.croplibrary.img.CropImageView.Style
import iknow.android.utils.BaseUtils
import java.io.File

/**
 * 创建时间：2019/5/2318:07
 * 更新时间 2019/5/2318:07
 * 版本：
 * 作者：Mr.jin
 * 描述：
 */
object CropLibrary {
    var REQUEST_CODE_ITEM_CROP = 3001
    var EXTRA_CROP_ITEM_OUT_URI = "extra_crop_item_out_uri"
    var RESULT_CODE_ITEM_CROP = 3002

    var CACHE_IMG_CROP: String? = null   //裁剪图片缓存路径
    var CACHE_VIDEO_CROP: String? = null   //裁剪视频缓存路径
    var CACHE_VIDEO_CROP_COVER: String? = null   //裁剪视频封面缓存路径

    var cutType = 2
    var isDynamicCrop = true
    var outPutX = 0
    var outPutY = 0
    var style: Style = Style.RECTANGLE
    var aspectRatio: AspectRatio = AspectRatio.IMG_SRC
    var quality = 100

    var cropImageCacheFolder: File? = null
    var cropVideoCacheFolder: File? = null


    fun setMultipleModle(
        cutType: Int = 2,
        outPutX: Int = 0,
        outPutY: Int = 0,
        isCrop: Boolean = false
    ) {
        this.cutType = cutType
        this.outPutX = outPutX
        this.outPutY = outPutY
        this.style = Style.RECTANGLE
        this.isDynamicCrop = true
    }

    fun setCircleCrop(
        cutType: Int = 0,
        outPutX: Int = 1,
        outPutY: Int = 1
    ) {
        this.cutType = cutType
        this.outPutX = outPutX
        this.outPutY = outPutY
        this.aspectRatio = AspectRatio(outPutX, outPutY)
        this.style = Style.CIRCLE
        this.isDynamicCrop = false
    }

    fun setRectangleCrop(
        cutType: Int = 1,
        outPutX: Int = 1,
        outPutY: Int = 1
    ) {
        this.cutType = cutType
        this.outPutX = outPutX
        this.outPutY = outPutY
        this.aspectRatio = AspectRatio(outPutX, outPutY)
        this.style = Style.RECTANGLE
        this.isDynamicCrop = false
    }

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

    fun init(context: Context, baseCachePath: String) {
        CACHE_IMG_CROP = "$baseCachePath/crop/picture"
        CACHE_VIDEO_CROP = "$baseCachePath/crop/video"
        CACHE_VIDEO_CROP_COVER = "$baseCachePath/crop/video/cover"
        BaseUtils.init(context)
    }
}