package com.jw.uploaddemo

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Environment
import android.support.annotation.RequiresApi
import android.util.Log
import android.view.View
import android.widget.Toast
import com.jw.cameralibrary.CameraLibrary
import com.jw.cameralibrary.ShotRecordMainActivity
import com.jw.croplibrary.CropLibrary
import com.jw.galarylibrary.base.activity.BaseGridActivity
import com.jw.galarylibrary.img.ImagePicker
import com.jw.galarylibrary.img.ui.ImageGridActivity
import com.jw.galarylibrary.video.ui.VideoGridActivity
import com.jw.library.model.BaseItem
import com.jw.library.model.VoiceItem
import com.jw.library.ui.BaseBindingActivity
import com.jw.library.utils.BitmapUtil
import com.jw.library.utils.RomUtil
import com.jw.library.utils.ThemeUtils
import com.jw.uilibrary.base.application.BaseApplication
import com.jw.uploaddemo.databinding.ActivityMainBinding
import com.jw.uploadlibrary.ProgressActivity
import com.jw.uploadlibrary.UploadLibrary
import com.jw.uploadlibrary.http.ScHttpClient
import com.jw.uploadlibrary.http.service.GoChatService
import com.jw.uploadlibrary.model.UserInfo
import com.jw.voicelibrary.VoiceRecordDialog2
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


/**
 * 创建时间：2019/5/1816:30
 * 更新时间 2019/5/1816:30
 * 版本：
 * 作者：Mr.jin
 * 描述：
 */
class MainActivity : BaseBindingActivity<ActivityMainBinding>() {
    private var outputType = 0//输出格式，0表示输出路径，1表示base64字符串
    override fun getLayoutId() = R.layout.activity_main

