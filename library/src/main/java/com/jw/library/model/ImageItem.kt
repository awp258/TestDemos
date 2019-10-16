package com.jw.library.model

import android.os.Parcel
import android.os.Parcelable

import java.io.Serializable

class ImageItem : BaseItem, Serializable, Parcelable {
    var width: Int = 0
    var height: Int = 0
    var addTime: Long = 0
    var orientation: Int = 0

    protected constructor(parcel: Parcel) {
        name = parcel.readString()
        path = parcel.readString()
        size = parcel.readLong()
        width = parcel.readInt()
        height = parcel.readInt()
        mimeType = parcel.readString()
        addTime = parcel.readLong()
        orientation = parcel.readInt()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun equals(o: Any?): Boolean {
        if (o is ImageItem) {
            val item = o as ImageItem?
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
        dest.writeInt(width)
        dest.writeInt(height)
        dest.writeString(mimeType)
        dest.writeLong(addTime)
        dest.writeInt(orientation)
    }

    companion object CREATOR : Parcelable.Creator<ImageItem> {
        override fun createFromParcel(parcel: Parcel): ImageItem {
            return ImageItem(parcel)
        }

        override fun newArray(size: Int): Array<ImageItem?> {
            return arrayOfNulls(size)
        }
    }
}
