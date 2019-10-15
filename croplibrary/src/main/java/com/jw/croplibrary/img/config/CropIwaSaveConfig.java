

package com.jw.croplibrary.img.config;

import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.support.annotation.IntRange;

public class CropIwaSaveConfig {
    private CompressFormat compressFormat;
    private int quality;
    private int width;
    private int height;
    private Uri dstUri;

    public CropIwaSaveConfig(Uri dstPath) {
        this.dstUri = dstPath;
        this.compressFormat = CompressFormat.PNG;
        this.width = -1;
        this.height = -1;
        this.quality = 90;
    }

    public CompressFormat getCompressFormat() {
        return this.compressFormat;
    }

    public int getQuality() {
        return this.quality;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public Uri getDstUri() {
        return this.dstUri;
    }

    public static class Builder {
        private CropIwaSaveConfig saveConfig;

        public Builder(Uri dstPath) {
            this.saveConfig = new CropIwaSaveConfig(dstPath);
        }

        public CropIwaSaveConfig.Builder setSize(int width, int height) {
            this.saveConfig.width = width;
            this.saveConfig.height = height;
            return this;
        }

        public CropIwaSaveConfig.Builder setCompressFormat(CompressFormat compressFormat) {
            this.saveConfig.compressFormat = compressFormat;
            return this;
        }

        public CropIwaSaveConfig.Builder setQuality(@IntRange(from = 0L,to = 100L) int quality) {
            this.saveConfig.quality = quality;
            return this;
        }

        public CropIwaSaveConfig.Builder saveToFile(Uri uri) {
            this.saveConfig.dstUri = uri;
            return this;
        }

        public CropIwaSaveConfig build() {
            return this.saveConfig;
        }
    }
}
