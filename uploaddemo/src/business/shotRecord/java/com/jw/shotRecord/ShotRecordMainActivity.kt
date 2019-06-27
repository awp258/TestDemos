package com.jw.shotRecord

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.cjt2325.cameralibrary.JCameraView
import com.cjt2325.cameralibrary.listener.ErrorListener
import com.cjt2325.cameralibrary.listener.JCameraListener
import com.cjt2325.cameralibrary.util.DeviceUtil
import com.cjt2325.cameralibrary.util.FileUtil
import com.jw.uploaddemo.R
import com.jw.uploaddemo.databinding.ActivityCameraBinding
import com.jw.uploaddemo.uploadPlugin.UploadPluginBindingActivity
import com.rxxb.imagepicker.ui.CropActivity
import java.io.File

class ShotRecordMainActivity : UploadPluginBindingActivity<ActivityCameraBinding>() {

    override fun getLayoutId() = R.layout.activity_camera

    override fun doConfig(arguments: Intent) {
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_camera)
        jCameraView = findViewById<View>(R.id.jcameraview) as JCameraView
        //设置视频保存路径
        jCameraView!!.setSaveVideoPath(filesDir.absolutePath + "/ShotRecorder/video")
        jCameraView!!.setFeatures(JCameraView.BUTTON_STATE_BOTH)
        jCameraView!!.setTip("轻触拍照，按住摄像")
        jCameraView!!.setMediaQuality(JCameraView.MEDIA_QUALITY_HIGH)
        jCameraView!!.setErrorLisenter(object : ErrorListener {
            override fun onError() {
                //错误监听
                Log.i("CJT", "camera error")
                val intent = Intent()
                setResult(103, intent)
                finish()
            }

            override fun AudioPermissionError() {
                Toast.makeText(this@ShotRecordMainActivity, "给点录音权限可以?", Toast.LENGTH_SHORT).show()
            }
        })
        //JCameraView监听
        jCameraView!!.setJCameraLisenter(object : JCameraListener {
            override fun captureEdiit(bitmap: Bitmap) {
                val path = FileUtil.saveBitmap(filesDir.absolutePath + "/ShotRecorder/image", bitmap)
                Log.v("path1",path)
                goCrop(path)
            }

            override fun captureSuccess(bitmap: Bitmap) {
                val path = FileUtil.saveBitmap(filesDir.absolutePath + "/ShotRecorder/image", bitmap)
                val intent1 = Intent()
                intent1.putExtra("path", path)
                setResult(RESULT_CODE_IMG, intent1)
                finish()
            }

            override fun recordSuccess(url: String, firstFrame: Bitmap) {
                //获取视频路径
                val path = FileUtil.saveBitmap("JCamera", firstFrame)
                Log.i("CJT", "url = $url, Bitmap = $path")
                val intent1 = Intent()
                intent1.putExtra("path", url)
                setResult(RESULT_CODE_VIDEO, intent1)
                finish()
            }

            override fun recordEdiit(url: String, firstFrame: Bitmap) {

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
                runOnUiThread {
                    jCameraView!!.showPicture(BitmapFactory.decodeFile(resultUri.path),true)
                    jCameraView!!.machine.state=jCameraView!!.machine.borrowPictureState
                }
            }
        }
    }

    fun goCrop(path:String) {
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

    companion object {
        const val RESULT_CODE_IMG = 2001
        const val RESULT_CODE_VIDEO = 2002
    }
}
