package com.jw.galary.base

import com.jw.galary.base.bean.BaseItem
import com.jw.galary.base.bean.Folder
import java.io.File
import java.util.*

open class BasePicker<ITEM : BaseItem> {
    val REQUEST_CODE_ITEM_TAKE = 1001
    val REQUEST_CODE_ITEM_CROP = 1002
    val REQUEST_CODE_ITEM_PREVIEW = 1003
    val RESULT_CODE_ITEMS = 1004
    val RESULT_CODE_ITEM_BACK = 1005
    val RESULT_CODE_ITEM_CROP = 1006
    val EXTRA_SELECTED_ITEM_POSITION = "selected_item_position"
    val EXTRA_ITEMS = "extra_items"
    val EXTRA_FROM_ITEMS = "extra_from_items"
    val EXTRA_CROP_ITEM_OUT_URI = "extra_crop_item_out_uri"
    val DH_CURRENT_ITEM_FOLDER_ITEMS = "dh_current_item_folder_items"

    var isMultiMode = true
    var selectLimit = 9
    var isOrigin = true
    var isCrop = true
    var isShowCamera = false

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

    interface OnItemSelectedListener<ITEM> {
        fun onItemSelected(position: Int, item: ITEM?, isAdd: Boolean)
    }
}