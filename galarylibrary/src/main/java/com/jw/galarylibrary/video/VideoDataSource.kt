package com.jw.galarylibrary.video

import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.FragmentActivity
import android.support.v4.app.LoaderManager.LoaderCallbacks
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import com.jw.galarylibrary.R
import com.jw.galarylibrary.base.adapter.GridAdapter
import com.jw.galarylibrary.base.bean.Folder
import com.jw.library.model.VideoItem
import java.io.File
import java.util.*

class VideoDataSource internal constructor(
    private val activity: FragmentActivity,
    path: String?,
    private val loadedListener: GridAdapter.OnItemsLoadedListener<VideoItem>
) : LoaderCallbacks<Cursor> {

    private val IMAGE_PROJECTION = arrayOf(
        MediaStore.Video.Media.DISPLAY_NAME,
        MediaStore.Video.Thumbnails.DATA,
        MediaStore.Video.Media.SIZE,
        MediaStore.Video.Media._ID,
        MediaStore.Video.Media.DURATION
    )
    private val videoFolders = ArrayList<Folder<VideoItem>>()

    init {
        val loaderManager = activity.supportLoaderManager
        if (path == null) {
            loaderManager.initLoader(0, null, this)
        } else {
            val bundle = Bundle()
            bundle.putString("path", path)
            loaderManager.initLoader(1, bundle, this)
        }

    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        var cursorLoader: CursorLoader? = null
        if (id == LOADER_ALL) {
            cursorLoader = CursorLoader(
                this.activity,
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                this.IMAGE_PROJECTION,
                MediaStore.Video.Media.MIME_TYPE + "=?",
                arrayOf("video/mp4"),
                MediaStore.Video.Media.DATE_MODIFIED + " desc"
            )
        }

        if (id == LOADER_CATEGORY) {
            cursorLoader = CursorLoader(
                this.activity,
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                this.IMAGE_PROJECTION,
                this.IMAGE_PROJECTION[1] + " like '%" + args!!.getString("path") + "%'",
                null,
                this.IMAGE_PROJECTION[6] + " DESC"
            )
        }

        return cursorLoader!!
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        if (this.videoFolders.size != 0)
            return
        this.videoFolders.clear()
        if (data != null) {
            val allVideos = ArrayList<VideoItem>()

            while (data.moveToNext()) {
                val videoName = data.getString(data.getColumnIndexOrThrow(this.IMAGE_PROJECTION[0]))
                val videoPath = data.getString(data.getColumnIndexOrThrow(this.IMAGE_PROJECTION[1]))
                val imageSize = data.getLong(data.getColumnIndexOrThrow(this.IMAGE_PROJECTION[2]))
                val videoId = data.getLong(data.getColumnIndexOrThrow(this.IMAGE_PROJECTION[3]))
                val duration = data.getLong(data.getColumnIndexOrThrow(this.IMAGE_PROJECTION[4]))
                /*                if(duration>MAX_LENGTH)
                    continue;*/
                //提前生成缩略图，再获取：http://stackoverflow.com/questions/27903264/how-to-get-the-video-thumbnail-path-and-+not-the-bitmap
                MediaStore.Video.Thumbnails.getThumbnail(
                    activity.contentResolver,
                    videoId,
                    MediaStore.Video.Thumbnails.MICRO_KIND,
                    null
                )
                val projection =
                    arrayOf(MediaStore.Video.Thumbnails._ID, MediaStore.Video.Thumbnails.DATA)

                val cursor = activity.contentResolver.query(
                    MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                    projection,
                    MediaStore.Video.Thumbnails.VIDEO_ID + "=?",
                    arrayOf(videoId.toString() + ""),
                    null
                )
                var thumbPath = ""
                while (cursor!!.moveToNext()) {
                    thumbPath =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA))
                }
                cursor.close()
                val file = File(videoPath)
                if (file.exists() && file.length() > 0L) {

                    val videoItem = VideoItem()
                    videoItem.name = videoName
                    videoItem.path = videoPath
                    videoItem.size = imageSize
                    videoItem.duration = duration
                    videoItem.thumbPath = thumbPath
                    allVideos.add(videoItem)
                    val videoFile = File(videoPath)
                    val videoParentFile = videoFile.parentFile
                    val videoFolder = Folder<VideoItem>()
                    videoFolder.name = videoParentFile.name
                    videoFolder.path = videoParentFile.absolutePath
                    if (!this.videoFolders.contains(videoFolder)) {
                        val videos = ArrayList<VideoItem>()
                        videos.add(videoItem)
                        videoFolder.cover = videoItem
                        videoFolder.items = videos
                        this.videoFolders.add(videoFolder)
                    } else {
                        (this.videoFolders.get(this.videoFolders.indexOf(videoFolder)) as Folder<VideoItem>).items!!.add(
                            videoItem
                        )
                    }
                }
            }

            if (data.count > 0 && allVideos.size > 0) {
                val allVideosFolder = Folder<VideoItem>()
                allVideosFolder.name = this.activity.resources.getString(R.string.ip_all_videos)
                allVideosFolder.path = "/"
                allVideosFolder.cover = allVideos[0]
                allVideosFolder.items = allVideos
                this.videoFolders.add(0, allVideosFolder)
            }
        }

        VideoPicker.itemFolders = this.videoFolders
        this.loadedListener.onItemsLoaded(this.videoFolders)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        println("--------")
    }

    companion object {
        private val LOADER_ALL = 0
        private val LOADER_CATEGORY = 1
        var MAX_LENGTH = VideoPicker.VIDEO_RECORD_LENGTH
    }
}
