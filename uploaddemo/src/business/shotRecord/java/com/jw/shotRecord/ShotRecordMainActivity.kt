package com.jw.shotRecord

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.Toast
import com.cjt2325.cameralibrary.JCameraView
import com.cjt2325.cameralibrary.listener.ErrorListener
import com.cjt2325.cameralibrary.listener.JCameraListener
import com.cjt2325.cameralibrary.util.DeviceUtil
import com.cjt2325.cameralibrary.util.FileUtil
import com.jw.uploaddemo.R
import com.jw.uploaddemo.UploadConfig
import com.jw.uploaddemo.databinding.ActivityCameraBinding
import com.jw.uploaddemo.uploadPlugin.UploadPluginBindingActivity
import com.jw.videopicker.trim.VideoTrimmerActivity
import com.rxxb.imagepicker.ui.CropActivity
import java.io.File

class ShotRecordMainActivity : UploadPluginBindingActivity<ActivityCameraBinding>() {
    private val CACHE_VIDEO_PATH = UploadConfig.CACHE_VIDEO_PATH //视频缓存路径
    private val CACHE_VIDEO_PATH_COVER = UploadConfig.CACHE_VIDEO_PATH_COVER //视频缓存路径
    private val CACHE_IMG_PATH = UploadConfig.CACHE_IMG_PATH //图片缓存路径
    private var picturePath: String? = null
    private var pictureFileName: String? = null

    override fun getLayoutId() = R.layout.activity_camera

    override fun doConfig(arguments: Intent) {
        releaseFolder()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_camera)
        jCameraView = findViewById<View>(R.id.jcameraview) as JCameraView
        //设置视频保存路径
        jCameraView!!.setSaveVideoPath(CACHE_VIDEO_PATH)
        jCameraView!!.setFeatures(JCameraView.BUTTON_STATE_BOTH)
        jCameraView!!.setTip("轻触拍照，按住摄像")
        jCameraView!!.setMediaQuality(JCameraView.MEDIA_QUALITY_HIGH)
        jCameraView!!.setErrorLisenter(object : ErrorListener {
            override fun onError() {
                //错误监听
                val intent = Intent()
                setResult(103, intent)
                finish()
            }

            override fun AudioPermissionError() {
                Toast.makeText(this@ShotRecordMainActivity, "录音权限没有开启，无法录制", Toast.LENGTH_SHORT).show()
            }
        })
        //JCameraView监听
        jCameraView!!.setJCameraLisenter(object : JCameraListener {
            override fun captureEdiit(bitmap: Bitmap) {
                if (pictureFileName == null) {
                    pictureFileName = "picture_" + System.currentTimeMillis() + ".jpg"
                }
                picturePath = FileUtil.saveBitmap(CACHE_IMG_PATH, pictureFileName, bitmap)
                Log.v("picturePathCrop", picturePath)
                goCrop(picturePath!!)
            }

            override fun captureSuccess(bitmap: Bitmap) {
                if (pictureFileName == null) {
                    pictureFileName = "picture_" + System.currentTimeMillis() + ".jpg"
                }
                picturePath = FileUtil.saveBitmap(CACHE_IMG_PATH, pictureFileName, bitmap)
                val intent1 = Intent()
                Log.v("picturePath", picturePath)
                intent1.putExtra("path", picturePath)
                setResult(RESULT_CODE_IMG, intent1)
                finish()
            }

            override fun recordSuccess(videoPath: String, cover: Bitmap) {
                val coverName = "cover_" + System.currentTimeMillis() + ".jpg"
                //获取视频路径
                val path = FileUtil.saveBitmap(CACHE_VIDEO_PATH_COVER, coverName, cover)
                Log.v("coverPath", path)
                Log.v("videoPath", videoPath)
                val intent1 = Intent()
                intent1.putExtra("path", videoPath)
                setResult(RESULT_CODE_VIDEO, intent1)
                finish()
            }

            override fun recordEdiit(url: String, cover: Bitmap) {
                val coverName = "cover_" + System.currentTimeMillis() + ".png"
                VideoTrimmerActivity.call(this@ShotRecordMainActivity, url, coverName)
            }
        })

        jCameraView!!.setLeftClickListener { finish() }
        jCameraView!!.setRightClickListener {
            Toast.makeText(this@ShotRecordMainActivity, "Right", Toast.LENGTH_SHORT).show()
        }

        Log.i("CJT", DeviceUtil.getDeviceModel())
    }


    private var jCameraView: JCameraView? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            //从图片编辑页面返回
            -1 -> {
                val resultUri = data!!.getParcelableExtra("extra_out_uri") as Uri
                val cropBitmap = BitmapFactory.decodeFile(resultUri.path)
                picturePath = FileUtil.saveBitmap(CACHE_IMG_PATH, pictureFileName, cropBitmap)
                runOnUiThread {
                    jCameraView!!.showPicture(BitmapFactory.decodeFile(picturePath), true)
                    jCameraView!!.machine.state = jCameraView!!.machine.borrowPictureState
                }
            }
        }
    }

    fun goCrop(path: String) {
        startActivityForResult(
            CropActivity.callingIntent(
                this,
                Uri.fromFile(File(path))
            ), 1002
        )
    }

    override fun onStart() {
        super.onStart()
        //全屏显示
        if (Build.VERSION.SDK_INT >= 19) {
            val decorView = window.decorView
            decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        } else {
            val decorView = window.decorView
            val option = View.SYSTEM_UI_FLAG_FULLSCREEN
            decorView.systemUiVisibility = option
        }
    }

    override fun onResume() {
        super.onResume()
        jCameraView!!.onResume()
    }

    override fun onPause() {
        super.onPause()
        jCameraView!!.onPause()
    }

    fun releaseFolder() {
        val folder = File(CACHE_IMG_PATH)
        if (!folder.exists()) {
            folder.mkdir()
        }
        val folder2 = File(CACHE_VIDEO_PATH)
        if (!folder2.exists()) {
            folder2.mkdir()
        }
        val folder3 = File(CACHE_VIDEO_PATH_COVER)
        if (!folder3.exists()) {
            folder3.mkdir()
        }
    }

    companion object {
        const val RESULT_CODE_IMG = 2001
        const val RESULT_CODE_VIDEO = 2002
    }
}