    override fun doConfig(arguments: Intent) {
        //login()
        // 刷新相册
        if (RomUtil.isEmui()) {
            MediaScannerConnection.scanFile(
                this, arrayOf(Environment.getExternalStorageDirectory().toString()), null
            ) { path, uri ->
            }
        }
        mBinding.apply {
            clickListener = View.OnClickListener {
                when (it.id) {
                    R.id.btn_record_voice -> {
                        voiceRecord()
                    }
                    R.id.btn_switch_shot_model_short -> {
                        CameraLibrary.SHOT_MODEL = 2
                    }
                    R.id.btn_switch_shot_model_long -> {
                        CameraLibrary.SHOT_MODEL = 1
                    }
                    R.id.btn_shot -> {
                        CameraLibrary.SHOT_TYPE = 4
                        CropLibrary.setMultipleModle()
                        shot()
                    }
                    R.id.btn_shot_only_video -> {
                        CameraLibrary.SHOT_TYPE = 6
                        shot()
                    }
                    R.id.btn_shot_only_picture -> {
                        CameraLibrary.SHOT_TYPE = 5
                        CropLibrary.setMultipleModle()
                        shot()
                    }
                    R.id.btn_shot_picture_crop_circle -> {
                        CameraLibrary.SHOT_TYPE = 5
                        CameraLibrary.isCrop = true
                        CropLibrary.setCircleCrop()
                        shot()
                    }
                    R.id.btn_shot_picture_crop_rectangle -> {
                        CameraLibrary.SHOT_TYPE = 5
                        CameraLibrary.isCrop = true
                        CropLibrary.isSaveToGalary = false
                        CropLibrary.setRectangleCrop()
                        shot()
                    }
                    R.id.btn_sel_picture -> {
                        ImagePicker.setMultipleModle()
                        getPictures()
                    }
                    R.id.btn_sel_picture_crop_circle -> {
                        ImagePicker.setCircleCrop()
                        getPictures()
                    }
                    R.id.btn_sel_picture_crop_rectangle -> {
                        CropLibrary.isSaveToGalary = false
                        ImagePicker.setRectangleCrop()
                        getPictures()
                    }
                    R.id.btn_sel_video -> {
                        getVideos()
                    }
                }
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission()
        }
    }

    @SuppressLint("CheckResult")
    private fun login() {
        val userInfo = UserInfo()
        userInfo.phone = UploadLibrary.phone
        userInfo.pwd = UploadLibrary.pwd
        userInfo.type = UploadLibrary.type
        ScHttpClient.getService(GoChatService::class.java).login(userInfo)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ jsonObject ->
                UploadLibrary.ticket = jsonObject.getLong("ticket")
            }, { })
    }

    private fun voiceRecord() {
        val hasPermission = ThemeUtils.checkPermission(
            this@MainActivity, Manifest.permission.RECORD_AUDIO
        )
        //如果开启
        if (hasPermission) {
            //VoiceRecordDialog().show(supportFragmentManager, "costumeBuyDialog")
            toVoiceRecordDialog()
        } else {
            //弹出请求框请求用户开启
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ThemeUtils.requestPermission(
                    this@MainActivity,
                    Manifest.permission.RECORD_AUDIO
                    , 200
                )
            }
        }
    }

    private fun getPictures() {
        ImageGridActivity.start(this)
    }

    private fun getVideos() {
        VideoGridActivity.start(this)
    }

    private fun shot() {
        val hasPermission = ThemeUtils.checkPermission(
            this@MainActivity, Manifest.permission.CAMERA
        )
        val hasPermission2 = ThemeUtils.checkPermission(
            this@MainActivity, Manifest.permission.RECORD_AUDIO
        )
        if (!hasPermission || !hasPermission2) {
            val stringArrays = ArrayList<String>()
            stringArrays.add(Manifest.permission.CAMERA)
            stringArrays.add(Manifest.permission.RECORD_AUDIO)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ThemeUtils.requestPermissions(
                    this@MainActivity, stringArrays, 300
                )
            }
        } else
            toShotRecordMainActivity()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkPermission() {
        //检查需要系统同意的请求是否开启
        val hasPermission = ThemeUtils.checkPermission(
            this@MainActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val hasPermission2 = ThemeUtils.checkPermission(
            this@MainActivity, Manifest.permission.READ_PHONE_STATE
        )
        val hasPermission3 = ThemeUtils.checkPermission(
            this@MainActivity, Manifest.permission.READ_EXTERNAL_STORAGE
        )
        if (!hasPermission || !hasPermission2 || !hasPermission3) {
            val stringArrays = ArrayList<String>()
            stringArrays.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            stringArrays.add(Manifest.permission.READ_PHONE_STATE)
            stringArrays.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            ThemeUtils.requestPermissions(
                this@MainActivity, stringArrays, 100
            )
        }
    }

    /**
     * 权限设置后的回调函数，判断相应设置
     * @param requestCode
     * @param permissions  requestPermissions传入的参数为几个权限
     * @param grantResults 对应权限的设置结果
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            //系统必要权限
            100 -> {
                for (permission in permissions) {
                    if (permission == Manifest.permission.WRITE_EXTERNAL_STORAGE && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(this@MainActivity, "存储卡读写权限没有开启,应用无法运行", Toast.LENGTH_SHORT)
                            .show()
                        BaseApplication.exit()
                    } else if (permission == Manifest.permission.READ_PHONE_STATE && grantResults[1] == PackageManager.PERMISSION_DENIED)
                        Toast.makeText(
                            this@MainActivity,
                            "读取系统状态权限没有开启,将失去部分功能",
                            Toast.LENGTH_SHORT
                        ).show()
                }
            }
            //录音权限
            200 -> {
                if (grantResults[0] == PackageManager.PERMISSION_DENIED)
                    Toast.makeText(this@MainActivity, "录音权限没有开启,无法录音", Toast.LENGTH_SHORT).show()
                else
                    toVoiceRecordDialog()
            }
            //相机权限
            300 -> {
                for (permission in permissions) {
                    if (permission == Manifest.permission.CAMERA && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(this@MainActivity, "相机权限没有开启,无法录屏", Toast.LENGTH_SHORT)
                            .show()
                    } else if (permission == Manifest.permission.RECORD_AUDIO && grantResults[1] == PackageManager.PERMISSION_DENIED)
                        Toast.makeText(
                            this@MainActivity,
                            "录音权限没有开启,无法录音",
                            Toast.LENGTH_SHORT
                        ).show()
                }
                if (grantResults[0] == PackageManager.PERMISSION_DENIED && grantResults[1] == PackageManager.PERMISSION_DENIED)
                    toShotRecordMainActivity()
                0
            }
        }
        super.onRequestPermissionsResult(
            requestCode, permissions,
            grantResults
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                BaseGridActivity.REQUEST_CODE_GRID -> {
                    val isImage = intent!!.getBooleanExtra("isImage", true)
                    val list =
                        intent.getSerializableExtra(CameraLibrary.EXTRA_ITEMS) as ArrayList<BaseItem>
                    if (isImage) {
                        correctImageFactory(list)
                        toUpload(UploadLibrary.TYPE_UPLOAD_IMG, list)
                    } else {
                        toUpload(UploadLibrary.TYPE_UPLOAD_VIDEO, list)
                    }
                }
                ShotRecordMainActivity.REQUEST_CODE_SHOT -> {
                    val isImage = intent!!.getBooleanExtra("isImage", true)
                    val list =
                        intent.getSerializableExtra(CameraLibrary.EXTRA_ITEMS) as ArrayList<BaseItem>
                    if (isImage) {
                        correctImageFactory(list)
                        toUpload(UploadLibrary.TYPE_UPLOAD_IMG, list)
                    } else {
                        toUpload(UploadLibrary.TYPE_UPLOAD_VIDEO, list)
                    }
                }
                ProgressActivity.REQUEST_CODE_UPLOAD -> {
                    Log.v("medias", intent!!.getStringExtra("medias"))
                }
            }
        }
    }

    private fun correctImageFactory(images: java.util.ArrayList<BaseItem>) {
        if (CropLibrary.isExactlyOutput) {
            for (image in images) {
                val bitmap = BitmapUtil.getScaledBitmap(
                    image.path!!,
                    CropLibrary.outPutX,
                    CropLibrary.outPutY
                )
                image.name = image.name!!.replace(".png", ".jpg")
                //按给定的宽高压缩
                BitmapUtil.saveBitmap2File(bitmap, image.path!!)
                CropLibrary.galleryAddMedia(this, image.path!!)
            }
        }
    }

    private fun toVoiceRecordDialog() {
        val voiceRecordDialog2 = VoiceRecordDialog2()
        voiceRecordDialog2.show(fragmentManager, "costumeBuyDialog")
        voiceRecordDialog2.setStartForResultListener(object :
            VoiceRecordDialog2.VoiceRecordListener {
            override fun onFinish(voiceItem: VoiceItem) {
                val voices = ArrayList<VoiceItem>()
                voices.add(voiceItem)
                toUpload(UploadLibrary.TYPE_UPLOAD_VOICE, voices)
            }
        })
    }


    private fun toUpload(type: Int, items: ArrayList<out BaseItem>) {
        for (item in items) {
            Log.v(
                "sel_result",
                "name：" + item.name + "  mimeType：" + item.mimeType + "  size：" + item.size + "  path：" + item.path
            )
        }
        //ProgressActivity.start(this, type, items)
    }

    private fun toShotRecordMainActivity() {
        ShotRecordMainActivity.start(this)
    }
}