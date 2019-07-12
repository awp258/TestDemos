//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.jw.galary.img.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.support.v7.app.AlertDialog.Builder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import com.jw.galary.img.util.NavigationBarChangeListener;
import com.jw.galary.img.util.NavigationBarChangeListener.OnSoftInputStateChangeListener;
import com.jw.uploaddemo.R;

import static com.jw.galary.img.ImagePicker.EXTRA_IMAGE_ITEMS;
import static com.jw.galary.img.ImagePicker.RESULT_CODE_IMAGE_BACK;

public class ImagePreviewDelActivity extends ImagePreviewBaseActivity implements OnClickListener {
    public ImagePreviewDelActivity() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageView mBtnDel = (ImageView) this.findViewById(R.id.btn_del);
        mBtnDel.setOnClickListener(this);
        mBtnDel.setVisibility(View.VISIBLE);
        this.topBar.findViewById(R.id.btn_back).setOnClickListener(this);
        this.mTitleCount.setText(this.getString(R.string.ip_preview_image_count, new Object[]{this.mCurrentPosition + 1, this.mImageItems.size()}));
        this.mViewPager.addOnPageChangeListener(new SimpleOnPageChangeListener() {
            public void onPageSelected(int position) {
                ImagePreviewDelActivity.this.mCurrentPosition = position;
                ImagePreviewDelActivity.this.mTitleCount.setText(ImagePreviewDelActivity.this.getString(R.string.ip_preview_image_count, new Object[]{ImagePreviewDelActivity.this.mCurrentPosition + 1, ImagePreviewDelActivity.this.mImageItems.size()}));
            }
        });
        NavigationBarChangeListener.with(this, 2).setListener(new OnSoftInputStateChangeListener() {
            public void onNavigationBarShow(int orientation, int height) {
                ImagePreviewDelActivity.this.topBar.setPadding(0, 0, height, 0);
            }

            public void onNavigationBarHide(int orientation) {
                ImagePreviewDelActivity.this.topBar.setPadding(0, 0, 0, 0);
            }
        });
    }

    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_del) {
            this.showDeleteDialog();
        } else if (id == R.id.btn_back) {
            this.onBackPressed();
        }

    }

    private void showDeleteDialog() {
        Builder builder = new Builder(this);
        builder.setTitle("提示");
        builder.setMessage("要删除这张照片吗？");
        builder.setNegativeButton("取消", (android.content.DialogInterface.OnClickListener) null);
        builder.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ImagePreviewDelActivity.this.mImageItems.remove(ImagePreviewDelActivity.this.mCurrentPosition);
                if (ImagePreviewDelActivity.this.mImageItems.size() > 0) {
                    ImagePreviewDelActivity.this.mAdapter.setData(ImagePreviewDelActivity.this.mImageItems);
                    ImagePreviewDelActivity.this.mAdapter.notifyDataSetChanged();
                    ImagePreviewDelActivity.this.mTitleCount.setText(ImagePreviewDelActivity.this.getString(R.string.ip_preview_image_count, new Object[]{ImagePreviewDelActivity.this.mCurrentPosition + 1, ImagePreviewDelActivity.this.mImageItems.size()}));
                } else {
                    ImagePreviewDelActivity.this.onBackPressed();
                }

            }
        });
        builder.show();
    }

    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_IMAGE_ITEMS, this.mImageItems);
        this.setResult(RESULT_CODE_IMAGE_BACK, intent);
        this.finish();
        super.onBackPressed();
    }

    public void onImageSingleTap() {
        if (this.topBar.getVisibility() == View.VISIBLE) {
            this.topBar.setAnimation(AnimationUtils.loadAnimation(this, R.anim.top_out));
            this.topBar.setVisibility(View.GONE);
        } else {
            this.topBar.setAnimation(AnimationUtils.loadAnimation(this, R.anim.top_in));
            this.topBar.setVisibility(View.VISIBLE);
        }

    }
}
