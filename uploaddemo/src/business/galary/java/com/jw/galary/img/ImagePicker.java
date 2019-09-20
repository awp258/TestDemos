package com.jw.galary.img;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.util.Log;
import com.jw.galary.img.bean.ImageFolder;
import com.jw.galary.img.bean.ImageItem;
import com.jw.galary.img.crop.AspectRatio;
import com.jw.galary.img.loader.ImageLoader;
import com.jw.galary.img.util.ProviderUtil;
import com.jw.galary.img.util.Utils;
import com.jw.galary.img.view.CropImageView.Style;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class ImagePicker {
    public static final String TAG = ImagePicker.class.getSimpleName();
    public static final int REQUEST_CODE_IMAGE_TAKE = 1001;
    public static final int REQUEST_CODE_IMAGE_CROP = 1002;
    public static final int REQUEST_CODE_IMAGE_PREVIEW = 1003;
    public static final int RESULT_CODE_IMAGE_ITEMS = 1004;
    public static final int RESULT_CODE_IMAGE_BACK = 1005;
    public static final String EXTRA_SELECTED_IMAGE_POSITION = "selected_image_position";
    public static final String EXTRA_IMAGE_ITEMS = "extra_image_items";
    public static final String EXTRA_FROM_IMAGE_ITEMS = "extra_from_image_items";
    public static final String EXTRA_CROP_IMAGE_OUT_URI = "extra_crop_image_out_uri";
    private int cutType = 2;
    private boolean isOrigin = true;
    private boolean multiMode = true;
    private int selectLimit = 9;
    private boolean crop = true;
    private boolean isDynamicCrop = false;
    private boolean showCamera = false;
    private boolean isSaveRectangle = false;
    private int outPutX = 1000;
    private int outPutY = 1000;
    private int focusWidth = 280;
    private int focusHeight = 280;
    private int quality = 90;
    private ImageLoader imageLoader;
    private Style style;
    private AspectRatio aspectRatio;
    private File cropCacheFolder;
    private File takeImageFile;
    private ArrayList<ImageItem> mSelectedImages;
    private List<ImageFolder> mImageFolders;
    private int mCurrentImageFolderPosition;
    private List<ImagePicker.OnImageSelectedListener> mImageSelectedListeners;
    private static ImagePicker mInstance;

    private ImagePicker() {
        this.style = Style.RECTANGLE;
        this.aspectRatio = AspectRatio.IMG_SRC;
        this.mSelectedImages = new ArrayList();
        this.mCurrentImageFolderPosition = 0;
    }

    public static ImagePicker getInstance() {
        if (mInstance == null) {
            Class var0 = ImagePicker.class;
            synchronized (ImagePicker.class) {
                if (mInstance == null) {
                    mInstance = new ImagePicker();
                }
            }
        }

        return mInstance;
    }

    public int getCutType() {
        return this.cutType;
    }

    public void setCutType(int cutType) {
        this.cutType = cutType;
    }

    public boolean isMultiMode() {
        return this.multiMode;
    }

    public void setMultiMode(boolean multiMode) {
        this.multiMode = multiMode;
    }

    public int getSelectLimit() {
        return this.selectLimit;
    }

    public void setSelectLimit(int selectLimit) {
        this.selectLimit = selectLimit;
    }

    public boolean isCrop() {
        return this.crop;
    }

    public void setCrop(boolean crop) {
        this.crop = crop;
    }

    public boolean isDynamicCrop() {
        return this.isDynamicCrop;
    }

    public void setDynamicCrop(boolean enabled) {
        this.isDynamicCrop = enabled;
    }

    public boolean isOrigin() {
        return this.isOrigin;
    }

    public void setOrigin(boolean origin) {
        this.isOrigin = origin;
    }

    public boolean isShowCamera() {
        return this.showCamera;
    }

    public void setShowCamera(boolean showCamera) {
        this.showCamera = showCamera;
    }

    public boolean isSaveRectangle() {
        return this.isSaveRectangle;
    }

    public void setSaveRectangle(boolean isSaveRectangle) {
        this.isSaveRectangle = isSaveRectangle;
    }

    public int getOutPutX() {
        return this.outPutX;
    }

    public void setOutPutX(int outPutX) {
        this.outPutX = outPutX;
    }

    public int getOutPutY() {
        return this.outPutY;
    }

    public void setOutPutY(int outPutY) {
        this.outPutY = outPutY;
    }

    public int getFocusWidth() {
        return this.focusWidth;
    }

    public void setFocusWidth(int focusWidth) {
        this.focusWidth = focusWidth;
    }

    public int getFocusHeight() {
        return this.focusHeight;
    }

    public void setFocusHeight(int focusHeight) {
        this.focusHeight = focusHeight;
    }

    public void setQuality(@IntRange(from = 0L, to = 100L) int quality) {
        this.quality = quality;
    }

    public int getQuality() {
        return this.quality;
    }

    public void setAspectRatio(AspectRatio ratio) {
        this.aspectRatio = ratio;
    }

    public AspectRatio getAspectRatio() {
        return this.aspectRatio;
    }

    public File getTakeImageFile() {
        return this.takeImageFile;
    }

    public File getCropCacheFolder(Context context) {
        if (this.cropCacheFolder == null) {
            this.cropCacheFolder = new File(context.getCacheDir() + "/RXImagePicker/cropTemp/");
        }

        return this.cropCacheFolder;
    }

    public void setCropCacheFolder(File cropCacheFolder) {
        this.cropCacheFolder = cropCacheFolder;
    }

    public ImageLoader getImageLoader() {
        return this.imageLoader;
    }

    public void setImageLoader(ImageLoader imageLoader) {
        this.imageLoader = imageLoader;
    }

    public Style getStyle() {
        return this.style;
    }

    public void setStyle(Style style) {
        this.style = style;
    }

    public List<ImageFolder> getImageFolders() {
        return this.mImageFolders;
    }

    public void setImageFolders(List<ImageFolder> imageFolders) {
        this.mImageFolders = imageFolders;
    }

    public int getCurrentImageFolderPosition() {
        return this.mCurrentImageFolderPosition;
    }

    public void setCurrentImageFolderPosition(int mCurrentSelectedImageSetPosition) {
        this.mCurrentImageFolderPosition = mCurrentSelectedImageSetPosition;
    }

    public ArrayList<ImageItem> getCurrentImageFolderItems() {
        return ((ImageFolder) this.mImageFolders.get(this.mCurrentImageFolderPosition)).images;
    }

    public boolean isSelect(ImageItem item) {
        return this.mSelectedImages.contains(item);
    }

    public int getSelectImageCount() {
        return this.mSelectedImages == null ? 0 : this.mSelectedImages.size();
    }

    public ArrayList<ImageItem> getSelectedImages() {
        return this.mSelectedImages;
    }

    public void clearSelectedImages() {
        if (this.mSelectedImages != null) {
            this.mSelectedImages.clear();
        }

    }

    public void clear() {
        if (this.mImageSelectedListeners != null) {
            this.mImageSelectedListeners.clear();
            this.mImageSelectedListeners = null;
        }

        if (this.mImageFolders != null) {
            this.mImageFolders.clear();
            this.mImageFolders = null;
        }

        if (this.mSelectedImages != null) {
            this.mSelectedImages.clear();
        }

        this.mCurrentImageFolderPosition = 0;
    }

    public void takePicture(Activity activity, int requestCode) {
        Intent takePictureIntent = new Intent("android.media.action.IMAGE_CAPTURE");
        takePictureIntent.setFlags(67108864);
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            if (Utils.existSDCard()) {
                this.takeImageFile = new File(Environment.getExternalStorageDirectory(), "/DCIM/camera/");
            } else {
                this.takeImageFile = Environment.getDataDirectory();
            }

            this.takeImageFile = createFile(this.takeImageFile, "IMG_", ".jpg");
            if (this.takeImageFile != null) {
                Uri uri;
                if (VERSION.SDK_INT <= 23) {
                    uri = Uri.fromFile(this.takeImageFile);
                } else {
                    uri = FileProvider.getUriForFile(activity, ProviderUtil.getFileProviderName(activity), this.takeImageFile);
                    List<ResolveInfo> resInfoList = activity.getPackageManager().queryIntentActivities(takePictureIntent, 65536);
                    Iterator var6 = resInfoList.iterator();

                    while (var6.hasNext()) {
                        ResolveInfo resolveInfo = (ResolveInfo) var6.next();
                        String packageName = resolveInfo.activityInfo.packageName;
                        activity.grantUriPermission(packageName, uri, 3);
                    }
                }

                Log.e("nanchen", ProviderUtil.getFileProviderName(activity));
                takePictureIntent.putExtra("output", uri);
            }
        }

        activity.startActivityForResult(takePictureIntent, requestCode);
    }

    public static File createFile(File folder, String prefix, String suffix) {
        if (!folder.exists() || !folder.isDirectory()) {
            folder.mkdirs();
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA);
        String filename = prefix + dateFormat.format(new Date(System.currentTimeMillis())) + suffix;
        return new File(folder, filename);
    }

    public static void galleryAddPic(Context context, @NonNull File file) {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    public static void galleryAddPic(Context context, @NonNull Uri contentUri) {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    public void addOnImageSelectedListener(ImagePicker.OnImageSelectedListener l) {
        if (this.mImageSelectedListeners == null) {
            this.mImageSelectedListeners = new ArrayList();
        }

        this.mImageSelectedListeners.add(l);
    }

    public void removeOnImageSelectedListener(ImagePicker.OnImageSelectedListener l) {
        if (this.mImageSelectedListeners != null) {
            this.mImageSelectedListeners.remove(l);
        }
    }

    public void addSelectedImageItem(int position, ImageItem item, boolean isAdd) {
        if (isAdd) {
            this.mSelectedImages.add(item);
        } else {
            this.mSelectedImages.remove(item);
        }

        this.notifyImageSelectedChanged(position, item, isAdd);
    }

    public void setSelectedImages(ArrayList<ImageItem> selectedImages) {
        if (selectedImages != null) {
            this.mSelectedImages = selectedImages;
        }
    }

    private void notifyImageSelectedChanged(int position, ImageItem item, boolean isAdd) {
        if (this.mImageSelectedListeners != null) {
            Iterator var4 = this.mImageSelectedListeners.iterator();

            while (var4.hasNext()) {
                ImagePicker.OnImageSelectedListener l = (ImagePicker.OnImageSelectedListener) var4.next();
                l.onImageSelected(position, item, isAdd);
            }

        }
    }

    public void restoreInstanceState(Bundle savedInstanceState) {
        this.cropCacheFolder = (File) savedInstanceState.getSerializable("cropCacheFolder");
        this.takeImageFile = (File) savedInstanceState.getSerializable("takeImageFile");
        this.imageLoader = (ImageLoader) savedInstanceState.getSerializable("imageLoader");
        this.style = (Style) savedInstanceState.getSerializable("style");
        this.multiMode = savedInstanceState.getBoolean("multiMode");
        this.crop = savedInstanceState.getBoolean("crop");
        this.showCamera = savedInstanceState.getBoolean("showCamera");
        this.isSaveRectangle = savedInstanceState.getBoolean("isSaveRectangle");
        this.selectLimit = savedInstanceState.getInt("selectLimit");
        this.outPutX = savedInstanceState.getInt("outPutX");
        this.outPutY = savedInstanceState.getInt("outPutY");
        this.focusWidth = savedInstanceState.getInt("focusWidth");
        this.focusHeight = savedInstanceState.getInt("focusHeight");
    }

    public void saveInstanceState(Bundle outState) {
        outState.putSerializable("cropCacheFolder", this.cropCacheFolder);
        outState.putSerializable("takeImageFile", this.takeImageFile);
        outState.putSerializable("imageLoader", this.imageLoader);
        outState.putSerializable("style", this.style);
        outState.putBoolean("multiMode", this.multiMode);
        outState.putBoolean("crop", this.crop);
        outState.putBoolean("showCamera", this.showCamera);
        outState.putBoolean("isSaveRectangle", this.isSaveRectangle);
        outState.putInt("selectLimit", this.selectLimit);
        outState.putInt("outPutX", this.outPutX);
        outState.putInt("outPutY", this.outPutY);
        outState.putInt("focusWidth", this.focusWidth);
        outState.putInt("focusHeight", this.focusHeight);
    }

    public interface OnImageSelectedListener {
        void onImageSelected(int var1, ImageItem var2, boolean var3);
    }
}
