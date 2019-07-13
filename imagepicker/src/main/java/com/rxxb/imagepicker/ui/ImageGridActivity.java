

package com.rxxb.imagepicker.ui;

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
import com.rxxb.imagepicker.DataHolder;
import com.rxxb.imagepicker.ImageDataSource;
import com.rxxb.imagepicker.ImageDataSource.OnImagesLoadedListener;
import com.rxxb.imagepicker.ImagePicker;
import com.rxxb.imagepicker.ImagePicker.*;
import com.rxxb.imagepicker.R;
import com.rxxb.imagepicker.R.id;
import com.rxxb.imagepicker.R.string;
import com.rxxb.imagepicker.adapter.ImageFolderAdapter;
import com.rxxb.imagepicker.adapter.ImageRecyclerAdapter;
import com.rxxb.imagepicker.adapter.ImageRecyclerAdapter.OnImageItemClickListener;
import com.rxxb.imagepicker.bean.ImageFolder;
import com.rxxb.imagepicker.bean.ImageItem;
import com.rxxb.imagepicker.util.Utils;
import com.rxxb.imagepicker.view.FolderPopUpWindow;
import com.rxxb.imagepicker.view.FolderPopUpWindow.OnItemClickListener;
import com.rxxb.imagepicker.view.GridSpacingItemDecoration;
import com.rxxb.imagepicker.view.SuperCheckBox;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.rxxb.imagepicker.ImagePicker.*;

public class ImageGridActivity extends ImageBaseActivity implements OnImagesLoadedListener, OnImageItemClickListener, OnImageSelectedListener, OnClickListener, OnCheckedChangeListener {
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
        this.imagePicker = ImagePicker.getInstance();
        this.imagePicker.clear();
        this.imagePicker.addOnImageSelectedListener(this);
        Intent data = this.getIntent();
        if (data != null && data.getExtras() != null) {
            this.directPhoto = data.getBooleanExtra(EXTRAS_TAKE_PICKERS, false);
            if (this.directPhoto) {
                if (!this.checkPermission("android.permission.CAMERA")) {
                    ActivityCompat.requestPermissions(this, new String[]{"android.permission.CAMERA"}, REQUEST_PERMISSION_CAMERA);
                } else {
                    this.imagePicker.takePicture(this, REQUEST_CODE_IMAGE_TAKE);
                }
            }

            ArrayList<ImageItem> images = (ArrayList) data.getSerializableExtra(EXTRAS_IMAGES);
            this.imagePicker.setSelectedImages(images);
        }

        this.mRecyclerView = (RecyclerView) this.findViewById(id.recycler);
        this.mCbOrigin = (SuperCheckBox) this.findViewById(id.cb_origin);
        this.mCbOrigin.setOnCheckedChangeListener(this);
        this.mCbOrigin.setChecked(this.imagePicker.isOrigin());
        this.findViewById(id.btn_back).setOnClickListener(this);
        this.mBtnOk = (Button) this.findViewById(id.btn_ok);
        this.mBtnOk.setOnClickListener(this);
        this.mBtnPre = (TextView) this.findViewById(id.btn_preview);
        this.mBtnPre.setOnClickListener(this);
        this.mFooterBar = this.findViewById(id.footer_bar);
        this.mllDir = this.findViewById(id.ll_dir);
        this.mllDir.setOnClickListener(this);
        this.mtvDir = (TextView) this.findViewById(id.tv_dir);
        if (this.imagePicker.isMultiMode()) {
            this.mBtnOk.setVisibility(View.VISIBLE);
            this.mBtnPre.setVisibility(View.VISIBLE);
        } else {
            this.mBtnOk.setVisibility(View.GONE);
            this.mBtnPre.setVisibility(View.GONE);
        }

