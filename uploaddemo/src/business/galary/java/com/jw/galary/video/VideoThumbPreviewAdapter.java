//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.jw.galary.video;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.jw.galary.img.util.Utils;
import com.jw.uploaddemo.R;

import java.util.ArrayList;

public class VideoThumbPreviewAdapter extends Adapter<ViewHolder> {
    private Activity mContext;
    private ArrayList<VideoItem> images;
    private int mImageSize;
    private int selectedPosition;
    private VideoThumbPreviewAdapter.OnVideoThumbItemClickListener listener;

    public VideoThumbPreviewAdapter(Activity context) {
        this.mContext = context;
        this.images = VideoPicker.getInstance().getSelectedVideos();
        this.mImageSize = Utils.getImageItemWidth(this.mContext, 6, 5);
    }

    public void setSelected(VideoItem item) {
        if (this.selectedPosition != -1) {
            this.notifyItemChanged(this.selectedPosition);
        }

        if (item == null) {
            this.selectedPosition = -1;
        } else {
            this.selectedPosition = this.images.indexOf(item);
        }

        if (this.selectedPosition != -1) {
            this.notifyItemChanged(this.selectedPosition);
        }

    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VideoThumbPreviewAdapter.ThumbViewHolder(LayoutInflater.from(this.mContext).inflate(R.layout.adapter_thumb_preview_list_item, parent, false));
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder instanceof VideoThumbPreviewAdapter.ThumbViewHolder) {
            ((VideoThumbPreviewAdapter.ThumbViewHolder) holder).bindThumb(position);
        }

    }

    public int getItemCount() {
        return this.images.size();
    }

    public void setOnThumbItemClickListener(VideoThumbPreviewAdapter.OnVideoThumbItemClickListener listener) {
        this.listener = listener;
    }

    private class ThumbViewHolder extends ViewHolder {
        FrameLayout mFrameLayout;
        ImageView mItemView;
        View thumbView;

        ThumbViewHolder(View itemView) {
            super(itemView);
            this.mFrameLayout = (FrameLayout) itemView.findViewById(R.id.frame_thumb_preview);
            this.mFrameLayout.setLayoutParams(new LayoutParams(VideoThumbPreviewAdapter.this.mImageSize, VideoThumbPreviewAdapter.this.mImageSize));
            this.mItemView = (ImageView) itemView.findViewById(R.id.iv_thumb_preview);
            this.thumbView = itemView.findViewById(R.id.view_thumb_preview);
        }

        void bindThumb(int position) {
            final VideoItem videoItem = (VideoItem) VideoThumbPreviewAdapter.this.images.get(position);
            if (VideoThumbPreviewAdapter.this.selectedPosition == position) {
                this.thumbView.setBackgroundResource(R.drawable.bg_thumb_selceted_shape);
            } else {
                this.thumbView.setBackgroundDrawable((Drawable) null);
            }

            this.mFrameLayout.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (VideoThumbPreviewAdapter.this.listener != null) {
                        VideoThumbPreviewAdapter.this.listener.onThumbItemClick(videoItem);
                    }

                }
            });
            VideoPicker.getInstance().getVideoLoader().displayImage(VideoThumbPreviewAdapter.this.mContext, videoItem.thumbPath, this.mItemView, VideoThumbPreviewAdapter.this.mImageSize, VideoThumbPreviewAdapter.this.mImageSize);
        }
    }

    public interface OnVideoThumbItemClickListener {
        void onThumbItemClick(VideoItem var1);
    }
}
