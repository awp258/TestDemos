package com.jw.cameralibrary

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.jw.cameralibrary.CameraLibrary.CACHE_IMG_PATH
import com.jw.cameralibrary.CameraLibrary.CACHE_VIDEO_PATH
import com.jw.cameralibrary.CameraLibrary.CACHE_VIDEO_PATH_COVER
import com.jw.cameralibrary.CameraLibrary.EXTRA_ITEMS
import com.jw.cameralibrary.databinding.ActivityCameraBinding
import com.jw.cameralibrary.listener.ClickListener
import com.jw.cameralibrary.listener.JCameraListener
import com.jw.croplibrary.CropLibrary
import com.jw.croplibrary.img.CropActivity
import com.jw.croplibrary.video.VideoTrimmerActivity
import com.jw.library.model.ImageItem
import com.jw.library.model.VideoItem
import com.jw.library.ui.BaseBindingActivity
import com.jw.library.utils.BitmapUtil
import com.jw.library.utils.FileUtils
import com.jw.library.utils.ThemeUtils
import com.jw.library.utils.VideoUtil
import java.io.File

class ShotRecordMainActivity : BaseBindingActivity<ActivityCameraBinding>() {
    private var picturePath: String? = null
    private var pictureFileName: String? = null
    private var cropType: Int = 0

    override fun getLayoutId() = R.layout.activity_camera

    override fun doConfig(arguments: Intent) {
        CameraInterface.mWidth = ThemeUtils.getWindowHeight(this)
        releaseFolder()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_camera)
        jCameraView = findViewById<View>(R.id.jcameraview) as JCameraView
        //设置视频保存路径
        jCameraView!!.setSaveVideoPath(CACHE_VIDEO_PATH)
        jCameraView!!.setFeatures(CameraLibrary.SHOT_TYPE)
        if (CameraLibrary.SHOT_MODEL == 2) {
            when (CameraLibrary.SHOT_TYPE) {
                JCameraView.BUTTON_STATE_BOTH -> jCameraView!!.setTip("轻触拍照，按住摄像")
                JCameraView.BUTTON_STATE_ONLY_CAPTURE -> jCameraView!!.setTip("轻触拍照")
                JCameraView.BUTTON_STATE_ONLY_RECORDER -> jCameraView!!.setTip("按住摄像")
            }
        }
        jCameraView!!.setMediaQuality(JCameraView.MEDIA_QUALITY_HIGH)
        //JCameraView监听
        jCameraView!!.setJCameraLisenter(object :
            JCameraListener {
            override fun captureEdit(bitmap: Bitmap) {
                if (pictureFileName == null) {
                    pictureFileName = "picture_" + System.currentTimeMillis() + ".jpg"
                }
                picturePath = FileUtils.saveBitmap(CACHE_IMG_PATH!!, pictureFileName!!, bitmap)
                cropType = 1
                goCrop(picturePath!!)
            }

            override fun captureSuccess(bitmap: Bitmap) {
                if (pictureFileName == null) {
                    pictureFileName = "picture_" + System.currentTimeMillis() + ".jpg"
                }
                picturePath = FileUtils.saveBitmap(CACHE_IMG_PATH!!, pictureFileName!!, bitmap)
                val imageItem = ImageItem(picturePath!!)
                val uri = BitmapUtil.saveBitmap2Galary(bitmap, this@ShotRecordMainActivity)
                CameraLibrary.galleryAddPic(this@ShotRecordMainActivity, uri)
                backCapture(imageItem)
            }

            override fun recordSuccess(videoPath: String, cover: Bitmap, duration: Long) {
                val coverName = "cover_" + System.currentTimeMillis() + ".jpg"
                val coverPath = FileUtils.saveBitmap(CACHE_VIDEO_PATH_COVER!!, coverName, cover)
                val videoItem = VideoItem(videoPath, coverPath, duration)
                val uri = VideoUtil.saveToGalary(this@ShotRecordMainActivity, videoPath, duration)
                CameraLibrary.galleryAddPic(this@ShotRecordMainActivity, uri)
                backRecord(videoItem)
            }

            override fun recordEdit(videoPath: String, cover: Bitmap, duration: Long) {
                val coverName = "cover_" + System.currentTimeMillis() + ".png"
                cropType = 2
                VideoTrimmerActivity.start(this@ShotRecordMainActivity, videoPath, coverName)
            }
        })

        jCameraView!!.setLeftClickListener(object :
            ClickListener {
            override fun onClick() {
                finish()
            }
        })
    }


    private var jCameraView: JCameraView? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                //从编辑页面返回
                CropActivity.REQUEST_CODE_ITEM_CROP -> {
                    val resultUri =
                        data!!.getParcelableExtra(CropLibrary.EXTRA_CROP_ITEM_OUT_URI) as Uri
                    val cropBitmap = BitmapFactory.decodeFile(resultUri.path)
                    picturePath =
                        FileUtils.saveBitmap(CACHE_IMG_PATH!!, pictureFileName!!, cropBitmap)
                    val imageItem = ImageItem(picturePath!!)
                    backCapture(imageItem)
                }
                VideoTrimmerActivity.REQUEST_CODE_ITEM_CROP -> {
                    val resultUri =
                        data!!.getParcelableExtra(CropLibrary.EXTRA_CROP_ITEM_OUT_URI) as Uri
                    val path = resultUri.path
                    val thumbPath = data.getStringExtra("thumbPath")
                    val duration = data.getLongExtra("duration", 0)
                    val item = VideoItem(path!!, thumbPath, duration)
                    backRecord(item)
                }
            }
        }
    }

    fun goCrop(path: String) {
        CropActivity.start(this, Uri.fromFile(File(path)))
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

    private fun backCapture(imageItem: ImageItem) {
        val imageItems = ArrayList<ImageItem>()
        imageItems.add(imageItem)
        val intent = Intent()
        intent.putExtra(EXTRA_ITEMS, imageItems)
        intent.putExtra("isImage", true)
        this.setResult(Activity.RESULT_OK, intent)
        this.finish()
    }

    private fun backRecord(videoItem: VideoItem) {
        val videoItems = ArrayList<VideoItem>()
        videoItems.add(videoItem)
        val intent = Intent()
        intent.putExtra(EXTRA_ITEMS, videoItems)
        intent.putExtra("isImage", false)
        this.setResult(Activity.RESULT_OK, intent)
        this.finish()
    }

    override fun onResume() {
        super.onResume()
        jCameraView!!.onResume()
    }

    override fun onPause() {
        super.onPause()
        jCameraView!!.onPause()
    }

    private fun releaseFolder() {
        FileUtils.releaseFolder(CACHE_IMG_PATH!!)
        FileUtils.releaseFolder(CACHE_VIDEO_PATH!!)
        FileUtils.releaseFolder(CACHE_VIDEO_PATH_COVER!!)
    }

    companion object {
        const val REQUEST_CODE_SHOT = 2001

        fun start(activity: AppCompatActivity) {
            val intent = Intent(activity, ShotRecordMainActivity::class.java)
            activity.startActivityForResult(intent, REQUEST_CODE_SHOT)
        }
    }
}
