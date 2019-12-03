package com.jw.croplibrary

import android.app.Application
import android.content.Context
import android.content.Intent
import com.jw.croplibrary.img.AspectRatio
import com.jw.croplibrary.img.CropImageView.Style
import com.jw.library.utils.BitmapUtil
import iknow.android.utils.BaseUtils

/**
 * 创建时间：2019/5/2318:07
 * 更新时间 2019/5/2318:07
 * 版本：
 * 作者：Mr.jin
 * 描述：
 */
object CropLibrary {
    var EXTRA_CROP_ITEM_OUT_URI = "extra_crop_item_out_uri"

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
    var isSaveToGalary = true   //裁剪后的图片是否存储到相册
    var isExactlyOutput = false   //是否强制固定尺寸输出

    fun setMultipleModle(
        cutType: Int = 2,
        cutWidth: Int = 1,
        cutHeight: Int = 1,
        outPutX: Int = 0,
        outPutY: Int = 0
    ) {
        this.cutType = cutType
        this.outPutX = outPutX
        this.outPutY = outPutY
        this.style = Style.RECTANGLE
        this.isDynamicCrop = true
    }

    fun setCircleCrop(
        cutType: Int = 0,
        cutWidth: Int = 1,
        cutHeight: Int = 1,
        outPutX: Int = 0,
        outPutY: Int = 0
    ) {
        this.cutType = cutType
        this.outPutX = outPutX
        this.outPutY = outPutY
        this.aspectRatio = AspectRatio(cutWidth, cutHeight)
        this.style = Style.CIRCLE
        this.isDynamicCrop = false
    }

    fun setRectangleCrop(
        cutType: Int = 1,
        cutWidth: Int = 1,
        cutHeight: Int = 1,
        outPutX: Int = 0,
        outPutY: Int = 0
    ) {
        this.cutType = cutType
        this.outPutX = outPutX
        this.outPutY = outPutY
        this.aspectRatio = AspectRatio(cutWidth, cutHeight)
        this.style = Style.RECTANGLE
        this.isDynamicCrop = false
    }

    fun galleryAddMedia(context: Context, path: String) {
        val uri = BitmapUtil.saveMedia2Galary(context, path)
        val mediaScanIntent = Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE")
        mediaScanIntent.data = uri
        context.sendBroadcast(mediaScanIntent)
    }

    fun init(application: Application, baseCachePath: String) {
        CACHE_IMG_CROP = "$baseCachePath/雷小锋"
        CACHE_VIDEO_CROP = "$baseCachePath/雷小锋"
        CACHE_VIDEO_CROP_COVER = "$baseCachePath/crop/video/cover"
        BaseUtils.init(application)
    }
}