

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

import com.jw.galary.base.Folder;
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
    private OnItemsLoadedListener loadedListener;
    private ArrayList imageFolders = new ArrayList<Folder<ImageItem>>();
    CursorLoader cursorLoader = null;

    public ImageDataSource(FragmentActivity activity, String path, OnItemsLoadedListener loadedListener) {
        this.activity = activity;
        this.loadedListener = loadedListener;
        LoaderManager loaderManager = activity.getSupportLoaderManager();
        if (path == null) {
            loaderManager.initLoader(0, null, this);
        } else {
            Bundle bundle = new Bundle();
            bundle.putString("path", path);
            loaderManager.initLoader(1, bundle, this);
        }

    }

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_ALL) {
            cursorLoader = new CursorLoader(this.activity, Media.EXTERNAL_CONTENT_URI, this.IMAGE_PROJECTION, null, null, this.IMAGE_PROJECTION[6] + " DESC");
        }

        if (id == LOADER_CATEGORY) {
            cursorLoader = new CursorLoader(this.activity, Media.EXTERNAL_CONTENT_URI, this.IMAGE_PROJECTION, this.IMAGE_PROJECTION[1] + " like '%" + args.getString("path") + "%'", null, this.IMAGE_PROJECTION[6] + " DESC");
        }

        return cursorLoader;
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (this.imageFolders.size() != 0)
            return;
        if (data != null) {
            ArrayList allImages = new ArrayList();

            while (data.moveToNext()) {
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
                    imageItem.setName(imageName);
                    imageItem.setPath(imagePath);
                    imageItem.setSize(imageSize);
                    imageItem.width = imageWidth;
                    imageItem.height = imageHeight;
                    imageItem.setMimeType(imageMimeType);
                    imageItem.addTime = imageAddTime;
                    allImages.add(imageItem);
                    File imageFile = new File(imagePath);
                    File imageParentFile = imageFile.getParentFile();
                    Folder<ImageItem> imageFolder = new Folder<ImageItem>();
                    imageFolder.setName(imageParentFile.getName());
                    imageFolder.setPath(imageParentFile.getAbsolutePath());
                    if (!this.imageFolders.contains(imageFolder)) {
                        ArrayList images = new ArrayList();
                        images.add(imageItem);
                        imageFolder.setCover(imageItem);
                        imageFolder.setItems(images);
                        this.imageFolders.add(imageFolder);
                    } else {
                        ((Folder) (this.imageFolders.get(this.imageFolders.indexOf(imageFolder)))).getItems().add(imageItem);
                    }
                }
            }

            if (data.getCount() > 0 && allImages.size() > 0) {
                Folder<ImageItem> allImagesFolder = new Folder<ImageItem>();
                allImagesFolder.setName(this.activity.getResources().getString(R.string.ip_all_images));
                allImagesFolder.setPath("/");
                allImagesFolder.setCover((ImageItem) allImages.get(0));
                allImagesFolder.setItems(allImages);
                this.imageFolders.add(0, allImagesFolder);
            }
        }

        ImagePicker.INSTANCE.setImageFolders(this.imageFolders);
        this.loadedListener.onItemsLoaded(this.imageFolders);
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        System.out.println("--------");
    }

    public interface OnItemsLoadedListener<Data> {
        void onItemsLoaded(List<Folder<Data>> var1);
    }
}
