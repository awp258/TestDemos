package com.jw.galary.img.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.jw.galary.base.BasePageAdapter;
import com.jw.galary.img.bean.ImageItem;
import com.jw.galary.img.loader.GlideImageLoader;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;

public class ImagePageAdapter extends BasePageAdapter<ImageItem> {
    public PhotoViewClickListener mListener;

    public ImagePageAdapter(Activity activity, ArrayList<ImageItem> images) {
        super(activity, images);
    }

    public void setPhotoViewClickListener(ImagePageAdapter.PhotoViewClickListener listener) {
        this.mListener = listener;
    }

    public Object instantiateItem(ViewGroup container, int position) {
        PhotoView photoView = new PhotoView(this.getMActivity());
        ImageItem imageItem = this.getMItems().get(position);
        GlideImageLoader.INSTANCE.displayImagePreview(this.getMActivity(), imageItem.getPath(), photoView, this.getMScreenWidth(), this.getMScreenHeight());
        photoView.setOnPhotoTapListener((view, x, y) -> {
            if (mListener != null) {
                mListener.OnPhotoTapListener(view, x, y);
            }
        });
        container.addView(photoView);
        return photoView;
    }

    public interface PhotoViewClickListener {
        void OnPhotoTapListener(View var1, float var2, float var3);
    }
}
