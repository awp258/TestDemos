//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.jw.videopicker;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.rxxb.imagepicker.ui.ImageBaseActivity;
import com.rxxb.imagepicker.util.Utils;
import com.rxxb.imagepicker.view.SuperCheckBox;
import com.rxxb.imagepicker.view.TextDrawable;

import java.util.ArrayList;
import java.util.List;

import static com.jw.videopicker.VideoPicker.REQUEST_CODE_TAKE;

public class VideoRecyclerAdapter extends Adapter<ViewHolder> {
    private VideoPicker videoPicker;
    private Activity mActivity;
    private ArrayList videos;
    private ArrayList<VideoItem> mSelectedVideos;
    private boolean isShowCamera;
    private int mImageSize;
    private LayoutInflater mInflater;
    private VideoRecyclerAdapter.OnVideoItemClickListener listener;
    private TextDrawable.IBuilder mDrawableBuilder;
    private ArrayList<Integer> alreadyChecked;

    void setOnVideoItemClickListener(VideoRecyclerAdapter.OnVideoItemClickListener listener) {
        this.listener = listener;
    }

    void refreshData(ArrayList<VideoItem> videos) {
        if (videos != null && videos.size() != 0) {
            this.videos = videos;
        } else {
            this.videos = new ArrayList();
        }

        this.notifyDataSetChanged();
    }


    void refreshCheckedData(int position) {
        List<Integer> checked = new ArrayList(this.videoPicker.getSelectLimit());
        if (this.alreadyChecked != null) {
            checked.addAll(this.alreadyChecked);
        }

        String payload = "add";
        if (!checked.contains(position)) {
            checked.add(position);
        } else {
            payload = "remove";
        }

        if (checked.size() == this.videoPicker.getSelectLimit()) {
            this.notifyItemRangeChanged(this.isShowCamera ? 1 : 0, this.videos.size(), payload);
        } else if (!checked.isEmpty()) {

            for (Integer check : checked) {
                this.notifyItemChanged(check, payload);
            }
        }

    }

