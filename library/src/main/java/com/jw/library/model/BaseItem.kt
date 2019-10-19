package com.jw.library.model

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

open class BaseItem() : Serializable, Parcelable {
    var name: String? = null
    lateinit var path: String
    var mimeType: String? = null
    var size: String? = null

    override fun equals(o: Any?): Boolean {
        if (o is VideoItem) {
            val item = o as VideoItem?
            return path!!.equals(item!!.path!!, ignoreCase = true)
        } else {
            return super.equals(o)
        }
    }

    constructor(parcel: Parcel) : this() {
        name = parcel.readString()
        path = parcel.readString()
        mimeType = parcel.readString()
        size = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(path)
        parcel.writeString(mimeType)
        parcel.writeString(size)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BaseItem> {
        override fun createFromParcel(parcel: Parcel): BaseItem {
            return BaseItem(parcel)
        }

        override fun newArray(size: Int): Array<BaseItem?> {
            return arrayOfNulls(size)
        }
    }
}