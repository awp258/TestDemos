

package com.jw.galary.img.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.ImageView;
import android.widget.Toast;

import com.jw.galary.base.BaseRecyclerAdapter;
import com.jw.galary.img.ImagePicker;
import com.jw.galary.img.bean.ImageItem;
import com.jw.galary.img.loader.GlideImageLoader;
import com.jw.galary.img.view.SuperCheckBox;
import com.jw.galary.video.VideoItem;
import com.jw.uploaddemo.R;

import java.util.ArrayList;
import java.util.List;

public class ImageRecyclerAdapter extends BaseRecyclerAdapter<ImageItem> {
    ImagePicker imagePicker;

    public ImageRecyclerAdapter(Activity activity, ArrayList<VideoItem> mItems) {
        super(activity, mItems);
        imagePicker = ImagePicker.INSTANCE;
        mIsShowCamera = imagePicker.isShowCamera();
        mSelectedVideos = imagePicker.getSelectedImages();
        mAalreadyChecked = new ArrayList(imagePicker.getSelectLimit());
        mSelectLimit = imagePicker.getSelectLimit();
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return viewType == 0 ? new CameraViewHolder(this.mInflater.inflate(R.layout.adapter_camera_item, parent, false)) : new ImageViewHolder(this.mInflater.inflate(R.layout.adapter_image_list_item, parent, false));
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder instanceof CameraViewHolder) {
            ((CameraViewHolder) holder).bindCamera();
        } else if (holder instanceof ImageViewHolder) {
            ((ImageViewHolder) holder).bind(position);
        }

    }

    public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
        if (holder instanceof CameraViewHolder) {
            ((CameraViewHolder) holder).bindCamera();
        } else if (holder instanceof ImageViewHolder) {
            ImageViewHolder viewHolder = (ImageViewHolder) holder;
            if (payloads != null && !payloads.isEmpty()) {
                ImageItem imageItem = this.getItem(position);
                int index = this.mSelectedVideos.indexOf(imageItem);
                if (index >= 0) {
                    if (!this.mAalreadyChecked.contains(position)) {
                        this.mAalreadyChecked.add(position);
                    }

                    viewHolder.cbCheck.setChecked(true);
                    viewHolder.cbCheck.setButtonDrawable(this.mDrawableBuilder.build(String.valueOf(index + 1), Color.parseColor("#1AAD19")));
                } else {
                    this.mAalreadyChecked.remove((Object) position);
                    viewHolder.cbCheck.setChecked(false);
                    viewHolder.cbCheck.setButtonDrawable(R.drawable.checkbox_normal);
                }

                int selectLimit = imagePicker.getSelectLimit();
                if (this.mSelectedVideos.size() >= selectLimit) {
                    viewHolder.mask.setVisibility(index < View.VISIBLE ? View.VISIBLE : View.GONE);
                } else {
                    viewHolder.mask.setVisibility(View.GONE);
                }
            } else {
                viewHolder.bind(position);
            }
        }

    }

    public class CameraViewHolder extends ViewHolder {
        View mItemView;

        public CameraViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
        }

        public void bindCamera() {
            mItemView.setLayoutParams(new LayoutParams(-1, mImageSize));
            mItemView.setTag(null);
            mItemView.setOnClickListener(v -> {


            });
        }
    }

    private class ImageViewHolder extends ViewHolder {
        View rootView;
        ImageView ivThumb;
        View mask;
        View checkView;
        SuperCheckBox cbCheck;

        ImageViewHolder(View itemView) {
            super(itemView);
            this.rootView = itemView;
            this.ivThumb = itemView.findViewById(R.id.iv_thumb);
            this.mask = itemView.findViewById(R.id.mask);
            this.checkView = itemView.findViewById(R.id.checkView);
            this.cbCheck = itemView.findViewById(R.id.cb_check);
            itemView.setLayoutParams(new LayoutParams(-1, mImageSize));
        }

        void bind(final int position) {
            final ImageItem imageItem = getItem(position);
            this.ivThumb.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onVideoItemClick(ImageViewHolder.this.rootView, imageItem, position);
                }

            });
            this.checkView.setOnClickListener(v -> {
                ImageViewHolder.this.cbCheck.setChecked(!ImageViewHolder.this.cbCheck.isChecked());
                int selectLimit = imagePicker.getSelectLimit();
                if (ImageViewHolder.this.cbCheck.isChecked() && mSelectedVideos.size() >= selectLimit) {
                    Toast.makeText(mActivity.getApplicationContext(), mActivity.getString(R.string.ip_select_limit, selectLimit), Toast.LENGTH_SHORT).show();
                    ImageViewHolder.this.cbCheck.setChecked(false);
                } else {
                    imagePicker.addSelectedImageItem(position, imageItem, ImageViewHolder.this.cbCheck.isChecked());
                }

            });
            if (imagePicker.isMultiMode()) {
                this.checkView.setVisibility(View.VISIBLE);
                int index = mSelectedVideos.indexOf(imageItem);
                if (index >= 0) {
                    if (!mAalreadyChecked.contains(position)) {
                        mAalreadyChecked.add(position);
                    }

                    this.cbCheck.setChecked(true);
                    this.cbCheck.setButtonDrawable(mDrawableBuilder.build(String.valueOf(index + 1), Color.parseColor("#1AAD19")));
                } else {
                    mAalreadyChecked.remove((Object) position);
                    this.cbCheck.setChecked(false);
                    this.cbCheck.setButtonDrawable(R.drawable.checkbox_normal);
                }

                int selectLimit = imagePicker.getSelectLimit();
                if (mSelectedVideos.size() >= selectLimit) {
                    this.mask.setVisibility(index < View.VISIBLE ? View.VISIBLE : View.GONE);
                } else {
                    this.mask.setVisibility(View.GONE);
                }
            } else {
                this.checkView.setVisibility(View.GONE);
            }

            GlideImageLoader.INSTANCE.displayImage(mActivity, imageItem.getPath(), this.ivThumb, mImageSize, mImageSize);
        }
    }
}
