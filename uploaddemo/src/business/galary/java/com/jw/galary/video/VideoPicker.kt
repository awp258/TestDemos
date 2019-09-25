package com.jw.galary.video

import com.jw.galary.base.Folder
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.List
import kotlin.collections.MutableList

object VideoPicker {
    val TAG = VideoPicker::class.java.simpleName
    const val REQUEST_CODE_VIDEO_TAKE = 2001
    const val REQUEST_CODE_VIDEO_CROP = 2002
    const val REQUEST_CODE_VIDEO_PREVIEW = 2003
    const val RESULT_CODE_VIDEO_ITEMS = 2004
    const val RESULT_CODE_VIDEO_BACK = 2005
    const val EXTRA_SELECTED_VIDEO_POSITION = "selected_video_position"
    const val EXTRA_VIDEO_ITEMS = "extra_video_items"
    const val EXTRA_FROM_VIDEO_ITEMS = "extra_from_items"
    const val EXTRA_CROP_VIDEOOUT_URI = "extra_crop_video_out_uri"
    const val DH_CURRENT_IMAGE_FOLDER_ITEMS = "dh_current_image_folder_items"

    var isMultiMode = true
    var selectLimit = 1
    var isShowCamera = false
    var cropCacheFolder: File? = null
    var currentVideoFolderPosition: Int = 0
    var mVideoSelectedListeners: MutableList<OnVideoSelectedListener>? = null

    var videoFolders: MutableList<Folder<VideoItem>>? = ArrayList()

    val currentVideoFolderItems: ArrayList<VideoItem>
        get() = this.videoFolders!![this.currentVideoFolderPosition].items!!

    val selectVideoCount: Int
        get() = this.selectedVideos.size

    var selectedVideos: ArrayList<VideoItem> = ArrayList()
    val data = HashMap<String, List<VideoItem>>()


    fun isSelect(item: VideoItem): Boolean {
        return this.selectedVideos.contains(item)
    }

    fun clearSelectedVideos() {
        this.selectedVideos.clear()

    }

    fun clear() {
        if (this.mVideoSelectedListeners != null) {
            this.mVideoSelectedListeners!!.clear()
            this.mVideoSelectedListeners = null
        }

        this.videoFolders!!.clear()
        this.videoFolders = null

        this.selectedVideos.clear()

        this.currentVideoFolderPosition = 0
    }

    fun addOnVideoSelectedListener(l: OnVideoSelectedListener) {
        if (this.mVideoSelectedListeners == null) {
            this.mVideoSelectedListeners = ArrayList()
        }

        this.mVideoSelectedListeners!!.add(l)
    }

    fun removeOnVideoSelectedListener(l: OnVideoSelectedListener) {
        if (this.mVideoSelectedListeners != null) {
            this.mVideoSelectedListeners!!.remove(l)
        }
    }

    fun addSelectedVideoItem(position: Int, item: VideoItem, isAdd: Boolean) {
        if (isAdd) {
            this.selectedVideos.add(item)
        } else {
            this.selectedVideos.remove(item)
        }

        this.notifyVideoSelectedChanged(position, item, isAdd)
    }

    private fun notifyVideoSelectedChanged(position: Int, item: VideoItem, isAdd: Boolean) {
        if (this.mVideoSelectedListeners != null) {

            for (l in this.mVideoSelectedListeners!!) {
                l.onVideoSelected(position, item, isAdd)
            }

        }
    }

    interface OnVideoSelectedListener {
        fun onVideoSelected(var1: Int, videoItem: VideoItem, var3: Boolean)
    }

}