        this.setConfirmButtonBg(this.mBtnOk);
        this.findViewById(id.top_bar).setBackgroundColor(Color.parseColor(this.imagePicker.getViewColor().getNaviBgColor()));
        ((TextView) this.findViewById(id.tv_des)).setTextColor(Color.parseColor(this.imagePicker.getViewColor().getNaviTitleColor()));
        this.mFooterBar.setBackgroundColor(Color.parseColor(this.imagePicker.getViewColor().getToolbarBgColor()));
        this.mBtnPre.setTextColor(Color.parseColor(this.imagePicker.getViewColor().getToolbarTitleColorDisabled()));
        this.mCbOrigin.setTextColor(Color.parseColor(this.imagePicker.getViewColor().getToolbarTitleColorNormal()));
        this.mtvDir.setTextColor(Color.parseColor(this.imagePicker.getViewColor().getToolbarTitleColorNormal()));
        this.mImageFolderAdapter = new ImageFolderAdapter(this, (List) null);
        this.mRecyclerAdapter = new ImageRecyclerAdapter(this, (ArrayList) null);
        this.onImageSelected(0, (ImageItem) null, false);
        if (this.checkPermission("android.permission.WRITE_EXTERNAL_STORAGE")) {
            new ImageDataSource(this, (String) null, this);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, REQUEST_PERMISSION_STORAGE);
        }

    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == 0) {
                new ImageDataSource(this, (String) null, this);
            } else {
                this.showToast("权限被禁止，无法选择本地图片");
            }
        } else if (requestCode == REQUEST_PERMISSION_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == 0) {
                this.imagePicker.takePicture(this, REQUEST_CODE_IMAGE_TAKE);
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
            intent.putExtra(EXTRA_SELECTED_IMAGE_POSITION, 0);
            intent.putExtra(EXTRA_IMAGE_ITEMS, this.imagePicker.getSelectedImages());
            intent.putExtra(EXTRA_FROM_IMAGE_ITEMS, true);
            this.startActivityForResult(intent, REQUEST_CODE_IMAGE_PREVIEW);
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
            this.mRecyclerAdapter.refreshData((ArrayList) null);
        } else {
            this.mRecyclerAdapter.refreshData(((ImageFolder) imageFolders.get(0)).images);
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
            intent.putExtra(EXTRA_SELECTED_IMAGE_POSITION, position);
            DataHolder.getInstance().save("dh_current_image_folder_items", this.imagePicker.getCurrentImageFolderItems());
            this.startActivityForResult(intent, REQUEST_CODE_IMAGE_PREVIEW);
        } else {
            this.imagePicker.clearSelectedImages();
            this.imagePicker.addSelectedImageItem(position, (ImageItem) this.imagePicker.getCurrentImageFolderItems().get(position), true);
            if (this.imagePicker.isCrop()) {
                this.startActivityForResult(CropActivity.callingIntent(this, Uri.fromFile(new File(imageItem.path))), REQUEST_CODE_IMAGE_CROP);
            } else {
                backGalary();
            }
        }

    }

    @SuppressLint({"StringFormatMatches"})
    public void onImageSelected(int position, ImageItem item, boolean isAdd) {
        if (this.imagePicker.getSelectImageCount() > 0) {
            this.mBtnOk.setText(this.getString(string.ip_select_complete, new Object[]{this.imagePicker.getSelectImageCount(), this.imagePicker.getSelectLimit()}));
            this.mBtnOk.setEnabled(true);
            this.mBtnPre.setEnabled(true);
            this.mBtnPre.setText(this.getResources().getString(string.ip_preview_count, new Object[]{this.imagePicker.getSelectImageCount()}));
            this.mBtnPre.setTextColor(Color.parseColor(this.imagePicker.getViewColor().getToolbarTitleColorNormal()));
            this.mBtnOk.setTextColor(Color.parseColor(this.imagePicker.getViewColor().getToolbarTitleColorNormal()));
        } else {
            this.mBtnOk.setText(this.getString(string.ip_complete));
            this.mBtnOk.setEnabled(false);
            this.mBtnPre.setEnabled(false);
            this.mBtnPre.setText(this.getResources().getString(string.ip_preview));
            this.mBtnPre.setTextColor(Color.parseColor(this.imagePicker.getViewColor().getToolbarTitleColorDisabled()));
            this.mBtnOk.setTextColor(Color.parseColor(this.imagePicker.getViewColor().getToolbarTitleColorDisabled()));
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
            if (requestCode == REQUEST_CODE_IMAGE_CROP) {
                Uri resultUri = (Uri) data.getParcelableExtra(EXTRA_CROP_IMAGE_OUT_URI);
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
        } else if (resultCode == RESULT_CODE_IMAGE_BACK) {
            this.mCbOrigin.setChecked(this.imagePicker.isOrigin());
        } else if (resultCode == -1 && requestCode == REQUEST_CODE_IMAGE_TAKE) {
            ImagePicker.galleryAddPic(this, this.imagePicker.getTakeImageFile());
            String path = this.imagePicker.getTakeImageFile().getAbsolutePath();
            imageItem = new ImageItem();
            imageItem.path = path;
            this.imagePicker.clearSelectedImages();
            this.imagePicker.addSelectedImageItem(0, imageItem, true);
            if (this.imagePicker.isCrop()) {
                this.startActivityForResult(CropActivity.callingIntent(this, Uri.fromFile(new File(imageItem.path))), REQUEST_CODE_IMAGE_CROP);
            } else {
                backGalary();
            }
        } else if (this.directPhoto) {
            this.finish();
        }

    }

    private void backGalary(){
        Intent intent = new Intent();
        intent.putExtra(EXTRA_IMAGE_ITEMS, this.imagePicker.getSelectedImages());
        this.setResult(RESULT_CODE_IMAGE_ITEMS, intent);
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
