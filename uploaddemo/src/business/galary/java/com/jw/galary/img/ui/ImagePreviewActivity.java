

package com.jw.galary.img.ui;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.jw.galary.img.ImagePicker.OnImageSelectedListener;
import com.jw.galary.img.bean.ImageItem;
import com.jw.galary.img.view.SuperCheckBox;
import com.jw.uploaddemo.ColorCofig;
import com.jw.uploaddemo.R;
import com.jw.uploaddemo.base.utils.ThemeUtils;

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
        //得到当前界面的装饰视图
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            //设置让应用主题内容占据状态栏和导航栏
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            //设置状态栏和导航栏颜色为透明
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            //getWindow().setNavigationBarColor(Color.TRANSPARENT);
        }
        this.imagePicker.addOnImageSelectedListener(this);
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
        this.mCbOrigin = this.findViewById(R.id.cb_preview_origin);
        this.marginView = this.findViewById(R.id.margin_bottom);
        this.mCbOrigin.setText(this.getString(R.string.ip_origin));
        this.mCbOrigin.setOnCheckedChangeListener(this);
        this.mCbOrigin.setChecked(this.imagePicker.isOrigin());
        this.onImageSelected(0, null, false);
        ImageItem item = this.mImageItems.get(this.mCurrentPosition);
        boolean isSelected = this.imagePicker.isSelect(item);
        this.mTitleCount.setText(this.getString(R.string.ip_preview_image_count, this.mCurrentPosition + 1, this.mImageItems.size()));
        this.mCbCheck.setChecked(isSelected);
        this.mViewPager.addOnPageChangeListener(new SimpleOnPageChangeListener() {
            public void onPageSelected(int position) {
                ImagePreviewActivity.this.mCurrentPosition = position;
                ImageItem item = ImagePreviewActivity.this.mImageItems.get(ImagePreviewActivity.this.mCurrentPosition);
                boolean isSelected = ImagePreviewActivity.this.imagePicker.isSelect(item);
                ImagePreviewActivity.this.mCbCheck.setChecked(isSelected);
                ImagePreviewActivity.this.mTitleCount.setText(ImagePreviewActivity.this.getString(R.string.ip_preview_image_count, ImagePreviewActivity.this.mCurrentPosition + 1, ImagePreviewActivity.this.mImageItems.size()));
                ImagePreviewActivity.this.thumbPreviewAdapter.setSelected(item);
            }
        });
        this.mCbCheck.setOnClickListener(v -> {
            ImageItem imageItem = ImagePreviewActivity.this.mImageItems.get(ImagePreviewActivity.this.mCurrentPosition);
            int selectLimit = ImagePreviewActivity.this.imagePicker.getSelectLimit();
            if (ImagePreviewActivity.this.mCbCheck.isChecked() && ImagePreviewActivity.this.selectedImages.size() >= selectLimit) {
                Toast.makeText(ImagePreviewActivity.this, ImagePreviewActivity.this.getString(R.string.ip_select_limit, selectLimit), Toast.LENGTH_SHORT).show();
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

        });
        topBar.setPadding(0, ThemeUtils.getStatusBarHeight(this), 0, 0);
        this.topBar.setBackgroundColor(Color.parseColor(ColorCofig.INSTANCE.getNaviBgColor()));
        this.bottomBar.setBackgroundColor(Color.parseColor(ColorCofig.INSTANCE.getToolbarBgColor()));
        this.mTitleCount.setTextColor(Color.parseColor(ColorCofig.INSTANCE.getNaviTitleColor()));
        tvPreviewEdit.setTextColor(Color.parseColor(ColorCofig.INSTANCE.getToolbarTitleColorNormal()));
        this.mCbOrigin.setTextColor(Color.parseColor(ColorCofig.INSTANCE.getToolbarTitleColorNormal()));
        this.mCbCheck.setTextColor(Color.parseColor(ColorCofig.INSTANCE.getToolbarTitleColorNormal()));
    }

    public void onImageSelected(int position, ImageItem item, boolean isAdd) {
        if (this.imagePicker.getSelectImageCount() > 0) {
            this.mBtnOk.setText(this.getString(R.string.ip_select_complete, this.imagePicker.getSelectImageCount(), this.imagePicker.getSelectLimit()));
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
                ImageItem imageItem = this.mImageItems.get(this.mCurrentPosition);
                this.imagePicker.addSelectedImageItem(this.mCurrentPosition, imageItem, this.mCbCheck.isChecked());
            }

            intent = new Intent();
            intent.putExtra(com.jw.galary.img.ImagePicker.EXTRA_IMAGE_ITEMS, this.imagePicker.getSelectedImages());
            this.setResult(com.jw.galary.img.ImagePicker.RESULT_CODE_IMAGE_ITEMS, intent);
            this.finish();
        } else if (id == R.id.btn_back) {
            intent = new Intent();
            this.setResult(com.jw.galary.img.ImagePicker.RESULT_CODE_IMAGE_BACK, intent);
            this.finish();
        } else if (id == R.id.tv_preview_edit) {
            this.startActivityForResult(CropActivity.callingIntent(this, Uri.fromFile(new File(this.mImageItems.get(this.mCurrentPosition).path))), com.jw.galary.img.ImagePicker.REQUEST_CODE_IMAGE_CROP);
        }

    }

    public void onBackPressed() {
        Intent intent = new Intent();
        this.setResult(com.jw.galary.img.ImagePicker.RESULT_CODE_IMAGE_BACK, intent);
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
            this.topBar.setAnimation(AnimationUtils.loadAnimation(this, R.anim.top_out));
            this.bottomBar.setAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out));
            this.topBar.setVisibility(View.GONE);
            this.bottomBar.setVisibility(View.GONE);
            ThemeUtils.changeStatusBar(this, Color.TRANSPARENT);
        } else {
            ThemeUtils.changeStatusBar(this, Color.parseColor("#393A3F"));
            this.topBar.setAnimation(AnimationUtils.loadAnimation(this, R.anim.top_in));
            this.bottomBar.setAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
            this.topBar.setVisibility(View.VISIBLE);
            this.bottomBar.setVisibility(View.VISIBLE);
            topBar.setPadding(0, ThemeUtils.getStatusBarHeight(this), 0, 0);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && data.getExtras() != null) {
            if (resultCode == -1 && requestCode == com.jw.galary.img.ImagePicker.REQUEST_CODE_IMAGE_CROP) {
                Uri resultUri = data.getParcelableExtra(com.jw.galary.img.ImagePicker.EXTRA_CROP_IMAGE_OUT_URI);
                if (resultUri != null) {
                    int fromSelectedPosition = -1;

                    for (int i = 0; i < this.selectedImages.size(); ++i) {
                        if (this.selectedImages.get(i).path.equals(this.mImageItems.get(this.mCurrentPosition).path)) {
                            fromSelectedPosition = i;
                            break;
                        }
                    }

                    ImageItem imageItem = new ImageItem();
                    imageItem.path = resultUri.getPath();
                    if (fromSelectedPosition != -1) {
                        this.imagePicker.addSelectedImageItem(fromSelectedPosition, this.selectedImages.get(fromSelectedPosition), false);
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
