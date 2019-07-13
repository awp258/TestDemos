package com.jw.galary.video.trim;

import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.widget.TextView;
import com.jw.uploaddemo.R;
import com.jw.uploaddemo.base.utils.ThemeUtils;
import com.jw.uploaddemo.databinding.ActivityVideoTrimBinding;
import com.jw.uploaddemo.uploadPlugin.UploadPluginActivity;

import static com.jw.galary.video.VideoPicker.EXTRA_CROP_VIDEOOUT_URI;
import static com.jw.galary.video.VideoPicker.REQUEST_CODE_VIDEO_CROP;

/**
 * Author：J.Chou
 * Date：  2016.08.01 2:23 PM
 * Email： who_know_me@163.com
 * Describe:
 */
public class VideoTrimmerActivity extends UploadPluginActivity implements VideoTrimListener {

    private static final String TAG = "jason";
    private static final String VIDEO_PATH_KEY = "video-file-path";
    private static final String COMPRESSED_VIDEO_FILE_NAME = "compress.mp4";
    private ActivityVideoTrimBinding mBinding;
    private ProgressDialog mProgressDialog;

    public static void call(FragmentActivity from, String videoPath, String videoName) {
        if (!TextUtils.isEmpty(videoPath)) {
            Bundle bundle = new Bundle();
            bundle.putString(VIDEO_PATH_KEY, videoPath);
            bundle.putString("videoName", videoName);
            Intent intent = new Intent(from, VideoTrimmerActivity.class);
            intent.putExtras(bundle);
            from.startActivityForResult(intent, REQUEST_CODE_VIDEO_CROP);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.changeStatusBar(this, Color.parseColor("#000000"));
        try {
            initUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initUI() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_video_trim);
        mBinding.topBar.findViewById(R.id.rl_tool_bar_bg).setBackgroundColor(Color.BLACK);
        setConfirmButtonBg(mBinding.topBar.findViewById(R.id.btn_ok));
        ((TextView) mBinding.topBar.findViewById(R.id.tv_des)).setText(null);
        mBinding.topBar.findViewById(R.id.btn_back).setOnClickListener(v -> mBinding.trimmerView.onCancelClicked());
        mBinding.topBar.findViewById(R.id.btn_ok).setOnClickListener(v -> mBinding.trimmerView.onSaveClicked());
        ((TextView) mBinding.topBar.findViewById(R.id.btn_ok)).setText("确定");
        Bundle bd = getIntent().getExtras();
        String path = "";
        if (bd != null) path = bd.getString(VIDEO_PATH_KEY);
        if (mBinding.trimmerView != null) {
            mBinding.trimmerView.setOnTrimVideoListener(this);
            mBinding.trimmerView.initVideoByURI(Uri.parse(path));
        }
        mBinding.trimmerView.findViewById(R.id.tv_reset).setOnClickListener(v -> initUI());
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mBinding.trimmerView.onVideoPause();
        mBinding.trimmerView.setRestoreState(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBinding.trimmerView.onDestroy();
    }

    @Override
    public void onStartTrim() {
        buildDialog(getResources().getString(R.string.trimming)).show();
    }

    @Override
    public void onFinishTrim(String in) {
        if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
        new Thread(() -> {
            String thumbPath = FfmpegUtil.getVideoPhoto(in, getIntent().getStringExtra("videoName"));
            long duration = FfmpegUtil.getVideoDuration(in);
            Intent intent = new Intent();
            intent.putExtra(EXTRA_CROP_VIDEOOUT_URI, in);
            intent.putExtra("thumbPath", thumbPath);
            intent.putExtra("duration", duration);
            setResult(-1, intent);
            finish();
        }).start();

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

    @Override
    public void onCancel() {
        mBinding.trimmerView.onDestroy();
        finish();
    }

    private ProgressDialog buildDialog(String msg) {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(this, "", msg);
        }
        mProgressDialog.setMessage(msg);
        return mProgressDialog;
    }
}
