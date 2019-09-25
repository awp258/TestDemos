

package com.jw.galary.img.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.jw.galary.img.ImagePicker;
import com.jw.galary.img.bean.ImageItem;
import com.jw.galary.img.util.Utils;
import com.jw.uploaddemo.R;

import java.util.ArrayList;

public class ImageThumbPreviewAdapter extends Adapter<ViewHolder> {
    private Activity mContext;
    private ArrayList<ImageItem> images;
    private int mImageSize;
    private int selectedPosition;
    private ImageThumbPreviewAdapter.OnThumbItemClickListener listener;

    public ImageThumbPreviewAdapter(Activity context) {
        this.mContext = context;
        this.images = ImagePicker.INSTANCE.getSelectedImages();
        this.mImageSize = Utils.getImageItemWidth(this.mContext, 6, 5);
    }

    public void setSelected(ImageItem item) {
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
        return new ImageThumbPreviewAdapter.ThumbViewHolder(LayoutInflater.from(this.mContext).inflate(R.layout.adapter_thumb_image_preview_list_item, parent, false));
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder instanceof ImageThumbPreviewAdapter.ThumbViewHolder) {
            ((ImageThumbPreviewAdapter.ThumbViewHolder) holder).bindThumb(position);
        }

    }

    public int getItemCount() {
        return this.images.size();
    }

    public void setOnThumbItemClickListener(ImageThumbPreviewAdapter.OnThumbItemClickListener listener) {
        this.listener = listener;
    }

    private class ThumbViewHolder extends ViewHolder {
        FrameLayout mFrameLayout;
        ImageView mItemView;
        View thumbView;

        ThumbViewHolder(View itemView) {
            super(itemView);
            this.mFrameLayout = itemView.findViewById(R.id.frame_thumb_preview);
            this.mFrameLayout.setLayoutParams(new LayoutParams(ImageThumbPreviewAdapter.this.mImageSize, ImageThumbPreviewAdapter.this.mImageSize));
            this.mItemView = itemView.findViewById(R.id.iv_thumb_preview);
            this.thumbView = itemView.findViewById(R.id.view_thumb_preview);
        }

        void bindThumb(int position) {
            final ImageItem imageItem = ImageThumbPreviewAdapter.this.images.get(position);
            if (ImageThumbPreviewAdapter.this.selectedPosition == position) {
                this.thumbView.setBackgroundResource(R.drawable.bg_thumb_selceted_shape);
            } else {
                this.thumbView.setBackgroundDrawable(null);
            }

            this.mFrameLayout.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (ImageThumbPreviewAdapter.this.listener != null) {
                        ImageThumbPreviewAdapter.this.listener.onThumbItemClick(imageItem);
                    }

                }
            });
            ImagePicker.INSTANCE.getImageLoader().displayImage(ImageThumbPreviewAdapter.this.mContext, imageItem.path, this.mItemView, ImageThumbPreviewAdapter.this.mImageSize, ImageThumbPreviewAdapter.this.mImageSize);
        }
    }

    public interface OnThumbItemClickListener {
        void onThumbItemClick(ImageItem var1);
    }
}
