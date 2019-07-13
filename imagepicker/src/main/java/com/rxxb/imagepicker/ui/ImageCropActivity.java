

package com.rxxb.imagepicker.ui;

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
import com.rxxb.imagepicker.ImagePicker;
import com.rxxb.imagepicker.R;
import com.rxxb.imagepicker.R.id;
import com.rxxb.imagepicker.R.layout;
import com.rxxb.imagepicker.R.string;
import com.rxxb.imagepicker.bean.ImageItem;
import com.rxxb.imagepicker.util.BitmapUtil;
import com.rxxb.imagepicker.view.CropImageView;
import com.rxxb.imagepicker.view.CropImageView.OnBitmapSaveCompleteListener;
import java.io.File;
import java.util.ArrayList;

import static com.rxxb.imagepicker.ImagePicker.RESULT_CODE_IMAGE_ITEMS;

public class ImageCropActivity extends ImageBaseActivity implements OnClickListener, OnBitmapSaveCompleteListener {
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
        this.setContentView(layout.activity_image_crop);
        this.imagePicker = ImagePicker.getInstance();
        this.findViewById(id.btn_back).setOnClickListener(this);
        Button btn_ok = (Button)this.findViewById(id.btn_ok);
        btn_ok.setText(this.getString(string.ip_complete));
        btn_ok.setOnClickListener(this);
        TextView tv_des = (TextView)this.findViewById(id.tv_des);
        tv_des.setText(this.getString(string.ip_photo_crop));
        this.mCropImageView = (CropImageView)this.findViewById(id.cv_crop_image);
        this.mCropImageView.setOnBitmapSaveCompleteListener(this);
        this.mOutputX = this.imagePicker.getOutPutX();
        this.mOutputY = this.imagePicker.getOutPutY();
        this.mIsSaveRectangle = this.imagePicker.isSaveRectangle();
        this.mImageItems = this.imagePicker.getSelectedImages();
        String imagePath = ((ImageItem)this.mImageItems.get(0)).path;
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
            this.mCropImageView.saveBitmapToFile(this.imagePicker.getCropCacheFolder(this), this.mOutputX, this.mOutputY, this.mIsSaveRectangle);
        }

    }

    public void onBitmapSaveSuccess(File file) {
        this.mImageItems.remove(0);
        ImageItem imageItem = new ImageItem();
        imageItem.path = file.getAbsolutePath();
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
        this.mCropImageView.setOnBitmapSaveCompleteListener((OnBitmapSaveCompleteListener)null);
        if (null != this.mBitmap && !this.mBitmap.isRecycled()) {
            this.mBitmap.recycle();
            this.mBitmap = null;
        }

    }
}
