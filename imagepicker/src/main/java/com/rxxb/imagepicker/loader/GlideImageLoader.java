//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.rxxb.imagepicker.loader;

import android.app.Activity;
import android.net.Uri;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.rxxb.imagepicker.R.drawable;
import java.io.File;

public class GlideImageLoader implements ImageLoader {
    public GlideImageLoader() {
    }

    public void displayImage(Activity activity, String path, ImageView imageView, int width, int height) {
        Glide.with(activity).load(Uri.fromFile(new File(path))).error(drawable.ic_default_image).placeholder(drawable.ic_default_image).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
    }

    public void displayImagePreview(Activity activity, String path, ImageView imageView, int width, int height) {
        Glide.with(activity).load(Uri.fromFile(new File(path))).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
    }

    public void clearMemoryCache() {
    }
}
