package com.jw.uploaddemo.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.Log
import android.view.View
import android.widget.Toast
import com.jw.galary.VoiceRecordDialog2
import com.jw.galary.img.ImagePicker
import com.jw.galary.img.bean.ImageItem
import com.jw.galary.img.loader.GlideImageLoader
import com.jw.galary.img.ui.ImageGridActivity
import com.jw.galary.img.util.BitmapUtil
import com.jw.galary.video.VideoGridActivity
import com.jw.galary.video.VideoItem
import com.jw.galary.video.VideoPicker
import com.jw.galary.video.VideoPicker.EXTRA_VIDEO_ITEMS
import com.jw.shotRecord.ShotRecordMainActivity
import com.jw.uploaddemo.R
import com.jw.uploaddemo.UploadConfig
import com.jw.uploaddemo.base.application.BaseApplication
import com.jw.uploaddemo.base.utils.ThemeUtils
import com.jw.uploaddemo.databinding.ActivityMainBinding
import com.jw.uploaddemo.http.ScHttpClient
import com.jw.uploaddemo.http.service.GoChatService
import com.jw.uploaddemo.model.UserInfo
import com.jw.uploaddemo.uploadPlugin.UploadPluginBindingActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File


/**
 * 创建时间：2019/5/1816:30
 * 更新时间 2019/5/1816:30
 * 版本：
 * 作者：Mr.jin
 * 描述：
 */
class MainActivity : UploadPluginBindingActivity<ActivityMainBinding>() {
    private var outputType = 0//输出格式，0表示输出路径，1表示base64字符串
    override fun getLayoutId() = R.layout.activity_main

