package com.jw.galary.img.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.jw.galary.base.BaseThumbPreviewAdapter;
import com.jw.galary.img.ImagePicker;
import com.jw.galary.img.adapter.ImagePageAdapter;
import com.jw.galary.img.adapter.ImageThumbPreviewAdapter;
import com.jw.galary.img.bean.ImageItem;
import com.jw.galary.img.util.SpaceItemDecoration;
import com.jw.galary.img.util.Utils;
import com.jw.galary.img.view.ViewPagerFixed;
import com.jw.uploaddemo.R;
import com.jw.uploaddemo.uploadPlugin.UploadPluginActivity;

import java.util.ArrayList;

import static com.jw.galary.img.ImagePicker.DH_CURRENT_IMAGE_FOLDER_ITEMS;

public abstract class ImagePreviewBaseActivity extends UploadPluginActivity {
    protected ImagePicker imagePicker;
    protected ArrayList<ImageItem> mImageItems;
    protected int mCurrentPosition = 0;
    protected TextView mTitleCount;
    protected ArrayList<ImageItem> selectedImages;
    protected View topBar;
    protected ViewPagerFixed mViewPager;
    protected ImagePageAdapter mAdapter;
    protected RecyclerView mRvPreview;
    protected ImageThumbPreviewAdapter thumbPreviewAdapter;
    protected boolean isFromItems = false;

    public ImagePreviewBaseActivity() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_image_preview);
        this.mCurrentPosition = this.getIntent().getIntExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, 0);
        this.isFromItems = this.getIntent().getBooleanExtra(ImagePicker.EXTRA_FROM_IMAGE_ITEMS, false);
        if (this.isFromItems) {
            this.mImageItems = (ArrayList) this.getIntent().getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS);
        } else {
            this.mImageItems = (ArrayList) imagePicker.getData().get(DH_CURRENT_IMAGE_FOLDER_ITEMS);
        }

        this.imagePicker = ImagePicker.INSTANCE;
        this.selectedImages = this.imagePicker.getSelectedImages();
        this.topBar = this.findViewById(R.id.top_bar);
        this.topBar.findViewById(R.id.btn_ok).setVisibility(View.GONE);
        this.topBar.findViewById(R.id.btn_back).setOnClickListener(v -> ImagePreviewBaseActivity.this.finish());
        this.mTitleCount = this.findViewById(R.id.tv_des);
        this.mViewPager = this.findViewById(R.id.viewpager);
        this.mAdapter = new ImagePageAdapter(this, this.mImageItems);
        this.mAdapter.setPhotoViewClickListener((view, v, v1) -> ImagePreviewBaseActivity.this.onImageSingleTap());
        this.mViewPager.setAdapter(this.mAdapter);
        this.mViewPager.setCurrentItem(this.mCurrentPosition, false);
        this.mTitleCount.setText(this.getString(R.string.ip_preview_image_count, this.mCurrentPosition + 1, this.mImageItems.size()));
        this.mRvPreview = this.findViewById(R.id.rv_preview);
        this.mRvPreview.setLayoutManager(new LinearLayoutManager(this.getApplicationContext(), 0, false));
        this.mRvPreview.addItemDecoration(new SpaceItemDecoration(Utils.dp2px(this, 6.0F)));
        this.thumbPreviewAdapter = new ImageThumbPreviewAdapter(this, imagePicker.getSelectedImages());
        this.mRvPreview.setAdapter(this.thumbPreviewAdapter);
        this.thumbPreviewAdapter.setOnThumbItemClickListener((BaseThumbPreviewAdapter.OnThumbItemClickListener) imageItem -> {
            int position = ImagePreviewBaseActivity.this.mImageItems.indexOf(imageItem);
            if (position != -1 && ImagePreviewBaseActivity.this.mCurrentPosition != position) {
                ImagePreviewBaseActivity.this.mCurrentPosition = position;
                ImagePreviewBaseActivity.this.mViewPager.setCurrentItem(ImagePreviewBaseActivity.this.mCurrentPosition, false);
            }
        });
    }

    public abstract void onImageSingleTap();

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        ImagePicker.INSTANCE.restoreInstanceState(savedInstanceState);
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ImagePicker.INSTANCE.saveInstanceState(outState);
    }
}
