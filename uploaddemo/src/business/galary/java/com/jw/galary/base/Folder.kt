package com.jw.galary.base

import java.io.Serializable
import java.util.*

class Folder<Data> : Serializable {
    var name: String? = null
    var path: String? = null
    var cover: Data? = null
    var items: ArrayList<Data>? = null
}