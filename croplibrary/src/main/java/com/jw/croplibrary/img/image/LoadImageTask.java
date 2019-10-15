

package com.jw.croplibrary.img.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;

class LoadImageTask extends AsyncTask<Void, Void, Throwable> {
    private Context context;
    private Uri uri;
    private int desiredWidth;
    private int desiredHeight;
    private Bitmap result;

    public LoadImageTask(Context context, Uri uri, int desiredWidth, int desiredHeight) {
        this.context = context;
        this.uri = uri;
        this.desiredWidth = desiredWidth;
        this.desiredHeight = desiredHeight;
    }

    protected Throwable doInBackground(Void... params) {
        try {
            this.result = com.jw.croplibrary.img.image.CropIwaBitmapManager.get().loadToMemory(this.context, this.uri, this.desiredWidth, this.desiredHeight);
            return this.result == null ? new NullPointerException("Failed to load bitmap") : null;
        } catch (Exception var3) {
            return var3;
        }
    }

    protected void onPostExecute(Throwable e) {
        com.jw.croplibrary.img.image.CropIwaBitmapManager.get().notifyListener(this.uri, this.result, e);
    }
}
