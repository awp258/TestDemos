package com.jw.galary.img

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Build.VERSION
import android.os.Bundle
import android.os.Environment
import android.support.v4.content.FileProvider
import android.util.Log
import com.jw.galary.img.bean.ImageFolder
import com.jw.galary.img.bean.ImageItem
import com.jw.galary.img.crop.AspectRatio
import com.jw.galary.img.loader.GlideImageLoader
import com.jw.galary.img.util.ProviderUtil
import com.jw.galary.img.util.Utils
import com.jw.galary.img.view.CropImageView.Style
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object ImagePicker {
    val TAG = ImagePicker::class.java.simpleName
    const val REQUEST_CODE_IMAGE_TAKE = 1001
    const val REQUEST_CODE_IMAGE_CROP = 1002
    const val REQUEST_CODE_IMAGE_PREVIEW = 1003
    const val RESULT_CODE_IMAGE_ITEMS = 1004
    const val RESULT_CODE_IMAGE_BACK = 1005
    const val EXTRA_SELECTED_IMAGE_POSITION = "selected_image_position"
    const val EXTRA_IMAGE_ITEMS = "extra_image_items"
    const val EXTRA_FROM_IMAGE_ITEMS = "extra_from_image_items"
    const val EXTRA_CROP_IMAGE_OUT_URI = "extra_crop_image_out_uri"
    var cutType = 2
    var isOrigin = true
    var isMultiMode = true
    var selectLimit = 9
    var isCrop = true
    var isDynamicCrop = false
    var isShowCamera = false
    var isSaveRectangle = false
    var outPutX = 1000
    var outPutY = 1000
    var focusWidth = 280
    var focusHeight = 280
    var quality = 90
    var imageLoader = GlideImageLoader()
    var style: Style = Style.RECTANGLE
    var aspectRatio: AspectRatio = AspectRatio.IMG_SRC
    var cropCacheFolder: File? = null
    var takeImageFile: File? = null
        private set
    var currentImageFolderPosition = 0
    private var mImageSelectedListeners: MutableList<OnImageSelectedListener>? = null

    var imageFolders: MutableList<ImageFolder>? = null

    val currentImageFolderItems: ArrayList<ImageItem>
        get() = imageFolders!![currentImageFolderPosition].images

    val selectImageCount: Int
        get() = selectedImages.size

    var selectedImages: ArrayList<ImageItem> = ArrayList()

    fun isSelect(item: ImageItem) = selectedImages.contains(item)

    fun clearSelectedImages() {
        selectedImages.clear()

    }

    fun clear() {
        if (mImageSelectedListeners != null) {
            mImageSelectedListeners!!.clear()
            mImageSelectedListeners = null
        }

        if (imageFolders != null) {
            imageFolders!!.clear()
            imageFolders = null
        }

        selectedImages.clear()

        currentImageFolderPosition = 0
    }

    fun takePicture(activity: Activity, requestCode: Int) {
        val takePictureIntent = Intent("android.media.action.IMAGE_CAPTURE")
        takePictureIntent.flags = 67108864
        if (takePictureIntent.resolveActivity(activity.packageManager) != null) {
            if (Utils.existSDCard()) {
                takeImageFile =
                    File(Environment.getExternalStorageDirectory(), "/DCIM/camera/")
            } else {
                takeImageFile = Environment.getDataDirectory()
            }

            takeImageFile = createFile(takeImageFile!!, "IMG_", ".jpg")
            if (takeImageFile != null) {
                val uri: Uri
                if (VERSION.SDK_INT <= 23) {
                    uri = Uri.fromFile(takeImageFile)
                } else {
                    uri = FileProvider.getUriForFile(
                        activity,
                        ProviderUtil.getFileProviderName(activity),
                        takeImageFile!!
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

    fun addOnImageSelectedListener(l: OnImageSelectedListener) {
        if (mImageSelectedListeners == null) {
            mImageSelectedListeners = ArrayList()
        }

        mImageSelectedListeners!!.add(l)
    }

    fun removeOnImageSelectedListener(l: OnImageSelectedListener) {
        if (mImageSelectedListeners != null) {
            mImageSelectedListeners!!.remove(l)
        }
    }

    fun addSelectedImageItem(position: Int, item: ImageItem, isAdd: Boolean) {
        if (isAdd) {
            selectedImages.add(item)
        } else {
            selectedImages.remove(item)
        }

        notifyImageSelectedChanged(position, item, isAdd)
    }

    private fun notifyImageSelectedChanged(position: Int, item: ImageItem, isAdd: Boolean) {
        if (mImageSelectedListeners != null) {
            val var4 = mImageSelectedListeners!!.iterator()

            while (var4.hasNext()) {
                var4.next().onImageSelected(position, item, isAdd)
            }

        }
    }

    fun restoreInstanceState(savedInstanceState: Bundle) {
        cropCacheFolder = savedInstanceState.getSerializable("cropCacheFolder") as File
        takeImageFile = savedInstanceState.getSerializable("takeImageFile") as File
        imageLoader = savedInstanceState.getSerializable("imageLoader") as GlideImageLoader
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
        outState.putSerializable("takeImageFile", takeImageFile)
        outState.putSerializable("imageLoader", imageLoader)
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

    interface OnImageSelectedListener {
        fun onImageSelected(var1: Int, var2: ImageItem, var3: Boolean)
    }
}
