package com.jw.galary.video.trim

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.text.TextUtils
import android.view.View
import com.jw.galary.video.VideoPicker
import com.jw.uploaddemo.R
import com.jw.uploaddemo.base.utils.ThemeUtils
import com.jw.uploaddemo.databinding.ActivityVideoTrimBinding
import com.jw.uploaddemo.uploadPlugin.UploadPluginBindingActivity

/**
 * Author：J.Chou
 * Date：  2016.08.01 2:23 PM
 * Email： who_know_me@163.com
 * Describe:
 */
class VideoTrimmerActivity : UploadPluginBindingActivity<ActivityVideoTrimBinding>(),
    VideoTrimListener {

    private var mProgressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                setOnClickListener({ v -> initUI() })
            }
            clickListener = View.OnClickListener {
                when (it.id) {
                    R.id.btn_back -> trimmerView.onCancelClicked()
                    R.id.btn_ok -> trimmerView.onSaveClicked()
                }
            }
        }
        setConfirmButtonBg(mBinding.topBar.btnOk)
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
            val thumbPath = FfmpegUtil.getVideoPhoto(`in`, intent.getStringExtra("videoName"))
            val duration = FfmpegUtil.getVideoDuration(`in`)
            val intent = Intent()
            intent.putExtra(VideoPicker.EXTRA_CROP_ITEM_OUT_URI, `in`)
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
                from.startActivityForResult(intent, VideoPicker.RESULT_CODE_ITEM_CROP)
            }
        }
    }
}
