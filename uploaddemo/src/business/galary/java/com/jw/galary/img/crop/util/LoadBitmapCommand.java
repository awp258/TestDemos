//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.jw.galary.img.crop.util;

import android.content.Context;
import android.net.Uri;
import com.jw.galary.img.crop.image.CropIwaBitmapManager;
import com.jw.galary.img.crop.image.CropIwaBitmapManager.BitmapLoadListener;

public class LoadBitmapCommand {
    private Uri uri;
    private int width;
    private int height;
    private BitmapLoadListener loadListener;
    private boolean executed;

    public LoadBitmapCommand(Uri uri, int width, int height, BitmapLoadListener loadListener) {
        this.uri = uri;
        this.width = width;
        this.height = height;
        this.loadListener = loadListener;
        this.executed = false;
    }

    public void setDimensions(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void tryExecute(Context context) {
        if (!this.executed) {
            if (this.width != 0 && this.height != 0) {
                this.executed = true;
                CropIwaBitmapManager.get().load(context, this.uri, this.width, this.height, this.loadListener);
            } else {
                CropIwaLog.d("LoadBitmapCommand for %s delayed, wrong dimensions {width=%d, height=%d}", new Object[]{this.uri.toString(), this.width, this.height});
            }
        }
    }
}
