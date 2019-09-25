

package com.jw.galary.img.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jw.galary.img.ImagePicker;
import com.jw.galary.img.bean.ImageFolder;
import com.jw.galary.img.util.Utils;
import com.jw.uploaddemo.R;

import java.util.ArrayList;
import java.util.List;

public class ImageFolderAdapter extends BaseAdapter {
    private ImagePicker imagePicker;
    private Activity mActivity;
    private LayoutInflater mInflater;
    private int mImageSize;
    private List<ImageFolder> imageFolders;
    private int lastSelected = 0;

    public ImageFolderAdapter(Activity activity, List<ImageFolder> folders) {
        this.mActivity = activity;
        if (folders != null && folders.size() > 0) {
            this.imageFolders = folders;
        } else {
            this.imageFolders = new ArrayList();
        }

        this.imagePicker = ImagePicker.INSTANCE;
        this.mImageSize = Utils.getImageItemWidth(this.mActivity);
        this.mInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void refreshData(List<ImageFolder> folders) {
        if (folders != null && folders.size() > 0) {
            this.imageFolders = folders;
        } else {
            this.imageFolders.clear();
        }

        this.notifyDataSetChanged();
    }

    public int getCount() {
        return this.imageFolders.size();
    }

    public ImageFolder getItem(int position) {
        return this.imageFolders.get(position);
    }

    public long getItemId(int position) {
        return (long)position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageFolderAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView = this.mInflater.inflate(R.layout.adapter_folder_list_item, parent, false);
            holder = new ImageFolderAdapter.ViewHolder(convertView);
        } else {
            holder = (ImageFolderAdapter.ViewHolder)convertView.getTag();
        }

        ImageFolder folder = this.getItem(position);
        holder.folderName.setText(folder.name);
        holder.imageCount.setText(this.mActivity.getString(R.string.ip_folder_image_count, folder.images.size()));
        this.imagePicker.getImageLoader().displayImage(this.mActivity, folder.cover.path, holder.cover, this.mImageSize, this.mImageSize);
        if (this.lastSelected == position) {
            holder.folderCheck.setVisibility(View.VISIBLE);
        } else {
            holder.folderCheck.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    public void setSelectIndex(int i) {
        if (this.lastSelected != i) {
            this.lastSelected = i;
            this.notifyDataSetChanged();
        }
    }

    public int getSelectIndex() {
        return this.lastSelected;
    }

    private class ViewHolder {
        ImageView cover;
        TextView folderName;
        TextView imageCount;
        ImageView folderCheck;

        ViewHolder(View view) {
            this.cover = view.findViewById(R.id.iv_cover);
            this.folderName = view.findViewById(R.id.tv_folder_name);
            this.imageCount = view.findViewById(R.id.tv_image_count);
            this.folderCheck = view.findViewById(R.id.iv_folder_check);
            view.setTag(this);
        }
    }
}
