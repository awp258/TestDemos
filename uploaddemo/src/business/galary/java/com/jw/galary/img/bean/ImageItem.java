//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.jw.galary.img.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class ImageItem implements Serializable, Parcelable {
    public String name;
    public String path;
    public long size;
    public int width;
    public int height;
    public String mimeType;
    public long addTime;
    public static final Creator<ImageItem> CREATOR = new Creator<ImageItem>() {
        public ImageItem createFromParcel(Parcel source) {
            return new ImageItem(source);
        }

        public ImageItem[] newArray(int size) {
            return new ImageItem[size];
        }
    };

    public boolean equals(Object o) {
        if (o instanceof ImageItem) {
            ImageItem item = (ImageItem)o;
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
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeString(this.mimeType);
        dest.writeLong(this.addTime);
    }

    public ImageItem() {
    }

    protected ImageItem(Parcel in) {
        this.name = in.readString();
        this.path = in.readString();
        this.size = in.readLong();
        this.width = in.readInt();
        this.height = in.readInt();
        this.mimeType = in.readString();
        this.addTime = in.readLong();
    }
}
