package com.jw.croplibrary.video

/**
 * Author：J.Chou
 * Date：  2016.08.01 2:23 PM
 * Email： who_know_me@163.com
 * Describe:
 */
interface VideoTrimListener {
    fun onStartTrim()
    fun onFinishTrim(url: String)
    fun onCancel()
}
