//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.jw.galary.img.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory.Options;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore.Images.Media;
import android.text.TextUtils;
import android.util.Base64;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapUtil {
    private BitmapUtil() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static int getBitmapDegree(String path) {
        short degree = 0;

        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt("Orientation", 1);
            switch(orientation) {
                case 3:
                    degree = 180;
                    break;
                case 6:
                    degree = 90;
                    break;
                case 8:
                    degree = 270;
            }
        } catch (IOException var4) {
            var4.printStackTrace();
        }

        return degree;
    }

    public static Bitmap rotateBitmapByDegree(Bitmap bitmap, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate((float)degree);
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        if (!bitmap.isRecycled()) {
            bitmap.recycle();
        }

        return newBitmap;
    }

    public static Uri getRotatedUri(Activity activity, String path) {
        int degree = getBitmapDegree(path);
        if (degree != 0) {
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            Bitmap newBitmap = rotateBitmapByDegree(bitmap, degree);
            return Uri.parse(Media.insertImage(activity.getContentResolver(), newBitmap, (String)null, (String)null));
        } else {
            return Uri.fromFile(new File(path));
        }
    }

    public static Bitmap rotateBitmapByDegree(String path, int degree) {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        return rotateBitmapByDegree(bitmap, degree);
    }

    public static Bitmap getBitmap(File file) {
        if (file == null) {
            return null;
        } else {
            BufferedInputStream is = null;

            Object var3;
            try {
                is = new BufferedInputStream(new FileInputStream(file));
                Bitmap var2 = BitmapFactory.decodeStream(is);
                return var2;
            } catch (FileNotFoundException var13) {
                var13.printStackTrace();
                var3 = null;
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException var12) {
                    var12.printStackTrace();
                }

            }

            return (Bitmap)var3;
        }
    }

    public static Bitmap getBitmap(String filePath) {
        return TextUtils.isEmpty(filePath) ? null : BitmapFactory.decodeFile(filePath);
    }

    public static Bitmap getScaledBitmap(String filePath, int width, int height) {
        Options decodeOptions = new Options();
        decodeOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, decodeOptions);
        int actualWidth = decodeOptions.outWidth;
        int actualHeight = decodeOptions.outHeight;
        int maxWidth;
        int maxHeight;
        if (actualHeight < actualWidth) {
            maxWidth = height;
            maxHeight = width;
        } else {
            maxWidth = width;
            maxHeight = height;
        }

        int desiredWidth = getResizedDimension(maxWidth, maxHeight, actualWidth, actualHeight);
        int desiredHeight = getResizedDimension(maxHeight, maxWidth, actualHeight, actualWidth);
        decodeOptions.inJustDecodeBounds = false;
        decodeOptions.inSampleSize = findBestSampleSize(actualWidth, actualHeight, desiredWidth, desiredHeight);
        Bitmap tempBitmap = BitmapFactory.decodeFile(filePath, decodeOptions);
        Bitmap bitmap;
        if (tempBitmap == null || tempBitmap.getWidth() <= desiredWidth && tempBitmap.getHeight() <= desiredHeight) {
            bitmap = tempBitmap;
        } else {
            bitmap = Bitmap.createScaledBitmap(tempBitmap, desiredWidth, desiredHeight, true);
            tempBitmap.recycle();
        }

        return bitmap;
    }

    public static Bitmap getScaledBitmap(Context context, int imageResId, int maxWidth, int maxHeight) {
        Options decodeOptions = new Options();
        decodeOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), imageResId, decodeOptions);
        int actualWidth = decodeOptions.outWidth;
        int actualHeight = decodeOptions.outHeight;
        int desiredWidth = getResizedDimension(maxWidth, maxHeight, actualWidth, actualHeight);
        int desiredHeight = getResizedDimension(maxHeight, maxWidth, actualHeight, actualWidth);
        decodeOptions.inJustDecodeBounds = false;
        decodeOptions.inSampleSize = findBestSampleSize(actualWidth, actualHeight, desiredWidth, desiredHeight);
        Bitmap tempBitmap = BitmapFactory.decodeResource(context.getResources(), imageResId, decodeOptions);
        Bitmap bitmap;
        if (tempBitmap == null || tempBitmap.getWidth() <= desiredWidth && tempBitmap.getHeight() <= desiredHeight) {
            bitmap = tempBitmap;
        } else {
            bitmap = Bitmap.createScaledBitmap(tempBitmap, desiredWidth, desiredHeight, true);
            tempBitmap.recycle();
        }

        return bitmap;
    }

    private static int findBestSampleSize(int actualWidth, int actualHeight, int desiredWidth, int desiredHeight) {
        double wr = (double)actualWidth / (double)desiredWidth;
        double hr = (double)actualHeight / (double)desiredHeight;
        double ratio = Math.min(wr, hr);

        float n;
        for(n = 1.0F; (double)(n * 2.0F) <= ratio; n *= 2.0F) {
        }

        return (int)n;
    }

    private static int getResizedDimension(int maxPrimary, int maxSecondary, int actualPrimary, int actualSecondary) {
        if (maxPrimary == 0 && maxSecondary == 0) {
            return actualPrimary;
        } else {
            double ratio;
            if (maxPrimary == 0) {
                ratio = (double)maxSecondary / (double)actualSecondary;
                return (int)((double)actualPrimary * ratio);
            } else if (maxSecondary == 0) {
                return maxPrimary;
            } else {
                ratio = (double)actualSecondary / (double)actualPrimary;
                int resized = maxPrimary;
                if ((double)maxPrimary * ratio > (double)maxSecondary) {
                    resized = (int)((double)maxSecondary / ratio);
                }

                return resized;
            }
        }
    }

    public static Bitmap compress(String srcImg) {
        Options options = new Options();
        options.inSampleSize = computeSize(options.outWidth, options.outHeight);
        return BitmapFactory.decodeFile(srcImg, options);
    }

    public static boolean saveBitmap2File(Bitmap bm, String savePath) {
        boolean saved = true;

        try {
            File f = new File(savePath);
            if (f.exists()) {
                f.delete();
            }

            FileOutputStream out = new FileOutputStream(f);
            bm.compress(CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException var5) {
            var5.printStackTrace();
            saved = false;
        } catch (IOException var6) {
            var6.printStackTrace();
            saved = false;
        }

        return saved;
    }

    private static int computeSize(int srcWidth, int srcHeight) {
        srcWidth = srcWidth % 2 == 1 ? srcWidth + 1 : srcWidth;
        srcHeight = srcHeight % 2 == 1 ? srcHeight + 1 : srcHeight;
        int longSide = Math.max(srcWidth, srcHeight);
        int shortSide = Math.min(srcWidth, srcHeight);
        float scale = (float)shortSide / (float)longSide;
        if (scale <= 1.0F && (double)scale > 0.5625D) {
            if (longSide < 1664) {
                return 1;
            } else if (longSide >= 1664 && longSide < 4990) {
                return 2;
            } else if (longSide > 4990 && longSide < 10240) {
                return 4;
            } else {
                return longSide / 1280 == 0 ? 1 : longSide / 1280;
            }
        } else if ((double)scale <= 0.5625D && (double)scale > 0.5D) {
            return longSide / 1280 == 0 ? 1 : longSide / 1280;
        } else {
            return (int)Math.ceil((double)longSide / (1280.0D / (double)scale));
        }
    }

    public static String base64Image(String filePath) {
        String imgBase64 = "";

        try {
            File file = new File(filePath);
            byte[] content = new byte[(int)file.length()];
            FileInputStream finputstream = new FileInputStream(file);
            finputstream.read(content);
            finputstream.close();
            imgBase64 = Base64.encodeToString(content, 0);
        } catch (IOException var5) {
            var5.printStackTrace();
        }

        return imgBase64;
    }
}
