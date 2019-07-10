//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.rxxb.imagepicker.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.AbsListView.LayoutParams;
import com.rxxb.imagepicker.ImagePicker;
import com.rxxb.imagepicker.R.id;
import com.rxxb.imagepicker.R.layout;
import com.rxxb.imagepicker.R.mipmap;
import com.rxxb.imagepicker.R.string;
import com.rxxb.imagepicker.bean.ImageItem;
import com.rxxb.imagepicker.ui.ImageBaseActivity;
import com.rxxb.imagepicker.util.Utils;
import com.rxxb.imagepicker.view.SuperCheckBox;
import com.rxxb.imagepicker.view.TextDrawable;
import com.rxxb.imagepicker.view.TextDrawable.IBuilder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.rxxb.imagepicker.ImagePicker.REQUEST_CODE_IMAGE_TAKE;

public class ImageRecyclerAdapter extends Adapter<ViewHolder> {
    private static final int ITEM_TYPE_CAMERA = 0;
    private static final int ITEM_TYPE_NORMAL = 1;
    private ImagePicker imagePicker;
    private Activity mActivity;
    private ArrayList<ImageItem> images;
    private ArrayList<ImageItem> mSelectedImages;
    private boolean isShowCamera;
    private int mImageSize;
    private LayoutInflater mInflater;
    private ImageRecyclerAdapter.OnImageItemClickListener listener;
    private IBuilder mDrawableBuilder;
    private List<Integer> alreadyChecked;

    public void setOnImageItemClickListener(ImageRecyclerAdapter.OnImageItemClickListener listener) {
        this.listener = listener;
    }

    public void refreshData(ArrayList<ImageItem> images) {
        if (images != null && images.size() != 0) {
            this.images = images;
        } else {
            this.images = new ArrayList();
        }

        this.notifyDataSetChanged();
    }

    public void refreshCheckedData(int position) {
        List<Integer> checked = new ArrayList(this.imagePicker.getSelectLimit());
        if (this.alreadyChecked != null) {
            checked.addAll(this.alreadyChecked);
        }

        String payload = "add";
        if (!checked.contains(position)) {
            checked.add(position);
        } else {
            payload = "remove";
        }

        if (checked.size() == this.imagePicker.getSelectLimit()) {
            this.notifyItemRangeChanged(this.isShowCamera ? 1 : 0, this.images.size(), payload);
        } else if (!checked.isEmpty()) {
            Iterator var4 = checked.iterator();

            while(var4.hasNext()) {
                Integer check = (Integer)var4.next();
                this.notifyItemChanged(check, payload);
            }
        }

    }

