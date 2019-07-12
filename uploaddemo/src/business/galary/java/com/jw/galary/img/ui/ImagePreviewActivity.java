//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.jw.galary.img.ui;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import com.jw.galary.img.ImagePicker.*;
import com.jw.galary.img.bean.ImageItem;
import com.jw.galary.img.util.NavigationBarChangeListener;
import com.jw.galary.img.util.NavigationBarChangeListener.OnSoftInputStateChangeListener;
import com.jw.galary.img.util.Utils;
import com.jw.galary.img.view.SuperCheckBox;
import com.jw.uilibrary.base.utils.ThemeUtils;
import com.jw.uploaddemo.R;

import java.io.File;

import static com.jw.galary.img.ImagePicker.*;

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
        this.mBtnOk = (Button) this.findViewById(R.id.btn_ok);
        this.mBtnOk.setVisibility(View.VISIBLE);
        this.setConfirmButtonBg(this.mBtnOk);
        this.mBtnOk.setOnClickListener(this);
        this.findViewById(R.id.btn_back).setOnClickListener(this);
        this.bottomBar = this.findViewById(R.id.bottom_bar);
        this.bottomBar.setVisibility(View.VISIBLE);
        TextView tvPreviewEdit = (TextView) this.findViewById(R.id.tv_preview_edit);
        tvPreviewEdit.setOnClickListener(this);
        this.mCbCheck = (SuperCheckBox) this.findViewById(R.id.cb_check);
        this.mCbOrigin = (SuperCheckBox) this.findViewById(R.id.cb_preview_origin);
        this.marginView = this.findViewById(R.id.margin_bottom);
        this.mCbOrigin.setText(this.getString(R.string.ip_origin));
        this.mCbOrigin.setOnCheckedChangeListener(this);
        this.mCbOrigin.setChecked(this.imagePicker.isOrigin());
        this.onImageSelected(0, (ImageItem) null, false);
        ImageItem item = (ImageItem) this.mImageItems.get(this.mCurrentPosition);
        boolean isSelected = this.imagePicker.isSelect(item);
        this.mTitleCount.setText(this.getString(R.string.ip_preview_image_count, new Object[]{this.mCurrentPosition + 1, this.mImageItems.size()}));
        this.mCbCheck.setChecked(isSelected);
        this.mViewPager.addOnPageChangeListener(new SimpleOnPageChangeListener() {
            public void onPageSelected(int position) {
                ImagePreviewActivity.this.mCurrentPosition = position;
                ImageItem item = (ImageItem) ImagePreviewActivity.this.mImageItems.get(ImagePreviewActivity.this.mCurrentPosition);
                boolean isSelected = ImagePreviewActivity.this.imagePicker.isSelect(item);
                ImagePreviewActivity.this.mCbCheck.setChecked(isSelected);
                ImagePreviewActivity.this.mTitleCount.setText(ImagePreviewActivity.this.getString(R.string.ip_preview_image_count, new Object[]{ImagePreviewActivity.this.mCurrentPosition + 1, ImagePreviewActivity.this.mImageItems.size()}));
                ImagePreviewActivity.this.thumbPreviewAdapter.setSelected(item);
            }
        });
        this.mCbCheck.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ImageItem imageItem = (ImageItem) ImagePreviewActivity.this.mImageItems.get(ImagePreviewActivity.this.mCurrentPosition);
                int selectLimit = ImagePreviewActivity.this.imagePicker.getSelectLimit();
                if (ImagePreviewActivity.this.mCbCheck.isChecked() && ImagePreviewActivity.this.selectedImages.size() >= selectLimit) {
                    Toast.makeText(ImagePreviewActivity.this, ImagePreviewActivity.this.getString(R.string.ip_select_limit, new Object[]{selectLimit}), Toast.LENGTH_SHORT).show();
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
                ImagePreviewActivity.this.marginView.setVisibility(View.VISIBLE);
                LayoutParams layoutParams = ImagePreviewActivity.this.marginView.getLayoutParams();
                if (layoutParams.height == 0) {
                    layoutParams.height = Utils.getNavigationBarHeight(ImagePreviewActivity.this);
                    ImagePreviewActivity.this.marginView.requestLayout();
                }

            }

            public void onNavigationBarHide(int orientation) {
                ImagePreviewActivity.this.marginView.setVisibility(View.GONE);
            }
        });
