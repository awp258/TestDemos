package com.jw.uploaddemo.activity

import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import com.jw.galary.img.bean.ImageItem
import com.jw.galary.video.VideoItem
import com.jw.uploaddemo.ColorCofig
import com.jw.uploaddemo.R
import com.jw.uploaddemo.UploadConfig
import com.jw.uploaddemo.UploadConfig.RESULT_UPLOAD_SUCCESS
import com.jw.uploaddemo.UploadConfig.ticket
import com.jw.uploaddemo.databinding.ActivityProgressBinding
import com.jw.uploaddemo.http.ScHttpClient
import com.jw.uploaddemo.http.service.GoChatService
import com.jw.uploaddemo.model.AuthorizationInfo
import com.jw.uploaddemo.model.KeyReqInfo
import com.jw.uploaddemo.model.MediaReq
import com.jw.uploaddemo.model.OrgInfo
import com.jw.uploaddemo.upload.UploadManager
import com.jw.uploaddemo.upload.UploadProgressCallBack
import com.jw.uploaddemo.upload.UploadProgressView
import com.jw.uploaddemo.uploadPlugin.UploadPluginBindingActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject


/**
 * 创建时间：2019/6/1417:35
 * 更新时间 2019/6/1417:35
 * 版本：
 * 作者：Mr.jin
 * 描述：
 */
