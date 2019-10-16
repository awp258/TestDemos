package com.jw.uploadlibrary.upload

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.jw.library.model.ImageItem
import com.jw.library.model.VideoItem
import com.jw.library.utils.BitmapUtil
import com.jw.uploadlibrary.UploadLibrary
import com.jw.uploadlibrary.UploadLibrary.TYPE_UPLOAD_IMG
import com.jw.uploadlibrary.UploadLibrary.appid
import com.jw.uploadlibrary.UploadLibrary.region
import com.jw.uploadlibrary.UploadLibrary.ticket
import com.jw.uploadlibrary.http.ScHttpClient
import com.jw.uploadlibrary.http.service.GoChatService
import com.jw.uploadlibrary.model.AuthorizationInfo
import com.jw.uploadlibrary.model.KeyReqInfo
import com.jw.uploadlibrary.model.OrgInfo
import com.jw.uploadlibrary.videoupload.TXUGCPublish
import com.jw.uploadlibrary.videoupload.TXUGCPublishTypeDef
import com.tencent.cos.xml.CosXmlService
import com.tencent.cos.xml.CosXmlServiceConfig
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
import nl.bravobit.ffmpeg.FFcommandExecuteResponseHandler
import org.json.JSONArray
import org.json.JSONObject


/**
 * 创建时间：2019/5/1816:55
 * 更新时间 2019/5/1816:55
 * 版本：
 * 作者：Mr.jin
 * 描述：上传管理类
 */
class UploadManager {
    private var context: Context? = null
    private var serviceConfig: CosXmlServiceConfig? = null
    private var callBack: UploadProgressCallBack? = null
    private var ffmpegListener: FFcommandExecuteResponseHandler? = null
    private val isCompress = UploadLibrary.isCompress

    fun init(context: Context) {
        this.context = context
        serviceConfig = CosXmlServiceConfig.Builder()
            .setAppidAndRegion(appid, region)
            .builder()
    }

