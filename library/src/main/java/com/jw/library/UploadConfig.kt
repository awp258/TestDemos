package com.jw.library

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
    var VOICE_RECORD_LENGTH = 60 * 1000   //语音最大录制时长默认1min
    var VIDEO_RECORD_LENGTH: Long = 60 * 1000   //视频最大录制时长默认1min
    var CACHE_VOICE_PATH: String? = null   //语音缓存路径
    var CACHE_IMG_PATH: String? = null   //拍照缓存路径
    var CACHE_IMG_CROP: String? = null   //裁剪图片缓存路径
    var CACHE_VIDEO_PATH: String? = null   //视频录制缓存路径
    var CACHE_VIDEO_PATH_COVER: String? = null   //视频录制封面缓存路径
    var CACHE_VIDEO_CROP: String? = null   //裁剪视频缓存路径
    var CACHE_VIDEO_COMPRESS: String? = null   //压缩视频缓存路径
    var SHOT_TYPE = 4   //相机模式 4:拍照、摄像都可 5：仅拍照 6:仅录制
    var SHOT_MODEL = 2   //相机样式 1：短视频 2：长视频
    const val RESULT_UPLOAD_SUCCESS = 5000   //上传成功RESULT_CODE
    val isCompress = false
}