open class ProgressActivity : UploadPluginBindingActivity<ActivityProgressBinding>(),
    UploadProgressCallBack {
    var results: ArrayList<Boolean> = ArrayList()
    var result: JSONObject? = null
    val mediaReq = MediaReq()
    var isExcuteUpload = false

    override fun getLayoutId() = R.layout.activity_progress

    override fun doConfig(arguments: Intent) {
        setConfirmButtonBg(mBinding.topBar.btnOk)
        mBinding.apply {
            topBar.tvDes.text = "上传进度"
            topBar.btnOk.text = "确定"
            topBar.btnOk.isEnabled = false
            topBar.btnOk.setTextColor(Color.parseColor(ColorCofig.toolbarTitleColorDisabled))
            topBar.btnBack.isEnabled = false
            clickListener = View.OnClickListener {
                when (it.id) {
                    R.id.btn_ok -> {
                        backProgress()
                    }
                    R.id.btn_back -> {
                        finish()
                    }
                }
            }
        }
        val type = arguments.getIntExtra("type", UploadConfig.TYPE_UPLOAD_IMG)
        when (type) {
            UploadConfig.TYPE_UPLOAD_VIDEO -> {
                val videos = arguments.getSerializableExtra("videos") as ArrayList<VideoItem>
                uploadVideo(videos)
            }
            UploadConfig.TYPE_UPLOAD_IMG -> {
                val images = arguments.getSerializableExtra("imageList") as ArrayList<ImageItem>
                uploadImg(images)
            }
            UploadConfig.TYPE_UPLOAD_VOICE -> {
                val voicePath = arguments.getStringExtra("path")
                uploadVoice(voicePath)
            }
        }
    }

    var count = 0


    var progressViewList: ArrayList<UploadProgressView> = ArrayList()

    /**
     * 上传图片
     * @param imageItems ArrayList<ImageItem>
     */
    private fun uploadImg(imageItems: ArrayList<ImageItem>) {
        val keyReqInfo = KeyReqInfo()
        keyReqInfo.orgId = UploadConfig.orgId
        for (image in imageItems) {
            val fileInfo = KeyReqInfo.FileInfo()
            fileInfo.name = image.name
            fileInfo.type = UploadConfig.TYPE_UPLOAD_IMG
            keyReqInfo.files.add(fileInfo)
            results.add(false)
        }
        addProgressView(imageItems, UploadConfig.TYPE_UPLOAD_IMG)
        UploadManager.instance.upload(keyReqInfo, count, imageItems)
        count += keyReqInfo.files.size
        UploadManager.instance.setUploadProgressListener(this)
    }

    /**
     * 上传视频
     * @param videoItems ArrayList<VideoItem>
     */
    private fun uploadVideo(videoItems: ArrayList<VideoItem>) {
        val orgInfo = OrgInfo()
        orgInfo.orgId = UploadConfig.orgId
        addProgressView(videoItems, UploadConfig.TYPE_UPLOAD_VIDEO)
        UploadManager.instance.uploadVideo(orgInfo, count, videoItems)
        for (image in videoItems)
            results.add(false)
        count += videoItems.size
        UploadManager.instance.setUploadProgressListener(this)
    }

    /**
     * 上传语音
     * @param path String
     */
    private fun uploadVoice(path: String) {
        val d = KeyReqInfo()
        d.orgId = UploadConfig.orgId
        val file = KeyReqInfo.FileInfo()
        file.name = path.split("/").last()
        file.type = UploadConfig.TYPE_UPLOAD_VOICE
        d.files.add(file)
        addProgressView(d.files, UploadConfig.TYPE_UPLOAD_VOICE)
        UploadManager.instance.upload(d, count, null)
        results.add(false)
        count += d.files.size
        UploadManager.instance.setUploadProgressListener(this)
    }

    /**
     * 上传成功回调
     * @param index Int
     * @param mediaId Long
     * @param isVideo Boolean
     * @param videoJson JSONObject?
     */
    override fun onSuccess(index: Int, mediaId: Long, isVideo: Boolean, videoJson: JSONObject?) {
        mediaReq.mediaIds.add(mediaId)
        runOnUiThread {
            results[index] = true
            mBinding.apply {
                topBar.btnOk.isEnabled = true
                topBar.btnOk.setTextColor(Color.parseColor(ColorCofig.toolbarTitleColorNormal))
                topBar.btnBack.isEnabled = true
            }
            for (result in results) {
                if (!result) {
                    mBinding.topBar.btnOk.isEnabled = false
                    mBinding.topBar.btnBack.isEnabled = false
                }
            }
            if (mBinding.topBar.btnOk.isEnabled && !isExcuteUpload) {
                isExcuteUpload = true
                if (isVideo) {
                    result = videoJson
                    Log.v("upload_success", result.toString())
                    backProgress()
                } else {
                    ScHttpClient.getService(GoChatService::class.java).getMedias(ticket, mediaReq)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ jsonObject ->
                            result = jsonObject
                            Log.v("upload_success", result.toString())
                            backProgress()
                        }, { })
                }
            }
        }
    }

    /**
     * 上传失败回调
     * @param index Int
     * @param error String
     */
    override fun onFail(
        index: Int,
        error: String,
        authorizationInfo: AuthorizationInfo?,
        path: String?,
        orgInfo: OrgInfo?,
        videoItem: VideoItem?
    ) {
        runOnUiThread {
            mBinding.topBar.btnOk!!.isEnabled = true
            progressViewList[index].setError()
            progressViewList[index].setUploadItemListener(object :
                UploadProgressView.UploadItemListener {
                override fun error() {
                    Toast.makeText(this@ProgressActivity, error, Toast.LENGTH_SHORT).show()
                    Log.v("upload_error", error)
                    //重新上传
                    if (path != null)
                        UploadManager.instance.uploadSingle(
                            authorizationInfo!!,
                            packageCodePath,
                            index
                            , null
                        )
                    else
                        UploadManager.instance.uploadVideoSingle(orgInfo!!, index, videoItem!!)
                }

                override fun success() {
                }

            })
        }
    }

    private fun backProgress(){
        val intent = Intent()
        intent.putExtra("medias", result.toString())
        setResult(RESULT_UPLOAD_SUCCESS, intent)
        finish()
    }

    /**
     * 上传进度回调
     * @param index Int
     * @param progress Int
     * @param authorizationInfo AuthorizationInfo?
     */
    override fun onProgress(index: Int, progress: Int, authorizationInfo: AuthorizationInfo?) {
        runOnUiThread {
            progressViewList[index].setProgress(progress)
        }
    }

    /**
     * 新增上传卡片
     * @param list ArrayList<*>
     * @param type Int
     */
    private fun addProgressView(list: ArrayList<*>, type: Int) {
        for (i in 1..list.size) {
            val uploadProgressView = UploadProgressView(this)
            uploadProgressView.setType(type, list[i - 1])
            uploadProgressView.setProgress(0)
            val width = LinearLayout.LayoutParams.WRAP_CONTENT
            val height = LinearLayout.LayoutParams.WRAP_CONTENT
            val layoutParams = LinearLayout.LayoutParams(width, height)
            layoutParams.topMargin = 20
            uploadProgressView.layoutParams = layoutParams
            mBinding.ll.addView(uploadProgressView)
            progressViewList.add(uploadProgressView)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mBinding.topBar.btnOk.isEnabled) {
                finish()
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}