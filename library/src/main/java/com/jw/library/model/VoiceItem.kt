package com.jw.library.model

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

/**
 * 创建时间：2019/10/1817:58
 * 更新时间 2019/10/1817:58
 * 版本：
 * 作者：Mr.jin
 * 描述：
 */
class VoiceItem : BaseItem, Serializable, Parcelable {
    protected constructor(parcel: Parcel) {
        name = parcel.readString()
        path = parcel.readString()
        size = parcel.readLong()
        mimeType = parcel.readString()
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
    }

    companion object CREATOR : Parcelable.Creator<VoiceItem> {
        override fun createFromParcel(parcel: Parcel): VoiceItem {
            return VoiceItem(parcel)
        }

        override fun newArray(size: Int): Array<VoiceItem?> {
            return arrayOfNulls(size)
        }
    }
}