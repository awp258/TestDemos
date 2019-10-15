

package com.jw.croplibrary.img.shape;

import android.graphics.Bitmap;

import java.io.Serializable;

public interface CropIwaShapeMask extends Serializable {
  Bitmap applyMaskTo(Bitmap var1);
}
