package com.jw.uploaddemo

import android.app.Application
import android.content.Context
import android.media.MediaScannerConnection
import android.os.Environment
import android.util.Log
import com.jw.cameralibrary.CameraLibrary
import com.jw.croplibrary.CropLibrary
import com.jw.galarylibrary.GalaryLibrary
import com.jw.library.ContextUtil
import com.jw.library.utils.RomUtil
import com.jw.uilibrary.base.application.BaseApplication
import com.jw.voicelibrary.VoiceLibrary
import nl.bravobit.ffmpeg.FFmpeg

class UploadPluginApplication : BaseApplication() {

    override fun onCreate() {
        super.onCreate()
        application = this
        ContextUtil.init(this)
        GalaryLibrary.init()
        CameraLibrary.init(externalCacheDir.absolutePath)
        CropLibrary.init(this, externalCacheDir.absolutePath)
        VoiceLibrary.init(externalCacheDir.absolutePath)
        //UploadLibrary.init(this)
        initFFmpegBinary(this)
        if (RomUtil.isEmui()) {
            // 刷新相册
            MediaScannerConnection.scanFile(
                this,
                arrayOf(Environment.getExternalStorageDirectory().toString()),
                null
            ) { path, uri -> }
        }
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
