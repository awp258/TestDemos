package com.jw.library.model

import android.os.Parcel
import android.os.Parcelable

import java.io.Serializable

class VideoItem : BaseItem, Serializable, Parcelable {
    lateinit var thumbPath: String
    var duration: Long = 0

    protected constructor(parcel: Parcel) {
        name = parcel.readString()
        path = parcel.readString()
        size = parcel.readString()
        mimeType = parcel.readString()
        duration = parcel.readLong()
        thumbPath = parcel.readString()
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

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(path)
        parcel.writeString(size)
        parcel.writeString(mimeType)
        parcel.writeLong(duration)
        parcel.writeString(thumbPath)
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
