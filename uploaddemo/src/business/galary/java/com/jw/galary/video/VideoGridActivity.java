

package com.jw.galary.video;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import com.jw.galary.img.util.Utils;
import com.jw.galary.img.view.FolderPopUpWindow;
import com.jw.galary.img.view.GridSpacingItemDecoration;
import com.jw.uploaddemo.ColorCofig;
import com.jw.uploaddemo.R;
import com.jw.uploaddemo.uploadPlugin.UploadPluginActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.jw.galary.video.VideoPicker.*;

public class VideoGridActivity extends UploadPluginActivity implements VideoDataSource.OnVideosLoadedListener, VideoRecyclerAdapter.OnVideoItemClickListener, VideoPicker.OnVideoSelectedListener, OnClickListener, OnCheckedChangeListener {
    public static String CACHE_VIDEO_CROP; //视频缓存路径
    public static final int REQUEST_PERMISSION_STORAGE = 1;
    public static final int REQUEST_PERMISSION_CAMERA = 2;
    public static final String EXTRAS_TAKE_PICKERS = "TAKE";
    public static final String EXTRAS_IMAGES = "IMAGES";
    public static final int SPAN_COUNT = 4;
    private VideoPicker videoPicker;
    private View mFooterBar;
    private Button mBtnOk;
    private View mllDir;
    private TextView mtvDir;
    private TextView mBtnPre;
    private VideoFolderAdapter mVideoFolderAdapter;
    private FolderPopUpWindow mFolderPopupWindow;
    private List<VideoFolder> mVideoFolders;
    private boolean directPhoto = false;
    private RecyclerView mRecyclerView;
    private VideoRecyclerAdapter mRecyclerAdapter;