/*        NavigationBarChangeListener.with(this, 2).setListener(new OnSoftInputStateChangeListener() {
            public void onNavigationBarShow(int orientation, int height) {
                ImagePreviewActivity.this.topBar.setPadding(0, 0, height, 0);
                ImagePreviewActivity.this.bottomBar.setPadding(0, 0, height, 0);
            }

            public void onNavigationBarHide(int orientation) {
                ImagePreviewActivity.this.topBar.setPadding(0, 0, 0, 0);
                ImagePreviewActivity.this.bottomBar.setPadding(0, 0, 0, 0);
            }
        });*/
        //this.topBar.setBackgroundColor(Color.parseColor(this.imagePicker.getViewColor().getNaviBgColor()));
        this.bottomBar.setBackgroundColor(Color.parseColor(this.imagePicker.getViewColor().getToolbarBgColor()));
        this.mTitleCount.setTextColor(Color.parseColor(this.imagePicker.getViewColor().getNaviTitleColor()));
        tvPreviewEdit.setTextColor(Color.parseColor(this.imagePicker.getViewColor().getToolbarTitleColorNormal()));
        this.mCbOrigin.setTextColor(Color.parseColor(this.imagePicker.getViewColor().getToolbarTitleColorNormal()));
        this.mCbCheck.setTextColor(Color.parseColor(this.imagePicker.getViewColor().getToolbarTitleColorNormal()));
    }

    public void onImageSelected(int position, ImageItem item, boolean isAdd) {
        if (this.imagePicker.getSelectImageCount() > 0) {
            this.mBtnOk.setText(this.getString(R.string.ip_select_complete, new Object[]{this.imagePicker.getSelectImageCount(), this.imagePicker.getSelectLimit()}));
        } else {
            this.mBtnOk.setText(this.getString(R.string.ip_complete));
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
                ImageItem imageItem = (ImageItem) this.mImageItems.get(this.mCurrentPosition);
                this.imagePicker.addSelectedImageItem(this.mCurrentPosition, imageItem, this.mCbCheck.isChecked());
            }

            intent = new Intent();
            intent.putExtra(EXTRA_IMAGE_ITEMS, this.imagePicker.getSelectedImages());
            this.setResult(RESULT_CODE_IMAGE_ITEMS, intent);
            this.finish();
        } else if (id == R.id.btn_back) {
            intent = new Intent();
            this.setResult(RESULT_CODE_IMAGE_BACK, intent);
            this.finish();
        } else if (id == R.id.tv_preview_edit) {
            this.startActivityForResult(CropActivity.callingIntent(this, Uri.fromFile(new File(((ImageItem) this.mImageItems.get(this.mCurrentPosition)).path))), REQUEST_CODE_IMAGE_CROP);
        }

    }

    public void onBackPressed() {
        Intent intent = new Intent();
        this.setResult(RESULT_CODE_IMAGE_BACK, intent);
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
        if (this.topBar.getVisibility() == View.VISIBLE) {
/*            this.topBar.setAnimation(AnimationUtils.loadAnimation(this, R.anim.top_out));
            this.bottomBar.setAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out));*/
            this.topBar.setVisibility(View.GONE);
            this.bottomBar.setVisibility(View.GONE);
            ThemeUtils.changeStatusBar(this, Color.BLACK);
        } else {
/*            this.topBar.setAnimation(AnimationUtils.loadAnimation(this, R.anim.top_in));
            this.bottomBar.setAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));*/
            this.topBar.setVisibility(View.VISIBLE);
            this.bottomBar.setVisibility(View.VISIBLE);
            ThemeUtils.changeStatusBar(this, Color.parseColor("#393A3F"));
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && data.getExtras() != null) {
            if (resultCode == -1 && requestCode == REQUEST_CODE_IMAGE_CROP) {
                Uri resultUri = (Uri) data.getParcelableExtra(EXTRA_CROP_IMAGE_OUT_URI);
                if (resultUri != null) {
                    int fromSelectedPosition = -1;

                    for (int i = 0; i < this.selectedImages.size(); ++i) {
                        if (((ImageItem) this.selectedImages.get(i)).path.equals(((ImageItem) this.mImageItems.get(this.mCurrentPosition)).path)) {
                            fromSelectedPosition = i;
                            break;
                        }
                    }

                    ImageItem imageItem = new ImageItem();
                    imageItem.path = resultUri.getPath();
                    if (fromSelectedPosition != -1) {
                        this.imagePicker.addSelectedImageItem(fromSelectedPosition, (ImageItem) this.selectedImages.get(fromSelectedPosition), false);
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
