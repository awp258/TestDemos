package com.jw.library.model

import android.os.Parcel
import android.os.Parcelable

/**
 * 图片类
 * @property width Int
 * @property height Int
 * @property addTime Long
 * @property orientation Int    图片方向
 */
class ImageItem : BaseItem {
    var width: Int = 0
    var height: Int = 0
    var addTime: Long = 0
    var orientation: Int = 0

    constructor()

    constructor(parcel: Parcel) : super(parcel) {
        width = parcel.readInt()
        height = parcel.readInt()
        addTime = parcel.readLong()
        orientation = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
        parcel.writeInt(width)
        parcel.writeInt(height)
        parcel.writeLong(addTime)
        parcel.writeInt(orientation)
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
