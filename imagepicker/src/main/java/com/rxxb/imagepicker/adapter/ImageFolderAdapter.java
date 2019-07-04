//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.rxxb.imagepicker.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.rxxb.imagepicker.ImagePicker;
import com.rxxb.imagepicker.R.id;
import com.rxxb.imagepicker.R.layout;
import com.rxxb.imagepicker.R.string;
import com.rxxb.imagepicker.bean.ImageFolder;
import com.rxxb.imagepicker.util.Utils;
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

        this.imagePicker = ImagePicker.getInstance();
        this.mImageSize = Utils.getImageItemWidth(this.mActivity);
        this.mInflater = (LayoutInflater)activity.getSystemService("layout_inflater");
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
        return (ImageFolder)this.imageFolders.get(position);
    }

    public long getItemId(int position) {
        return (long)position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageFolderAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView = this.mInflater.inflate(layout.adapter_folder_list_item, parent, false);
            holder = new ImageFolderAdapter.ViewHolder(convertView);
        } else {
            holder = (ImageFolderAdapter.ViewHolder)convertView.getTag();
        }

        ImageFolder folder = this.getItem(position);
        holder.folderName.setText(folder.name);
        holder.imageCount.setText(this.mActivity.getString(string.ip_folder_image_count, new Object[]{folder.images.size()}));
        this.imagePicker.getImageLoader().displayImage(this.mActivity, folder.cover.path, holder.cover, this.mImageSize, this.mImageSize);
        if (this.lastSelected == position) {
            holder.folderCheck.setVisibility(0);
        } else {
            holder.folderCheck.setVisibility(4);
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

        public ViewHolder(View view) {
            this.cover = (ImageView)view.findViewById(id.iv_cover);
            this.folderName = (TextView)view.findViewById(id.tv_folder_name);
            this.imageCount = (TextView)view.findViewById(id.tv_image_count);
            this.folderCheck = (ImageView)view.findViewById(id.iv_folder_check);
            view.setTag(this);
        }
    }
}