    public ImageRecyclerAdapter(Activity activity, ArrayList<ImageItem> images) {
        this.mActivity = activity;
        if (images != null && images.size() != 0) {
            this.images = images;
        } else {
            this.images = new ArrayList();
        }

        this.mImageSize = Utils.getImageItemWidth(this.mActivity);
        this.imagePicker = ImagePicker.getInstance();
        this.isShowCamera = this.imagePicker.isShowCamera();
        this.mSelectedImages = this.imagePicker.getSelectedImages();
        this.mInflater = LayoutInflater.from(activity);
        this.mDrawableBuilder = TextDrawable.builder().beginConfig().width(Utils.dp2px(activity, 18.0F)).height(Utils.dp2px(activity, 18.0F)).endConfig().roundRect(Utils.dp2px(activity, 3.0F));
        this.alreadyChecked = new ArrayList(this.imagePicker.getSelectLimit());
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return (ViewHolder)(viewType == 0 ? new ImageRecyclerAdapter.CameraViewHolder(this.mInflater.inflate(layout.adapter_camera_item, parent, false)) : new ImageRecyclerAdapter.ImageViewHolder(this.mInflater.inflate(layout.adapter_image_list_item, parent, false)));
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder instanceof ImageRecyclerAdapter.CameraViewHolder) {
            ((ImageRecyclerAdapter.CameraViewHolder)holder).bindCamera();
        } else if (holder instanceof ImageRecyclerAdapter.ImageViewHolder) {
            ((ImageRecyclerAdapter.ImageViewHolder)holder).bind(position);
        }

    }

    public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
        if (holder instanceof ImageRecyclerAdapter.CameraViewHolder) {
            ((ImageRecyclerAdapter.CameraViewHolder)holder).bindCamera();
        } else if (holder instanceof ImageRecyclerAdapter.ImageViewHolder) {
            ImageRecyclerAdapter.ImageViewHolder viewHolder = (ImageRecyclerAdapter.ImageViewHolder)holder;
            if (payloads != null && !payloads.isEmpty()) {
                ImageItem imageItem = this.getItem(position);
                int index = this.mSelectedImages.indexOf(imageItem);
                if (index >= 0) {
                    if (!this.alreadyChecked.contains(position)) {
                        this.alreadyChecked.add(position);
                    }

                    viewHolder.cbCheck.setChecked(true);
                    viewHolder.cbCheck.setButtonDrawable(this.mDrawableBuilder.build(String.valueOf(index + 1), Color.parseColor("#1AAD19")));
                } else {
                    this.alreadyChecked.remove((Object)position);
                    viewHolder.cbCheck.setChecked(false);
                    viewHolder.cbCheck.setButtonDrawable(mipmap.checkbox_normal);
                }

                int selectLimit = this.imagePicker.getSelectLimit();
                if (this.mSelectedImages.size() >= selectLimit) {
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
        return (long)position;
    }

    public int getItemCount() {
        return this.isShowCamera ? this.images.size() + 1 : this.images.size();
    }

    public ImageItem getItem(int position) {
        if (this.isShowCamera) {
            return position == 0 ? null : (ImageItem)this.images.get(position - 1);
        } else {
            return (ImageItem)this.images.get(position);
        }
    }

    private class CameraViewHolder extends ViewHolder {
        View mItemView;

        CameraViewHolder(View itemView) {
            super(itemView);
            this.mItemView = itemView;
        }

        void bindCamera() {
            this.mItemView.setLayoutParams(new LayoutParams(-1, ImageRecyclerAdapter.this.mImageSize));
            this.mItemView.setTag((Object)null);
            this.mItemView.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (!((ImageBaseActivity)ImageRecyclerAdapter.this.mActivity).checkPermission("android.permission.CAMERA")) {
                        ActivityCompat.requestPermissions(ImageRecyclerAdapter.this.mActivity, new String[]{"android.permission.CAMERA"}, 2);
                    } else {
                        ImageRecyclerAdapter.this.imagePicker.takePicture(ImageRecyclerAdapter.this.mActivity, REQUEST_CODE_IMAGE_TAKE);
                    }

                }
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
            this.ivThumb = (ImageView)itemView.findViewById(id.iv_thumb);
            this.mask = itemView.findViewById(id.mask);
            this.checkView = itemView.findViewById(id.checkView);
            this.cbCheck = (SuperCheckBox)itemView.findViewById(id.cb_check);
            itemView.setLayoutParams(new LayoutParams(-1, ImageRecyclerAdapter.this.mImageSize));
        }

        void bind(final int position) {
            final ImageItem imageItem = ImageRecyclerAdapter.this.getItem(position);
            this.ivThumb.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (ImageRecyclerAdapter.this.listener != null) {
                        ImageRecyclerAdapter.this.listener.onImageItemClick(ImageViewHolder.this.rootView, imageItem, position);
                    }

                }
            });
            this.checkView.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    ImageViewHolder.this.cbCheck.setChecked(!ImageViewHolder.this.cbCheck.isChecked());
                    int selectLimit = ImageRecyclerAdapter.this.imagePicker.getSelectLimit();
                    if (ImageViewHolder.this.cbCheck.isChecked() && ImageRecyclerAdapter.this.mSelectedImages.size() >= selectLimit) {
                        Toast.makeText(ImageRecyclerAdapter.this.mActivity.getApplicationContext(), ImageRecyclerAdapter.this.mActivity.getString(string.ip_select_limit, new Object[]{selectLimit}), Toast.LENGTH_SHORT).show();
                        ImageViewHolder.this.cbCheck.setChecked(false);
                    } else {
                        ImageRecyclerAdapter.this.imagePicker.addSelectedImageItem(position, imageItem, ImageViewHolder.this.cbCheck.isChecked());
                    }

                }
            });
            if (ImageRecyclerAdapter.this.imagePicker.isMultiMode()) {
                this.checkView.setVisibility(View.VISIBLE);
                int index = ImageRecyclerAdapter.this.mSelectedImages.indexOf(imageItem);
                if (index >= 0) {
                    if (!ImageRecyclerAdapter.this.alreadyChecked.contains(position)) {
                        ImageRecyclerAdapter.this.alreadyChecked.add(position);
                    }

                    this.cbCheck.setChecked(true);
                    this.cbCheck.setButtonDrawable(ImageRecyclerAdapter.this.mDrawableBuilder.build(String.valueOf(index + 1), Color.parseColor("#1AAD19")));
                } else {
                    ImageRecyclerAdapter.this.alreadyChecked.remove((Object)position);
                    this.cbCheck.setChecked(false);
                    this.cbCheck.setButtonDrawable(mipmap.checkbox_normal);
                }

                int selectLimit = ImageRecyclerAdapter.this.imagePicker.getSelectLimit();
                if (ImageRecyclerAdapter.this.mSelectedImages.size() >= selectLimit) {
                    this.mask.setVisibility(index < View.VISIBLE ? View.VISIBLE : View.GONE);
                } else {
                    this.mask.setVisibility(View.GONE);
                }
            } else {
                this.checkView.setVisibility(View.GONE);
            }

            ImageRecyclerAdapter.this.imagePicker.getImageLoader().displayImage(ImageRecyclerAdapter.this.mActivity, imageItem.path, this.ivThumb, ImageRecyclerAdapter.this.mImageSize, ImageRecyclerAdapter.this.mImageSize);
        }
    }

    public interface OnImageItemClickListener {
        void onImageItemClick(View var1, ImageItem var2, int var3);
    }
}
