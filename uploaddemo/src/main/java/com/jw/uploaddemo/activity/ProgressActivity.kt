package com.jw.uploaddemo.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jw.uploaddemo.R
import com.jw.uploaddemo.UploadConfig
import com.jw.uploaddemo.UploadProgressCallBack
import com.jw.uploaddemo.UploadProgressView
import com.jw.uploaddemo.databinding.ActivityProgressBinding
import com.jw.uploaddemo.http.ScHttpClient
import com.jw.uploaddemo.http.service.GoChatService
import com.jw.uploaddemo.model.*
import com.jw.uploaddemo.tencent.TencentUpload
import com.jw.uploaddemo.uploadPlugin.UploadPluginBindingActivity
import com.jw.videopicker.VideoItem
import com.rxxb.imagepicker.bean.ImageItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * 创建时间：2019/6/1417:35
 * 更新时间 2019/6/1417:35
 * 版本：
 * 作者：Mr.jin
 * 描述：
 */
class ProgressActivity : UploadPluginBindingActivity<ActivityProgressBinding>(),
    UploadProgressCallBack {

    override fun getLayoutId() = R.layout.activity_progress

    override fun doConfig(arguments: Intent) {
        setToolbar(findViewById(R.id.toolbar)!!)
        val type = arguments.getIntExtra("type",1)
        when(type){
            0->{
                val videos = arguments.getSerializableExtra("videos") as ArrayList<VideoItem>
                uploadVideo(videos)
            }
            1->{
                val images = arguments.getSerializableExtra("imageList") as ArrayList<ImageItem>
                uploadImg(images)
            }
            2->{
                val voicePath = arguments.getStringExtra("path")
                Log.v("voice",voicePath)
                uploadVoice(voicePath)
            }
        }
    }

    var count = 0


    var progressViewList: ArrayList<UploadProgressView> = ArrayList()

    private fun uploadImg(imageItems:ArrayList<ImageItem>) {
        val d = D()
        d.orgId = UploadConfig.orgId
        for(image in imageItems){
            val file = D.FileParam()
            file.name = image.name
            file.type = 1
            d.files.add(file)
        }
        addProgressView(d.files, UploadConfig.TYPE_UPLOAD_IMG)
        TencentUpload.instance.upload(d, count)
        count += d.files.size
        TencentUpload.instance.setUploadProgressListener(this)
    }

    private fun uploadVideo(videos:ArrayList<VideoItem>) {
        val e = E()
        e.orgId = UploadConfig.orgId
        var list = ArrayList<Video>()
        for(item in videos){
            val video = Video()
            video.name = item.name
            video.path = item.path
            video.type = UploadConfig.TYPE_UPLOAD_VIDEO
            list.add(video)
        }

        addProgressView(list, UploadConfig.TYPE_UPLOAD_VIDEO)
        TencentUpload.instance.uploadVideo(e, count,list)
        count += list.size
        TencentUpload.instance.setUploadProgressListener(this)
    }

    private fun uploadVoice(path:String) {
        val d = D()
        d.orgId = UploadConfig.orgId
        val file = D.FileParam()
        file.name = path.split("/").last()
        file.type = UploadConfig.TYPE_UPLOAD_VOICE
        d.files.add(file)
        addProgressView(d.files, UploadConfig.TYPE_UPLOAD_VOICE)
        TencentUpload.instance.upload(d, count)
        count += d.files.size
        TencentUpload.instance.setUploadProgressListener(this)
    }

    override fun onSuccess(index: Int, path: String) {
        runOnUiThread {
            Toast.makeText(this, "成功", Toast.LENGTH_SHORT).show()

        }
    }

    override fun onFail(index: Int, error: String) {
        runOnUiThread {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onProgress(index: Int, progress: Int, authorizationInfo: AuthorizationInfo?) {
        runOnUiThread {
            progressViewList[index].setProgress(progress)
        }
    }

    @SuppressLint("CheckResult")
    fun getMedias(authorizationInfo: AuthorizationInfo) {
        ScHttpClient.getService(GoChatService::class.java).getMedias(UploadConfig.ticket, authorizationInfo)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ jsonObject ->
                val medias = Gson().fromJson<ArrayList<Media>>(
                    jsonObject.getJSONArray("medias").toString(),
                    object : TypeToken<ArrayList<Media>>() {}.type
                )
            }, { })
    }

    private fun addProgressView(list: ArrayList<*>, type: Int) {
        for (i in 1..list.size) {
            val uploadProgressView = UploadProgressView(this)
            uploadProgressView.setType(type)
            uploadProgressView.setProgress(0)
            val width = LinearLayout.LayoutParams.WRAP_CONTENT
            val height = LinearLayout.LayoutParams.WRAP_CONTENT
            val layoutParams = LinearLayout.LayoutParams(width, height)
            layoutParams.topMargin = 20
            uploadProgressView.layoutParams = layoutParams
            binding.ll.addView(uploadProgressView)
            progressViewList.add(uploadProgressView)
        }
    }
}