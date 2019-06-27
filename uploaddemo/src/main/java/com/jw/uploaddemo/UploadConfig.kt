package com.jw.uploaddemo

/**
 * 创建时间：2019/5/2318:07
 * 更新时间 2019/5/2318:07
 * 版本：
 * 作者：Mr.jin
 * 描述：
 */
object UploadConfig {
    const val BASE_HTTP = "http://api.121wty.com/test/jserver/"
    const val region = "ap-guangzhou"
    const val appid = "1252766259"
    const val orgId:Long = 2553715
    const val ticket:Long = 1561378317942
    const val TYPE_UPLOAD_VIDEO = 0   //视频
    const val TYPE_UPLOAD_IMG = 1  //图片
    const val TYPE_UPLOAD_VOICE = 2   //语音
    const val VOICE_RECORD_LENGTH = 60*1000   //语音
}