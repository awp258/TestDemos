package com.jw.library.utils

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import java.io.File

object VideoUtil {
    fun saveToGalary(context: Context, path: String, duration: Long): Uri {
        val resolver = context.contentResolver
        val contentValues = getVideoContentValues(path, duration, System.currentTimeMillis())
        val uri = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)
        return uri
    }

    private fun getVideoContentValues(path: String, duration: Long, time: Long): ContentValues {
        val file = File(path)
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Video.Media.TITLE, file.name)
        contentValues.put(MediaStore.Video.Media.DISPLAY_NAME, file.name)
        contentValues.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
        contentValues.put(MediaStore.Video.Media.DATE_TAKEN, time)
        contentValues.put(MediaStore.Video.Media.DATE_MODIFIED, time)
        contentValues.put(MediaStore.Video.Media.DATE_ADDED, time)
        contentValues.put(MediaStore.Video.Media.DATA, path)
        contentValues.put(MediaStore.Video.Media.SIZE, file.length())
        contentValues.put(MediaStore.Video.Media.DURATION, duration)
        return contentValues
    }
}