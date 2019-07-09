package com.jw.uploaddemo.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.support.annotation.RequiresApi
import android.view.View
import android.widget.Toast
import com.jw.shotRecord.ShotRecordMainActivity
import com.jw.shotRecord.ShotRecordMainActivity.Companion.RESULT_CODE_IMG
import com.jw.shotRecord.ShotRecordMainActivity.Companion.RESULT_CODE_VIDEO
import com.jw.shotRecord.VoiceRecordDialog
import com.jw.uilibrary.base.application.BaseApplication
import com.jw.uploaddemo.R
import com.jw.uploaddemo.databinding.ActivityMainBinding
import com.jw.uploaddemo.uploadPlugin.UploadPluginBindingActivity
import com.jw.uploaddemo.utils.ThemeUtils
import com.jw.videopicker.VideoGridActivity
import com.jw.videopicker.VideoItem
import com.jw.videopicker.VideoPicker
import com.rxxb.imagepicker.ImagePicker
import com.rxxb.imagepicker.bean.ImageItem
import com.rxxb.imagepicker.loader.GlideImageLoader
import com.rxxb.imagepicker.ui.ImageGridActivity
import com.rxxb.imagepicker.util.BitmapUtil
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
        binding.apply {
            clickListener = View.OnClickListener {
                when (it.id) {
                    R.id.btnUploadVoice -> {
                        val hasPermission = ThemeUtils.checkPermission(
                            this@MainActivity, Manifest.permission.RECORD_AUDIO
                        )
                        //如果开启
                        if (hasPermission == PackageManager.PERMISSION_GRANTED) {
                            VoiceRecordDialog().show(supportFragmentManager, "costumeBuyDialog")
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
                            startActivityForResult(Intent(this@MainActivity,ShotRecordMainActivity::class.java), 0)
                    }
                    R.id.selFromGalary -> {
                        ImagePicker.getInstance().imageLoader = GlideImageLoader()
                        startActivityForResult(Intent(this@MainActivity,ImageGridActivity::class.java), 400)
                    }
                    R.id.selFromGalary2 -> {
                        VideoPicker.getInstance().imageLoader = GlideImageLoader()
                        startActivityForResult(Intent(this@MainActivity,VideoGridActivity::class.java), 0)
                    }
                }
            }
        }
        ThemeUtils.changeStatusBar(this, Color.parseColor("#424242"))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission()
        }
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
        if (hasPermission != PackageManager.PERMISSION_GRANTED || hasPermission2 != PackageManager.PERMISSION_GRANTED) {
            val stringArrays = ArrayList<String>()
            stringArrays.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            stringArrays.add(Manifest.permission.READ_PHONE_STATE)
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
                        Toast.makeText(this@MainActivity, "存储卡读写全蝎没有开启,应用无法运行", Toast.LENGTH_SHORT).show()
                        BaseApplication.exit()
                    } else if (permission == Manifest.permission.READ_PHONE_STATE && grantResults[1] == PackageManager.PERMISSION_DENIED)
                        Toast.makeText(this@MainActivity, "读取系统状态权限没有开启,将失去部分功能", Toast.LENGTH_SHORT).show()
                }
            }
            //录音权限
            200 -> {
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this@MainActivity, "录音权限没有开启,无法录音", Toast.LENGTH_SHORT).show()
                } else {
                    VoiceRecordDialog().show(supportFragmentManager, "costumeBuyDialog")
                }
            }
            //相机权限
            300 -> {
                for (permission in permissions) {
                    if (permission == Manifest.permission.CAMERA && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(this@MainActivity, "相机权限没有开启,无法录屏", Toast.LENGTH_SHORT).show()
                    } else if (permission == Manifest.permission.RECORD_AUDIO && grantResults[1] == PackageManager.PERMISSION_DENIED)
                        Toast.makeText(this@MainActivity, "录音权限没有开启,无法录音", Toast.LENGTH_SHORT).show()
                }
                if (grantResults[0] == PackageManager.PERMISSION_DENIED && grantResults[1] == PackageManager.PERMISSION_DENIED)
                    startActivityForResult(Intent(this@MainActivity,ShotRecordMainActivity::class.java), 0)
            }
        }
        super.onRequestPermissionsResult(
            requestCode, permissions,
            grantResults
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        when (resultCode) {
            //从图库返回
            ImagePicker.RESULT_CODE_ITEMS -> {
                val images = intent!!.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS) as ArrayList<ImageItem>
                if (images.isEmpty()) {
                    return
                }
                correctImageFactory(images)
            }
            //拍照返回
            RESULT_CODE_IMG -> {
                val list = ArrayList<ImageItem>()
                val imageItem = ImageItem()
                imageItem.path = intent!!.getStringExtra("path")
                list.add(imageItem)
                if (list.isEmpty()) {
                    return
                }
                correctImageFactory(list)
            }
            //拍摄视频返回
            RESULT_CODE_VIDEO -> {
                val progressIntent = Intent(getActivity(), ProgressActivity::class.java)
                val path = intent!!.getStringExtra("path")
                val name = path.split("ShotVideoRecorder/")[1]
                progressIntent.putExtra("path", path)
                progressIntent.putExtra("name", name)
                progressIntent.putExtra("type", 0)
                start(progressIntent)
            }
            VideoPicker.RESULT_CODE_ITEMS -> {
                val videoItems = intent!!.getSerializableExtra("extra_result_videos") as ArrayList<VideoItem>
                val progressIntent = Intent(getActivity(), ProgressActivity::class.java)
                progressIntent.putExtra("path",videoItems[0].path)
                progressIntent.putExtra("name",videoItems[0].name)
                progressIntent.putExtra("type", 0)
                progressIntent.putParcelableArrayListExtra("videos",videoItems)
                start(progressIntent)
            }
        }
    }

    private fun correctImageFactory(images: ArrayList<ImageItem>) {
        Thread {
            run {
                val imagePath = ArrayList<String>()
                for (image in images) {
                    val result = image.path
                    var saved = false
                    val destPath = ImagePicker.createFile(
                        ImagePicker.getInstance().getCropCacheFolder(this),
                        "IMG_" + System.currentTimeMillis(),
                        ".png"
                    ).absolutePath
                    if (ImagePicker.getInstance().isOrigin || ImagePicker.getInstance().outPutX == 0 || ImagePicker.getInstance().outPutY == 0) {
                        //原图按图片原始尺寸压缩, size小于150kb的不压缩
                        if (isNeedCompress(150, result)) {
                            saved = BitmapUtil.saveBitmap2File(BitmapUtil.compress(result), destPath)
                        }
                    } else {
                        //按给定的宽高压缩
                        saved = BitmapUtil.saveBitmap2File(
                            BitmapUtil.getScaledBitmap(
                                result,
                                ImagePicker.getInstance().outPutX,
                                ImagePicker.getInstance().outPutY
                            ), destPath
                        )
                    }
                    if (outputType == 0) {
                        imagePath.add(if (saved) destPath else result)
                    } else {
                        imagePath.add(BitmapUtil.base64Image(if (saved) destPath else result))
                    }
                }
                val intent = Intent(this, ProgressActivity::class.java)
                intent.putStringArrayListExtra("imageList", imagePath)
                intent.putExtra("type", 1)
                start(intent)
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