package com.jw.voicelibrary

/**
 * 创建时间：2019/5/2318:07
 * 更新时间 2019/5/2318:07
 * 版本：
 * 作者：Mr.jin
 * 描述：
 */
object VoiceLibrary {
    var VOICE_RECORD_LENGTH: Int? = null
    var CACHE_VOICE_PATH: String? = null

    fun init(baseCachePath: String) {
        CACHE_VOICE_PATH = "$baseCachePath/VoiceRecorder"
        VOICE_RECORD_LENGTH = 60 * 1000
    }
}