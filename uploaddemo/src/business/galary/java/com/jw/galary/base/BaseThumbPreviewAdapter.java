

package com.jw.galary.base;

import android.app.Activity;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;

import com.jw.galary.img.util.Utils;

import java.util.ArrayList;

public abstract class BaseThumbPreviewAdapter<Data> extends Adapter<ViewHolder> {
    protected Activity mContext;
    protected ArrayList<Data> mItems;
    protected int mItemsSize;
    protected int mSelectedPosition;
    protected BaseThumbPreviewAdapter.OnThumbItemClickListener<Data> mListener;

    protected BaseThumbPreviewAdapter(Activity context, ArrayList<Data> items) {
        mContext = context;
        mItems = items;
        mItemsSize = Utils.getImageItemWidth(mContext, 6, 5);
    }

    public void setSelected(Data item) {
        if (mSelectedPosition != -1) {
            notifyItemChanged(mSelectedPosition);
        }

        if (item == null) {
            mSelectedPosition = -1;
        } else {
            mSelectedPosition = mItems.indexOf(item);
        }

        if (mSelectedPosition != -1) {
            notifyItemChanged(mSelectedPosition);
        }

    }

    public int getItemCount() {
        return mItems.size();
    }

    public void setOnThumbItemClickListener(OnThumbItemClickListener<Data> listener) {
        mListener = listener;
    }

    public interface OnThumbItemClickListener<Data> {
        void onThumbItemClick(Data var1);
    }
}
