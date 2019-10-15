package com.jw.croplibrary.video

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.text.TextUtils
import android.view.View
import com.jw.croplibrary.CropConfig
import com.jw.croplibrary.R
import com.jw.croplibrary.databinding.ActivityVideoTrimBinding
import com.jw.library.ui.BaseBindingActivity
import com.jw.library.utils.ThemeUtils
import kotlinx.android.synthetic.main.activity_video_trim.*
import kotlinx.android.synthetic.main.include_top_bar.view.*
import java.io.File

/**
 * Author：J.Chou
 * Date：  2016.08.01 2:23 PM
 * Email： who_know_me@163.com
 * Describe:
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
            top_bar.apply {
                rl_tool_bar_bg.setBackgroundColor(Color.BLACK)
                tv_des.text = null
                btn_ok.text = "确定"
            }
            trimmerView.apply {
                setOnTrimVideoListener(this@VideoTrimmerActivity)
                initVideoByURI(Uri.parse(path))
                setOnClickListener({ v -> initUI() })
            }
            clickListener = View.OnClickListener {
                when (it.id) {
                    R.id.btn_back -> trimmerView.onCancelClicked()
                    R.id.btn_ok -> trimmerView.onSaveClicked()
                }
            }
            setConfirmButtonBg(top_bar.btn_ok)
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

    override fun onFinishTrim(`in`: String) {
        if (mProgressDialog!!.isShowing) mProgressDialog!!.dismiss()
        Thread {
            val thumbPath = FfmpegUtil.getVideoPhoto(
                `in`,
                intent.getStringExtra("videoName")
            )
            val duration = FfmpegUtil.getVideoDuration(`in`)
            val intent = Intent()
            intent.putExtra(CropConfig.EXTRA_CROP_ITEM_OUT_URI, `in`)
            intent.putExtra("thumbPath", thumbPath)
            intent.putExtra("duration", duration)
            setResult(-1, intent)
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

    fun releaseFolder() {
        val folder = File(CropConfig.CACHE_VIDEO_CROP)
        if (!folder.exists()) {
            folder.mkdirs()
        }
        val folder2 = File(CropConfig.CACHE_VIDEO_CROP_COVER)
        if (!folder2.exists()) {
            folder2.mkdirs()
        }
        CropConfig.cropVideoCacheFolder = folder
    }

    companion object {

        private val TAG = "jason"
        private val VIDEO_PATH_KEY = "video-file-path"
        private val COMPRESSED_VIDEO_FILE_NAME = "compress.mp4"

        fun call(from: FragmentActivity, videoPath: String, videoName: String) {
            if (!TextUtils.isEmpty(videoPath)) {
                val bundle = Bundle()
                bundle.putString(VIDEO_PATH_KEY, videoPath)
                bundle.putString("videoName", videoName)
                val intent = Intent(from, VideoTrimmerActivity::class.java)
                intent.putExtras(bundle)
                from.startActivityForResult(intent, CropConfig.RESULT_CODE_ITEM_CROP)
            }
        }
    }
}
