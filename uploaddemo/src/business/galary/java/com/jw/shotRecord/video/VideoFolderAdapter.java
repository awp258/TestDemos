//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.jw.shotRecord.video;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.jw.uploaddemo.R;
import com.rxxb.imagepicker.R.id;
import com.rxxb.imagepicker.R.layout;
import com.rxxb.imagepicker.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class VideoFolderAdapter extends BaseAdapter {
    private VideoPicker imagePicker;
    private Activity mActivity;
    private LayoutInflater mInflater;
    private int mImageSize;
    private List<VideoFolder> videoFolders;
    private int lastSelected = 0;

    VideoFolderAdapter(Activity activity, List<VideoFolder> folders) {
        this.mActivity = activity;
        if (folders != null && folders.size() > 0) {
            this.videoFolders = folders;
        } else {
            this.videoFolders = new ArrayList();
        }

        this.imagePicker = VideoPicker.getInstance();
        this.mImageSize = Utils.getImageItemWidth(this.mActivity);
        this.mInflater = (LayoutInflater) activity.getSystemService("layout_inflater");
    }

    void refreshData(List<VideoFolder> folders) {
        if (folders != null && folders.size() > 0) {
            this.videoFolders = folders;
        } else {
            this.videoFolders.clear();
        }

        this.notifyDataSetChanged();
    }

    public int getCount() {
        return this.videoFolders.size();
    }

    public VideoFolder getItem(int position) {
        return this.videoFolders.get(position);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        VideoFolderAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView = this.mInflater.inflate(layout.adapter_folder_list_item, parent, false);
            holder = new VideoFolderAdapter.ViewHolder(convertView);
        } else {
            holder = (VideoFolderAdapter.ViewHolder) convertView.getTag();
        }

        VideoFolder folder = this.getItem(position);
        holder.folderName.setText(folder.name);
        holder.imageCount.setText(this.mActivity.getString(R.string.ip_folder_image_count, folder.videos.size()));
        this.imagePicker.getVideoLoader().displayImage(this.mActivity, folder.cover.thumbPath, holder.cover, this.mImageSize, this.mImageSize);
        if (this.lastSelected == position) {
            holder.folderCheck.setVisibility(View.VISIBLE);
        } else {
            holder.folderCheck.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    void setSelectIndex(int i) {
        if (this.lastSelected != i) {
            this.lastSelected = i;
            this.notifyDataSetChanged();
        }
    }

    int getSelectIndex() {
        return this.lastSelected;
    }

    private class ViewHolder {
        ImageView cover;
        TextView folderName;
        TextView imageCount;
        ImageView folderCheck;

        ViewHolder(View view) {
            this.cover = view.findViewById(id.iv_cover);
            this.folderName = view.findViewById(id.tv_folder_name);
            this.imageCount = view.findViewById(id.tv_image_count);
            this.folderCheck = view.findViewById(id.iv_folder_check);
            view.setTag(this);
        }
    }
}
