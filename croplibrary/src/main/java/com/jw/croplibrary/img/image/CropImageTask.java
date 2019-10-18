

package com.jw.croplibrary.img.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;

import com.jw.croplibrary.img.config.CropIwaSaveConfig;
import com.jw.croplibrary.img.shape.CropIwaShapeMask;
import com.jw.croplibrary.img.util.CropIwaUtils;

import java.io.IOException;
import java.io.OutputStream;

class CropImageTask extends AsyncTask<Void, Void, Throwable> {
  private Context context;
    private CropArea cropArea;
  private CropIwaShapeMask mask;
  private Uri srcUri;
  private CropIwaSaveConfig saveConfig;
  private float mCurrentAngle;

    public CropImageTask(Context context, CropArea cropArea, CropIwaShapeMask mask, Uri srcUri, CropIwaSaveConfig saveConfig, float mCurrentAngle) {
    this.context = context;
    this.cropArea = cropArea;
    this.mask = mask;
    this.srcUri = srcUri;
    this.saveConfig = saveConfig;
    this.mCurrentAngle = mCurrentAngle;
  }

  protected Throwable doInBackground(Void... params) {
    try {
        Bitmap bitmap = CropIwaBitmapManager.get().loadToMemory(this.context, this.srcUri, this.saveConfig.getWidth(), this.saveConfig.getHeight());
      if (bitmap == null) {
        return new NullPointerException("Failed to load bitmap");
      } else {
        if (this.mCurrentAngle != 0.0F) {
          Matrix tempMatrix = new Matrix();
          tempMatrix.setRotate(this.mCurrentAngle, (float)(bitmap.getWidth() / 2), (float)(bitmap.getHeight() / 2));
          Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), tempMatrix, true);
          if (bitmap != rotatedBitmap) {
            bitmap.recycle();
          }

          bitmap = rotatedBitmap;
        }

        Bitmap cropped = this.cropArea.applyCropTo(bitmap);
        cropped = this.mask.applyMaskTo(cropped);
        Uri dst = this.saveConfig.getDstUri();
        OutputStream os = this.context.getContentResolver().openOutputStream(dst);
        cropped.compress(this.saveConfig.getCompressFormat(), this.saveConfig.getQuality(), os);
        CropIwaUtils.closeSilently(os);
        bitmap.recycle();
        cropped.recycle();
        return null;
      }
    } catch (IOException var6) {
      return var6;
    }
  }

  protected void onPostExecute(Throwable throwable) {
    if (throwable == null) {
        CropIwaResultReceiver.onCropCompleted(this.context, this.saveConfig.getDstUri());
    } else {
        CropIwaResultReceiver.onCropFailed(this.context, throwable);
    }

  }
}
