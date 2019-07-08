package com.jw.uploaddemo.tencent

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.jw.uploaddemo.UploadConfig.TYPE_UPLOAD_IMG
import com.jw.uploaddemo.UploadConfig.appid
import com.jw.uploaddemo.UploadConfig.region
import com.jw.uploaddemo.UploadConfig.ticket
import com.jw.uploaddemo.UploadProgressCallBack
import com.jw.uploaddemo.http.ScHttpClient
import com.jw.uploaddemo.http.service.GoChatService
import com.jw.uploaddemo.model.AuthorizationInfo
import com.jw.uploaddemo.model.D
import com.jw.uploaddemo.model.E
import com.jw.uploaddemo.model.Video
import com.jw.uploaddemo.videoupload.TXUGCPublish
import com.jw.uploaddemo.videoupload.TXUGCPublishTypeDef
import com.tencent.cos.xml.CosXmlServiceConfig
import com.tencent.cos.xml.CosXmlSimpleService
import com.tencent.cos.xml.exception.CosXmlClientException
import com.tencent.cos.xml.exception.CosXmlServiceException
import com.tencent.cos.xml.listener.CosXmlResultListener
import com.tencent.cos.xml.model.CosXmlRequest
import com.tencent.cos.xml.model.CosXmlResult
import com.tencent.cos.xml.transfer.TransferConfig
import com.tencent.cos.xml.transfer.TransferManager
import com.tencent.qcloud.core.auth.BasicLifecycleCredentialProvider
import com.tencent.qcloud.core.auth.QCloudLifecycleCredentials
import com.tencent.qcloud.core.auth.SessionQCloudCredentials
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


/**
 * 创建时间：2019/5/1816:55
 * 更新时间 2019/5/1816:55
 * 版本：
 * 作者：Mr.jin
 * 描述：
 */
class TencentUpload {
    private var context: Context? = null
    private var serviceConfig: CosXmlServiceConfig? = null
    private var callBack: UploadProgressCallBack? = null

    fun init(context: Context) {
        this.context = context
        serviceConfig = CosXmlServiceConfig.Builder()
            .setAppidAndRegion(appid, region)
            .builder()
    }

    @SuppressLint("CheckResult")
    fun upload(d: D, count: Int) {
        //获取存储桶
        ScHttpClient.getService(GoChatService::class.java).getAuthorization(ticket, d)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ jsonObject ->

                val authorizationInfo = Gson().fromJson(jsonObject.toString(), AuthorizationInfo::class.java)
                for (i in 0..d.files.size) {
                    val fileName = d.files[i].name
                    var path: String
                    path = if (d.files[i].type == TYPE_UPLOAD_IMG)
                        context!!.cacheDir.absolutePath + "/RXImagePicker/cropTemp/" + fileName
                    else
                        context!!.cacheDir.absolutePath + "/VoiceRecorder/" + fileName
                    //执行单个文件上传
                    val index = count + i
                    uploadSingle(authorizationInfo, path, index, fileName!!)
                }
            }, { })
    }

    @SuppressLint("CheckResult")
    fun uploadVideo(e: E, count: Int,videos:ArrayList<Video>) {
        for(video in videos){
            ScHttpClient.getService(GoChatService::class.java).getVideoSign(ticket, e)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { jsonObject ->
                    val fileName = jsonObject.getString("fileName")
                    val mediaId = jsonObject.getString("mediaId")
                    val sign = jsonObject.getString("sign")

                    val mVideoPublish = TXUGCPublish(context, appid)
                    val param = TXUGCPublishTypeDef.TXPublishParam()
                    param.signature = sign
                    param.videoPath = video.path
                    val index = count+videos.indexOf(video)
                    mVideoPublish.setListener(object : TXUGCPublishTypeDef.ITXVideoPublishListener {
                        override fun onPublishProgress(uploadBytes: Long, totalBytes: Long) {
                            val progress = (100 * uploadBytes / totalBytes).toInt()
                            callBack!!.onProgress(index, progress, null)
                        }

                        override fun onPublishComplete(result: TXUGCPublishTypeDef.TXPublishResult) {
                            callBack!!.onSuccess(index,result.toString())
                            //mResultMsg.setText(result.retCode.toString() + " Msg:" + if (result.retCode == 0) result.videoURL else result.descMsg)
                        }
                    })
                    val publishCode = mVideoPublish.publishVideo(param)
                }
        }
    }

    /**
     *
     * @param tmpSecretId String 开发者拥有的项目身份识别 ID，用以身份认证
     * @param tmpSecretKey String 开发者拥有的项目身份密钥
     * @param sessionToken String
     * @param bucket String COS 中用于存储数据的容器
     * @param path String
     */
    private fun uploadSingle(authorizationInfo: AuthorizationInfo, path: String, index: Int, fileName: String) {
        val transferConfig = TransferConfig.Builder().build()
        val credentialProvider = MyCredentialProvider(
            authorizationInfo.tmpSecretId,
            authorizationInfo.tmpSecretKey,
            authorizationInfo.sessionToken
        )
        val cosXmlSimpleService = CosXmlSimpleService(context, serviceConfig, credentialProvider)
        val transferManager = TransferManager(cosXmlSimpleService, transferConfig)
        val cosxmlUploadTask = transferManager.upload(authorizationInfo.bucket, fileName, path, null)
        cosxmlUploadTask.setCosXmlResultListener(object : CosXmlResultListener {
            override fun onSuccess(request: CosXmlRequest?, result: CosXmlResult?) {
                callBack!!.onSuccess(index, result.toString())
            }

            override fun onFail(
                request: CosXmlRequest?,
                exception: CosXmlClientException?,
                serviceException: CosXmlServiceException?
            ) {
                callBack!!.onFail(
                    index,
                    request.toString() + "--" + exception.toString() + "--" + serviceException.toString()
                )
            }
        })
        //设置上传进度回调
        cosxmlUploadTask.setCosXmlProgressListener { complete, target ->
            val progress = 1.0f * complete / target * 100
            callBack!!.onProgress(index, progress.toInt(), authorizationInfo)
        }
        //设置任务状态回调, 可以查看任务过程
        cosxmlUploadTask.setTransferStateListener { state -> Log.d("TEST22", "Task state:" + state.name) }
    }

    fun setUploadProgressListener(callBack: UploadProgressCallBack) {
        this.callBack = callBack
    }

    internal inner class MyCredentialProvider(
        private val id: String,
        private val key: String,
        private val token: String
    ) :
        BasicLifecycleCredentialProvider() {

        override fun fetchNewCredentials(): QCloudLifecycleCredentials {

            //解析响应，获取密钥信息

            // 使用本地永久秘钥计算得到临时秘钥
            val current = System.currentTimeMillis()/ 1000
            val expired = current + 60*60

            // 最后返回临时密钥信息对象
            return SessionQCloudCredentials(this.id, this.key, this.token, current, expired)
        }
    }

    companion object {
        val instance = TencentUpload()
    }

}