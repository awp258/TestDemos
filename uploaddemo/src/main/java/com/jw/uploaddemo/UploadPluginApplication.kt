package com.jw.uploaddemo

import android.app.Application
import android.content.Context
import android.util.Log
import com.jw.cameralibrary.CameraLibrary
import com.jw.croplibrary.CropLibrary
import com.jw.galarylibrary.GalaryLibrary
import com.jw.uilibrary.base.application.BaseApplication
import com.jw.voicelibrary.VoiceLibrary
import nl.bravobit.ffmpeg.FFmpeg

class UploadPluginApplication : BaseApplication() {

    override fun onCreate() {
        super.onCreate()
        application = this
        //初始化应用上下文
        UploadPlugin.getInstance().init(this)
        GalaryLibrary.init()
        CameraLibrary.init(externalCacheDir.absolutePath)
        CropLibrary.init(this, externalCacheDir.absolutePath)
        VoiceLibrary.init(externalCacheDir.absolutePath)
        //UploadLibrary.init(this)
        initFFmpegBinary(this)
    }

    private fun initFFmpegBinary(context: Context) {
        if (!FFmpeg.getInstance(context).isSupported) {
            Log.e("ZApplication", "Android cup arch not supported!")
        }
    }

    companion object {
        var application: Application? = null
            private set
    }
}
