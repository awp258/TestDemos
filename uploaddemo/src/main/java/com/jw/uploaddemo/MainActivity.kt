package com.jw.uploaddemo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.Log
import android.view.View
import android.widget.Toast
import com.jw.cameralibrary.CameraLibrary
import com.jw.cameralibrary.ShotRecordMainActivity
import com.jw.croplibrary.CropLibrary
import com.jw.galary.VoiceRecordDialog2
import com.jw.galarylibrary.img.ImagePicker
import com.jw.galarylibrary.img.ui.ImageGridActivity
import com.jw.galarylibrary.video.ui.VideoGridActivity
import com.jw.library.model.ImageItem
import com.jw.library.model.VideoItem
import com.jw.library.model.VoiceItem
import com.jw.library.ui.BaseBindingActivity
import com.jw.library.utils.ThemeUtils
import com.jw.uilibrary.base.application.BaseApplication
import com.jw.uploaddemo.databinding.ActivityMainBinding
import com.jw.uploadlibrary.ProgressActivity
import com.jw.uploadlibrary.UploadLibrary


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
        startActivityForResult(
            Intent(
                this@MainActivity,
                ImageGridActivity::class.java
            ), 400
        )
    }

    private fun getVideos() {
        startActivityForResult(
            Intent(
                this@MainActivity,
                VideoGridActivity::class.java
            ), 0
        )
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
            }
        }
        super.onRequestPermissionsResult(
            requestCode, permissions,
            grantResults
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (intent?.extras != null) {
            when (resultCode) {
                ImagePicker.RESULT_CODE_ITEMS -> {
                    val isImage = intent.getBooleanExtra("isImage", true)
                    val list =
                        intent.getSerializableExtra(ImagePicker.EXTRA_ITEMS)
                    val intent = Intent(this, ProgressActivity::class.java)
                    if (isImage) {
                        intent.putExtra("type", UploadLibrary.TYPE_UPLOAD_IMG)
                        intent.putParcelableArrayListExtra("images", list as ArrayList<ImageItem>)
                    } else {
                        intent.putExtra("type", UploadLibrary.TYPE_UPLOAD_VIDEO)
                        intent.putParcelableArrayListExtra("videos", list as ArrayList<VideoItem>)
                    }
                    startActivityForResult(intent, 0)
                }
                UploadLibrary.RESULT_UPLOAD_SUCCESS -> {
                    Log.v("medias", intent.getStringExtra("medias"))
                }
            }
        }
    }

    private fun toVoiceRecordDialog() {
        val voiceRecordDialog2 = VoiceRecordDialog2()
        voiceRecordDialog2.show(fragmentManager, "costumeBuyDialog")
        voiceRecordDialog2.setStartForResultListener(object :
            VoiceRecordDialog2.VoiceRecordListener {
            override fun onFinish(voiceItem: VoiceItem) {
                val intent = Intent(this@MainActivity, ProgressActivity::class.java)
                intent.putExtra("type", UploadLibrary.TYPE_UPLOAD_VOICE)
                val voices = ArrayList<VoiceItem>()
                voices.add(voiceItem)
                intent.putParcelableArrayListExtra("voices", voices)
                startActivityForResult(intent, 0)
            }
        })
    }

    private fun toShotRecordMainActivity() {
        startActivityForResult(
            Intent(
                this@MainActivity,
                ShotRecordMainActivity::class.java
            ), 0
        )
    }
}