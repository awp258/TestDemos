

package com.jw.galary.img.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.jw.galary.img.ImagePicker;
import com.jw.galary.img.crop.CropIwaView;
import com.jw.galary.img.crop.CropIwaView.CropSaveCompleteListener;
import com.jw.galary.img.crop.CropIwaView.ErrorListener;
import com.jw.galary.img.crop.config.CropIwaSaveConfig.Builder;
import com.jw.galary.img.crop.shape.CropIwaOvalShape;
import com.jw.galary.img.view.CropImageView.Style;
import com.jw.uploaddemo.ColorCofig;
import com.jw.uploaddemo.R;
import com.jw.uploaddemo.uploadPlugin.UploadPluginActivity;

import java.io.File;

public class CropActivity extends UploadPluginActivity implements OnClickListener {
    private static final String EXTRA_URI = "CropImage";
    private ImagePicker imagePicker;
    private CropIwaView cropView;
    private ProgressDialog mProgressDialog;
    private String dstPath;
    private float originAngle;
    private View topBar;
    private View bottomBar;

    public CropActivity() {
    }

    public static Intent callingIntent(Context context, Uri imageUri) {
        Intent intent = new Intent(context, CropActivity.class);
        intent.putExtra("CropImage", imageUri);
        return intent;
    }

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_crop);
        this.findViewById(R.id.tv_rotate).setOnClickListener(this);
        this.findViewById(R.id.tv_recover).setOnClickListener(this);
        this.findViewById(R.id.btn_back).setOnClickListener(this);
        Button mBtnOk = this.findViewById(R.id.btn_ok);
        mBtnOk.setText(this.getString(R.string.ip_complete));
        mBtnOk.setOnClickListener(this);
        this.imagePicker = ImagePicker.INSTANCE;
        Uri imageUri = this.getIntent().getParcelableExtra("CropImage");
        this.cropView = this.findViewById(R.id.cv_crop_image);
        this.cropView.setImageUri(imageUri);
        this.cropView.configureOverlay().setAspectRatio(this.imagePicker.getAspectRatio()).setDynamicCrop(this.imagePicker.isDynamicCrop()).apply();
        this.cropView.setOnClickListener(this);
        if (this.imagePicker.getStyle() == Style.CIRCLE) {
            this.cropView.configureOverlay().setCropShape(new CropIwaOvalShape(this.cropView.configureOverlay())).apply();
        }

        File cropCacheFolder;
        if (this.imagePicker.getCutType() == 2) {
            cropCacheFolder = new File(Environment.getExternalStorageDirectory() + "/RXImagePicker/");
        } else {
            cropCacheFolder = ImagePicker.INSTANCE.getCropCacheFolder();
        }

        if (!cropCacheFolder.exists() || !cropCacheFolder.isDirectory()) {
            cropCacheFolder.mkdirs();
        }

        this.dstPath = (new File(cropCacheFolder, "IMG_" + System.currentTimeMillis() + ".png")).getAbsolutePath();
        this.cropView.setCropSaveCompleteListener(new CropSaveCompleteListener() {
            public void onCroppedRegionSaved(Uri bitmapUri) {
                CropActivity.this.dismiss();
                ImagePicker.INSTANCE.galleryAddPic(CropActivity.this.getApplicationContext(), bitmapUri);
                Intent intent = new Intent();
                intent.putExtra(ImagePicker.EXTRA_CROP_IMAGE_OUT_URI, bitmapUri);
                CropActivity.this.setResult(-1, intent);
                CropActivity.this.finish();
            }
        });
        this.cropView.setErrorListener(new ErrorListener() {
            public void onError(Throwable e) {
                CropActivity.this.dismiss();
            }
        });
        this.originAngle = this.cropView.getMatrixAngle();
        this.setConfirmButtonBg(mBtnOk);
        topBar = this.findViewById(R.id.top_bar);
        bottomBar = this.findViewById(R.id.bottom_bar);
        topBar.setBackgroundColor(Color.parseColor(ColorCofig.INSTANCE.getNaviBgColor()));
        ((TextView) this.findViewById(R.id.tv_des)).setTextColor(Color.parseColor(ColorCofig.INSTANCE.getNaviTitleColor()));
    }

    private void dismiss() {
        if (this.mProgressDialog != null && this.mProgressDialog.isShowing()) {
            this.mProgressDialog.dismiss();
        }

    }

    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_back) {
            this.setResult(0);
            this.finish();
        } else if (id == R.id.tv_rotate) {
            this.cropView.rotateImage(90.0F);
        } else if (id == R.id.tv_recover) {
            float currentAngle = this.cropView.getMatrixAngle();
            if (this.originAngle != currentAngle) {
                this.cropView.rotateImage(this.originAngle - currentAngle);
            }

            this.cropView.initialize();
        } else if (id == R.id.btn_ok) {
            if (this.mProgressDialog == null) {
                this.mProgressDialog = new ProgressDialog(this);
                this.mProgressDialog.setMessage("正在处理中...");
                this.mProgressDialog.setCanceledOnTouchOutside(false);
                this.mProgressDialog.setCancelable(false);
            }

            this.mProgressDialog.show();
            Builder builder = new Builder(Uri.fromFile(new File(this.dstPath)));
            if (this.imagePicker.getOutPutX() != 0 && this.imagePicker.getOutPutY() != 0 && !this.imagePicker.isOrigin()) {
                builder.setSize(this.imagePicker.getOutPutX(), this.imagePicker.getOutPutY());
            }

            builder.setQuality(this.imagePicker.getQuality());
            this.cropView.crop(builder.build());
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        this.dismiss();
        this.mProgressDialog = null;
    }
}
