package com.jw.uploaddemo

import android.app.Application
import android.content.Context
import android.util.Log
import com.facebook.stetho.Stetho
import com.jw.galary.video.VideoDataSource
import com.jw.library.UploadConfig.BASE_HTTP
import com.jw.shotRecord.JCameraView
import com.jw.uilibrary.base.application.BaseApplication
import com.jw.uploadlibrary.http.ScHttpClient
import com.jw.uploadlibrary.http.ScHttpConfig
import com.jw.uploadlibrary.upload.UploadManager
import iknow.android.utils.BaseUtils
import nl.bravobit.ffmpeg.FFmpeg

class UploadPluginApplication : BaseApplication() {

    override fun onCreate() {
        super.onCreate()
        application = this
        //初始化应用上下文
        UploadPlugin.getInstance().init(this)
        //初始化http请求引擎
        ScHttpClient.init(ScHttpConfig.create().setBaseUrl(BASE_HTTP))
        //HttpUtils.init(ScHttpClient.getOkHttpClient())
        //stetho调试集成
        Stetho.initializeWithDefaults(this)
        UploadManager.instance.init(this)
        com.jw.library.UploadConfig.CACHE_VOICE_PATH = cacheDir.absolutePath + "/VoiceRecorder"
        com.jw.library.UploadConfig.CACHE_IMG_PATH = cacheDir.absolutePath + "/shot/picture"
        com.jw.library.UploadConfig.CACHE_VIDEO_PATH = cacheDir.absolutePath + "/shot/video"
        com.jw.library.UploadConfig.CACHE_VIDEO_PATH_COVER =
            cacheDir.absolutePath + "/shot/video/cover"
        com.jw.library.UploadConfig.CACHE_IMG_CROP = cacheDir.absolutePath + "/crop/picture"
        com.jw.library.UploadConfig.CACHE_VIDEO_CROP = cacheDir.absolutePath + "/crop/video"
        com.jw.library.UploadConfig.CACHE_VIDEO_COMPRESS = cacheDir.absolutePath + "/compress/video"
        JCameraView.MAX_RECOLD_DURATION = com.jw.library.UploadConfig.VIDEO_RECORD_LENGTH.toInt()
        VideoDataSource.MAX_LENGTH = com.jw.library.UploadConfig.VIDEO_RECORD_LENGTH
        BaseUtils.init(this)
        initFFmpegBinary(this)
    }

    private fun initFFmpegBinary(context: Context) {
        if (!FFmpeg.getInstance(context).isSupported()) {
            Log.e("ZApplication", "Android cup arch not supported!")
        }
    }

    companion object {
        var application: Application? = null
            private set

    }
}
