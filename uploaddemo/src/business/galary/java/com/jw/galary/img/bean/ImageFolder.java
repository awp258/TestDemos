//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.jw.galary.img.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class ImageFolder implements Serializable {
    public String name;
    public String path;
    public ImageItem cover;
    public ArrayList<ImageItem> images;

    public ImageFolder() {
    }

    public boolean equals(Object o) {
        try {
            ImageFolder other = (ImageFolder)o;
            return this.path.equalsIgnoreCase(other.path) && this.name.equalsIgnoreCase(other.name);
        } catch (ClassCastException var3) {
            var3.printStackTrace();
            return super.equals(o);
        }
    }
}
