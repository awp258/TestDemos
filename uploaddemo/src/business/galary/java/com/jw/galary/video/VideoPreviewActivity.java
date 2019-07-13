

package com.jw.galary.video;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import com.jw.galary.img.util.NavigationBarChangeListener;
import com.jw.galary.img.util.Utils;
import com.jw.galary.img.view.SuperCheckBox;
import com.jw.galary.video.trim.VideoTrimmerActivity;
import com.jw.uploaddemo.ColorCofig;
import com.jw.uploaddemo.R;
import com.jw.uploaddemo.base.utils.ThemeUtils;

import java.io.File;
import java.util.List;

import static com.jw.galary.video.VideoPicker.*;

public class VideoPreviewActivity extends VideoPreviewBaseActivity implements VideoPicker.OnVideoSelectedListener, OnClickListener, OnCheckedChangeListener {
    public static final String ISORIGIN = "isOrigin";
    private SuperCheckBox mCbCheck;
    private Button mBtnOk;
    private View bottomBar;
    private View marginView;

    public VideoPreviewActivity() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.imagePicker.addOnVideoSelectedListener(this);
        this.mBtnOk = this.findViewById(R.id.btn_ok);
        this.mBtnOk.setVisibility(View.VISIBLE);
        this.setConfirmButtonBg(this.mBtnOk);
        this.mBtnOk.setOnClickListener(this);
        this.findViewById(R.id.btn_back).setOnClickListener(this);
        this.bottomBar = this.findViewById(R.id.bottom_bar);
        this.bottomBar.setVisibility(View.VISIBLE);
        TextView tvPreviewEdit = this.findViewById(R.id.tv_preview_edit);
        tvPreviewEdit.setOnClickListener(this);
        this.mCbCheck = this.findViewById(R.id.cb_check);
        this.marginView = this.findViewById(R.id.margin_bottom);
        this.onVideoSelected(0, null, false);
        VideoItem item = this.mImageItems.get(this.mCurrentPosition);
        boolean isSelected = this.imagePicker.isSelect(item);
        this.mTitleCount.setText(this.getString(R.string.ip_preview_video_count, this.mCurrentPosition + 1, this.mImageItems.size()));
        this.mCbCheck.setChecked(isSelected);
        this.mViewPager.addOnPageChangeListener(new SimpleOnPageChangeListener() {
            public void onPageSelected(int position) {
                VideoPreviewActivity.this.mCurrentPosition = position;
                VideoItem item = VideoPreviewActivity.this.mImageItems.get(VideoPreviewActivity.this.mCurrentPosition);
                boolean isSelected = VideoPreviewActivity.this.imagePicker.isSelect(item);
                VideoPreviewActivity.this.mCbCheck.setChecked(isSelected);
                VideoPreviewActivity.this.mTitleCount.setText(VideoPreviewActivity.this.getString(R.string.ip_preview_video_count, new Object[]{VideoPreviewActivity.this.mCurrentPosition + 1, VideoPreviewActivity.this.mImageItems.size()}));
                VideoPreviewActivity.this.thumbPreviewAdapter.setSelected(item);
            }
        });
        this.mCbCheck.setOnClickListener(v -> {
            VideoItem imageItem = VideoPreviewActivity.this.mImageItems.get(VideoPreviewActivity.this.mCurrentPosition);
            int selectLimit = VideoPreviewActivity.this.imagePicker.getSelectLimit();
            if (VideoPreviewActivity.this.mCbCheck.isChecked() && VideoPreviewActivity.this.selectedImages.size() >= selectLimit) {
                Toast.makeText(VideoPreviewActivity.this, VideoPreviewActivity.this.getString(R.string.ip_select_limit, selectLimit), Toast.LENGTH_SHORT).show();
                VideoPreviewActivity.this.mCbCheck.setChecked(false);
            } else {
                int changPosition = VideoPreviewActivity.this.imagePicker.getSelectVideoCount();
                if (!VideoPreviewActivity.this.mCbCheck.isChecked()) {
                    changPosition = VideoPreviewActivity.this.imagePicker.getSelectedVideos().indexOf(imageItem);
                    VideoPreviewActivity.this.thumbPreviewAdapter.notifyItemRemoved(changPosition);
                } else {
                    VideoPreviewActivity.this.thumbPreviewAdapter.notifyItemInserted(changPosition);
                }

                VideoPreviewActivity.this.imagePicker.addSelectedVideoItem(VideoPreviewActivity.this.mCurrentPosition, imageItem, VideoPreviewActivity.this.mCbCheck.isChecked());
            }

        });
        NavigationBarChangeListener.with(this).setListener(new NavigationBarChangeListener.OnSoftInputStateChangeListener() {
            public void onNavigationBarShow(int orientation, int height) {
                VideoPreviewActivity.this.marginView.setVisibility(View.VISIBLE);
                LayoutParams layoutParams = VideoPreviewActivity.this.marginView.getLayoutParams();
                if (layoutParams.height == 0) {
                    layoutParams.height = Utils.getNavigationBarHeight(VideoPreviewActivity.this);
                    VideoPreviewActivity.this.marginView.requestLayout();
                }

            }

            public void onNavigationBarHide(int orientation) {
                VideoPreviewActivity.this.marginView.setVisibility(View.GONE);
            }
        });
/*        NavigationBarChangeListener.with(this, 2).setListener(new OnSoftInputStateChangeListener() {
            public void onNavigationBarShow(int orientation, int height) {
                VideoPreviewActivity.this.topBar.setPadding(0, 0, height, 0);
                VideoPreviewActivity.this.bottomBar.setPadding(0, 0, height, 0);
            }

            public void onNavigationBarHide(int orientation) {
                VideoPreviewActivity.this.topBar.setPadding(0, 0, 0, 0);
                VideoPreviewActivity.this.bottomBar.setPadding(0, 0, 0, 0);
            }
        });*/
        //this.topBar.setBackgroundColor(Color.parseColor(this.imagePicker.getViewColor().getNaviBgColor()));
        this.bottomBar.setBackgroundColor(Color.parseColor(ColorCofig.INSTANCE.getToolbarBgColor()));
        this.mTitleCount.setTextColor(Color.parseColor(ColorCofig.INSTANCE.getNaviTitleColor()));
        tvPreviewEdit.setTextColor(Color.parseColor(ColorCofig.INSTANCE.getToolbarTitleColorNormal()));
        this.mCbCheck.setTextColor(Color.parseColor(ColorCofig.INSTANCE.getToolbarTitleColorNormal()));
    }

    @Override
    public void onImageSingleTap(VideoItem videoItem) {
        if (this.topBar.getVisibility() == View.VISIBLE) {
/*            this.topBar.setAnimation(AnimationUtils.loadAnimation(this, com.rxxb.imagepicker.R.anim.top_out));
            this.bottomBar.setAnimation(AnimationUtils.loadAnimation(this, com.rxxb.imagepicker.R.anim.fade_out));*/
            this.topBar.setVisibility(View.GONE);
            this.bottomBar.setVisibility(View.GONE);
            ThemeUtils.changeStatusBar(this, Color.BLACK);
        } else {
/*            this.topBar.setAnimation(AnimationUtils.loadAnimation(this, com.rxxb.imagepicker.R.anim.top_in));
            this.bottomBar.setAnimation(AnimationUtils.loadAnimation(this, com.rxxb.imagepicker.R.anim.fade_in));*/
            this.topBar.setVisibility(View.VISIBLE);
            this.bottomBar.setVisibility(View.VISIBLE);
            ThemeUtils.changeStatusBar(this, Color.parseColor("#393A3F"));
        }

    }

    @Override
    public void onStartVideo(VideoItem videoItem) {
        openFile(new File(videoItem.path));
    }


    @Override
    public void onVideoSelected(int var1, VideoItem videoItem, boolean isAdd) {
        if (this.imagePicker.getSelectVideoCount() > 0) {
            this.mBtnOk.setText(this.getString(R.string.ip_select_complete, this.imagePicker.getSelectVideoCount(), this.imagePicker.getSelectLimit()));
        } else {
            this.mBtnOk.setText(this.getString(R.string.ip_complete));
        }

        if (isAdd) {
            this.thumbPreviewAdapter.setSelected(videoItem);
        }
    }

    public void onClick(View v) {
        int id = v.getId();
        Intent intent;
        if (id == R.id.btn_ok) {
            List<VideoItem> videoItems = this.imagePicker.getSelectedVideos();
            for (VideoItem videoItem : videoItems) {
                if (videoItem.duration > VideoDataSource.MAX_LENGTH) {
                    Toast.makeText(this, "您选中的视频时长不能超过60秒，请裁剪！", Toast.LENGTH_SHORT).show();
                    intent = new Intent(this, VideoPreviewActivity.class);
                    intent.putExtra(EXTRA_SELECTED_VIDEO_POSITION, 0);
                    intent.putExtra(EXTRA_VIDEO_ITEMS, this.imagePicker.getSelectedVideos());
                    intent.putExtra(EXTRA_FROM_VIDEO_ITEMS, true);
                    this.startActivityForResult(intent, VideoPicker.REQUEST_CODE_VIDEO_PREVIEW);
                    return;
                }
            }
            if (this.imagePicker.getSelectedVideos().size() == 0) {
                this.mCbCheck.setChecked(true);
                VideoItem imageItem = this.mImageItems.get(this.mCurrentPosition);
                this.imagePicker.addSelectedVideoItem(this.mCurrentPosition, imageItem, this.mCbCheck.isChecked());
            }
            //直接上传
            intent = new Intent();
            intent.putExtra(EXTRA_VIDEO_ITEMS, this.imagePicker.getSelectedVideos());
            this.setResult(VideoPicker.RESULT_CODE_VIDEO_ITEMS, intent);
            this.finish();
        } else if (id == R.id.btn_back) {
            intent = new Intent();
            this.setResult(VideoPicker.RESULT_CODE_VIDEO_BACK, intent);
            this.finish();
        } else if (id == R.id.tv_preview_edit) {
            VideoTrimmerActivity.call(this, this.mImageItems.get(this.mCurrentPosition).path, this.mImageItems.get(this.mCurrentPosition).name);
        }

    }

    public void onBackPressed() {
        Intent intent = new Intent();
        this.setResult(VideoPicker.RESULT_CODE_VIDEO_BACK, intent);
        this.finish();
        super.onBackPressed();
    }

    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();


    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && data.getExtras() != null) {
            if (resultCode == -1 && requestCode == VideoPicker.REQUEST_CODE_VIDEO_CROP) {
                String path = data.getStringExtra(EXTRA_CROP_VIDEOOUT_URI);
                String thumbPath = data.getStringExtra("thumbPath");
                long duration = data.getLongExtra("duration", 0);
                if (path != null) {
                    int fromSelectedPosition = -1;
                    for (int i = 0; i < this.selectedImages.size(); ++i) {
                        if (this.selectedImages.get(i).path.equals(this.mImageItems.get(this.mCurrentPosition).path)) {
                            fromSelectedPosition = i;
                            break;
                        }
                    }
                    VideoItem imageItem = new VideoItem();
                    imageItem.path = path;
                    imageItem.thumbPath = thumbPath;
                    imageItem.duration = duration;
                    if (fromSelectedPosition != -1) {
                        this.imagePicker.addSelectedVideoItem(fromSelectedPosition, this.selectedImages.get(fromSelectedPosition), false);
                        this.imagePicker.addSelectedVideoItem(fromSelectedPosition, imageItem, true);
                    }
                    if (this.isFromItems) {
                        this.mImageItems.remove(this.mCurrentPosition);
                    }
                    this.mImageItems.add(this.mCurrentPosition, imageItem);
                    this.mAdapter.notifyDataSetChanged();
                }
            }
        }
    }


    /**
     * 打开文件
     *
     * @param file
     */
    private void openFile(File file) {

        Intent intent = new Intent();
        // 这是比较流氓的方法，绕过7.0的文件权限检查
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        //设置intent的Action属性
        intent.setAction(Intent.ACTION_VIEW);
        //获取文件file的MIME类型
        String type = Utils.getMIMEType(file);
        //设置intent的data和Type属性。
        intent.setDataAndType(/*uri*/Uri.fromFile(file), type);
        //跳转
        startActivity(intent);

    }

    protected void onDestroy() {
        this.imagePicker.removeOnVideoSelectedListener(this);
        super.onDestroy();
    }

}
