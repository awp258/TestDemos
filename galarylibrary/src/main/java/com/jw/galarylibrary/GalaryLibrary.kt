package com.jw.galarylibrary

/**
 * 创建时间：2019/5/2318:07
 * 更新时间 2019/5/2318:07
 * 版本：
 * 作者：Mr.jin
 * 描述：
 */
object GalaryLibrary {
    var VIDEO_RECORD_LENGTH: Long = 60 * 1000   //视频最大录制时长默认1min

    fun init() {
        VIDEO_RECORD_LENGTH = 60 * 1000
    }
}