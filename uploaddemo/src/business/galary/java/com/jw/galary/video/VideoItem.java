package com.jw.galary.video;

import android.os.Parcel;
import android.os.Parcelable;

import com.jw.galary.base.bean.BaseItem;

import java.io.Serializable;

public class VideoItem extends BaseItem implements Serializable, Parcelable {
    public String thumbPath;
    public long duration;
    public static final Creator<VideoItem> CREATOR = new Creator<VideoItem>() {
        public VideoItem createFromParcel(Parcel source) {
            return new VideoItem(source);
        }

        public VideoItem[] newArray(int size) {
            return new VideoItem[size];
        }
    };

    protected VideoItem(Parcel in) {
        setName(in.readString());
        setPath(in.readString());
        setSize(in.readLong());
        setMimeType(in.readString());
        duration = in.readLong();
        thumbPath = in.readString();
    }

    public int describeContents() {
        return 0;
    }

    public boolean equals(Object o) {
        if (o instanceof VideoItem) {
            VideoItem item = (VideoItem) o;
            return getPath().equalsIgnoreCase(item.getPath());
        } else {
            return super.equals(o);
        }
    }

    public VideoItem() {
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getName());
        dest.writeString(getPath());
        dest.writeLong(getSize());
        dest.writeString(getMimeType());
        dest.writeLong(duration);
        dest.writeString(thumbPath);
    }
}
