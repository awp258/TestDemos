//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.jw.shotRecord.video;

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
import com.jw.uploaddemo.R;
import com.rxxb.imagepicker.R.id;
import com.rxxb.imagepicker.ui.ImageBaseActivity;
import com.rxxb.imagepicker.util.Utils;
import com.rxxb.imagepicker.view.FolderPopUpWindow;
import com.rxxb.imagepicker.view.GridSpacingItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class VideoGridActivity extends ImageBaseActivity implements VideoDataSource.OnVideosLoadedListener, VideoRecyclerAdapter.OnVideoItemClickListener, VideoPicker.OnVideoSelectedListener, OnClickListener, OnCheckedChangeListener {
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

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.directPhoto = savedInstanceState.getBoolean("TAKE", false);
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("TAKE", this.directPhoto);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_video_grid);
        this.videoPicker = VideoPicker.getInstance();
        this.videoPicker.clear();
        this.videoPicker.addOnVideoSelectedListener(this);
        Intent data = this.getIntent();
        if (data != null && data.getExtras() != null) {
            this.directPhoto = data.getBooleanExtra("TAKE", false);
            if (this.directPhoto) {
                if (!this.checkPermission("android.permission.CAMERA")) {
                    ActivityCompat.requestPermissions(this, new String[]{"android.permission.CAMERA"}, 2);
                } else {
                    this.videoPicker.takePicture(this, 1001);
                }
            }

            ArrayList<VideoItem> videos = (ArrayList) data.getSerializableExtra("IMAGES");
            this.videoPicker.setSelectedVideos(videos);
        }

        this.mRecyclerView = this.findViewById(id.recycler);
        this.findViewById(id.btn_back).setOnClickListener(this);
        this.mBtnOk = findViewById(id.btn_ok);
        this.mBtnOk.setOnClickListener(this);
        this.mBtnPre = this.findViewById(id.btn_preview);
        this.mBtnPre.setOnClickListener(this);
        this.mFooterBar = this.findViewById(id.footer_bar);
        this.mllDir = this.findViewById(id.ll_dir);
        this.mllDir.setOnClickListener(this);
        this.mtvDir = this.findViewById(id.tv_dir);
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
        this.findViewById(id.top_bar).setBackgroundColor(Color.parseColor(this.videoPicker.getViewColor().getNaviBgColor()));
        ((TextView) this.findViewById(id.tv_des)).setTextColor(Color.parseColor(this.videoPicker.getViewColor().getNaviTitleColor()));
        this.mFooterBar.setBackgroundColor(Color.parseColor(this.videoPicker.getViewColor().getToolbarBgColor()));
        this.mBtnPre.setTextColor(Color.parseColor(this.videoPicker.getViewColor().getToolbarTitleColorDisabled()));
        this.mtvDir.setTextColor(Color.parseColor(this.videoPicker.getViewColor().getToolbarTitleColorNormal()));
        this.mVideoFolderAdapter = new VideoFolderAdapter(this, null);
        this.mRecyclerAdapter = new VideoRecyclerAdapter(this, null);
        this.onVideoSelected(0, null, false);
        if (this.checkPermission("android.permission.WRITE_EXTERNAL_STORAGE")) {
            new VideoDataSource(this, null, this);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 1);
        }

    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == 0) {
                new VideoDataSource(this, null, this);
            } else {
                this.showToast("权限被禁止，无法选择本地图片");
            }
        } else if (requestCode == 2) {
            if (grantResults.length > 0 && grantResults[0] == 0) {
                this.videoPicker.takePicture(this, 1001);
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
            intent = new Intent();
            intent.putExtra("extra_result_items", this.videoPicker.getSelectedVideos());
            this.setResult(1004, intent);
            this.finish();
        } else if (id == R.id.ll_dir) {
            if (this.mVideoFolders == null) {
                Log.i("VideoGridActivity", "您的手机没有图片");
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
/*            intent = new Intent(this, VideoPreviewActivity.class);
            intent.putExtra("selected_video_position", 0);
            intent.putExtra("extra_video_items", this.videoPicker.getSelectedVideos());
            intent.putExtra("extra_from_items", true);
            this.startActivityForResult(intent, 1003);*/
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
        this.mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        this.mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(4, Utils.dp2px(this, 2.0F), false));
        this.mRecyclerView.setAdapter(this.mRecyclerAdapter);
        this.mVideoFolderAdapter.refreshData(videoFolders);
    }

    @Override
    public void onVideoItemClick(View var1, VideoItem videoItem, int position) {
        position = this.videoPicker.isShowCamera() ? position - 1 : position;
        Intent intent;
        /*if (this.videoPicker.isMultiMode()) {
            intent = new Intent(this, VideoPreviewActivity.class);
            intent.putExtra("selected_video_position", position);
            DataHolder.getInstance().save("dh_current_video_folder_items", this.videoPicker.getCurrentVideoFolderItems());
            this.startActivityForResult(intent, 1003);
        } else {
            this.videoPicker.clearSelectedVideos();
            this.videoPicker.addSelectedVideoItem(position, (VideoItem)this.videoPicker.getCurrentVideoFolderItems().get(position), true);
            if (this.videoPicker.isCrop()) {
                this.startActivityForResult(CropActivity.callingIntent(this, Uri.fromFile(new File(videoItem.path))), 1002);
            } else {
                intent = new Intent();
                intent.putExtra("extra_result_items", this.videoPicker.getSelectedVideos());
                this.setResult(1004, intent);
                this.finish();
            }
        }*/
    }

    @Override
    public void onVideoSelected(int var1, VideoItem videoItem, boolean var3) {
        if (this.videoPicker.getSelectVideoCount() > 0) {
            this.mBtnOk.setText(this.getString(R.string.ip_select_complete, this.videoPicker.getSelectVideoCount(), this.videoPicker.getSelectLimit()));
            this.mBtnOk.setEnabled(true);
            this.mBtnPre.setEnabled(true);
            this.mBtnPre.setText(this.getResources().getString(R.string.ip_preview_count, this.videoPicker.getSelectVideoCount()));
            this.mBtnPre.setTextColor(Color.parseColor(this.videoPicker.getViewColor().getToolbarTitleColorNormal()));
            this.mBtnOk.setTextColor(Color.parseColor(this.videoPicker.getViewColor().getToolbarTitleColorNormal()));
        } else {
            this.mBtnOk.setText(this.getString(R.string.ip_complete));
            this.mBtnOk.setEnabled(false);
            this.mBtnPre.setEnabled(false);
            this.mBtnPre.setText(this.getResources().getString(R.string.ip_preview));
            this.mBtnPre.setTextColor(Color.parseColor(this.videoPicker.getViewColor().getToolbarTitleColorDisabled()));
            this.mBtnOk.setTextColor(Color.parseColor(this.videoPicker.getViewColor().getToolbarTitleColorDisabled()));
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
        if (data != null && data.getExtras() != null) {
            if (resultCode == -1 && requestCode == 1002) {
                Uri resultUri = (Uri) data.getParcelableExtra("extra_out_uri");
                if (resultUri != null) {
                    videoItem = new VideoItem();
                    videoItem.path = resultUri.getPath();
                    this.videoPicker.clearSelectedVideos();
                    this.videoPicker.addSelectedVideoItem(0, videoItem, true);
                    intent = new Intent();
                    intent.putExtra("extra_result_items", this.videoPicker.getSelectedVideos());
                    this.setResult(1004, intent);
                    this.finish();
                }
            } else {
                if (data.getSerializableExtra("extra_result_items") != null) {
                    this.setResult(1004, data);
                }

                this.finish();
            }
        } else if (resultCode == 1005) {
        } else if (resultCode == -1 && requestCode == 1001) {
            VideoPicker.galleryAddPic(this, this.videoPicker.getTakeVideoFile());
            String path = this.videoPicker.getTakeVideoFile().getAbsolutePath();
            videoItem = new VideoItem();
            videoItem.path = path;
            this.videoPicker.clearSelectedVideos();
            this.videoPicker.addSelectedVideoItem(0, videoItem, true);

            intent = new Intent();
            intent.putExtra("extra_result_items", this.videoPicker.getSelectedVideos());
            this.setResult(1004, intent);
            this.finish();

        } else if (this.directPhoto) {
            this.finish();
        }

    }

    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
    }
}
