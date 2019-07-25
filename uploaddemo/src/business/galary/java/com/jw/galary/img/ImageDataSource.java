

package com.jw.galary.img;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import com.jw.galary.img.bean.ImageFolder;
import com.jw.galary.img.bean.ImageItem;
import com.jw.uploaddemo.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImageDataSource implements LoaderCallbacks<Cursor> {
    public static final int LOADER_ALL = 0;
    public static final int LOADER_CATEGORY = 1;

    private final String[] IMAGE_PROJECTION = new String[]{
            MediaStore.Images.Media.DISPLAY_NAME
            , Media.DATA
            , Media.SIZE
            , Media.WIDTH
            , Media.HEIGHT
            , Media.MIME_TYPE
            , Media.DATE_ADDED
    };
    private FragmentActivity activity;
    private ImageDataSource.OnImagesLoadedListener loadedListener;
    private ArrayList<ImageFolder> imageFolders = new ArrayList();
    CursorLoader cursorLoader = null;

    public ImageDataSource(FragmentActivity activity, String path, ImageDataSource.OnImagesLoadedListener loadedListener) {
        this.activity = activity;
        this.loadedListener = loadedListener;
        LoaderManager loaderManager = activity.getSupportLoaderManager();
        if (path == null) {
            loaderManager.initLoader(0, (Bundle)null, this);
        } else {
            Bundle bundle = new Bundle();
            bundle.putString("path", path);
            loaderManager.initLoader(1, bundle, this);
        }

    }

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_ALL) {
            cursorLoader = new CursorLoader(this.activity, Media.EXTERNAL_CONTENT_URI, this.IMAGE_PROJECTION, (String)null, (String[])null, this.IMAGE_PROJECTION[6] + " DESC");
        }

        if (id == LOADER_CATEGORY) {
            cursorLoader = new CursorLoader(this.activity, Media.EXTERNAL_CONTENT_URI, this.IMAGE_PROJECTION, this.IMAGE_PROJECTION[1] + " like '%" + args.getString("path") + "%'", (String[])null, this.IMAGE_PROJECTION[6] + " DESC");
        }

        return cursorLoader;
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(this.imageFolders.size()!=0)
            return;
        this.imageFolders.clear();
        if (data != null) {
            ArrayList allImages = new ArrayList();

            while(data.moveToNext()) {
                String imageName = data.getString(data.getColumnIndexOrThrow(this.IMAGE_PROJECTION[0]));
                String imagePath = data.getString(data.getColumnIndexOrThrow(this.IMAGE_PROJECTION[1]));
                File file = new File(imagePath);
                if (file.exists() && file.length() > 0L) {
                    long imageSize = data.getLong(data.getColumnIndexOrThrow(this.IMAGE_PROJECTION[2]));
                    int imageWidth = data.getInt(data.getColumnIndexOrThrow(this.IMAGE_PROJECTION[3]));
                    int imageHeight = data.getInt(data.getColumnIndexOrThrow(this.IMAGE_PROJECTION[4]));
                    String imageMimeType = data.getString(data.getColumnIndexOrThrow(this.IMAGE_PROJECTION[5]));
                    long imageAddTime = data.getLong(data.getColumnIndexOrThrow(this.IMAGE_PROJECTION[6]));
                    ImageItem imageItem = new ImageItem();
                    imageItem.name = imageName;
                    imageItem.path = imagePath;
                    imageItem.size = imageSize;
                    imageItem.width = imageWidth;
                    imageItem.height = imageHeight;
                    imageItem.mimeType = imageMimeType;
                    imageItem.addTime = imageAddTime;
                    allImages.add(imageItem);
                    File imageFile = new File(imagePath);
                    File imageParentFile = imageFile.getParentFile();
                    ImageFolder imageFolder = new ImageFolder();
                    imageFolder.name = imageParentFile.getName();
                    imageFolder.path = imageParentFile.getAbsolutePath();
                    if (!this.imageFolders.contains(imageFolder)) {
                        ArrayList<ImageItem> images = new ArrayList();
                        images.add(imageItem);
                        imageFolder.cover = imageItem;
                        imageFolder.images = images;
                        this.imageFolders.add(imageFolder);
                    } else {
                        ((ImageFolder)this.imageFolders.get(this.imageFolders.indexOf(imageFolder))).images.add(imageItem);
                    }
                }
            }

            if (data.getCount() > 0 && allImages.size() > 0) {
                ImageFolder allImagesFolder = new ImageFolder();
                allImagesFolder.name = this.activity.getResources().getString(R.string.ip_all_images);
                allImagesFolder.path = "/";
                allImagesFolder.cover = (ImageItem)allImages.get(0);
                allImagesFolder.images = allImages;
                this.imageFolders.add(0, allImagesFolder);
            }
        }

        ImagePicker.getInstance().setImageFolders(this.imageFolders);
        this.loadedListener.onImagesLoaded(this.imageFolders);
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        System.out.println("--------");
    }

    public interface OnImagesLoadedListener {
        void onImagesLoaded(List<ImageFolder> var1);
    }
}
