//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.jw.shotRecord.video;

import java.io.Serializable;
import java.util.ArrayList;

public class VideoFolder implements Serializable {
    public String name;
    public String path;
    public VideoItem cover;
    public ArrayList<VideoItem> videos;

    public VideoFolder() {
    }

    public boolean equals(Object o) {
        try {
            VideoFolder other = (VideoFolder) o;
            return this.path.equalsIgnoreCase(other.path) && this.name.equalsIgnoreCase(other.name);
        } catch (ClassCastException var3) {
            var3.printStackTrace();
            return super.equals(o);
        }
    }
}
