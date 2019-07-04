//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.rxxb.imagepicker.ui;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import com.rxxb.imagepicker.ImagePicker.OnImageSelectedListener;
import com.rxxb.imagepicker.R;
import com.rxxb.imagepicker.R.id;
import com.rxxb.imagepicker.R.string;
import com.rxxb.imagepicker.bean.ImageItem;
import com.rxxb.imagepicker.util.NavigationBarChangeListener;
import com.rxxb.imagepicker.util.NavigationBarChangeListener.OnSoftInputStateChangeListener;
import com.rxxb.imagepicker.util.Utils;
import com.rxxb.imagepicker.view.SuperCheckBox;

import java.io.File;

public class ImagePreviewActivity extends ImagePreviewBaseActivity implements OnImageSelectedListener, OnClickListener, OnCheckedChangeListener {
    public static final String ISORIGIN = "isOrigin";
    private SuperCheckBox mCbCheck;
    private SuperCheckBox mCbOrigin;
    private Button mBtnOk;
    private View bottomBar;
    private View marginView;

    public ImagePreviewActivity() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.imagePicker.addOnImageSelectedListener(this);
        this.mBtnOk = (Button)this.findViewById(id.btn_ok);
        this.mBtnOk.setVisibility(0);
        this.setConfirmButtonBg(this.mBtnOk);
        this.mBtnOk.setOnClickListener(this);
        this.findViewById(id.btn_back).setOnClickListener(this);
        this.bottomBar = this.findViewById(id.bottom_bar);
        this.bottomBar.setVisibility(0);
        TextView tvPreviewEdit = (TextView)this.findViewById(id.tv_preview_edit);
        tvPreviewEdit.setOnClickListener(this);
        this.mCbCheck = (SuperCheckBox)this.findViewById(id.cb_check);
        this.mCbOrigin = (SuperCheckBox)this.findViewById(id.cb_preview_origin);
        this.marginView = this.findViewById(id.margin_bottom);
        this.mCbOrigin.setText(this.getString(string.ip_origin));
        this.mCbOrigin.setOnCheckedChangeListener(this);
        this.mCbOrigin.setChecked(this.imagePicker.isOrigin());
        this.onImageSelected(0, (ImageItem)null, false);
        ImageItem item = (ImageItem)this.mImageItems.get(this.mCurrentPosition);
        boolean isSelected = this.imagePicker.isSelect(item);
        this.mTitleCount.setText(this.getString(string.ip_preview_image_count, new Object[]{this.mCurrentPosition + 1, this.mImageItems.size()}));
        this.mCbCheck.setChecked(isSelected);
        this.mViewPager.addOnPageChangeListener(new SimpleOnPageChangeListener() {
            public void onPageSelected(int position) {
                ImagePreviewActivity.this.mCurrentPosition = position;
                ImageItem item = (ImageItem)ImagePreviewActivity.this.mImageItems.get(ImagePreviewActivity.this.mCurrentPosition);
                boolean isSelected = ImagePreviewActivity.this.imagePicker.isSelect(item);
                ImagePreviewActivity.this.mCbCheck.setChecked(isSelected);
                ImagePreviewActivity.this.mTitleCount.setText(ImagePreviewActivity.this.getString(string.ip_preview_image_count, new Object[]{ImagePreviewActivity.this.mCurrentPosition + 1, ImagePreviewActivity.this.mImageItems.size()}));
                ImagePreviewActivity.this.thumbPreviewAdapter.setSelected(item);
            }
        });
        this.mCbCheck.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ImageItem imageItem = (ImageItem)ImagePreviewActivity.this.mImageItems.get(ImagePreviewActivity.this.mCurrentPosition);
                int selectLimit = ImagePreviewActivity.this.imagePicker.getSelectLimit();
                if (ImagePreviewActivity.this.mCbCheck.isChecked() && ImagePreviewActivity.this.selectedImages.size() >= selectLimit) {
                    Toast.makeText(ImagePreviewActivity.this, ImagePreviewActivity.this.getString(string.ip_select_limit, new Object[]{selectLimit}), 0).show();
                    ImagePreviewActivity.this.mCbCheck.setChecked(false);
                } else {
                    int changPosition = ImagePreviewActivity.this.imagePicker.getSelectImageCount();
                    if (!ImagePreviewActivity.this.mCbCheck.isChecked()) {
                        changPosition = ImagePreviewActivity.this.imagePicker.getSelectedImages().indexOf(imageItem);
                        ImagePreviewActivity.this.thumbPreviewAdapter.notifyItemRemoved(changPosition);
                    } else {
                        ImagePreviewActivity.this.thumbPreviewAdapter.notifyItemInserted(changPosition);
                    }

                    ImagePreviewActivity.this.imagePicker.addSelectedImageItem(ImagePreviewActivity.this.mCurrentPosition, imageItem, ImagePreviewActivity.this.mCbCheck.isChecked());
                }

            }
        });
        NavigationBarChangeListener.with(this).setListener(new OnSoftInputStateChangeListener() {
            public void onNavigationBarShow(int orientation, int height) {
                ImagePreviewActivity.this.marginView.setVisibility(0);
                LayoutParams layoutParams = ImagePreviewActivity.this.marginView.getLayoutParams();
                if (layoutParams.height == 0) {
                    layoutParams.height = Utils.getNavigationBarHeight(ImagePreviewActivity.this);
                    ImagePreviewActivity.this.marginView.requestLayout();
                }

            }

            public void onNavigationBarHide(int orientation) {
                ImagePreviewActivity.this.marginView.setVisibility(8);
            }
        });
        NavigationBarChangeListener.with(this, 2).setListener(new OnSoftInputStateChangeListener() {
            public void onNavigationBarShow(int orientation, int height) {
                ImagePreviewActivity.this.topBar.setPadding(0, 0, height, 0);
                ImagePreviewActivity.this.bottomBar.setPadding(0, 0, height, 0);
            }

            public void onNavigationBarHide(int orientation) {
                ImagePreviewActivity.this.topBar.setPadding(0, 0, 0, 0);
                ImagePreviewActivity.this.bottomBar.setPadding(0, 0, 0, 0);
            }
        });
        this.topBar.setBackgroundColor(Color.parseColor(this.imagePicker.getViewColor().getNaviBgColor()));
        this.bottomBar.setBackgroundColor(Color.parseColor(this.imagePicker.getViewColor().getToolbarBgColor()));
        this.mTitleCount.setTextColor(Color.parseColor(this.imagePicker.getViewColor().getNaviTitleColor()));
        tvPreviewEdit.setTextColor(Color.parseColor(this.imagePicker.getViewColor().getToolbarTitleColorNormal()));
        this.mCbOrigin.setTextColor(Color.parseColor(this.imagePicker.getViewColor().getToolbarTitleColorNormal()));
        this.mCbCheck.setTextColor(Color.parseColor(this.imagePicker.getViewColor().getToolbarTitleColorNormal()));
    }

    public void onImageSelected(int position, ImageItem item, boolean isAdd) {
        if (this.imagePicker.getSelectImageCount() > 0) {
            this.mBtnOk.setText(this.getString(string.ip_select_complete, new Object[]{this.imagePicker.getSelectImageCount(), this.imagePicker.getSelectLimit()}));
        } else {
            this.mBtnOk.setText(this.getString(string.ip_complete));
        }

        if (isAdd) {
            this.thumbPreviewAdapter.setSelected(item);
        }

    }

    public void onClick(View v) {
        int id = v.getId();
        Intent intent;
        if (id == R.id.btn_ok) {
            if (this.imagePicker.getSelectedImages().size() == 0) {
                this.mCbCheck.setChecked(true);
                ImageItem imageItem = (ImageItem)this.mImageItems.get(this.mCurrentPosition);
                this.imagePicker.addSelectedImageItem(this.mCurrentPosition, imageItem, this.mCbCheck.isChecked());
            }

            intent = new Intent();
            intent.putExtra("extra_result_items", this.imagePicker.getSelectedImages());
            this.setResult(1004, intent);
            this.finish();
        } else if (id == R.id.btn_back) {
            intent = new Intent();
            this.setResult(1005, intent);
            this.finish();
        } else if (id == R.id.tv_preview_edit) {
            this.startActivityForResult(CropActivity.callingIntent(this, Uri.fromFile(new File(((ImageItem)this.mImageItems.get(this.mCurrentPosition)).path))), 1002);
        }

    }

    public void onBackPressed() {
        Intent intent = new Intent();
        this.setResult(1005, intent);
        this.finish();
        super.onBackPressed();
    }

    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        if (id == R.id.cb_preview_origin) {
            if (isChecked) {
                this.imagePicker.setOrigin(true);
            } else {
                this.imagePicker.setOrigin(false);
            }
        }

    }

    public void onImageSingleTap() {
        if (this.topBar.getVisibility() == 0) {
            this.topBar.setAnimation(AnimationUtils.loadAnimation(this, R.anim.top_out));
            this.bottomBar.setAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out));
            this.topBar.setVisibility(8);
            this.bottomBar.setVisibility(8);
            this.tintManager.setStatusBarTintResource(0);
        } else {
            this.topBar.setAnimation(AnimationUtils.loadAnimation(this, R.anim.top_in));
            this.bottomBar.setAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
            this.topBar.setVisibility(0);
            this.bottomBar.setVisibility(0);
            this.tintManager.setStatusBarTintResource(R.color.ip_color_primary_dark);
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && data.getExtras() != null) {
            if (resultCode == -1 && requestCode == 1002) {
                Uri resultUri = (Uri)data.getParcelableExtra("extra_out_uri");
                if (resultUri != null) {
                    int fromSelectedPosition = -1;

                    for(int i = 0; i < this.selectedImages.size(); ++i) {
                        if (((ImageItem)this.selectedImages.get(i)).path.equals(((ImageItem)this.mImageItems.get(this.mCurrentPosition)).path)) {
                            fromSelectedPosition = i;
                            break;
                        }
                    }

                    ImageItem imageItem = new ImageItem();
                    imageItem.path = resultUri.getPath();
                    if (fromSelectedPosition != -1) {
                        this.imagePicker.addSelectedImageItem(fromSelectedPosition, (ImageItem)this.selectedImages.get(fromSelectedPosition), false);
                        this.imagePicker.addSelectedImageItem(fromSelectedPosition, imageItem, true);
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

    protected void onDestroy() {
        this.imagePicker.removeOnImageSelectedListener(this);
        super.onDestroy();
    }
}
