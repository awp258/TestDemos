package com.jw.galary.base.bean

import java.io.Serializable
import java.util.*

class Folder<Data> : Serializable {
    var name: String? = null
    var path: String? = null
    var cover: Data? = null
    var items: ArrayList<Data>? = null

    override fun equals(o: Any?): Boolean {
        try {
            val other = o as Folder<*>?
            return this.path.equals(
                other!!.path,
                ignoreCase = true
            ) && this.name.equals(other.name, ignoreCase = true)
        } catch (var3: ClassCastException) {
            var3.printStackTrace()
            return super.equals(o)
        }

    }
}