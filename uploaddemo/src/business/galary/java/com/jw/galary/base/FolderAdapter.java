

package com.jw.galary.base;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jw.galary.img.loader.GlideImageLoader;
import com.jw.galary.img.util.Utils;
import com.jw.uploaddemo.R;

import java.util.ArrayList;
import java.util.List;

public class FolderAdapter<Data> extends BaseAdapter {
    private Activity mActivity;
    private LayoutInflater mInflater;
    private int mSize;
    private List<Folder<Data>> mFolders;
    private int lastSelected = 0;

    public FolderAdapter(Activity activity, List<Folder<Data>> mFolders) {
        this.mActivity = activity;
        if (mFolders != null && mFolders.size() > 0) {
            this.mFolders = mFolders;
        } else {
            this.mFolders = new ArrayList();
        }

        this.mSize = Utils.getImageItemWidth(this.mActivity);
        this.mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void refreshData(List<Folder<Data>> folders) {
        if (folders != null && folders.size() > 0) {
            this.mFolders = folders;
        } else {
            this.mFolders.clear();
        }

        this.notifyDataSetChanged();
    }

    public int getCount() {
        return this.mFolders.size();
    }

    public Folder<Data> getItem(int position) {
        return this.mFolders.get(position);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = this.mInflater.inflate(R.layout.adapter_folder_list_item, parent, false);
            holder = new ViewHolder(convertView);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Folder<Data> folder = this.getItem(position);
        holder.folderName.setText(folder.getName());
        holder.imageCount.setText(this.mActivity.getString(R.string.ip_folder_image_count, folder.getItems().size()));
        String path = ((BaseItem) folder.getCover()).getPath();
        GlideImageLoader.INSTANCE.displayImage(this.mActivity, path, holder.cover, this.mSize, this.mSize);
        if (this.lastSelected == position) {
            holder.folderCheck.setVisibility(View.VISIBLE);
        } else {
            holder.folderCheck.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    public int getSelectIndex() {
        return this.lastSelected;
    }

    public void setSelectIndex(int i) {
        if (this.lastSelected != i) {
            this.lastSelected = i;
            this.notifyDataSetChanged();
        }
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
