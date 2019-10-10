package com.jw.galary.video.bean

import android.os.Parcel
import android.os.Parcelable

import com.jw.galary.base.bean.BaseItem

import java.io.Serializable

class VideoItem : BaseItem, Serializable, Parcelable {
    lateinit var thumbPath: String
    var duration: Long = 0

    protected constructor(`in`: Parcel) {
        name = `in`.readString()
        path = `in`.readString()
        size = `in`.readLong()
        mimeType = `in`.readString()
        duration = `in`.readLong()
        thumbPath = `in`.readString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun equals(o: Any?): Boolean {
        if (o is VideoItem) {
            val item = o as VideoItem?
            return path!!.equals(item!!.path!!, ignoreCase = true)
        } else {
            return super.equals(o)
        }
    }

    constructor() {}

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(name)
        dest.writeString(path)
        dest.writeLong(size)
        dest.writeString(mimeType)
        dest.writeLong(duration)
        dest.writeString(thumbPath)
    }

    companion object CREATOR : Parcelable.Creator<VideoItem> {
        override fun createFromParcel(parcel: Parcel): VideoItem {
            return VideoItem(parcel)
        }

        override fun newArray(size: Int): Array<VideoItem?> {
            return arrayOfNulls(size)
        }
    }
}
