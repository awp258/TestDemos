//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.jw.galary.video;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.jw.galary.img.ui.ImageBaseActivity;
import com.jw.galary.img.util.SpaceItemDecoration;
import com.jw.galary.img.util.Utils;
import com.jw.galary.img.view.ViewPagerFixed;
import com.jw.uploaddemo.R;

import java.util.ArrayList;

import static com.jw.galary.video.VideoPicker.*;

public abstract class VideoPreviewBaseActivity extends ImageBaseActivity {
    protected VideoPicker imagePicker;
    protected ArrayList<VideoItem> mImageItems;
    protected int mCurrentPosition = 0;
    protected TextView mTitleCount;
    protected ArrayList<VideoItem> selectedImages;
    protected View content;
    protected View topBar;
    protected ViewPagerFixed mViewPager;
    protected VideoPageAdapter mAdapter;
    protected RecyclerView mRvPreview;
    protected VideoThumbPreviewAdapter thumbPreviewAdapter;
    protected boolean isFromItems = false;

    public VideoPreviewBaseActivity() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_video_preview);
        this.mCurrentPosition = this.getIntent().getIntExtra(EXTRA_SELECTED_VIDEO_POSITION, 0);
        this.isFromItems = this.getIntent().getBooleanExtra(EXTRA_FROM_VIDEO_ITEMS, false);
        if (this.isFromItems) {
            this.mImageItems = (ArrayList) this.getIntent().getSerializableExtra(EXTRA_VIDEO_ITEMS);
        } else {
            this.mImageItems = (ArrayList) DataHolder2.getInstance().retrieve("dh_current_image_folder_items");
        }

        this.imagePicker = VideoPicker.getInstance();
        this.selectedImages = this.imagePicker.getSelectedVideos();
        this.content = this.findViewById(R.id.content);
        this.topBar = this.findViewById(R.id.top_bar);
        this.topBar.findViewById(R.id.btn_ok).setVisibility(View.GONE);
        this.topBar.findViewById(R.id.btn_back).setOnClickListener(v -> VideoPreviewBaseActivity.this.finish());
        this.mTitleCount = (TextView) this.findViewById(R.id.tv_des);
        this.mViewPager = (ViewPagerFixed) this.findViewById(R.id.viewpager);
        this.mAdapter = new VideoPageAdapter(this, this.mImageItems);
        this.mAdapter.setPhotoViewClickListener(new VideoPageAdapter.PhotoViewClickListener() {
            @Override
            public void OnStartClickListener(VideoItem videoItem) {
                onStartVideo(videoItem);
            }

            @Override
            public void OnImageClickListener(VideoItem videoItem) {
                onImageSingleTap(videoItem);
            }
        });
        this.mViewPager.setAdapter(this.mAdapter);
        this.mViewPager.setCurrentItem(this.mCurrentPosition, false);
        this.mTitleCount.setText(this.getString(R.string.ip_preview_video_count, new Object[]{this.mCurrentPosition + 1, this.mImageItems.size()}));
        this.mRvPreview = (RecyclerView) this.findViewById(R.id.rv_preview);
        this.mRvPreview.setLayoutManager(new LinearLayoutManager(this.getApplicationContext(), 0, false));
        this.mRvPreview.addItemDecoration(new SpaceItemDecoration(Utils.dp2px(this, 6.0F)));
        this.thumbPreviewAdapter = new VideoThumbPreviewAdapter(this);
        this.mRvPreview.setAdapter(this.thumbPreviewAdapter);
        this.thumbPreviewAdapter.setOnThumbItemClickListener(imageItem -> {
            int position = VideoPreviewBaseActivity.this.mImageItems.indexOf(imageItem);
            if (position != -1 && VideoPreviewBaseActivity.this.mCurrentPosition != position) {
                VideoPreviewBaseActivity.this.mCurrentPosition = position;
                VideoPreviewBaseActivity.this.mViewPager.setCurrentItem(VideoPreviewBaseActivity.this.mCurrentPosition, false);
            }

        });
    }

    public abstract void onImageSingleTap(VideoItem videoItem);

    public abstract void onStartVideo(VideoItem videoItem);
}
