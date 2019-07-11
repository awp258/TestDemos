//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.jw.videopicker;

import android.app.Activity;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.rxxb.imagepicker.util.Utils;

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
        VideoItem imageItem = this.images.get(position);
        View view = View.inflate(this.mActivity, R.layout.pager_preview, null);
        ImageView iv = view.findViewById(R.id.iv1);
        ImageView ivStart = view.findViewById(R.id.iv_start);
        iv.setImageURI(Uri.parse(imageItem.thumbPath));
        iv.setOnClickListener(v -> listener.OnImageClickListener(imageItem));
        ivStart.setOnClickListener(v -> listener.OnStartClickListener(imageItem));
        container.addView(view);
        return view;
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
        void OnStartClickListener(VideoItem videoItem);
        void OnImageClickListener(VideoItem videoItem);
    }
}
