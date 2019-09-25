package com.jw.galary.img.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.jw.galary.base.BaseItem;

import java.io.Serializable;

public class ImageItem extends BaseItem implements Serializable, Parcelable {
    public int width;
    public int height;
    public long addTime;
    public static final Creator<ImageItem> CREATOR = new Creator<ImageItem>() {
        public ImageItem createFromParcel(Parcel source) {
            return new ImageItem(source);
        }

        public ImageItem[] newArray(int size) {
            return new ImageItem[size];
        }
    };

    protected ImageItem(Parcel in) {
        setName(in.readString());
        setPath(in.readString());
        setSize(in.readLong());
        width = in.readInt();
        height = in.readInt();
        setMimeType(in.readString());
        addTime = in.readLong();
    }

    public int describeContents() {
        return 0;
    }

    public boolean equals(Object o) {
        if (o instanceof ImageItem) {
            ImageItem item = (ImageItem) o;
            return getPath().equalsIgnoreCase(item.getPath());
        } else {
            return super.equals(o);
        }
    }

    public ImageItem() {
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getName());
        dest.writeString(getPath());
        dest.writeLong(getSize());
        dest.writeInt(width);
        dest.writeInt(height);
        dest.writeString(getMimeType());
        dest.writeLong(addTime);
    }
}
