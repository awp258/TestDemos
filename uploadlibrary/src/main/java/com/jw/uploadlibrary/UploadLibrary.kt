package com.jw.uploadlibrary

import android.content.Context
import com.facebook.stetho.Stetho
import com.jw.uploadlibrary.http.ScHttpClient
import com.jw.uploadlibrary.http.ScHttpConfig
import com.jw.uploadlibrary.upload.UploadManager

/**
 * 创建时间：2019/5/2318:07
 * 更新时间 2019/5/2318:07
 * 版本：
 * 作者：Mr.jin
 * 描述：
 */
object UploadLibrary {
    var BASE_HTTP = ""
    var region = ""
    var appid = ""
    var orgId: Long = 1
    const val phone: Long = 13407194558
    const val pwd: String = "6234ef5192de321f27b0d7b18ba02f8166af27df"
    const val type: Int = 2
    var ticket: Long = 1564198433438
    const val TYPE_UPLOAD_VIDEO = 0   //视频
    const val TYPE_UPLOAD_IMG = 1  //图片
    const val TYPE_UPLOAD_VOICE = 2   //语音
    val isCompress = false
    var CACHE_VIDEO_COMPRESS: String? = null   //压缩视频缓存路径

    fun init(context: Context) {
        //初始化http请求引擎
        ScHttpClient.init(ScHttpConfig.create().setBaseUrl(BASE_HTTP))
        //HttpUtils.init(ScHttpClient.getOkHttpClient())
        //stetho调试集成
        Stetho.initializeWithDefaults(context)
        UploadManager.instance.init(context)
    }
}