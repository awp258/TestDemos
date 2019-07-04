//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.jw.shotRecord.video;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.util.Log;
import com.rxxb.imagepicker.bean.ViewColor;
import com.rxxb.imagepicker.crop.AspectRatio;
import com.rxxb.imagepicker.loader.ImageLoader;
import com.rxxb.imagepicker.util.ProviderUtil;
import com.rxxb.imagepicker.util.Utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VideoPicker {
    public static final String TAG = VideoPicker.class.getSimpleName();
    public static final int REQUEST_CODE_TAKE = 1001;
    public static final int REQUEST_CODE_CROP = 1002;
    public static final int REQUEST_CODE_PREVIEW = 1003;
    public static final int RESULT_CODE_ITEMS = 1004;
    public static final int RESULT_CODE_BACK = 1005;
    public static final String EXTRA_RESULT_ITEMS = "extra_result_items";
    public static final String EXTRA_SELECTED_IMAGE_POSITION = "selected_image_position";
    public static final String EXTRA_IMAGE_ITEMS = "extra_image_items";
    public static final String EXTRA_FROM_ITEMS = "extra_from_items";
    public static final String EXTRA_OUT_URI = "extra_out_uri";
    private boolean multiMode = true;
    private int selectLimit = 9;
    private boolean showCamera = true;
    public ImageLoader imageLoader;
    private AspectRatio aspectRatio;
    private ViewColor mColor;
    private File cropCacheFolder;
    private File takeVideoFile;
    private ArrayList<VideoItem> mSelectedVideos;
    private List<VideoFolder> mVideoFolders;
    private int mCurrentVideoFolderPosition;
    private List<VideoPicker.OnVideoSelectedListener> mVideoSelectedListeners;
    private static VideoPicker mInstance;

    private VideoPicker() {
        this.aspectRatio = AspectRatio.IMG_SRC;
        this.mColor = new ViewColor();
        this.mSelectedVideos = new ArrayList();
        this.mCurrentVideoFolderPosition = 0;
    }

    public static VideoPicker getInstance() {
        if (mInstance == null) {
            synchronized (VideoPicker.class) {
                if (mInstance == null) {
                    mInstance = new VideoPicker();
                }
            }
        }

        return mInstance;
    }

    boolean isMultiMode() {
        return this.multiMode;
    }

    public void setMultiMode(boolean multiMode) {
        this.multiMode = multiMode;
    }

    int getSelectLimit() {
        return this.selectLimit;
    }

    public void setSelectLimit(int selectLimit) {
        this.selectLimit = selectLimit;
    }


    boolean isShowCamera() {
        return this.showCamera;
    }

    public void setShowCamera(boolean showCamera) {
        this.showCamera = showCamera;
    }

    ViewColor getViewColor() {
        return this.mColor;
    }

    public void setAspectRatio(AspectRatio ratio) {
        this.aspectRatio = ratio;
    }

    public AspectRatio getAspectRatio() {
        return this.aspectRatio;
    }

    File getTakeVideoFile() {
        return this.takeVideoFile;
    }

    public File getCropCacheFolder(Context context) {
        if (this.cropCacheFolder == null) {
            this.cropCacheFolder = new File(context.getCacheDir() + "/RXVideoPicker/cropTemp/");
        }

        return this.cropCacheFolder;
    }

    public void setCropCacheFolder(File cropCacheFolder) {
        this.cropCacheFolder = cropCacheFolder;
    }

    ImageLoader getVideoLoader() {
        return this.imageLoader;
    }

    public void setVideoLoader(ImageLoader imageLoader) {
        this.imageLoader = imageLoader;
    }

    public List<VideoFolder> getVideoFolders() {
        return this.mVideoFolders;
    }

    void setVideoFolders(List<VideoFolder> imageFolders) {
        this.mVideoFolders = imageFolders;
    }

    public int getCurrentVideoFolderPosition() {
        return this.mCurrentVideoFolderPosition;
    }

    void setCurrentVideoFolderPosition(int mCurrentSelectedVideoSetPosition) {
        this.mCurrentVideoFolderPosition = mCurrentSelectedVideoSetPosition;
    }

    public ArrayList<VideoItem> getCurrentVideoFolderItems() {
        return ((VideoFolder) this.mVideoFolders.get(this.mCurrentVideoFolderPosition)).videos;
    }

    public boolean isSelect(VideoItem item) {
        return this.mSelectedVideos.contains(item);
    }

    int getSelectVideoCount() {
        return this.mSelectedVideos == null ? 0 : this.mSelectedVideos.size();
    }

    ArrayList<VideoItem> getSelectedVideos() {
        return this.mSelectedVideos;
    }

    void clearSelectedVideos() {
        if (this.mSelectedVideos != null) {
            this.mSelectedVideos.clear();
        }

    }

    void clear() {
        if (this.mVideoSelectedListeners != null) {
            this.mVideoSelectedListeners.clear();
            this.mVideoSelectedListeners = null;
        }

        if (this.mVideoFolders != null) {
            this.mVideoFolders.clear();
            this.mVideoFolders = null;
        }

        if (this.mSelectedVideos != null) {
            this.mSelectedVideos.clear();
        }

        this.mCurrentVideoFolderPosition = 0;
    }

    void takePicture(Activity activity, int requestCode) {
        Intent takePictureIntent = new Intent("android.media.action.IMAGE_CAPTURE");
        takePictureIntent.setFlags(67108864);
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            if (Utils.existSDCard()) {
                this.takeVideoFile = new File(Environment.getExternalStorageDirectory(), "/DCIM/camera/");
            } else {
                this.takeVideoFile = Environment.getDataDirectory();
            }

            this.takeVideoFile = createFile(this.takeVideoFile, "IMG_", ".jpg");
            Uri uri;
            if (VERSION.SDK_INT <= 23) {
                uri = Uri.fromFile(this.takeVideoFile);
            } else {
                uri = FileProvider.getUriForFile(activity, ProviderUtil.getFileProviderName(activity), this.takeVideoFile);
                List<ResolveInfo> resInfoList = activity.getPackageManager().queryIntentActivities(takePictureIntent, 65536);

                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    activity.grantUriPermission(packageName, uri, 3);
                }
            }

            Log.e("nanchen", ProviderUtil.getFileProviderName(activity));
            takePictureIntent.putExtra("output", uri);
        }

        activity.startActivityForResult(takePictureIntent, requestCode);
    }

    private static File createFile(File folder, String prefix, String suffix) {
        if (!folder.exists() || !folder.isDirectory()) {
            folder.mkdirs();
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA);
        String filename = prefix + dateFormat.format(new Date(System.currentTimeMillis())) + suffix;
        return new File(folder, filename);
    }

    static void galleryAddPic(Context context, @NonNull File file) {
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

    void addOnVideoSelectedListener(VideoPicker.OnVideoSelectedListener l) {
        if (this.mVideoSelectedListeners == null) {
            this.mVideoSelectedListeners = new ArrayList();
        }

        this.mVideoSelectedListeners.add(l);
    }

    public void removeOnVideoSelectedListener(VideoPicker.OnVideoSelectedListener l) {
        if (this.mVideoSelectedListeners != null) {
            this.mVideoSelectedListeners.remove(l);
        }
    }

    void addSelectedVideoItem(int position, VideoItem item, boolean isAdd) {
        if (isAdd) {
            this.mSelectedVideos.add(item);
        } else {
            this.mSelectedVideos.remove(item);
        }

        this.notifyVideoSelectedChanged(position, item, isAdd);
    }

    void setSelectedVideos(ArrayList<VideoItem> selectedVideos) {
        if (selectedVideos != null) {
            this.mSelectedVideos = selectedVideos;
        }
    }

    private void notifyVideoSelectedChanged(int position, VideoItem item, boolean isAdd) {
        if (this.mVideoSelectedListeners != null) {

            for (OnVideoSelectedListener l : this.mVideoSelectedListeners) {
                l.onVideoSelected(position, item, isAdd);
            }

        }
    }

    public interface OnVideoSelectedListener {
        void onVideoSelected(int var1, VideoItem videoItem, boolean var3);
    }
}
