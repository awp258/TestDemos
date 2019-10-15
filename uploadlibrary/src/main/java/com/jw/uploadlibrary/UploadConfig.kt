package com.jw.uploadlibrary

/**
 * 创建时间：2019/5/2318:07
 * 更新时间 2019/5/2318:07
 * 版本：
 * 作者：Mr.jin
 * 描述：
 */
object UploadConfig {
    var BASE_HTTP = "http://api.121wty.com/jserver/"
    var region = "ap-guangzhou"
    var appid = "1252766259"
    var orgId: Long = 1
    const val phone: Long = 13407191215
    const val pwd: String = "6234ef5192de321f27b0d7b18ba02f8166af27df"
    const val type: Int = 2
    var ticket: Long = 1564198433438
    const val TYPE_UPLOAD_VIDEO = 0   //视频
    const val TYPE_UPLOAD_IMG = 1  //图片
    const val TYPE_UPLOAD_VOICE = 2   //语音
    val isCompress = false
    const val RESULT_UPLOAD_SUCCESS = 5000   //上传成功RESULT_CODE
}