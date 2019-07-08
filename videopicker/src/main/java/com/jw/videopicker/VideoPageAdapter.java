//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.jw.videopicker;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.rxxb.imagepicker.util.Utils;
import uk.co.senab.photoview.PhotoView;

import java.util.ArrayList;

public class VideoPageAdapter extends PagerAdapter {
    private int screenWidth;
    private int screenHeight;
    private VideoPicker imagePicker;
    private ArrayList<VideoItem> images = new ArrayList();
    private Activity mActivity;
    public VideoPageAdapter.PhotoViewClickListener listener;

    public VideoPageAdapter(Activity activity, ArrayList<VideoItem> images) {
        this.mActivity = activity;
        this.images = images;
        DisplayMetrics dm = Utils.getScreenPix(activity);
        this.screenWidth = dm.widthPixels;
        this.screenHeight = dm.heightPixels;
        this.imagePicker = VideoPicker.getInstance();
    }

    public void setData(ArrayList<VideoItem> images) {
        this.images = images;
    }

    public void setPhotoViewClickListener(VideoPageAdapter.PhotoViewClickListener listener) {
        this.listener = listener;
    }

    public Object instantiateItem(ViewGroup container, int position) {
        VideoItem imageItem = (VideoItem)this.images.get(position);
        PhotoView photoView = new PhotoView(this.mActivity);
        this.imagePicker.getVideoLoader().displayImagePreview(this.mActivity, imageItem.thumbPath, photoView, this.screenWidth, this.screenHeight);
        photoView.setOnPhotoTapListener((view, x, y) -> { });
        container.addView(photoView);
        ImageView ivStart = new ImageView(this.mActivity);
        ivStart.setImageResource(R.mipmap.video_start);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(100, 100);
        ivStart.setLayoutParams(layoutParams);
        ivStart.setOnClickListener(v -> {
            if (VideoPageAdapter.this.listener != null) {
                VideoPageAdapter.this.listener.OnStartClickListener(imageItem);
            }
        });
        container.addView(ivStart);
        return photoView;
    }

    public int getCount() {
        return this.images.size();
    }

    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }

    public int getItemPosition(Object object) {
        return -2;
    }

    public interface PhotoViewClickListener {
        void OnPhotoTapListener(View var1, float var2, float var3,VideoItem videoItem);
        void OnStartClickListener(VideoItem videoItem);
    }
}
