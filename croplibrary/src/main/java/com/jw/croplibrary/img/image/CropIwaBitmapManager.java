

package com.jw.croplibrary.img.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.jw.croplibrary.img.config.CropIwaSaveConfig;
import com.jw.croplibrary.img.shape.CropIwaShapeMask;
import com.jw.croplibrary.img.util.CropIwaLog;
import com.jw.croplibrary.img.util.CropIwaUtils;
import com.jw.croplibrary.img.util.ImageHeaderParser;
import com.jw.library.utils.BitmapUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class CropIwaBitmapManager {
    private static final CropIwaBitmapManager INSTANCE = new CropIwaBitmapManager();
    public static final int SIZE_UNSPECIFIED = -1;
    private final Object loadRequestLock = new Object();
    private Map<Uri, CropIwaBitmapManager.BitmapLoadListener> requestResultListeners = new HashMap();
    private Map<Uri, File> localCache = new HashMap();

    public static CropIwaBitmapManager get() {
        return INSTANCE;
    }

    private CropIwaBitmapManager() {
    }

    private static Options getOptimalSizeOptions(Context context, Uri bitmapUri, int reqWidth, int reqHeight) throws FileNotFoundException {
        InputStream is = context.getContentResolver().openInputStream(bitmapUri);
        Options result = new Options();
        result.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, result);
        result.inJustDecodeBounds = false;
        result.inSampleSize = calculateInSampleSize(result, reqWidth, reqHeight);
        return result;
    }

    public void load(@NonNull Context context, @NonNull Uri uri, int width, int height, CropIwaBitmapManager.BitmapLoadListener listener) {
        synchronized(this.loadRequestLock) {
            boolean requestInProgress = this.requestResultListeners.containsKey(uri);
            this.requestResultListeners.put(uri, listener);
            if (requestInProgress) {
                CropIwaLog.d("request for {%s} is already in progress", uri.toString());
                return;
            }
        }

        CropIwaLog.d("load bitmap request for {%s}", uri.toString());
        com.jw.croplibrary.img.image.LoadImageTask task = new com.jw.croplibrary.img.image.LoadImageTask(context.getApplicationContext(), uri, width, height);
        task.execute();
    }

    public void crop(Context context, CropArea cropArea, CropIwaShapeMask mask, Uri uri, CropIwaSaveConfig saveConfig, float mCurrentAngle) {
        CropImageTask cropTask = new CropImageTask(context.getApplicationContext(), cropArea, mask, uri, saveConfig, mCurrentAngle);
        cropTask.execute();
    }

    public void unregisterLoadListenerFor(Uri uri) {
        synchronized(this.loadRequestLock) {
            if (this.requestResultListeners.containsKey(uri)) {
                CropIwaLog.d("listener for {%s} loading unsubscribed", uri.toString());
                this.requestResultListeners.put(uri, null);
            }

        }
    }

    public void removeIfCached(Uri uri) {
        CropIwaUtils.delete(this.localCache.remove(uri));
    }

    void notifyListener(Uri uri, Bitmap result, Throwable e) {
        CropIwaBitmapManager.BitmapLoadListener listener;
        synchronized(this.loadRequestLock) {
            listener = this.requestResultListeners.remove(uri);
        }

        if (listener != null) {
            if (e != null) {
                listener.onLoadFailed(e);
            } else {
                listener.onBitmapLoaded(uri, result);
            }

            CropIwaLog.d("{%s} loading completed, listener got the result", uri.toString());
        } else {
            this.removeIfCached(uri);
            CropIwaLog.d("{%s} loading completed, but there was no listeners", uri.toString());
        }

    }

    @Nullable
    Bitmap loadToMemory(Context context, Uri uri, int width, int height) throws IOException {
        Uri localResUri = this.toLocalUri(context, uri);
        Options options = this.getBitmapFactoryOptions(context, localResUri, width, height);
        Bitmap result = this.tryLoadBitmap(context, localResUri, options);
        if (result != null) {
            CropIwaLog.d("loaded image with dimensions {width=%d, height=%d}", result.getWidth(), result.getHeight());
        }

        return result;
    }

    private Bitmap tryLoadBitmap(Context context, Uri uri, Options options) throws FileNotFoundException {
        while(true) {
            return BitmapUtil.getRotatedBitmap(context, uri, options);
        }
    }

    private Uri toLocalUri(Context context, Uri uri) throws IOException {
        if (this.isWebUri(uri)) {
            File cached = this.localCache.get(uri);
            if (cached == null) {
                cached = this.cacheLocally(context, uri);
                this.localCache.put(uri, cached);
            }

            return Uri.fromFile(cached);
        } else {
            return uri;
        }
    }

    private Options getBitmapFactoryOptions(Context c, Uri uri, int width, int height) throws FileNotFoundException {
        if (width != -1 && height != -1) {
            return getOptimalSizeOptions(c, uri, width, height);
        } else {
            Options options = new Options();
            options.inSampleSize = 1;
            return options;
        }
    }

    private File cacheLocally(Context context, Uri input) throws IOException {
        File local = new File(context.getExternalCacheDir(), this.generateLocalTempFileName(input));
        URL url = new URL(input.toString());
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        try {
            byte[] buffer = new byte[1024];
            bis = new BufferedInputStream(url.openStream());
            bos = new BufferedOutputStream(new FileOutputStream(local));

            while(true) {
                int read;
                if ((read = bis.read(buffer)) == -1) {
                    bos.flush();
                    break;
                }

                bos.write(buffer, 0, read);
            }
        } finally {
            CropIwaUtils.closeSilently(bis);
            CropIwaUtils.closeSilently(bos);
        }

        CropIwaLog.d("cached {%s} as {%s}", input.toString(), local.getAbsolutePath());
        return local;
    }

    private static int calculateInSampleSize(Options options, int reqWidth, int reqHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            int halfHeight = height / 2;

            for(int halfWidth = width / 2; halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth; inSampleSize *= 2) {
            }
        }

        return inSampleSize;
    }

    private static Bitmap ensureCorrectRotation(Context context, Uri uri, Bitmap bitmap) {
        int degrees = exifToDegrees(extractExifOrientation(context, uri));
        if (degrees != 0) {
            Matrix matrix = new Matrix();
            matrix.preRotate((float)degrees);
            return transformBitmap(bitmap, matrix);
        } else {
            return bitmap;
        }
    }

    private static Bitmap transformBitmap(@NonNull Bitmap bitmap, @NonNull Matrix transformMatrix) {
        Bitmap result = bitmap;

        try {
            Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), transformMatrix, true);
            if (!bitmap.sameAs(converted)) {
                result = converted;
                bitmap.recycle();
            }
        } catch (OutOfMemoryError var4) {
            CropIwaLog.e(var4.getMessage(), var4);
        }

        return result;
    }

    private static int extractExifOrientation(@NonNull Context context, @NonNull Uri imageUri) {
        InputStream is = null;

        byte var4;
        try {
            is = context.getContentResolver().openInputStream(imageUri);
            if (is != null) {
                int var10 = (new ImageHeaderParser(is)).getOrientation();
                return var10;
            }

            byte var3 = 0;
            return var3;
        } catch (IOException var8) {
            CropIwaLog.e(var8.getMessage(), var8);
            var4 = 0;
        } finally {
            CropIwaUtils.closeSilently(is);
        }

        return var4;
    }

    private static int exifToDegrees(int exifOrientation) {
        short rotation;
        switch(exifOrientation) {
            case 3:
            case 4:
                rotation = 180;
                break;
            case 5:
            case 6:
                rotation = 90;
                break;
            case 7:
            case 8:
                rotation = 270;
                break;
            default:
                rotation = 0;
        }

        return rotation;
    }

    private boolean isWebUri(Uri uri) {
        String scheme = uri.getScheme();
        return "http".equals(scheme) || "https".equals(scheme);
    }

    private String generateLocalTempFileName(Uri uri) {
        return "temp_" + uri.getLastPathSegment() + "_" + System.currentTimeMillis();
    }

    public interface BitmapLoadListener {
        void onBitmapLoaded(Uri var1, Bitmap var2);

        void onLoadFailed(Throwable var1);
    }
}
