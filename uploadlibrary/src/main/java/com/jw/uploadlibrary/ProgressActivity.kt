package com.jw.uploadlibrary

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.LinearLayout
import com.jw.library.ColorCofig
import com.jw.library.model.BaseItem
import com.jw.library.model.VideoItem
import com.jw.library.ui.BaseBindingActivity
import com.jw.uploadlibrary.UploadProgressView.Companion.STATE_ERROR
import com.jw.uploadlibrary.UploadProgressView.Companion.STATE_PROGRESS
import com.jw.uploadlibrary.databinding.ActivityProgressBinding
import com.jw.uploadlibrary.http.ScHttpClient
import com.jw.uploadlibrary.http.service.GoChatService
import com.jw.uploadlibrary.model.AuthorizationInfo
import com.jw.uploadlibrary.model.KeyReqInfo
import com.jw.uploadlibrary.model.MediaReq
import com.jw.uploadlibrary.model.OrgInfo
import com.jw.uploadlibrary.upload.UploadManager
import com.jw.uploadlibrary.upload.UploadProgressCallBack
import org.json.JSONObject


/**
 * 创建时间：2019/6/1417:35
 * 更新时间 2019/6/1417:35
 * 版本：
 * 作者：Mr.jin
 * 描述：
 */
open class ProgressActivity : BaseBindingActivity<ActivityProgressBinding>(),
    UploadProgressCallBack, UploadProgressView.UploadItemListener {

    var results: ArrayList<Boolean> = ArrayList()
    var result: JSONObject? = null
    var isExcuteUpload = false
    var progressViewList: ArrayList<UploadProgressView> = ArrayList()
    val mediaReq = MediaReq()
    var error: String? = null
    var authorizationInfo: AuthorizationInfo? = null
    var orgInfo: OrgInfo? = null

    override fun getLayoutId() = R.layout.activity_progress

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        val type = intent.getIntExtra("type", UploadLibrary.TYPE_UPLOAD_IMG)
        val items = intent.getSerializableExtra("items") as ArrayList<BaseItem>
        when (type) {
            UploadLibrary.TYPE_UPLOAD_VIDEO -> uploadVideo(items)
            UploadLibrary.TYPE_UPLOAD_IMG -> uploadImgOrVoice(items, true)
            UploadLibrary.TYPE_UPLOAD_VOICE -> uploadImgOrVoice(items, false)
        }
    }

    /**
     * 上传图片或语音
     * @param items ArrayList<out BaseItem>
     * @param isImage Boolean
     */
    private fun uploadImgOrVoice(items: ArrayList<out BaseItem>, isImage: Boolean) {
        if (isImage)
            addProgressView(UploadLibrary.TYPE_UPLOAD_IMG, items)
        else
            addProgressView(UploadLibrary.TYPE_UPLOAD_VOICE, items)
        val keyReqInfo = KeyReqInfo()
        keyReqInfo.orgId = UploadLibrary.orgId
        for (image in items) {
            val fileInfo = KeyReqInfo.FileInfo()
            fileInfo.name = image.name
            if (isImage)
                fileInfo.type = UploadLibrary.TYPE_UPLOAD_IMG
            else
                fileInfo.type = UploadLibrary.TYPE_UPLOAD_VOICE
            keyReqInfo.files.add(fileInfo)
            results.add(false)
        }
        UploadManager.instance.uploadImgOrVoice(keyReqInfo, items)
        UploadManager.instance.setUploadProgressListener(this)
    }

    /**
     * 上传视频
     * @param videoItems ArrayList<VideoItem>
     */
    private fun uploadVideo(videoItems: ArrayList<BaseItem>) {
        val orgInfo = OrgInfo()
        orgInfo.orgId = UploadLibrary.orgId
        addProgressView(UploadLibrary.TYPE_UPLOAD_VIDEO, videoItems)
        UploadManager.instance.setUploadProgressListener(this)
        UploadManager.instance.uploadVideo(orgInfo, videoItems)
        for (image in videoItems)
            results.add(false)
    }

    /**
     * 上传成功回调
     * @param index Int
     * @param mediaId Long
     * @param isVideo Boolean
     * @param videoJson JSONObject?
     */
    override fun onSuccess(
        index: Int,
        mediaIds: ArrayList<Long>,
        isVideo: Boolean,
        videoJson: JSONObject?
    ) {
        //按上传成功顺序
        //mediaReq.mediaIds.saveToGalary(mediaId)
        //按选择顺序
        mediaReq.mediaIds = mediaIds
        runOnUiThread {
            results[index] = true
            setConfirmEnable(true)
            for (result in results) {
                if (!result)
                    setConfirmEnable(false)
            }
            if (mBinding.topBar.btnOk.isEnabled && !isExcuteUpload) {
                //按选择顺序
                //mediaReq.mediaIds.saveToGalary(mediaId)
                isExcuteUpload = true
                setConfirmEnable(false)
                if (isVideo) {
                    result = videoJson
                    Log.v("upload_success", result.toString())
                    setConfirmEnable(true)
                    backProgress()
                } else {
                    ScHttpClient.getService(GoChatService::class.java)
                        .getMedias(UploadLibrary.ticket, mediaReq)
                        .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                        .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                        .subscribe({ jsonObject ->
                            result = jsonObject
                            Log.v("upload_success", result.toString())
                            setConfirmEnable(true)
                            backProgress()
                        }, { })
                }
            }
        }
    }

    private fun setConfirmEnable(isEnable: Boolean) {
        if (isEnable) {
            mBinding.apply {
                topBar.btnOk.isEnabled = true
                topBar.btnOk.setTextColor(Color.parseColor(ColorCofig.toolbarTitleColorNormal))
                topBar.btnBack.isEnabled = true
            }
        } else {
            mBinding.apply {
                topBar.btnOk.isEnabled = false
                topBar.btnBack.isEnabled = false
                topBar.btnOk.setTextColor(Color.parseColor(ColorCofig.toolbarTitleColorDisabled))
            }
        }
    }

    private fun backProgress() {
        val intent = Intent()
        intent.putExtra("medias", result.toString())
        setResult(Activity.RESULT_OK, intent)
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
            progressViewList[index].refresh(STATE_PROGRESS, progress, null)
        }
    }

    /**
     * 上传失败回调
     * @param index Int
     * @param error String
     */
    override fun onFail(
        index: Int,
        item: BaseItem,
        error: String,
        authorizationInfo: AuthorizationInfo?,
        orgInfo: OrgInfo?
    ) {
        runOnUiThread {
            setConfirmEnable(true)
            progressViewList[index].refresh(STATE_ERROR, 0, null)
            this.error = error
            this.authorizationInfo = authorizationInfo
            this.orgInfo = orgInfo
        }
    }

    /**
     * 重新上传按钮点击
     * @param position Int
     * @param item BaseItem
     */
    override fun reUpload(position: Int, item: BaseItem) {
        Log.v("upload_error", error)
        //重新上传
        if (item is VideoItem)
            UploadManager.instance.excUploadVideo(orgInfo!!, position, item)
        else
            UploadManager.instance.excUploadImgOrVoice(position, item, authorizationInfo!!)
    }

    /**
     * 新增上传卡片
     * @param list ArrayList<*>
     * @param type Int
     */
    private fun addProgressView(type: Int, list: ArrayList<out BaseItem>) {
        for (i in 0 until list.size) {
            val uploadProgressView = UploadProgressView(this)
            uploadProgressView.setUploadItemListener(this)
            val width = LinearLayout.LayoutParams.WRAP_CONTENT
            val height = LinearLayout.LayoutParams.WRAP_CONTENT
            val layoutParams = LinearLayout.LayoutParams(width, height)
            layoutParams.topMargin = 20
            uploadProgressView.layoutParams = layoutParams
            uploadProgressView.setItem(i, type, list[i])
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

    companion object {
        const val REQUEST_CODE_UPLOAD = 5000
        fun start(activity: Activity, type: Int, items: ArrayList<out BaseItem>) {
            val intent = Intent(activity, ProgressActivity::class.java)
            intent.putExtra("type", type)
            intent.putParcelableArrayListExtra("items", items)
            ActivityCompat.startActivityForResult(activity, intent, REQUEST_CODE_UPLOAD, null)
        }
    }
}