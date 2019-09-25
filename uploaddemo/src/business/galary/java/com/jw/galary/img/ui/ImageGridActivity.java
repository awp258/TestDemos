

package com.jw.galary.img.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.jw.galary.img.DataHolder;
import com.jw.galary.img.ImageDataSource;
import com.jw.galary.img.ImageDataSource.OnImagesLoadedListener;
import com.jw.galary.img.ImagePicker;
import com.jw.galary.img.ImagePicker.OnImageSelectedListener;
import com.jw.galary.img.adapter.ImageFolderAdapter;
import com.jw.galary.img.adapter.ImageRecyclerAdapter;
import com.jw.galary.img.adapter.ImageRecyclerAdapter.OnImageItemClickListener;
import com.jw.galary.img.bean.ImageFolder;
import com.jw.galary.img.bean.ImageItem;
import com.jw.galary.img.util.Utils;
import com.jw.galary.img.view.FolderPopUpWindow;
import com.jw.galary.img.view.FolderPopUpWindow.OnItemClickListener;
import com.jw.galary.img.view.GridSpacingItemDecoration;
import com.jw.galary.img.view.SuperCheckBox;
import com.jw.uploaddemo.ColorCofig;
import com.jw.uploaddemo.R;
import com.jw.uploaddemo.uploadPlugin.UploadPluginActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImageGridActivity extends UploadPluginActivity implements OnImagesLoadedListener, OnImageItemClickListener, OnImageSelectedListener, OnClickListener, OnCheckedChangeListener {
    public static final int REQUEST_PERMISSION_STORAGE = 1;
    public static final int REQUEST_PERMISSION_CAMERA = 2;
    public static final String EXTRAS_TAKE_PICKERS = "TAKE";
    public static final String EXTRAS_IMAGES = "IMAGES";
    public static final int SPAN_COUNT = 4;
    private ImagePicker imagePicker;
    private SuperCheckBox mCbOrigin;
    private View mFooterBar;
    private Button mBtnOk;
    private View mllDir;
    private TextView mtvDir;
    private TextView mBtnPre;
    private ImageFolderAdapter mImageFolderAdapter;
    private FolderPopUpWindow mFolderPopupWindow;
    private List<ImageFolder> mImageFolders;
    private boolean directPhoto = false;
    private RecyclerView mRecyclerView;
    private ImageRecyclerAdapter mRecyclerAdapter;

    public ImageGridActivity() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_image_grid);
        this.imagePicker = ImagePicker.INSTANCE;
        this.imagePicker.clear();
        this.imagePicker.addOnImageSelectedListener(this);
        Intent data = this.getIntent();
        if (data != null && data.getExtras() != null) {
            this.directPhoto = data.getBooleanExtra(EXTRAS_TAKE_PICKERS, false);
            if (this.directPhoto) {
                if (!this.checkPermission("android.permission.CAMERA")) {
                    ActivityCompat.requestPermissions(this, new String[]{"android.permission.CAMERA"}, REQUEST_PERMISSION_CAMERA);
                } else {
                    this.imagePicker.takePicture(this, ImagePicker.REQUEST_CODE_IMAGE_TAKE);
                }
            }

            ArrayList<ImageItem> images = (ArrayList) data.getSerializableExtra(EXTRAS_IMAGES);
            this.imagePicker.setSelectedImages(images);
        }

        this.mRecyclerView = this.findViewById(R.id.recycler);
        this.mCbOrigin = this.findViewById(R.id.cb_origin);
        this.mCbOrigin.setOnCheckedChangeListener(this);
        this.mCbOrigin.setChecked(this.imagePicker.isOrigin());
        this.findViewById(R.id.btn_back).setOnClickListener(this);
        this.mBtnOk = this.findViewById(R.id.btn_ok);
        this.mBtnOk.setOnClickListener(this);
        this.mBtnPre = this.findViewById(R.id.btn_preview);
        this.mBtnPre.setOnClickListener(this);
        this.mFooterBar = this.findViewById(R.id.footer_bar);
        this.mllDir = this.findViewById(R.id.ll_dir);
        this.mllDir.setOnClickListener(this);
        this.mtvDir = this.findViewById(R.id.tv_dir);
        if (this.imagePicker.isMultiMode()) {
            this.mBtnOk.setVisibility(View.VISIBLE);
            this.mBtnPre.setVisibility(View.VISIBLE);
        } else {
            this.mBtnOk.setVisibility(View.GONE);
            this.mBtnPre.setVisibility(View.GONE);
        }

        this.setConfirmButtonBg(this.mBtnOk);
        this.findViewById(R.id.top_bar).setBackgroundColor(Color.parseColor(ColorCofig.INSTANCE.getNaviBgColor()));
        ((TextView) this.findViewById(R.id.tv_des)).setTextColor(Color.parseColor(ColorCofig.INSTANCE.getNaviTitleColor()));
        this.mFooterBar.setBackgroundColor(Color.parseColor(ColorCofig.INSTANCE.getToolbarBgColor()));
        this.mBtnPre.setTextColor(Color.parseColor(ColorCofig.INSTANCE.getToolbarTitleColorDisabled()));
        this.mCbOrigin.setTextColor(Color.parseColor(ColorCofig.INSTANCE.getToolbarTitleColorNormal()));
        this.mtvDir.setTextColor(Color.parseColor(ColorCofig.INSTANCE.getToolbarTitleColorNormal()));
        this.mImageFolderAdapter = new ImageFolderAdapter(this, null);
        this.mRecyclerAdapter = new ImageRecyclerAdapter(this, null);
        this.onImageSelected(0, null, false);
        if (this.checkPermission("android.permission.WRITE_EXTERNAL_STORAGE")) {
            new ImageDataSource(this, null, this);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, REQUEST_PERMISSION_STORAGE);
        }

    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == 0) {
                new ImageDataSource(this, null, this);
            } else {
                this.showToast("权限被禁止，无法选择本地图片");
            }
        } else if (requestCode == REQUEST_PERMISSION_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == 0) {
                this.imagePicker.takePicture(this, ImagePicker.REQUEST_CODE_IMAGE_TAKE);
            } else {
                this.showToast("权限被禁止，无法打开相机");
            }
        }

    }

    protected void onDestroy() {
        this.imagePicker.removeOnImageSelectedListener(this);
        super.onDestroy();
    }

    public void onClick(View v) {
        int id = v.getId();
        Intent intent;
        if (id == R.id.btn_ok) {
/*            if(ImagePicker.getInstance().getAspectRatio().getRatio()!=1.0){
                Toast.makeText(this,"请裁剪全部图片！",Toast.LENGTH_SHORT).show();
                intent = new Intent(this, ImagePreviewActivity.class);
                intent.putExtra(EXTRA_SELECTED_IMAGE_POSITION, 0);
                intent.putExtra(EXTRA_IMAGE_ITEMS, this.imagePicker.getSelectedImages());
                intent.putExtra(EXTRA_FROM_IMAGE_ITEMS, true);
                this.startActivityForResult(intent, REQUEST_CODE_IMAGE_PREVIEW);
            }else{
                backGalary();
            }*/
            backGalary();
        } else if (id == R.id.ll_dir) {
            if (this.mImageFolders == null) {
                Log.i("ImageGridActivity", "您的手机没有图片");
                return;
            }

            this.createPopupFolderList();
            this.mImageFolderAdapter.refreshData(this.mImageFolders);
            if (this.mFolderPopupWindow.isShowing()) {
                this.mFolderPopupWindow.dismiss();
            } else {
                this.mFolderPopupWindow.showAtLocation(this.mFooterBar, 0, 0, 0);
                int index = this.mImageFolderAdapter.getSelectIndex();
                index = index == 0 ? index : index - 1;
                this.mFolderPopupWindow.setSelection(index);
            }
        } else if (id == R.id.btn_preview) {
            intent = new Intent(this, ImagePreviewActivity.class);
            intent.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, 0);
            intent.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, this.imagePicker.getSelectedImages());
            intent.putExtra(ImagePicker.EXTRA_FROM_IMAGE_ITEMS, true);
            this.startActivityForResult(intent, ImagePicker.REQUEST_CODE_IMAGE_PREVIEW);
        } else if (id == R.id.btn_back) {
            this.finish();
        }

    }

    private void createPopupFolderList() {
        this.mFolderPopupWindow = new FolderPopUpWindow(this, this.mImageFolderAdapter);
        this.mFolderPopupWindow.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                ImageGridActivity.this.mImageFolderAdapter.setSelectIndex(position);
                ImageGridActivity.this.imagePicker.setCurrentImageFolderPosition(position);
                ImageGridActivity.this.mFolderPopupWindow.dismiss();
                ImageFolder imageFolder = (ImageFolder) adapterView.getAdapter().getItem(position);
                if (null != imageFolder) {
                    ImageGridActivity.this.mRecyclerAdapter.refreshData(imageFolder.images);
                    ImageGridActivity.this.mtvDir.setText(imageFolder.name);
                }

            }
        });
        this.mFolderPopupWindow.setMargin(this.mFooterBar.getHeight());
    }

    public void onImagesLoaded(List<ImageFolder> imageFolders) {
        this.mImageFolders = imageFolders;
        this.imagePicker.setImageFolders(imageFolders);
        if (imageFolders.size() == 0) {
            this.mRecyclerAdapter.refreshData(null);
        } else {
            this.mRecyclerAdapter.refreshData(imageFolders.get(0).images);
        }

        this.mRecyclerAdapter.setOnImageItemClickListener(this);
        this.mRecyclerView.setLayoutManager(new GridLayoutManager(this, SPAN_COUNT));
        this.mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(SPAN_COUNT, Utils.dp2px(this, 2.0F), false));
        this.mRecyclerView.setAdapter(this.mRecyclerAdapter);
        this.mImageFolderAdapter.refreshData(imageFolders);
    }

    public void onImageItemClick(View view, ImageItem imageItem, int position) {
        position = this.imagePicker.isShowCamera() ? position - 1 : position;
        Intent intent;
        if (this.imagePicker.isMultiMode()) {
            intent = new Intent(this, ImagePreviewActivity.class);
            intent.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, position);
            DataHolder.getInstance().save("dh_current_image_folder_items", this.imagePicker.getCurrentImageFolderItems());
            this.startActivityForResult(intent, ImagePicker.REQUEST_CODE_IMAGE_PREVIEW);
        } else {
            this.imagePicker.clearSelectedImages();
            this.imagePicker.addSelectedImageItem(position, this.imagePicker.getCurrentImageFolderItems().get(position), true);
            if (this.imagePicker.isCrop()) {
                this.startActivityForResult(CropActivity.callingIntent(this, Uri.fromFile(new File(imageItem.path))), ImagePicker.REQUEST_CODE_IMAGE_CROP);
            } else {
                backGalary();
            }
        }

    }

    @SuppressLint({"StringFormatMatches"})
    public void onImageSelected(int position, ImageItem item, boolean isAdd) {
        if (this.imagePicker.getSelectImageCount() > 0) {
            this.mBtnOk.setText(this.getString(R.string.ip_select_complete, this.imagePicker.getSelectImageCount(), this.imagePicker.getSelectLimit()));
            this.mBtnOk.setEnabled(true);
            this.mBtnPre.setEnabled(true);
            this.mBtnPre.setText(this.getResources().getString(R.string.ip_preview_count, this.imagePicker.getSelectImageCount()));
            this.mBtnPre.setTextColor(Color.parseColor(ColorCofig.INSTANCE.getToolbarTitleColorNormal()));
            this.mBtnOk.setTextColor(Color.parseColor(ColorCofig.INSTANCE.getToolbarTitleColorNormal()));
        } else {
            this.mBtnOk.setText(this.getString(R.string.ip_complete));
            this.mBtnOk.setEnabled(false);
            this.mBtnPre.setEnabled(false);
            this.mBtnPre.setText(this.getResources().getString(R.string.ip_preview));
            this.mBtnPre.setTextColor(Color.parseColor(ColorCofig.INSTANCE.getToolbarTitleColorDisabled()));
            this.mBtnOk.setTextColor(Color.parseColor(ColorCofig.INSTANCE.getToolbarTitleColorDisabled()));
        }

        if (this.imagePicker.isMultiMode()) {
            for (int i = this.imagePicker.isShowCamera() ? 1 : 0; i < this.mRecyclerAdapter.getItemCount(); ++i) {
                if (this.mRecyclerAdapter.getItem(i).path != null && this.mRecyclerAdapter.getItem(i).path.equals(item.path)) {
                    this.mRecyclerAdapter.refreshCheckedData(i);
                    return;
                }
            }

        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImageItem imageItem;
        Intent intent;
        if (data != null && data.getExtras() != null) {
            if (requestCode == ImagePicker.REQUEST_CODE_IMAGE_CROP) {
                Uri resultUri = data.getParcelableExtra(ImagePicker.EXTRA_CROP_IMAGE_OUT_URI);
                if (resultUri != null) {
                    imageItem = new ImageItem();
                    imageItem.path = resultUri.getPath();
                    this.imagePicker.clearSelectedImages();
                    this.imagePicker.addSelectedImageItem(0, imageItem, true);
                    backGalary();
                }
            } else {
                backGalary();
            }
        } else if (resultCode == ImagePicker.RESULT_CODE_IMAGE_BACK) {
            this.mCbOrigin.setChecked(this.imagePicker.isOrigin());
        } else if (resultCode == -1 && requestCode == ImagePicker.REQUEST_CODE_IMAGE_TAKE) {
            ImagePicker.INSTANCE.galleryAddPic(this, this.imagePicker.getTakeImageFile());
            String path = this.imagePicker.getTakeImageFile().getAbsolutePath();
            imageItem = new ImageItem();
            imageItem.path = path;
            this.imagePicker.clearSelectedImages();
            this.imagePicker.addSelectedImageItem(0, imageItem, true);
            if (this.imagePicker.isCrop()) {
                this.startActivityForResult(CropActivity.callingIntent(this, Uri.fromFile(new File(imageItem.path))), ImagePicker.REQUEST_CODE_IMAGE_CROP);
            } else {
                backGalary();
            }
        } else if (this.directPhoto) {
            this.finish();
        }

    }

    private void backGalary(){
        Intent intent = new Intent();
        intent.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, this.imagePicker.getSelectedImages());
        this.setResult(ImagePicker.RESULT_CODE_IMAGE_ITEMS, intent);
        this.finish();
    }

    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        if (id == R.id.cb_origin) {
            if (isChecked) {
                this.imagePicker.setOrigin(true);
            } else {
                this.imagePicker.setOrigin(false);
            }
        }

    }
}
