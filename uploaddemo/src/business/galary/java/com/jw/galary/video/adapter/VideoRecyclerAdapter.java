

package com.jw.galary.video.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jw.galary.base.BaseRecyclerAdapter;
import com.jw.galary.img.loader.GlideImageLoader;
import com.jw.galary.img.view.SuperCheckBox;
import com.jw.galary.video.DateUtils;
import com.jw.galary.video.VideoItem;
import com.jw.galary.video.VideoPicker;
import com.jw.uploaddemo.R;

import java.util.ArrayList;
import java.util.List;

public class VideoRecyclerAdapter extends BaseRecyclerAdapter<VideoItem> {
    VideoPicker videoPicker;

    public VideoRecyclerAdapter(Activity activity, ArrayList<VideoItem> mItems) {
        super(activity, mItems);
        videoPicker = VideoPicker.INSTANCE;
        mIsShowCamera = videoPicker.isShowCamera();
        mSelectedVideos = videoPicker.getSelectedVideos();
        mAalreadyChecked = new ArrayList(videoPicker.getSelectLimit());
        mSelectLimit = videoPicker.getSelectLimit();
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return (viewType == 0 ? new CameraViewHolder(mInflater.inflate(R.layout.adapter_camera_item, parent, false)) : new VideoViewHolder(mInflater.inflate(R.layout.adapter_video_list_item, parent, false)));
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder instanceof CameraViewHolder) {
            ((CameraViewHolder) holder).bindCamera();
        } else if (holder instanceof VideoViewHolder) {
            ((VideoViewHolder) holder).bind(position);
        }

    }

    public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
        if (holder instanceof CameraViewHolder) {
            ((CameraViewHolder) holder).bindCamera();
        } else if (holder instanceof VideoViewHolder) {
            VideoViewHolder viewHolder = (VideoViewHolder) holder;
            if (payloads != null && !payloads.isEmpty()) {
                VideoItem imageItem = getItem(position);
                int index = mSelectedVideos.indexOf(imageItem);
                if (index >= 0) {
                    if (!mAalreadyChecked.contains(position)) {
                        mAalreadyChecked.add(position);
                    }

                    viewHolder.cbCheck.setChecked(true);
                    viewHolder.cbCheck.setButtonDrawable(mDrawableBuilder.build(String.valueOf(index + 1), Color.parseColor("#1AAD19")));
                } else {
                    mAalreadyChecked.remove((Object) position);
                    viewHolder.cbCheck.setChecked(false);
                    viewHolder.cbCheck.setButtonDrawable(R.drawable.checkbox_normal);
                }

                int selectLimit = videoPicker.getSelectLimit();
                if (mSelectedVideos.size() >= selectLimit) {
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

    private class VideoViewHolder extends ViewHolder {
        View rootView;
        ImageView ivThumb;
        View mask;
        View checkView;
        SuperCheckBox cbCheck;
        TextView tvDuration;

        VideoViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            ivThumb = itemView.findViewById(R.id.iv_thumb);
            mask = itemView.findViewById(R.id.mask);
            checkView = itemView.findViewById(R.id.checkView);
            cbCheck = itemView.findViewById(R.id.cb_check);
            tvDuration = itemView.findViewById(R.id.tv_duration);
            itemView.setLayoutParams(new LayoutParams(-1, mImageSize));
        }

        void bind(final int position) {
            final VideoItem videoItem = VideoRecyclerAdapter.this.getItem(position);
            ivThumb.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onVideoItemClick(rootView, videoItem, position);
                }

            });
            checkView.setOnClickListener(v -> {
                cbCheck.setChecked(!cbCheck.isChecked());
                int selectLimit = videoPicker.getSelectLimit();
                if (cbCheck.isChecked() && mSelectedVideos.size() >= selectLimit) {
                    Toast.makeText(mActivity.getApplicationContext(), mActivity.getString(R.string.ip_select_video_limit, selectLimit), Toast.LENGTH_SHORT).show();
                    cbCheck.setChecked(false);
                } else {
                    videoPicker.addSelectedVideoItem(position, videoItem, cbCheck.isChecked());
                }

            });
            if (videoPicker.isMultiMode()) {
                checkView.setVisibility(View.VISIBLE);
                int index = mSelectedVideos.indexOf(videoItem);
                if (index >= 0) {
                    if (!mAalreadyChecked.contains(position)) {
                        mAalreadyChecked.add(position);
                    }
                    cbCheck.setChecked(true);
                    cbCheck.setButtonDrawable(mDrawableBuilder.build(String.valueOf(index + 1), Color.parseColor("#1AAD19")));
                } else {
                    mAalreadyChecked.remove((Object) position);
                    cbCheck.setChecked(false);
                    cbCheck.setButtonDrawable(R.drawable.checkbox_normal);
                }

                int selectLimit = videoPicker.getSelectLimit();
                if (mSelectedVideos.size() >= selectLimit) {
                    mask.setVisibility(index < View.VISIBLE ? View.VISIBLE : View.GONE);
                } else {
                    mask.setVisibility(View.GONE);
                }
            } else {
                checkView.setVisibility(View.GONE);
            }
            tvDuration.setText(DateUtils.getDuration(videoItem.duration, "mm:ss"));
            GlideImageLoader.INSTANCE.displayImage(mActivity, videoItem.thumbPath, ivThumb, mImageSize, mImageSize);
        }
    }
}
