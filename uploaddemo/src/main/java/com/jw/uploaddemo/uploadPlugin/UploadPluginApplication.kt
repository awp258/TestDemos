package com.jw.uploaddemo.uploadPlugin

import android.app.Application
import android.content.Context
import android.util.Log
import com.facebook.stetho.Stetho
import com.jw.galary.video.VideoDataSource
import com.jw.galary.video.VideoGridActivity
import com.jw.shotRecord.JCameraView
import com.jw.uploaddemo.UploadConfig
import com.jw.uploaddemo.UploadConfig.BASE_HTTP
import com.jw.uploaddemo.UploadPlugin
import com.jw.uploaddemo.base.application.BaseApplication
import com.jw.uploaddemo.http.ScHttpClient
import com.jw.uploaddemo.http.ScHttpConfig
import com.jw.uploaddemo.upload.UploadManager
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
        UploadConfig.CACHE_VOICE_PATH = cacheDir.absolutePath+"/VoiceRecorder"
        UploadConfig.CACHE_IMG_PATH = cacheDir.absolutePath+"/ShotPictureRecorder"
        UploadConfig.CACHE_VIDEO_PATH = cacheDir.absolutePath+"/ShotVideoRecorder"
        UploadConfig.CACHE_VIDEO_PATH_COVER = cacheDir.absolutePath+"/ShotVideoRecorder/cover"
        UploadConfig.CACHE_VIDEO_CROP = externalCacheDir.absolutePath+"/VideoPicker"
        JCameraView.MAX_RECOLD_DURATION = UploadConfig.VIDEO_RECORD_LENGTH.toInt()
        VideoDataSource.MAX_LENGTH = UploadConfig.VIDEO_RECORD_LENGTH
        VideoGridActivity.CACHE_VIDEO_CROP = UploadConfig.CACHE_VIDEO_CROP
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
