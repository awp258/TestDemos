package com.jw.library.model

import android.os.Parcel
import android.os.Parcelable

/**
 * 视频类
 * @property thumbPath String?  缩略图地址
 * @property duration Long  时长
 */
class VideoItem : BaseItem {
    var thumbPath: String? = null
    var duration: Long = 0

    constructor()

    constructor(parcel: Parcel) : super(parcel) {
        duration = parcel.readLong()
        thumbPath = parcel.readString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
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
