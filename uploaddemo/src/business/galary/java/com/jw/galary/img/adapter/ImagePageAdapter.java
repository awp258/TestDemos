

package com.jw.galary.img.adapter;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import com.jw.galary.img.ImagePicker;
import com.jw.galary.img.bean.ImageItem;
import com.jw.galary.img.util.Utils;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;

import java.util.ArrayList;

public class ImagePageAdapter extends PagerAdapter {
    private int screenWidth;
    private int screenHeight;
    private ImagePicker imagePicker;
    private ArrayList<ImageItem> images;
    private Activity mActivity;
    public ImagePageAdapter.PhotoViewClickListener listener;

    public ImagePageAdapter(Activity activity, ArrayList<ImageItem> images) {
        this.mActivity = activity;
        this.images = images;
        DisplayMetrics dm = Utils.getScreenPix(activity);
        this.screenWidth = dm.widthPixels;
        this.screenHeight = dm.heightPixels;
        this.imagePicker = ImagePicker.getInstance();
    }

    public void setData(ArrayList<ImageItem> images) {
        this.images = images;
    }

    public void setPhotoViewClickListener(ImagePageAdapter.PhotoViewClickListener listener) {
        this.listener = listener;
    }

    public Object instantiateItem(ViewGroup container, int position) {
        PhotoView photoView = new PhotoView(this.mActivity);
        ImageItem imageItem = (ImageItem)this.images.get(position);
        this.imagePicker.getImageLoader().displayImagePreview(this.mActivity, imageItem.path, photoView, this.screenWidth, this.screenHeight);
        photoView.setOnPhotoTapListener(new OnPhotoTapListener() {
            public void onPhotoTap(View view, float x, float y) {
                if (ImagePageAdapter.this.listener != null) {
                    ImagePageAdapter.this.listener.OnPhotoTapListener(view, x, y);
                }

            }
        });
        container.addView(photoView);
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
        void OnPhotoTapListener(View var1, float var2, float var3);
    }
}
