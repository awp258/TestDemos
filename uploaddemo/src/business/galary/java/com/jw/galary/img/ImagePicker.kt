package com.jw.galary.img

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.jw.galary.base.BasePicker
import com.jw.galary.img.bean.ImageItem
import com.jw.galary.img.crop.AspectRatio
import com.jw.galary.img.view.CropImageView.Style
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object ImagePicker : BasePicker<ImageItem>() {
    val TAG = ImagePicker::class.java.simpleName

    var cutType = 2
    var isDynamicCrop = false
    var isSaveRectangle = false
    var outPutX = 1000
    var outPutY = 1000
    var focusWidth = 280
    var focusHeight = 280
    var quality = 90
    var style: Style = Style.RECTANGLE
    var aspectRatio: AspectRatio = AspectRatio.IMG_SRC

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

    fun createFile(folder: File, prefix: String, suffix: String): File {
        if (!folder.exists() || !folder.isDirectory) {
            folder.mkdirs()
        }

        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA)
        val filename = prefix + dateFormat.format(Date(System.currentTimeMillis())) + suffix
        return File(folder, filename)
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


}
