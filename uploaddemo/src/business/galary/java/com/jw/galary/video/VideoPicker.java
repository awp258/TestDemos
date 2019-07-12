//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.jw.galary.video;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.util.Log;
import com.jw.galary.img.bean.ViewColor;
import com.jw.galary.img.loader.ImageLoader;
import com.jw.galary.img.util.ProviderUtil;
import com.jw.galary.img.util.Utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VideoPicker {
    public static final String TAG = VideoPicker.class.getSimpleName();
    public static final int REQUEST_CODE_VIDEO_TAKE = 2001;
    public static final int REQUEST_CODE_VIDEO_CROP = 2002;
    public static final int REQUEST_CODE_VIDEO_PREVIEW = 2003;
    public static final int RESULT_CODE_VIDEO_ITEMS = 2004;
    public static final int RESULT_CODE_VIDEO_BACK = 2005;
    public static final String EXTRA_SELECTED_VIDEO_POSITION = "selected_video_position";
    public static final String EXTRA_VIDEO_ITEMS = "extra_video_items";
    public static final String EXTRA_FROM_VIDEO_ITEMS = "extra_from_items";
    public static final String EXTRA_CROP_VIDEOOUT_URI = "extra_crop_video_out_uri";
    private boolean multiMode = true;
    private int selectLimit = 9;
    private boolean showCamera = false;
    public ImageLoader imageLoader;
    private ViewColor mColor;
    private File cropCacheFolder;
    private File takeVideoFile;
    private ArrayList<VideoItem> mSelectedVideos;
    private List<VideoFolder> mVideoFolders;
    private int mCurrentVideoFolderPosition;
    private List<OnVideoSelectedListener> mVideoSelectedListeners;
    private static VideoPicker mInstance;

    private VideoPicker() {
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

    public void restoreInstanceState(Bundle savedInstanceState) {
        this.cropCacheFolder = (File)savedInstanceState.getSerializable("cropCacheFolder");
        this.imageLoader = (ImageLoader)savedInstanceState.getSerializable("imageLoader");
        this.multiMode = savedInstanceState.getBoolean("multiMode");
        this.showCamera = savedInstanceState.getBoolean("showCamera");
        this.selectLimit = savedInstanceState.getInt("selectLimit");
    }

    public void saveInstanceState(Bundle outState) {
        outState.putSerializable("cropCacheFolder", this.cropCacheFolder);
        outState.putSerializable("imageLoader", this.imageLoader);
        outState.putBoolean("multiMode", this.multiMode);
        outState.putBoolean("showCamera", this.showCamera);
        outState.putInt("selectLimit", this.selectLimit);
    }

    public interface OnVideoSelectedListener {
        void onVideoSelected(int var1, VideoItem videoItem, boolean var3);
    }
}
