package com.jw.galarylibrary.base.bean

import java.io.Serializable
import java.util.*

/**
 * 创建时间：
 * 更新时间
 * 版本：
 * 作者：Mr.jin
 * 描述：文件夹实体类
 */
class Folder<ITEM> : Serializable {
    var name: String? = null
    var path: String? = null
    var cover: ITEM? = null
    var items: ArrayList<ITEM>? = null

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