    /**
     * 执行上传图片和语音
     * @param keyReqInfo KeyReqInfo
     * @param count Int
     */
    @SuppressLint("CheckResult")
    fun upload(keyReqInfo: KeyReqInfo, imageItems: ArrayList<ImageItem>?) {
        //获取存储桶
        ScHttpClient.getService(GoChatService::class.java).getAuthorization(ticket, keyReqInfo)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ jsonObject ->

                val authorizationInfo =
                    Gson().fromJson(jsonObject.toString(), AuthorizationInfo::class.java)
                for (i in 0..keyReqInfo.files.size) {
                    val fileName = keyReqInfo.files[i].name
                    var path: String
                    path = (if (keyReqInfo.files[i].type == TYPE_UPLOAD_IMG)
                        imageItems!![i].path
                    else
                        context!!.cacheDir.absolutePath + "/VoiceRecorder/" + fileName)!!
                    //执行单个文件上传
                    uploadSingle(authorizationInfo, path, i, imageItems?.get(i))
                }
            }, { })
    }

    /**
     * z上传视频
     * @param orgInfo OrgInfo
     * @param count Int
     * @param videos ArrayList<VideoItem>
     */
    fun uploadVideo(orgInfo: OrgInfo, videos: ArrayList<VideoItem>) {
        for (video in videos) {
            val index = videos.indexOf(video)
            if (isCompress)
                compress(orgInfo, index, video)
            else
                uploadVideoSingle(orgInfo, index, video)
        }
    }

    fun compress(orgInfo: OrgInfo, index: Int, video: VideoItem) {
        val outputPath = UploadLibrary.CACHE_VIDEO_COMPRESS + "/" + video.name
        VideoCompressor.compress(
            context,
            video.path,
            outputPath,
            object : FFcommandExecuteResponseHandler {
                override fun onFinish() {
                    ffmpegListener!!.onFinish()
                }

                override fun onSuccess(message: String?) {
                    ffmpegListener!!.onSuccess(message)
                    video.path = outputPath
                    uploadVideoSingle(orgInfo, index, video)
                }

                override fun onFailure(message: String?) {
                    ffmpegListener!!.onFailure(message)
                }

                override fun onProgress(message: String?) {
                    ffmpegListener!!.onProgress(message)
                }

                override fun onStart() {
                    ffmpegListener!!.onStart()
                }

            })
    }

    @SuppressLint("CheckResult")
    fun uploadVideoSingle(orgInfo: OrgInfo, index: Int, video: VideoItem) {
        ScHttpClient.getService(GoChatService::class.java).getVideoSign(ticket, orgInfo)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { jsonObject ->
                val sign = jsonObject.getString("sign")
                val fileName = jsonObject.getString("fileName")
                val mediaId = jsonObject.getLong("mediaId")
                val mVideoPublish = TXUGCPublish(context, appid)
                val param = TXUGCPublishTypeDef.TXPublishParam()
                param.signature = sign
                param.videoPath = video.path

                mVideoPublish.setListener(object : TXUGCPublishTypeDef.ITXVideoPublishListener {
                    override fun onPublishProgress(uploadBytes: Long, totalBytes: Long) {
                        val progress = (100 * uploadBytes / totalBytes).toInt()
                        callBack!!.onProgress(index, progress, null)
                    }

                    override fun onPublishComplete(result: TXUGCPublishTypeDef.TXPublishResult) {
                        if (result.videoURL != null) {
                            val videoUrl = result.videoURL
                            val videoId = result.videoId
                            val videoJson = JSONObject()
                            val medias = JSONArray()
                            val media = JSONObject()
                            media.put("mediaType", 0)
                            media.put("videoUrl", videoUrl)
                            media.put("videoFileName", fileName)
                            media.put("mediaId", mediaId)
                            media.put("fileId", videoId)
                            medias.put(media)
                            videoJson.put("medias", medias)

                            callBack!!.onSuccess(index, arrayListOf(mediaId), true, videoJson)
                        } else {
                            callBack!!.onFail(index, result.descMsg, null, null, orgInfo, video)
                        }
                    }
                })
                mVideoPublish.publishVideo(param)
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
    fun uploadSingle(
        authorizationInfo: AuthorizationInfo,
        path: String,
        index: Int,
        imageItem: ImageItem?
    ) {
        val transferConfig = TransferConfig.Builder().build()
        val credentialProvider = MyCredentialProvider(
            authorizationInfo.tmpSecretId,
            authorizationInfo.tmpSecretKey,
            authorizationInfo.sessionToken
        )
        val cosXmlService = CosXmlService(context, serviceConfig, credentialProvider)
        val transferManager = TransferManager(cosXmlService, transferConfig)
        if (imageItem != null && imageItem.orientation != 0) {
            val bitmap = BitmapUtil.rotateBitmapByDegree(imageItem.path, imageItem.orientation)
            transferManager.upload(
                authorizationInfo.bucket,
                authorizationInfo.keys[index],
                BitmapUtil.Bitmap2Bytes(bitmap)
            )
        }
        val cosxmlUploadTask =
            transferManager.upload(
                authorizationInfo.bucket,
                authorizationInfo.keys[index],
                path,
                null
            )
        cosxmlUploadTask.setCosXmlResultListener(object : CosXmlResultListener {
            override fun onSuccess(request: CosXmlRequest?, result: CosXmlResult?) {
                callBack!!.onSuccess(index, authorizationInfo.mediaIds, false, null)
            }

            override fun onFail(
                request: CosXmlRequest?,
                exception: CosXmlClientException?,
                serviceException: CosXmlServiceException?
            ) {
                callBack!!.onFail(
                    index,
                    request.toString() + "--" + exception.toString() + "--" + serviceException.toString(),
                    authorizationInfo,
                    path, null, null
                )
            }
        })
        //设置上传进度回调
        cosxmlUploadTask.setCosXmlProgressListener { complete, target ->
            val progress = 1.0f * complete / target * 100
            callBack!!.onProgress(index, progress.toInt(), authorizationInfo)
        }
        //设置任务状态回调, 可以查看任务过程
        cosxmlUploadTask.setTransferStateListener { state ->
            Log.d(
                "TEST22",
                "Task state:" + state.name
            )
        }
    }

    fun setUploadProgressListener(callBack: UploadProgressCallBack) {
        this.callBack = callBack
    }

    fun setVideoCompressListener(callBack: FFcommandExecuteResponseHandler) {
        this.ffmpegListener = callBack
    }

    class MyCredentialProvider(
        private val id: String,
        private val key: String,
        private val token: String
    ) :
        BasicLifecycleCredentialProvider() {

        override fun fetchNewCredentials(): QCloudLifecycleCredentials {

            //解析响应，获取密钥信息

            // 使用本地永久秘钥计算得到临时秘钥
            val current = System.currentTimeMillis() / 1000
            val expired = current + 60 * 60

            // 最后返回临时密钥信息对象
            return SessionQCloudCredentials(this.id, this.key, this.token, current, expired)
        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        val instance = UploadManager()
    }

}