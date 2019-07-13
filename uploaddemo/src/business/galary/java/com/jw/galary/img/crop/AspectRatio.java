

package com.jw.galary.img.crop;

import android.support.annotation.IntRange;

public class AspectRatio {
    public static final AspectRatio IMG_SRC = new AspectRatio(-1, -1);
    private final int width;
    private final int height;

    public AspectRatio(@IntRange(from = 1L) int w, @IntRange(from = 1L) int h) {
        this.width = w;
        this.height = h;
    }

    public int getWidth() {
        return this.width;
    }

    public boolean isSquare() {
        return this.width == this.height;
    }

    public int getHeight() {
        return this.height;
    }

    public float getRatio() {
        return (float)this.width / (float)this.height;
    }
}
