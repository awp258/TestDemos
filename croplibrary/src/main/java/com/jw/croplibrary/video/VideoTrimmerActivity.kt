package com.jw.croplibrary.video

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import com.jw.croplibrary.CropLibrary
import com.jw.croplibrary.R
import com.jw.croplibrary.databinding.ActivityVideoTrimBinding
import com.jw.library.ui.BaseBindingActivity
import com.jw.library.utils.FileUtils
import com.jw.library.utils.ThemeUtils
import com.jw.library.utils.VideoUtil
import java.io.File

/**
 * 创建时间：
 * 更新时间
 * 版本：
 * 作者：Mr.jin
 * 描述：视频裁剪Activity
 */
class VideoTrimmerActivity : BaseBindingActivity<ActivityVideoTrimBinding>(),
    VideoTrimListener {

    private var mProgressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        releaseFolder()
        ThemeUtils.changeStatusBar(this, Color.parseColor("#000000"))
        try {
            initUI()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun getLayoutId() = R.layout.activity_video_trim

    fun initUI() {
        val bd = intent.extras
        var path: String? = ""
        if (bd != null) path = bd.getString(VIDEO_PATH_KEY)
        mBinding.apply {
            topBar.apply {
                rlToolBarBg.setBackgroundColor(Color.BLACK)
                tvDes.text = null
                btnOk.text = "确定"
            }
            trimmerView.apply {
                setOnTrimVideoListener(this@VideoTrimmerActivity)
                initVideoByURI(Uri.parse(path))
                setOnClickListener { initUI() }
            }
            clickListener = View.OnClickListener {
                when (it.id) {
                    R.id.btn_back -> trimmerView.onCancelClicked()
                    R.id.btn_ok -> trimmerView.onSaveClicked()
                }
            }
            setConfirmButtonBg(topBar.btnOk)
        }
    }

    public override fun onResume() {
        super.onResume()
    }

    public override fun onPause() {
        super.onPause()
        mBinding.trimmerView.onVideoPause()
        mBinding.trimmerView.setRestoreState(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.trimmerView.onDestroy()
    }

    override fun onStartTrim() {
        buildDialog(resources.getString(R.string.trimming)).show()
    }

    override fun onFinishTrim(videoPath: String) {
        if (mProgressDialog!!.isShowing) mProgressDialog!!.dismiss()
        Thread {
            val thumbPath = FfmpegUtil.getVideoPhoto(
                videoPath,
                intent.getStringExtra("videoName")
            )
            val duration = FfmpegUtil.getVideoDuration(videoPath)
            val intent = Intent()
            intent.putExtra(CropLibrary.EXTRA_CROP_ITEM_OUT_URI, Uri.fromFile(File(videoPath)))
            intent.putExtra("thumbPath", thumbPath)
            intent.putExtra("duration", duration)
            setResult(Activity.RESULT_OK, intent)
            val uri = VideoUtil.saveToGalary(this, videoPath, duration)
            CropLibrary.galleryAddMedia(this, uri.path!!)
            finish()
        }.start()

        //TODO: please handle your trimmed video url here!!!
        //String out = StorageUtil.getCacheDir() + File.separator + COMPRESSED_VIDEO_FILE_NAME;
        //buildDialog(getResources().getString(R.string.compressing)).show();
        //VideoCompressor.compress(this, in, out, new VideoCompressListener() {
        //  @Override public void onSuccess(String message) {
        //  }
        //
        //  @Override public void onFailure(String message) {
        //  }
        //
        //  @Override public void onFinish() {
        //    if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
        //    finish();
        //  }
        //});
    }

    override fun onCancel() {
        mBinding.trimmerView.onDestroy()
        finish()
    }

    private fun buildDialog(msg: String): ProgressDialog {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(this, "", msg)
        }
        mProgressDialog!!.setMessage(msg)
        return mProgressDialog!!
    }

    private fun releaseFolder() {
        FileUtils.releaseFolder(CropLibrary.CACHE_VIDEO_CROP!!)
        FileUtils.releaseFolder(CropLibrary.CACHE_VIDEO_CROP_COVER!!)
    }

    companion object {
        const val REQUEST_CODE_ITEM_CROP = 3002
        private val VIDEO_PATH_KEY = "video-file-path"

        fun start(activity: AppCompatActivity, videoPath: String, videoName: String) {
            if (!TextUtils.isEmpty(videoPath)) {
                val bundle = Bundle()
                bundle.putString(VIDEO_PATH_KEY, videoPath)
                bundle.putString("videoName", videoName)
                val intent = Intent(activity, VideoTrimmerActivity::class.java)
                intent.putExtras(bundle)
                activity.startActivityForResult(intent, REQUEST_CODE_ITEM_CROP)
            }
        }
    }
}
