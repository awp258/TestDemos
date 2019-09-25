

package com.jw.galary.video.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.jw.galary.base.BaseThumbPreviewAdapter;
import com.jw.galary.img.loader.GlideImageLoader;
import com.jw.galary.video.VideoItem;
import com.jw.uploaddemo.R;

import java.util.ArrayList;

public class VideoThumbPreviewAdapter extends BaseThumbPreviewAdapter<VideoItem> {


    public VideoThumbPreviewAdapter(Activity context, ArrayList<VideoItem> images) {
        super(context, images);
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VideoThumbPreviewAdapter.ThumbViewHolder(LayoutInflater.from(this.mContext).inflate(R.layout.adapter_thumb_preview_list_item, parent, false));
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        ((ThumbViewHolder) holder).bindThumb(position);
    }

    private class ThumbViewHolder extends ViewHolder {
        FrameLayout mFrameLayout;
        ImageView mItemView;
        View thumbView;

        ThumbViewHolder(View itemView) {
            super(itemView);
            this.mFrameLayout = itemView.findViewById(R.id.frame_thumb_preview);
            this.mFrameLayout.setLayoutParams(new LayoutParams(mItemsSize, mItemsSize));
            this.mItemView = itemView.findViewById(R.id.iv_thumb_preview);
            this.thumbView = itemView.findViewById(R.id.view_thumb_preview);
        }

        void bindThumb(int position) {
            final VideoItem videoItem = mItems.get(position);
            if (mSelectedPosition == position) {
                this.thumbView.setBackgroundResource(R.drawable.bg_thumb_selceted_shape);
            } else {
                this.thumbView.setBackgroundDrawable(null);
            }

            this.mFrameLayout.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onThumbItemClick(videoItem);
                }

            });
            GlideImageLoader.INSTANCE.displayImage(mContext, videoItem.thumbPath, this.mItemView, mItemsSize, mItemsSize);
        }
    }

}
