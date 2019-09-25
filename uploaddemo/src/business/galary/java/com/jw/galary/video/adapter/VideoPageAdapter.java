package com.jw.galary.video.adapter;

import android.app.Activity;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jw.galary.base.BasePageAdapter;
import com.jw.galary.video.VideoItem;
import com.jw.uploaddemo.R;

import java.util.ArrayList;

public class VideoPageAdapter extends BasePageAdapter<VideoItem> {
    public PhotoViewClickListener mListener;

    public VideoPageAdapter(Activity activity, ArrayList<VideoItem> images) {
        super(activity, images);
    }

    public void setPhotoViewClickListener(PhotoViewClickListener listener) {
        this.mListener = listener;
    }

    public Object instantiateItem(ViewGroup container, int position) {
        VideoItem imageItem = this.getMItems().get(position);
        View view = View.inflate(this.getMActivity(), R.layout.pager_preview, null);
        ImageView iv = view.findViewById(R.id.iv1);
        ImageView ivStart = view.findViewById(R.id.iv_start);
        iv.setImageURI(Uri.parse(imageItem.thumbPath));
        iv.setOnClickListener(v -> mListener.OnImageClickListener(imageItem));
        ivStart.setOnClickListener(v -> mListener.OnStartClickListener(imageItem));
        container.addView(view);
        return view;
    }

    public int getItemPosition(Object object) {
        return -2;
    }

    public interface PhotoViewClickListener {
        void OnStartClickListener(VideoItem videoItem);

        void OnImageClickListener(VideoItem videoItem);
    }
}