    override fun doConfig(arguments: Intent) {
        login()
        binding.apply {
            clickListener = View.OnClickListener {
                when (it.id) {
                    R.id.btnUploadVoice -> {
                        val hasPermission = ThemeUtils.checkPermission(
                            this@MainActivity, Manifest.permission.RECORD_AUDIO
                        )
                        //如果开启
                        if (hasPermission == PackageManager.PERMISSION_GRANTED) {
                            //VoiceRecordDialog().show(supportFragmentManager, "costumeBuyDialog")
                            VoiceRecordDialog2().show(fragmentManager, "costumeBuyDialog")
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
                    R.id.paishe -> {
                        val hasPermission = ThemeUtils.checkPermission(
                            this@MainActivity, Manifest.permission.CAMERA
                        )
                        val hasPermission2 = ThemeUtils.checkPermission(
                            this@MainActivity, Manifest.permission.RECORD_AUDIO
                        )
                        if (hasPermission != PackageManager.PERMISSION_GRANTED || hasPermission2 != PackageManager.PERMISSION_GRANTED) {
                            val stringArrays = ArrayList<String>()
                            stringArrays.add(Manifest.permission.CAMERA)
                            stringArrays.add(Manifest.permission.RECORD_AUDIO)
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                ThemeUtils.requestPermissions(
                                    this@MainActivity, stringArrays, 300
                                )
                            }
                        } else
                            startActivityForResult(
                                Intent(
                                    this@MainActivity,
                                    ShotRecordMainActivity::class.java
                                ), 0
                            )
                    }
                    R.id.selFromGalary -> {
                        ImagePicker.getInstance().imageLoader = GlideImageLoader()
                        startActivityForResult(
                            Intent(
                                this@MainActivity,
                                ImageGridActivity::class.java
                            ), 400
                        )
                    }
                    R.id.selFromGalary2 -> {
                        VideoPicker.getInstance().imageLoader = GlideImageLoader()
                        startActivityForResult(
                            Intent(
                                this@MainActivity,
                                VideoGridActivity::class.java
                            ), 0
                        )
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
        userInfo.phone = UploadConfig.phone
        userInfo.pwd = UploadConfig.pwd
        userInfo.type = UploadConfig.type
        ScHttpClient.getService(GoChatService::class.java).login(userInfo)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ jsonObject ->
                UploadConfig.ticket = jsonObject.getLong("ticket")
            }, { })
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
        if (hasPermission != PackageManager.PERMISSION_GRANTED || hasPermission2 != PackageManager.PERMISSION_GRANTED||hasPermission3 != PackageManager.PERMISSION_GRANTED) {
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
                        Toast.makeText(this@MainActivity, "存储卡读写全蝎没有开启,应用无法运行", Toast.LENGTH_SHORT)
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
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this@MainActivity, "录音权限没有开启,无法录音", Toast.LENGTH_SHORT).show()
                } else {
                    //VoiceRecordDialog().show(supportFragmentManager, "costumeBuyDialog")
                    VoiceRecordDialog2().show(fragmentManager, "costumeBuyDialog")
                }
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
                    startActivityForResult(
                        Intent(
                            this@MainActivity,
                            ShotRecordMainActivity::class.java
                        ), 0
                    )
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
                ImagePicker.RESULT_CODE_IMAGE_ITEMS -> {
                    val list =
                        intent.getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS) as java.util.ArrayList<ImageItem>
                    if (list.size == 0)
                        return
                    correctImageFactory(list)
                }
                VideoPicker.RESULT_CODE_VIDEO_ITEMS -> {
                    val list2 =
                        intent.getSerializableExtra(EXTRA_VIDEO_ITEMS) as java.util.ArrayList<VideoItem>
                    val intent = Intent(getActivity(), ProgressActivity::class.java)
                    intent.putExtra("path", list2[0].path)
                    intent.putExtra("name", list2[0].name)
                    intent.putExtra("type", UploadConfig.TYPE_UPLOAD_VIDEO)
                    intent.putParcelableArrayListExtra("videos", list2)
                    startActivityForResult(intent, 0)
                }
                UploadConfig.RESULT_UPLOAD_SUCCESS -> {
                    Log.v("bbbbbbbbbb", intent.getStringExtra("result"))
                }
            }
        }
    }

    private fun correctImageFactory(images: ArrayList<ImageItem>) {
        Thread {
            run {
                for (image in images) {
                    var saved = false
                    val destPath = ImagePicker.createFile(
                        ImagePicker.getInstance().getCropCacheFolder(this),
                        "IMG_" + System.currentTimeMillis(),
                        ".png"
                    ).absolutePath
                    if (ImagePicker.getInstance().isOrigin || ImagePicker.getInstance().outPutX == 0 || ImagePicker.getInstance().outPutY == 0) {
                        //原图按图片原始尺寸压缩, size小于150kb的不压缩
                        if (isNeedCompress(150, image.path)) {
                            saved = BitmapUtil.saveBitmap2File(
                                BitmapUtil.compress(image.path),
                                destPath
                            )
                        }
                    } else {
                        //按给定的宽高压缩
                        saved = BitmapUtil.saveBitmap2File(
                            BitmapUtil.getScaledBitmap(
                                image.path,
                                ImagePicker.getInstance().outPutX,
                                ImagePicker.getInstance().outPutY
                            ), destPath
                        )
                    }
                    if (outputType == 0) {
                        image.path = if (saved) destPath else image.path
                    } else {
                        image.path = BitmapUtil.base64Image(if (saved) destPath else image.path)
                    }
                    image.name =
                        image.path.split("/").last()
                    Log.v("uuuuuuuuu",image.name)
                }
                val intent = Intent(this, ProgressActivity::class.java)
                intent.putExtra("imageList", images)
                intent.putExtra("type", UploadConfig.TYPE_UPLOAD_IMG)
                startActivityForResult(intent,0)
            }
        }.start()
    }

    private fun isNeedCompress(leastCompressSize: Int, path: String): Boolean {
        if (leastCompressSize > 0) {
            val source = File(path)
            if (!source.exists()) {
                return false
            }

            if (source.length() <= leastCompressSize shl 10) {
                return false
            }
        }
        return true
    }
}