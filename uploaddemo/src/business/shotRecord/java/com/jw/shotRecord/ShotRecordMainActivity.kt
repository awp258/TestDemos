package com.jw.shotRecord

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.view.View
import com.jw.galary.img.ImagePicker
import com.jw.galary.img.ImagePicker.EXTRA_ITEMS
import com.jw.galary.img.ui.CropActivity
import com.jw.galary.video.VideoPicker
import com.jw.galary.video.trim.VideoTrimmerActivity
import com.jw.library.model.ImageItem
import com.jw.library.model.VideoItem
import com.jw.library.ui.BaseBindingActivity
import com.jw.library.utils.BitmapUtil
import com.jw.library.utils.FileUtils
import com.jw.library.utils.ThemeUtils
import com.jw.shotRecord.listener.ClickListener
import com.jw.shotRecord.listener.JCameraListener
import com.jw.uploaddemo.R
import com.jw.uploaddemo.databinding.ActivityCameraBinding
import java.io.File

class ShotRecordMainActivity : BaseBindingActivity<ActivityCameraBinding>() {
    private val CACHE_VIDEO_PATH = com.jw.library.UploadConfig.CACHE_VIDEO_PATH //视频缓存路径
    private val CACHE_VIDEO_PATH_COVER = com.jw.library.UploadConfig.CACHE_VIDEO_PATH_COVER //视频缓存路径
    private val CACHE_IMG_PATH = com.jw.library.UploadConfig.CACHE_IMG_PATH //图片缓存路径
    private var picturePath: String? = null
    private var pictureFileName: String? = null

    override fun getLayoutId() = R.layout.activity_camera

    override fun doConfig(arguments: Intent) {
        CameraInterface.mWidth = ThemeUtils.getWindowHeight(this)
        releaseFolder()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_camera)
        jCameraView = findViewById<View>(R.id.jcameraview) as JCameraView
        //设置视频保存路径
        jCameraView!!.setSaveVideoPath(CACHE_VIDEO_PATH)
        jCameraView!!.setFeatures(com.jw.library.UploadConfig.SHOT_TYPE)
        if (com.jw.library.UploadConfig.SHOT_MODEL == 2) {
            when (com.jw.library.UploadConfig.SHOT_TYPE) {
                JCameraView.BUTTON_STATE_BOTH -> jCameraView!!.setTip("轻触拍照，按住摄像")
                JCameraView.BUTTON_STATE_ONLY_CAPTURE -> jCameraView!!.setTip("轻触拍照")
                JCameraView.BUTTON_STATE_ONLY_RECORDER -> jCameraView!!.setTip("按住摄像")
            }
        }
        jCameraView!!.setMediaQuality(JCameraView.MEDIA_QUALITY_HIGH)
        //JCameraView监听
        jCameraView!!.setJCameraLisenter(object : JCameraListener {
            override fun captureEdit(bitmap: Bitmap) {
                if (pictureFileName == null) {
                    pictureFileName = "picture_" + System.currentTimeMillis() + ".jpg"
                }
                picturePath = FileUtils.saveBitmap(CACHE_IMG_PATH, pictureFileName, bitmap)
                goCrop(picturePath!!)
            }

            override fun captureSuccess(bitmap: Bitmap) {
                if (pictureFileName == null) {
                    pictureFileName = "picture_" + System.currentTimeMillis() + ".jpg"
                }
                picturePath = FileUtils.saveBitmap(CACHE_IMG_PATH, pictureFileName, bitmap)
                val imageItem = ImageItem()
                imageItem.path = picturePath
                backCapture(imageItem)
                val uri = BitmapUtil.bitmap2Uri(bitmap, this@ShotRecordMainActivity)
                ImagePicker.galleryAddPic(this@ShotRecordMainActivity, uri)
            }

            override fun recordSuccess(videoPath: String, cover: Bitmap) {
                val coverName = "cover_" + System.currentTimeMillis() + ".jpg"
                val coverPath = FileUtils.saveBitmap(CACHE_VIDEO_PATH_COVER, coverName, cover)
                val videoItem = VideoItem()
                videoItem.thumbPath = coverPath
                videoItem.path = videoPath
                backRecord(videoItem)
            }

            override fun recordEdit(videoPath: String, cover: Bitmap) {
                val coverName = "cover_" + System.currentTimeMillis() + ".png"
                VideoTrimmerActivity.call(this@ShotRecordMainActivity, videoPath, coverName)
            }
        })

        jCameraView!!.setLeftClickListener(object : ClickListener {
            override fun onClick() {
                finish()
            }
        })
    }


    private var jCameraView: JCameraView? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data?.extras != null) {
            when (requestCode) {
                //从图片编辑页面返回
                ImagePicker.REQUEST_CODE_ITEM_CROP -> {
                    val resultUri =
                        data.getParcelableExtra(ImagePicker.EXTRA_CROP_ITEM_OUT_URI) as Uri
                    val cropBitmap = BitmapFactory.decodeFile(resultUri.path)
                    picturePath = FileUtils.saveBitmap(CACHE_IMG_PATH, pictureFileName, cropBitmap)
                    if (pictureFileName == null) {
                        pictureFileName = "picture_" + System.currentTimeMillis() + ".jpg"
                    }
                    val path = picturePath
                    val imageItem = ImageItem()
                    imageItem.path = path
                    backCapture(imageItem)
                }
                //从视频编辑页面返回
                VideoPicker.REQUEST_CODE_ITEM_CROP -> {
                    val path = data.getStringExtra(VideoPicker.EXTRA_CROP_ITEM_OUT_URI)
                    val thumbPath = data.getStringExtra("thumbPath")
                    val duration = data.getLongExtra("duration", 0)
                    val videoItem = VideoItem()
                    videoItem.path = path
                    videoItem.thumbPath = thumbPath
                    videoItem.duration = duration
                    videoItem.name = path.split("cache/")[1]
                    backRecord(videoItem)
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

    private fun backCapture(imageItem: ImageItem) {
        val imageItems = ArrayList<ImageItem>()
        imageItems.add(imageItem)
        val intent = Intent()
        intent.putExtra(EXTRA_ITEMS, imageItems)
        intent.putExtra("isImage", true)
        this.setResult(ImagePicker.RESULT_CODE_ITEMS, intent)
        this.finish()
    }

    private fun backRecord(videoItem: VideoItem) {
        val videoItems = ArrayList<VideoItem>()
        videoItems.add(videoItem)
        val intent = Intent()
        intent.putExtra(VideoPicker.EXTRA_ITEMS, videoItems)
        intent.putExtra("isImage", false)
        this.setResult(VideoPicker.RESULT_CODE_ITEMS, intent)
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

    fun releaseFolder() {
        val folder = File(CACHE_IMG_PATH)
        if (!folder.exists()) {
            folder.mkdirs()
        }
        val folder2 = File(CACHE_VIDEO_PATH)
        if (!folder2.exists()) {
            folder2.mkdirs()
        }
        val folder3 = File(CACHE_VIDEO_PATH_COVER)
        if (!folder3.exists()) {
            folder3.mkdirs()
        }

    }
}
