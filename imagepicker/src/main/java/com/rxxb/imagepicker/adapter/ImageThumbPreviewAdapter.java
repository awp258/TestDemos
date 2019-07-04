//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.rxxb.imagepicker.adapter;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.AbsListView.LayoutParams;
import com.rxxb.imagepicker.ImagePicker;
import com.rxxb.imagepicker.R.drawable;
import com.rxxb.imagepicker.R.id;
import com.rxxb.imagepicker.R.layout;
import com.rxxb.imagepicker.bean.ImageItem;
import com.rxxb.imagepicker.util.Utils;
import java.util.ArrayList;

public class ImageThumbPreviewAdapter extends Adapter<ViewHolder> {
    private Activity mContext;
    private ArrayList<ImageItem> images;
    private int mImageSize;
    private int selectedPosition;
    private ImageThumbPreviewAdapter.OnThumbItemClickListener listener;

    public ImageThumbPreviewAdapter(Activity context) {
        this.mContext = context;
        this.images = ImagePicker.getInstance().getSelectedImages();
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
        return new ImageThumbPreviewAdapter.ThumbViewHolder(LayoutInflater.from(this.mContext).inflate(layout.adapter_thumb_preview_list_item, parent, false));
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder instanceof ImageThumbPreviewAdapter.ThumbViewHolder) {
            ((ImageThumbPreviewAdapter.ThumbViewHolder)holder).bindThumb(position);
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
            this.mFrameLayout = (FrameLayout)itemView.findViewById(id.frame_thumb_preview);
            this.mFrameLayout.setLayoutParams(new LayoutParams(ImageThumbPreviewAdapter.this.mImageSize, ImageThumbPreviewAdapter.this.mImageSize));
            this.mItemView = (ImageView)itemView.findViewById(id.iv_thumb_preview);
            this.thumbView = itemView.findViewById(id.view_thumb_preview);
        }

        void bindThumb(int position) {
            final ImageItem imageItem = (ImageItem)ImageThumbPreviewAdapter.this.images.get(position);
            if (ImageThumbPreviewAdapter.this.selectedPosition == position) {
                this.thumbView.setBackgroundResource(drawable.bg_thumb_selceted_shape);
            } else {
                this.thumbView.setBackgroundDrawable((Drawable)null);
            }

            this.mFrameLayout.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (ImageThumbPreviewAdapter.this.listener != null) {
                        ImageThumbPreviewAdapter.this.listener.onThumbItemClick(imageItem);
                    }

                }
            });
            ImagePicker.getInstance().getImageLoader().displayImage(ImageThumbPreviewAdapter.this.mContext, imageItem.path, this.mItemView, ImageThumbPreviewAdapter.this.mImageSize, ImageThumbPreviewAdapter.this.mImageSize);
        }
    }

    public interface OnThumbItemClickListener {
        void onThumbItemClick(ImageItem var1);
    }
}
