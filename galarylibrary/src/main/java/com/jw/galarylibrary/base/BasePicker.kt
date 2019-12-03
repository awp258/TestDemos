package com.jw.galarylibrary.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.jw.galarylibrary.base.bean.Folder
import com.jw.library.model.BaseItem
import com.jw.library.utils.BitmapUtil
import java.io.File
import java.util.*

abstract class BasePicker<ITEM : BaseItem> {
    var EXTRA_ITEMS = "extra_items" //数据传递key
    var DH_CURRENT_ITEM_FOLDER_ITEMS = "dh_current_item_folder_items"   //当前所在文件夹所有文件

    var isMultiMode = true  //是否为多选模式
    var selectLimit = 1 //选择条目限制
    var isOrigin = true //是否保持原始
    var isCrop = false  //是否裁剪
    var isShowCamera = false    //是否允许从系统拍摄

    var itemFolders: MutableList<Folder<ITEM>>? = null  //所有文件夹
    var currentItemFolderPosition = 0   //当前文件夹position
    val currentItemFolderItems: ArrayList<ITEM> //当前文件夹所有文件
        get() = itemFolders!![currentItemFolderPosition].items!!

    var selectedItems: ArrayList<ITEM> = ArrayList()    //选中的文件
    val selectItemCount: Int    //选中文件数目
        get() = selectedItems.size

    val data = HashMap<String, List<ITEM>>()
    var mItemSelectedListeners: MutableList<OnItemSelectedListener<ITEM>>? = null
    var takeFile: File? = null

    /**
     * 是否包含该条目
     * @param item ITEM
     * @return Boolean
     */
    fun isSelect(item: ITEM): Boolean {
        return this.selectedItems.contains(item)
    }

    /**
     * 选中条目
     * @param position Int
     * @param item ITEM
     * @param isAdd Boolean
     */
    fun addSelectedItem(position: Int, item: ITEM, isAdd: Boolean) {
        if (isAdd) {
            this.selectedItems.add(item)
        } else {
            this.selectedItems.remove(item)
        }

        this.notifyItemSelectedChanged(position, item, isAdd)
    }

    /**
     * 清除所有选择的条目
     */
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

    fun notifyItemSelectedChanged(position: Int, item: ITEM, isAdd: Boolean) {
        if (this.mItemSelectedListeners != null) {

            for (l in this.mItemSelectedListeners!!) {
                l.onItemSelected(position, item, isAdd)
            }

        }
    }

    abstract fun takeCapture(activity: Activity, requestCode: Int)

    fun galleryAddMedia(context: Context, path: String) {
        val uri = BitmapUtil.saveMedia2Galary(context, path)
        val mediaScanIntent = Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE")
        mediaScanIntent.data = uri
        context.sendBroadcast(mediaScanIntent)
    }

    interface OnItemSelectedListener<ITEM> {
        fun onItemSelected(position: Int, item: ITEM?, isAdd: Boolean)
    }
}