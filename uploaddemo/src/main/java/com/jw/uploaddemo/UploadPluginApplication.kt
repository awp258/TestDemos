package com.jw.uploaddemo

import android.app.Application
import android.content.Context
import android.util.Log
import com.facebook.stetho.Stetho
import com.jw.cameralibrary.CameraConfig
import com.jw.croplibrary.CropConfig
import com.jw.galarylibrary.GalaryConfig
import com.jw.uilibrary.base.application.BaseApplication
import com.jw.uploadlibrary.UploadConfig.BASE_HTTP
import com.jw.uploadlibrary.http.ScHttpClient
import com.jw.uploadlibrary.http.ScHttpConfig
import com.jw.uploadlibrary.upload.UploadManager
import com.jw.voicelibrary.VoiceConfig
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

        BaseUtils.init(this)
        initFFmpegBinary(this)
        initCache()
        initConfig()
    }

    private fun initCache() {
        CropConfig.CACHE_IMG_CROP = cacheDir.absolutePath + "/crop/picture"
        CropConfig.CACHE_VIDEO_CROP = cacheDir.absolutePath + "/crop/video"
        CropConfig.CACHE_VIDEO_CROP_COVER = cacheDir.absolutePath + "/crop/video/cover"

        CameraConfig.CACHE_IMG_PATH = cacheDir.absolutePath + "/shot/picture"
        CameraConfig.CACHE_VIDEO_PATH = cacheDir.absolutePath + "/shot/video"
        CameraConfig.CACHE_VIDEO_PATH_COVER = cacheDir.absolutePath + "/shot/video/cover"

        VoiceConfig.CACHE_VOICE_PATH = cacheDir.absolutePath + "/VoiceRecorder"
    }

    private fun initConfig() {
        CameraConfig.VIDEO_RECORD_LENGTH = 60 * 1000
        GalaryConfig.VIDEO_RECORD_LENGTH = 60 * 1000
        VoiceConfig.VOICE_RECORD_LENGTH = 60 * 1000
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
