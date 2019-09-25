

package com.jw.galary.img.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.jw.galary.img.ImagePicker;
import com.jw.galary.img.bean.ImageItem;
import com.jw.galary.img.util.BitmapUtil;
import com.jw.galary.img.view.CropImageView;
import com.jw.galary.img.view.CropImageView.OnBitmapSaveCompleteListener;
import com.jw.uploaddemo.R;
import com.jw.uploaddemo.uploadPlugin.UploadPluginActivity;

import java.io.File;
import java.util.ArrayList;

import static com.jw.galary.img.ImagePicker.RESULT_CODE_IMAGE_ITEMS;

public class ImageCropActivity extends UploadPluginActivity implements OnClickListener, OnBitmapSaveCompleteListener {
    private CropImageView mCropImageView;
    private Bitmap mBitmap;
    private boolean mIsSaveRectangle;
    private int mOutputX;
    private int mOutputY;
    private ArrayList<ImageItem> mImageItems;
    private ImagePicker imagePicker;

    public ImageCropActivity() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_image_crop);
        this.imagePicker = ImagePicker.INSTANCE;
        this.findViewById(R.id.btn_back).setOnClickListener(this);
        Button btn_ok = this.findViewById(R.id.btn_ok);
        btn_ok.setText(this.getString(R.string.ip_complete));
        btn_ok.setOnClickListener(this);
        TextView tv_des = this.findViewById(R.id.tv_des);
        tv_des.setText(this.getString(R.string.ip_photo_crop));
        this.mCropImageView = this.findViewById(R.id.cv_crop_image);
        this.mCropImageView.setOnBitmapSaveCompleteListener(this);
        this.mOutputX = this.imagePicker.getOutPutX();
        this.mOutputY = this.imagePicker.getOutPutY();
        this.mIsSaveRectangle = this.imagePicker.isSaveRectangle();
        this.mImageItems = this.imagePicker.getSelectedImages();
        String imagePath = this.mImageItems.get(0).getPath();
        this.mCropImageView.setFocusStyle(this.imagePicker.getStyle());
        this.mCropImageView.setFocusWidth(this.imagePicker.getFocusWidth());
        this.mCropImageView.setFocusHeight(this.imagePicker.getFocusHeight());
        Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        options.inSampleSize = this.calculateInSampleSize(options, displayMetrics.widthPixels, displayMetrics.heightPixels);
        options.inJustDecodeBounds = false;
        this.mBitmap = BitmapFactory.decodeFile(imagePath, options);
        this.mCropImageView.setImageBitmap(this.mCropImageView.rotate(this.mBitmap, BitmapUtil.getBitmapDegree(imagePath)));
    }

    public int calculateInSampleSize(Options options, int reqWidth, int reqHeight) {
        int width = options.outWidth;
        int height = options.outHeight;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = width / reqWidth;
            } else {
                inSampleSize = height / reqHeight;
            }
        }

        return inSampleSize;
    }

    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_back) {
            this.setResult(0);
            this.finish();
        } else if (id == R.id.btn_ok) {
            this.mCropImageView.saveBitmapToFile(ImagePicker.INSTANCE.getCropCacheFolder(), this.mOutputX, this.mOutputY, this.mIsSaveRectangle);
        }

    }

    public void onBitmapSaveSuccess(File file) {
        this.mImageItems.remove(0);
        ImageItem imageItem = new ImageItem();
        imageItem.setPath(file.getAbsolutePath());
        this.mImageItems.add(imageItem);
        Intent intent = new Intent();
        intent.putExtra("extra_result_items", this.mImageItems);
        this.setResult(RESULT_CODE_IMAGE_ITEMS, intent);
        this.finish();
    }

    public void onBitmapSaveError(File file) {
    }

    protected void onDestroy() {
        super.onDestroy();
        this.mCropImageView.setOnBitmapSaveCompleteListener(null);
        if (null != this.mBitmap && !this.mBitmap.isRecycled()) {
            this.mBitmap.recycle();
            this.mBitmap = null;
        }

    }
}
