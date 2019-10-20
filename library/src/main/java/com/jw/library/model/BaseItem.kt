package com.jw.library.model

import android.os.Parcel
import android.os.Parcelable
import com.jw.library.utils.FileUtils
import java.io.File

/**
 * 媒体对象基类
 * @property name String?   文件名
 * @property path String?   文件路径
 * @property mimeType String?   文件mimeType
 * @property size String?   文件大小
 */
open class BaseItem() : Parcelable {
    var name: String? = null
    var path: String? = null
    var mimeType: String? = null
    var size: String? = null

    /**
     * 根据路径自动补充文件名、文件mineType、文件大小
     * @param path String
     * @constructor
     */
    constructor(path: String) : this() {
        this.path = path
        this.name = path.split("/").last()
        this.size = FileUtils.getAutoFileOrFilesSize(path)
        this.mimeType = FileUtils.getMIMEType(File(path))
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

    override fun equals(o: Any?): Boolean {
        if (o is BaseItem) {
            val item = o as BaseItem?
            return path.equals(item!!.path, ignoreCase = true)
        } else {
            return super.equals(o)
        }
    }

    override fun hashCode(): Int {
        var result = name?.hashCode() ?: 0
        result = 31 * result + (path?.hashCode() ?: 0)
        result = 31 * result + (mimeType?.hashCode() ?: 0)
        result = 31 * result + (size?.hashCode() ?: 0)
        return result
    }
}