    public VideoGridActivity() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        releaseFolder();
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_video_grid);
        this.videoPicker = VideoPicker.getInstance();
        this.videoPicker.clear();
        this.videoPicker.addOnVideoSelectedListener(this);
        Intent data = this.getIntent();
        if (data != null && data.getExtras() != null) {
            this.directPhoto = data.getBooleanExtra(EXTRAS_TAKE_PICKERS, false);
            if (this.directPhoto) {
                if (!this.checkPermission("android.permission.CAMERA")) {
                    ActivityCompat.requestPermissions(this, new String[]{"android.permission.CAMERA"}, REQUEST_PERMISSION_CAMERA);
                } else {
                    this.videoPicker.takePicture(this, VideoPicker.REQUEST_CODE_VIDEO_TAKE);
                }
            }

            ArrayList<VideoItem> videos = (ArrayList) data.getSerializableExtra(EXTRAS_IMAGES);
            this.videoPicker.setSelectedVideos(videos);
        }
        ((TextView) findViewById(R.id.tv_des)).setText("视频");
        this.mRecyclerView = this.findViewById(R.id.recycler);
        this.findViewById(R.id.btn_back).setOnClickListener(this);
        this.mBtnOk = findViewById(R.id.btn_ok);
        this.mBtnOk.setOnClickListener(this);
        this.mBtnPre = this.findViewById(R.id.btn_preview);
        this.mBtnPre.setOnClickListener(this);
        this.mFooterBar = this.findViewById(R.id.footer_bar);
        this.mllDir = this.findViewById(R.id.ll_dir);
        this.mllDir.setOnClickListener(this);
        this.mtvDir = this.findViewById(R.id.tv_dir);
        this.mBtnOk.setVisibility(View.VISIBLE);
        this.mBtnPre.setVisibility(View.VISIBLE);
        if (this.videoPicker.isMultiMode()) {
            this.mBtnOk.setVisibility(View.VISIBLE);
            this.mBtnPre.setVisibility(View.VISIBLE);
        } else {
            this.mBtnOk.setVisibility(View.GONE);
            this.mBtnPre.setVisibility(View.GONE);
        }

        this.setConfirmButtonBg(this.mBtnOk);
        this.findViewById(R.id.top_bar).setBackgroundColor(Color.parseColor(ColorCofig.INSTANCE.getNaviBgColor()));
        ((TextView) this.findViewById(R.id.tv_des)).setTextColor(Color.parseColor(ColorCofig.INSTANCE.getNaviBgColor()));
        this.mFooterBar.setBackgroundColor(Color.parseColor(ColorCofig.INSTANCE.getToolbarBgColor()));
        this.mBtnPre.setTextColor(Color.parseColor(ColorCofig.INSTANCE.getToolbarTitleColorDisabled()));
        this.mtvDir.setTextColor(Color.parseColor(ColorCofig.INSTANCE.getToolbarTitleColorNormal()));
        this.mVideoFolderAdapter = new VideoFolderAdapter(this, null);
        this.mRecyclerAdapter = new VideoRecyclerAdapter(this, null);
        this.onVideoSelected(0, null, false);
        if (this.checkPermission("android.permission.WRITE_EXTERNAL_STORAGE")) {
            new VideoDataSource(this, null, this);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, REQUEST_PERMISSION_STORAGE);
        }

    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == 0) {
                new VideoDataSource(this, null, this);
            } else {
                this.showToast("权限被禁止，无法选择本地视频");
            }
        } else if (requestCode == REQUEST_PERMISSION_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == 0) {
                this.videoPicker.takePicture(this, VideoPicker.REQUEST_CODE_VIDEO_TAKE);
            } else {
                this.showToast("权限被禁止，无法打开相机");
            }
        }

    }

    protected void onDestroy() {
        this.videoPicker.removeOnVideoSelectedListener(this);
        super.onDestroy();
    }

    public void onClick(View v) {
        int id = v.getId();
        Intent intent;
        if (id == R.id.btn_ok) {
            List<VideoItem> videoItems = this.videoPicker.getSelectedVideos();
            for (VideoItem videoItem : videoItems) {
                if (videoItem.duration > VideoDataSource.MAX_LENGTH) {
                    Toast.makeText(this, "您选中的视频时长不能超过60秒，请裁剪！", Toast.LENGTH_SHORT).show();
                    intent = new Intent(this, VideoPreviewActivity.class);
                    intent.putExtra(EXTRA_SELECTED_VIDEO_POSITION, 0);
                    intent.putExtra(EXTRA_VIDEO_ITEMS, this.videoPicker.getSelectedVideos());
                    intent.putExtra(EXTRA_FROM_VIDEO_ITEMS, true);
                    this.startActivityForResult(intent, VideoPicker.REQUEST_CODE_VIDEO_PREVIEW);
                    return;
                }
            }
            intent = new Intent();
            intent.putExtra(EXTRA_VIDEO_ITEMS, this.videoPicker.getSelectedVideos());
            this.setResult(VideoPicker.RESULT_CODE_VIDEO_ITEMS, intent);
            this.finish();
        } else if (id == R.id.ll_dir) {
            if (this.mVideoFolders == null) {
                Log.i("VideoGridActivity", "您的手机没有视频");
                return;
            }

            this.createPopupFolderList();
            this.mVideoFolderAdapter.refreshData(this.mVideoFolders);
            if (this.mFolderPopupWindow.isShowing()) {
                this.mFolderPopupWindow.dismiss();
            } else {
                this.mFolderPopupWindow.showAtLocation(this.mFooterBar, 0, 0, 0);
                int index = this.mVideoFolderAdapter.getSelectIndex();
                index = index == 0 ? index : index - 1;
                this.mFolderPopupWindow.setSelection(index);
            }
        } else if (id == R.id.btn_preview) {
            intent = new Intent(this, VideoPreviewActivity.class);
            intent.putExtra(EXTRA_SELECTED_VIDEO_POSITION, 0);
            intent.putExtra(EXTRA_VIDEO_ITEMS, this.videoPicker.getSelectedVideos());
            intent.putExtra(EXTRA_FROM_VIDEO_ITEMS, true);
            this.startActivityForResult(intent, VideoPicker.REQUEST_CODE_VIDEO_PREVIEW);
        } else if (id == R.id.btn_back) {
            this.finish();
        }

    }

    private void createPopupFolderList() {
        this.mFolderPopupWindow = new FolderPopUpWindow(this, this.mVideoFolderAdapter);
        this.mFolderPopupWindow.setOnItemClickListener((adapterView, view, position, l) -> {
            VideoGridActivity.this.mVideoFolderAdapter.setSelectIndex(position);
            VideoGridActivity.this.videoPicker.setCurrentVideoFolderPosition(position);
            VideoGridActivity.this.mFolderPopupWindow.dismiss();
            VideoFolder videoFolder = (VideoFolder) adapterView.getAdapter().getItem(position);
            if (null != videoFolder) {
                VideoGridActivity.this.mRecyclerAdapter.refreshData(videoFolder.videos);
                VideoGridActivity.this.mtvDir.setText(videoFolder.name);
            }

        });
        this.mFolderPopupWindow.setMargin(this.mFooterBar.getHeight());
    }

    @Override
    public void onVideosLoaded(List<VideoFolder> videoFolders) {
        this.mVideoFolders = videoFolders;
        this.videoPicker.setVideoFolders(videoFolders);
        if (videoFolders.size() == 0) {
            this.mRecyclerAdapter.refreshData(null);
        } else {
            this.mRecyclerAdapter.refreshData(videoFolders.get(0).videos);
        }

        this.mRecyclerAdapter.setOnVideoItemClickListener(this);
        this.mRecyclerView.setLayoutManager(new GridLayoutManager(this, SPAN_COUNT));
        this.mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(SPAN_COUNT, Utils.dp2px(this, 2.0F), false));
        this.mRecyclerView.setAdapter(this.mRecyclerAdapter);
        this.mVideoFolderAdapter.refreshData(videoFolders);
    }

    @Override
    public void onVideoItemClick(View var1, VideoItem videoItem, int position) {
        position = this.videoPicker.isShowCamera() ? position - 1 : position;
        Intent intent;
        if (this.videoPicker.isMultiMode()) {
            intent = new Intent(this, VideoPreviewActivity.class);
            intent.putExtra(EXTRA_SELECTED_VIDEO_POSITION, position);
            DataHolder2.getInstance().save("dh_current_image_folder_items", this.videoPicker.getCurrentVideoFolderItems());
            this.startActivityForResult(intent, VideoPicker.REQUEST_CODE_VIDEO_PREVIEW);
        } else {
            this.videoPicker.clearSelectedVideos();
            this.videoPicker.addSelectedVideoItem(position, (VideoItem) this.videoPicker.getCurrentVideoFolderItems().get(position), true);

            intent = new Intent();
            intent.putExtra(EXTRA_VIDEO_ITEMS, this.videoPicker.getSelectedVideos());
            this.setResult(VideoPicker.RESULT_CODE_VIDEO_ITEMS, intent);
            this.finish();

        }
    }

    @Override
    public void onVideoSelected(int var1, VideoItem videoItem, boolean var3) {
        if (this.videoPicker.getSelectVideoCount() > 0) {
            this.mBtnOk.setText(this.getString(R.string.ip_select_complete, this.videoPicker.getSelectVideoCount(), this.videoPicker.getSelectLimit()));
            this.mBtnOk.setEnabled(true);
            this.mBtnPre.setEnabled(true);
            this.mBtnPre.setText(this.getResources().getString(R.string.ip_preview_count, this.videoPicker.getSelectVideoCount()));
            this.mBtnPre.setTextColor(Color.parseColor(ColorCofig.INSTANCE.getToolbarTitleColorNormal()));
            this.mBtnOk.setTextColor(Color.parseColor(ColorCofig.INSTANCE.getToolbarTitleColorNormal()));
        } else {
            this.mBtnOk.setText(this.getString(R.string.ip_complete));
            this.mBtnOk.setEnabled(false);
            this.mBtnPre.setEnabled(false);
            this.mBtnPre.setText(this.getResources().getString(R.string.ip_preview));
            this.mBtnPre.setTextColor(Color.parseColor(ColorCofig.INSTANCE.getToolbarTitleColorDisabled()));
            this.mBtnOk.setTextColor(Color.parseColor(ColorCofig.INSTANCE.getToolbarTitleColorDisabled()));
        }

        if (this.videoPicker.isMultiMode()) {
            for (int i = this.videoPicker.isShowCamera() ? 1 : 0; i < this.mRecyclerAdapter.getItemCount(); ++i) {
                if (this.mRecyclerAdapter.getItem(i).path != null && this.mRecyclerAdapter.getItem(i).path.equals(videoItem.path)) {
                    this.mRecyclerAdapter.refreshCheckedData(i);
                    return;
                }
            }

        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        VideoItem videoItem;
        Intent intent;
        if (data != null && data.getExtras() != null) { //上一个页面带数据(1.裁剪，2.拍摄)
            switch (resultCode) {
                case -1:
                    switch (requestCode) {
                        case REQUEST_CODE_VIDEO_CROP:
                            Uri resultUri = (Uri) data.getParcelableExtra(EXTRA_CROP_VIDEOOUT_URI);
                            if (resultUri != null) {
                                videoItem = new VideoItem();
                                videoItem.path = resultUri.getPath();
                                this.videoPicker.clearSelectedVideos();
                                this.videoPicker.addSelectedVideoItem(0, videoItem, true);
                                intent = new Intent();
                                intent.putExtra(EXTRA_VIDEO_ITEMS, this.videoPicker.getSelectedVideos());
                                this.setResult(VideoPicker.RESULT_CODE_VIDEO_ITEMS, intent);
                                this.finish();
                            }
                            break;
                        case REQUEST_CODE_VIDEO_TAKE:
                            galleryAddPic(this, this.videoPicker.getTakeVideoFile());
                            String path = this.videoPicker.getTakeVideoFile().getAbsolutePath();
                            videoItem = new VideoItem();
                            videoItem.path = path;
                            this.videoPicker.clearSelectedVideos();
                            this.videoPicker.addSelectedVideoItem(0, videoItem, true);

                            intent = new Intent();
                            intent.putExtra(EXTRA_VIDEO_ITEMS, this.videoPicker.getSelectedVideos());
                            this.setResult(VideoPicker.RESULT_CODE_VIDEO_ITEMS, intent);
                            this.finish();
                            break;
                    }
                    break;
                case RESULT_CODE_VIDEO_ITEMS: //直接上传
                    intent = new Intent();
                    intent.putExtra(EXTRA_VIDEO_ITEMS, this.videoPicker.getSelectedVideos());
                    this.setResult(VideoPicker.RESULT_CODE_VIDEO_ITEMS, intent);
                    finish();
                    break;
            }
        } else if (resultCode == VideoPicker.RESULT_CODE_VIDEO_BACK) {
        } else if (this.directPhoto) {
            this.finish();
        }
    }

    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
    }

    private void releaseFolder() {
        File folder = new File(CACHE_VIDEO_CROP);
        if (!folder.exists()) {
            folder.mkdir();
        }
        File folder2 = new File(CACHE_VIDEO_CROP + "/cover");
        if (!folder2.exists()) {
            folder2.mkdir();
        }
    }
}
