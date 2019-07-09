package com.jw.uploaddemo.uploadPlugin

import android.app.Application
import com.cjt2325.cameralibrary.JCameraView
import com.facebook.stetho.Stetho
import com.jw.uilibrary.base.application.BaseApplication
import com.jw.uploaddemo.UploadConfig
import com.jw.uploaddemo.UploadConfig.BASE_HTTP
import com.jw.uploaddemo.UploadPlugin
import com.jw.uploaddemo.http.ScHttpClient
import com.jw.uploaddemo.http.ScHttpConfig
import com.jw.uploaddemo.tencent.TencentUpload

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
        TencentUpload.instance.init(this)
        UploadConfig.CACHE_VOICE_PATH = cacheDir.absolutePath+"/VoiceRecorder"
        UploadConfig.CACHE_IMG_PATH = cacheDir.absolutePath+"/ShotPictureRecorder"
        UploadConfig.CACHE_VIDEO_PATH = cacheDir.absolutePath+"/ShotVideoRecorder"
        UploadConfig.CACHE_VIDEO_PATH_COVER = cacheDir.absolutePath+"/ShotVideoRecorder/cover"
        JCameraView.MAX_RECOLD_DURATION = UploadConfig.VIDEO_RECORD_LENGTH
    }

    companion object {
        var application: Application? = null
            private set

    }
}
