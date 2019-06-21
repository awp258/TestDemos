package com.jw.uploaddemo.uploadPlugin

import android.app.Application
import com.facebook.stetho.Stetho
import com.jw.uilibrary.base.application.BaseApplication
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
    }

    companion object {
        var application: Application? = null
            private set

    }
}
