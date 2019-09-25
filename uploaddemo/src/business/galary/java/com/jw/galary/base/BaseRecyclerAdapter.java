package com.jw.galary.base;

import android.app.Activity;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;

import com.jw.galary.img.util.Utils;
import com.jw.galary.img.view.TextDrawable;
import com.jw.galary.video.VideoItem;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseRecyclerAdapter<Data> extends Adapter<ViewHolder> {
    private static final int ITEM_TYPE_CAMERA = 0;
    private static final int ITEM_TYPE_NORMAL = 1;
    public Activity mActivity;
    public ArrayList mItems;
    public ArrayList<Data> mSelectedVideos;
    public boolean mIsShowCamera;
    public int mImageSize;
    public LayoutInflater mInflater;
    public OnItemClickListener mListener;
    public TextDrawable.IBuilder mDrawableBuilder;
    public ArrayList<Integer> mAalreadyChecked;
    public int mSelectLimit;

    public BaseRecyclerAdapter(Activity activity, ArrayList<VideoItem> items) {
        mActivity = activity;
        if (mItems != null && mItems.size() != 0) {
            mItems = items;
        } else {
            mItems = new ArrayList();
        }

        mImageSize = Utils.getImageItemWidth(mActivity);

        mInflater = LayoutInflater.from(activity);
        mDrawableBuilder = TextDrawable.builder().beginConfig().width(Utils.dp2px(activity, 18.0F)).height(Utils.dp2px(activity, 18.0F)).endConfig().roundRect(Utils.dp2px(activity, 3.0F));
    }

    public void setOnVideoItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public void refreshData(ArrayList<Data> items) {
        if (items != null && items.size() != 0) {
            mItems = items;
        } else {
            mItems = new ArrayList();
        }

        notifyDataSetChanged();
    }

    public void refreshCheckedData(int position) {
        List<Integer> checked = new ArrayList(mSelectLimit);
        if (mAalreadyChecked != null) {
            checked.addAll(mAalreadyChecked);
        }

        String payload = "add";
        if (!checked.contains(position)) {
            checked.add(position);
        } else {
            payload = "remove";
        }

        if (checked.size() == mSelectLimit) {
            notifyItemRangeChanged(mIsShowCamera ? ITEM_TYPE_NORMAL : ITEM_TYPE_CAMERA, mItems.size(), payload);
        } else if (!checked.isEmpty()) {

            for (Integer check : checked) {
                notifyItemChanged(check, payload);
            }
        }

    }

    public int getItemViewType(int position) {
        if (mIsShowCamera) {
            return position == 0 ? ITEM_TYPE_CAMERA : ITEM_TYPE_NORMAL;
        } else {
            return 1;
        }
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public int getItemCount() {
        return mIsShowCamera ? mItems.size() + 1 : mItems.size();
    }

    public Data getItem(int position) {
        if (mIsShowCamera) {
            return position == 0 ? null : (Data) mItems.get(position - 1);
        } else {
            return (Data) mItems.get(position);
        }
    }

    public interface OnItemClickListener<Data> {
        void onVideoItemClick(View var1, Data var2, int var3);
    }
}