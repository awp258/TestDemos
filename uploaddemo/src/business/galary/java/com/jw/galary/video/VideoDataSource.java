

package com.jw.galary.video;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.jw.galary.base.adapter.GridAdapter;
import com.jw.galary.base.bean.Folder;
import com.jw.uploaddemo.R;

import java.io.File;
import java.util.ArrayList;

public class VideoDataSource implements LoaderCallbacks<Cursor> {
    private static final int LOADER_ALL = 0;
    private static final int LOADER_CATEGORY = 1;
    public static long MAX_LENGTH = Long.MAX_VALUE;

    private final String[] IMAGE_PROJECTION = new String[]{
            MediaStore.Video.Media.DISPLAY_NAME
            , MediaStore.Video.Thumbnails.DATA
            , MediaStore.Video.Media.SIZE
            , MediaStore.Video.Media._ID
            , MediaStore.Video.Media.DURATION

    };
    private FragmentActivity activity;
    private GridAdapter.OnItemsLoadedListener loadedListener;
    private ArrayList videoFolders = new ArrayList();

    VideoDataSource(FragmentActivity activity, String path, GridAdapter.OnItemsLoadedListener loadedListener) {
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
        CursorLoader cursorLoader = null;
        if (id == LOADER_ALL) {
            cursorLoader = new CursorLoader(this.activity, MediaStore.Video.Media.EXTERNAL_CONTENT_URI, this.IMAGE_PROJECTION, MediaStore.Video.Media.MIME_TYPE + "=?", new String[]{"video/mp4"}, MediaStore.Video.Media.DATE_MODIFIED + " desc");
        }

        if (id == LOADER_CATEGORY) {
            cursorLoader = new CursorLoader(this.activity, MediaStore.Video.Media.EXTERNAL_CONTENT_URI, this.IMAGE_PROJECTION, this.IMAGE_PROJECTION[1] + " like '%" + args.getString("path") + "%'", null, this.IMAGE_PROJECTION[6] + " DESC");
        }

        return cursorLoader;
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (this.videoFolders.size() != 0)
            return;
        this.videoFolders.clear();
        if (data != null) {
            ArrayList allVideos = new ArrayList();

            while (data.moveToNext()) {
                String videoName = data.getString(data.getColumnIndexOrThrow(this.IMAGE_PROJECTION[0]));
                String videoPath = data.getString(data.getColumnIndexOrThrow(this.IMAGE_PROJECTION[1]));
                long imageSize = data.getLong(data.getColumnIndexOrThrow(this.IMAGE_PROJECTION[2]));
                long videoId = data.getLong(data.getColumnIndexOrThrow(this.IMAGE_PROJECTION[3]));
                long duration = data.getLong(data.getColumnIndexOrThrow(this.IMAGE_PROJECTION[4]));
/*                if(duration>MAX_LENGTH)
                    continue;*/
                //提前生成缩略图，再获取：http://stackoverflow.com/questions/27903264/how-to-get-the-video-thumbnail-path-and-+not-the-bitmap
                MediaStore.Video.Thumbnails.getThumbnail(activity.getContentResolver(), videoId, MediaStore.Video.Thumbnails.MICRO_KIND, null);
                String[] projection = {MediaStore.Video.Thumbnails._ID, MediaStore.Video.Thumbnails.DATA};

                Cursor cursor = activity.getContentResolver().query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI
                        , projection
                        , MediaStore.Video.Thumbnails.VIDEO_ID + "=?"
                        , new String[]{videoId + ""}
                        , null);
                String thumbPath = "";
                while (cursor.moveToNext()) {
                    thumbPath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA));
                }
                cursor.close();
                File file = new File(videoPath);
                if (file.exists() && file.length() > 0L) {

                    VideoItem videoItem = new VideoItem();
                    videoItem.setName(videoName);
                    videoItem.setPath(videoPath);
                    videoItem.setSize(imageSize);
                    videoItem.duration = duration;
                    videoItem.thumbPath = thumbPath;
                    allVideos.add(videoItem);
                    File videoFile = new File(videoPath);
                    File videoParentFile = videoFile.getParentFile();
                    Folder<VideoItem> videoFolder = new Folder<>();
                    videoFolder.setName(videoParentFile.getName());
                    videoFolder.setPath(videoParentFile.getAbsolutePath());
                    if (!this.videoFolders.contains(videoFolder)) {
                        ArrayList videos = new ArrayList();
                        videos.add(videoItem);
                        videoFolder.setCover(videoItem);
                        videoFolder.setItems(videos);
                        this.videoFolders.add(videoFolder);
                    } else {
                        ((Folder<VideoItem>) this.videoFolders.get(this.videoFolders.indexOf(videoFolder))).getItems().add(videoItem);
                    }
                }
            }

            if (data.getCount() > 0 && allVideos.size() > 0) {
                Folder<VideoItem> allVideosFolder = new Folder<VideoItem>();
                allVideosFolder.setName(this.activity.getResources().getString(R.string.ip_all_videos));
                allVideosFolder.setPath("/");
                allVideosFolder.setCover((VideoItem) allVideos.get(0));
                allVideosFolder.setItems(allVideos);
                this.videoFolders.add(0, allVideosFolder);
            }
        }

        VideoPicker.INSTANCE.setItemFolders(this.videoFolders);
        this.loadedListener.onItemsLoaded(this.videoFolders);
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        System.out.println("--------");
    }
}
