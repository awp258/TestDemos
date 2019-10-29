package com.jw.library

import android.content.Context

/**
 * 创建时间：2019/10/2118:18
 * 更新时间 2019/10/2118:18
 * 版本：
 * 作者：Mr.jin
 * 描述：
 */
object ContextUtil {
    var context: Context? = null

    fun init(context: Context) {
        this.context = context
    }
}