    VideoRecyclerAdapter(Activity activity, ArrayList<VideoItem> videos) {
        this.mActivity = activity;
        if (videos != null && videos.size() != 0) {
            this.videos = videos;
        } else {
            this.videos = new ArrayList();
        }

        this.mImageSize = Utils.getImageItemWidth(this.mActivity);
        this.videoPicker = VideoPicker.getInstance();
        this.isShowCamera = this.videoPicker.isShowCamera();
        this.mSelectedVideos = this.videoPicker.getSelectedVideos();
        this.mInflater = LayoutInflater.from(activity);
        this.mDrawableBuilder = TextDrawable.builder().beginConfig().width(Utils.dp2px(activity, 18.0F)).height(Utils.dp2px(activity, 18.0F)).endConfig().roundRect(Utils.dp2px(activity, 3.0F));
        this.alreadyChecked = new ArrayList(this.videoPicker.getSelectLimit());
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return (viewType == 0 ? new CameraViewHolder(this.mInflater.inflate(R.layout.adapter_camera_item, parent, false)) : new VideoViewHolder(this.mInflater.inflate(R.layout.adapter_video_list_item, parent, false)));
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder instanceof VideoRecyclerAdapter.CameraViewHolder) {
            ((VideoRecyclerAdapter.CameraViewHolder) holder).bindCamera();
        } else if (holder instanceof VideoRecyclerAdapter.VideoViewHolder) {
            ((VideoRecyclerAdapter.VideoViewHolder) holder).bind(position);
        }

    }

    public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
        if (holder instanceof VideoRecyclerAdapter.CameraViewHolder) {
            ((VideoRecyclerAdapter.CameraViewHolder) holder).bindCamera();
        } else if (holder instanceof VideoRecyclerAdapter.VideoViewHolder) {
            VideoRecyclerAdapter.VideoViewHolder viewHolder = (VideoRecyclerAdapter.VideoViewHolder) holder;
            if (payloads != null && !payloads.isEmpty()) {
                VideoItem imageItem = this.getItem(position);
                int index = this.mSelectedVideos.indexOf(imageItem);
                if (index >= 0) {
                    if (!this.alreadyChecked.contains(position)) {
                        this.alreadyChecked.add(position);
                    }

                    viewHolder.cbCheck.setChecked(true);
                    viewHolder.cbCheck.setButtonDrawable(this.mDrawableBuilder.build(String.valueOf(index + 1), Color.parseColor("#1AAD19")));
                } else {
                    VideoRecyclerAdapter.this.alreadyChecked.remove((Object) position);
                    viewHolder.cbCheck.setChecked(false);
                    viewHolder.cbCheck.setButtonDrawable(R.mipmap.checkbox_normal);
                }

                int selectLimit = this.videoPicker.getSelectLimit();
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

    public int getItemViewType(int position) {
        if (this.isShowCamera) {
            return position == 0 ? 0 : 1;
        } else {
            return 1;
        }
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public int getItemCount() {
        return this.isShowCamera ? this.videos.size() + 1 : this.videos.size();
    }

    VideoItem getItem(int position) {
        if (this.isShowCamera) {
            return position == 0 ? null : (VideoItem) this.videos.get(position - 1);
        } else {
            return (VideoItem) this.videos.get(position);
        }
    }

    private class CameraViewHolder extends ViewHolder {
        View mItemView;

        CameraViewHolder(View itemView) {
            super(itemView);
            this.mItemView = itemView;
        }

        void bindCamera() {
            this.mItemView.setLayoutParams(new LayoutParams(-1, VideoRecyclerAdapter.this.mImageSize));
            this.mItemView.setTag(null);
            this.mItemView.setOnClickListener(v -> {
                if (!((ImageBaseActivity) VideoRecyclerAdapter.this.mActivity).checkPermission("android.permission.CAMERA")) {
                    ActivityCompat.requestPermissions(VideoRecyclerAdapter.this.mActivity, new String[]{"android.permission.CAMERA"}, 2);
                } else {
                    VideoRecyclerAdapter.this.videoPicker.takePicture(VideoRecyclerAdapter.this.mActivity, REQUEST_CODE_TAKE);
                }

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
            this.rootView = itemView;
            this.ivThumb = itemView.findViewById(R.id.iv_thumb);
            this.mask = itemView.findViewById(R.id.mask);
            this.checkView = itemView.findViewById(R.id.checkView);
            this.cbCheck = itemView.findViewById(R.id.cb_check);
            this.tvDuration = itemView.findViewById(R.id.tv_duration);
            itemView.setLayoutParams(new LayoutParams(-1, VideoRecyclerAdapter.this.mImageSize));
        }

        void bind(final int position) {
            final VideoItem videoItem = VideoRecyclerAdapter.this.getItem(position);
            this.ivThumb.setOnClickListener(v -> {
                if (VideoRecyclerAdapter.this.listener != null) {
                    VideoRecyclerAdapter.this.listener.onVideoItemClick(VideoViewHolder.this.rootView, videoItem, position);
                }

            });
            this.checkView.setOnClickListener(v -> {
                VideoViewHolder.this.cbCheck.setChecked(!VideoViewHolder.this.cbCheck.isChecked());
                int selectLimit = VideoRecyclerAdapter.this.videoPicker.getSelectLimit();
                if (VideoViewHolder.this.cbCheck.isChecked() && VideoRecyclerAdapter.this.mSelectedVideos.size() >= selectLimit) {
                    Toast.makeText(VideoRecyclerAdapter.this.mActivity.getApplicationContext(), VideoRecyclerAdapter.this.mActivity.getString(R.string.ip_select_limit, selectLimit), Toast.LENGTH_SHORT).show();
                    VideoViewHolder.this.cbCheck.setChecked(false);
                } else {
                    VideoRecyclerAdapter.this.videoPicker.addSelectedVideoItem(position, videoItem, VideoViewHolder.this.cbCheck.isChecked());
                }

            });
            if (VideoRecyclerAdapter.this.videoPicker.isMultiMode()) {
                this.checkView.setVisibility(View.VISIBLE);
                int index = VideoRecyclerAdapter.this.mSelectedVideos.indexOf(videoItem);
                if (index >= 0) {
                    if (!VideoRecyclerAdapter.this.alreadyChecked.contains(position)) {
                        VideoRecyclerAdapter.this.alreadyChecked.add(position);
                    }
                    this.cbCheck.setChecked(true);
                    this.cbCheck.setButtonDrawable(VideoRecyclerAdapter.this.mDrawableBuilder.build(String.valueOf(index + 1), Color.parseColor("#1AAD19")));
                } else {
                    VideoRecyclerAdapter.this.alreadyChecked.remove((Object) position);
                    this.cbCheck.setChecked(false);
                    this.cbCheck.setButtonDrawable(R.mipmap.checkbox_normal);
                }

                int selectLimit = VideoRecyclerAdapter.this.videoPicker.getSelectLimit();
                if (VideoRecyclerAdapter.this.mSelectedVideos.size() >= selectLimit) {
                    this.mask.setVisibility(index < View.VISIBLE ? View.VISIBLE : View.GONE);
                } else {
                    this.mask.setVisibility(View.GONE);
                }
            } else {
                this.checkView.setVisibility(View.GONE);
            }
            tvDuration.setText(DateUtils.getDuration(videoItem.duration, "ss:SS"));
            VideoRecyclerAdapter.this.videoPicker.getVideoLoader().displayImage(VideoRecyclerAdapter.this.mActivity, videoItem.thumbPath, this.ivThumb, VideoRecyclerAdapter.this.mImageSize, VideoRecyclerAdapter.this.mImageSize);
        }
    }

    public interface OnVideoItemClickListener {
        void onVideoItemClick(View var1, VideoItem var2, int var3);
    }
}
