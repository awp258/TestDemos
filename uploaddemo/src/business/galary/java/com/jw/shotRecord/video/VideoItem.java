//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.jw.shotRecord.video;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class VideoItem implements Serializable, Parcelable {
    public String name;
    public String path;
    public String thumbPath;
    public long size;
    public long duration;
    public String mimeType;
    public static final Creator<VideoItem> CREATOR = new Creator<VideoItem>() {
        public VideoItem createFromParcel(Parcel source) {
            return new VideoItem(source);
        }

        public VideoItem[] newArray(int size) {
            return new VideoItem[size];
        }
    };

    public boolean equals(Object o) {
        if (o instanceof VideoItem) {
            VideoItem item = (VideoItem) o;
            return this.path.equalsIgnoreCase(item.path);
        } else {
            return super.equals(o);
        }
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.path);
        dest.writeLong(this.size);
        dest.writeString(this.mimeType);
        dest.writeLong(this.duration);
        dest.writeString(this.thumbPath);
    }

    public VideoItem() {
    }

    protected VideoItem(Parcel in) {
        this.name = in.readString();
        this.path = in.readString();
        this.size = in.readLong();
        this.mimeType = in.readString();
        this.duration = in.readLong();
        this.thumbPath = in.readString();
    }
}
