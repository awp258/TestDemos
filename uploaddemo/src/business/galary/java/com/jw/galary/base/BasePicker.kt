package com.jw.galary.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.jw.galary.base.bean.BaseItem
import com.jw.galary.base.bean.Folder
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

abstract class BasePicker<ITEM : BaseItem> {
    var REQUEST_CODE_ITEM_TAKE = 1001
    var REQUEST_CODE_ITEM_CROP = 1002
    var REQUEST_CODE_ITEM_PREVIEW = 1003
    var RESULT_CODE_ITEMS = 1004
    var RESULT_CODE_ITEM_BACK = 1005
    var RESULT_CODE_ITEM_CROP = 1006
    var EXTRA_SELECTED_ITEM_POSITION = "selected_item_position"
    var EXTRA_ITEMS = "extra_items"
    var EXTRA_FROM_ITEMS = "extra_from_items"
    var EXTRA_CROP_ITEM_OUT_URI = "extra_crop_item_out_uri"
    var DH_CURRENT_ITEM_FOLDER_ITEMS = "dh_current_item_folder_items"

    var isMultiMode = true
    var selectLimit = 1
    var isOrigin = true
    var isCrop = false
    var isShowCamera = false

    var takeFile: File? = null

    var currentItemFolderPosition = 0
    var mItemSelectedListeners: MutableList<OnItemSelectedListener<ITEM>>? = null

    var itemFolders: MutableList<Folder<ITEM>>? = null

    val currentItemFolderItems: ArrayList<ITEM>
        get() = itemFolders!![currentItemFolderPosition].items!!

    val selectItemCount: Int
        get() = selectedItems.size

    var selectedItems: ArrayList<ITEM> = ArrayList()

    val data = HashMap<String, List<ITEM>>()

    var cropCacheFolder: File? = null

    fun isSelect(item: ITEM): Boolean {
        return this.selectedItems.contains(item)
    }

    fun clearSelectedItems() {
        this.selectedItems.clear()

    }

    fun clear() {
        if (this.mItemSelectedListeners != null) {
            this.mItemSelectedListeners!!.clear()
            this.mItemSelectedListeners = null
        }
        if (this.itemFolders != null) {
            this.itemFolders!!.clear()
            this.itemFolders = null
        }

        this.selectedItems.clear()

        this.currentItemFolderPosition = 0
    }

    fun addOnItemSelectedListener(l: OnItemSelectedListener<ITEM>) {
        if (this.mItemSelectedListeners == null) {
            this.mItemSelectedListeners = ArrayList()
        }

        this.mItemSelectedListeners!!.add(l)
    }

    fun removeOnItemSelectedListener(l: OnItemSelectedListener<ITEM>) {
        if (this.mItemSelectedListeners != null) {
            this.mItemSelectedListeners!!.remove(l)
        }
    }

    fun addSelectedItem(position: Int, item: ITEM, isAdd: Boolean) {
        if (isAdd) {
            this.selectedItems.add(item)
        } else {
            this.selectedItems.remove(item)
        }

        this.notifyItemSelectedChanged(position, item, isAdd)
    }

    fun notifyItemSelectedChanged(position: Int, item: ITEM, isAdd: Boolean) {
        if (this.mItemSelectedListeners != null) {

            for (l in this.mItemSelectedListeners!!) {
                l.onItemSelected(position, item, isAdd)
            }

        }
    }

    fun createFile(folder: File, prefix: String, suffix: String): File {
        if (!folder.exists() || !folder.isDirectory) {
            folder.mkdirs()
        }

        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA)
        val filename = prefix + dateFormat.format(Date(System.currentTimeMillis())) + suffix
        return File(folder, filename)
    }

    abstract fun takeCapture(activity: Activity, requestCode: Int)

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

    interface OnItemSelectedListener<ITEM> {
        fun onItemSelected(position: Int, item: ITEM?, isAdd: Boolean)
    }
}