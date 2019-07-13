

package com.jw.galary.img.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.jw.galary.img.DataHolder;
import com.jw.galary.img.ImagePicker;
import com.jw.galary.img.adapter.ImagePageAdapter;
import com.jw.galary.img.adapter.ImagePageAdapter.PhotoViewClickListener;
import com.jw.galary.img.adapter.ImageThumbPreviewAdapter;
import com.jw.galary.img.adapter.ImageThumbPreviewAdapter.OnThumbItemClickListener;
import com.jw.galary.img.bean.ImageItem;
import com.jw.galary.img.util.SpaceItemDecoration;
import com.jw.galary.img.util.Utils;
import com.jw.galary.img.view.ViewPagerFixed;
import com.jw.uploaddemo.R;
import com.jw.uploaddemo.uploadPlugin.UploadPluginActivity;

import java.util.ArrayList;

import static com.jw.galary.img.ImagePicker.*;

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
        this.mCurrentPosition = this.getIntent().getIntExtra(EXTRA_SELECTED_IMAGE_POSITION, 0);
        this.isFromItems = this.getIntent().getBooleanExtra(EXTRA_FROM_IMAGE_ITEMS, false);
        if (this.isFromItems) {
            this.mImageItems = (ArrayList) this.getIntent().getSerializableExtra(EXTRA_IMAGE_ITEMS);
        } else {
            this.mImageItems = (ArrayList) DataHolder.getInstance().retrieve("dh_current_image_folder_items");
        }

        this.imagePicker = ImagePicker.getInstance();
        this.selectedImages = this.imagePicker.getSelectedImages();
        this.topBar = this.findViewById(R.id.top_bar);
        this.topBar.findViewById(R.id.btn_ok).setVisibility(View.GONE);
        this.topBar.findViewById(R.id.btn_back).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ImagePreviewBaseActivity.this.finish();
            }
        });
        this.mTitleCount = (TextView) this.findViewById(R.id.tv_des);
        this.mViewPager = (ViewPagerFixed) this.findViewById(R.id.viewpager);
        this.mAdapter = new ImagePageAdapter(this, this.mImageItems);
        this.mAdapter.setPhotoViewClickListener(new PhotoViewClickListener() {
            public void OnPhotoTapListener(View view, float v, float v1) {
                ImagePreviewBaseActivity.this.onImageSingleTap();
            }
        });
        this.mViewPager.setAdapter(this.mAdapter);
        this.mViewPager.setCurrentItem(this.mCurrentPosition, false);
        this.mTitleCount.setText(this.getString(R.string.ip_preview_image_count, new Object[]{this.mCurrentPosition + 1, this.mImageItems.size()}));
        this.mRvPreview = (RecyclerView) this.findViewById(R.id.rv_preview);
        this.mRvPreview.setLayoutManager(new LinearLayoutManager(this.getApplicationContext(), 0, false));
        this.mRvPreview.addItemDecoration(new SpaceItemDecoration(Utils.dp2px(this, 6.0F)));
        this.thumbPreviewAdapter = new ImageThumbPreviewAdapter(this);
        this.mRvPreview.setAdapter(this.thumbPreviewAdapter);
        this.thumbPreviewAdapter.setOnThumbItemClickListener(new OnThumbItemClickListener() {
            public void onThumbItemClick(ImageItem imageItem) {
                int position = ImagePreviewBaseActivity.this.mImageItems.indexOf(imageItem);
                if (position != -1 && ImagePreviewBaseActivity.this.mCurrentPosition != position) {
                    ImagePreviewBaseActivity.this.mCurrentPosition = position;
                    ImagePreviewBaseActivity.this.mViewPager.setCurrentItem(ImagePreviewBaseActivity.this.mCurrentPosition, false);
                }

            }
        });
    }

    public abstract void onImageSingleTap();

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        ImagePicker.getInstance().restoreInstanceState(savedInstanceState);
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ImagePicker.getInstance().saveInstanceState(outState);
    }
}
