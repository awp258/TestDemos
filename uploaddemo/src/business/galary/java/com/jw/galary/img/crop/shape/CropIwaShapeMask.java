

package com.jw.galary.img.crop.shape;

import android.graphics.Bitmap;
import java.io.Serializable;

public interface CropIwaShapeMask extends Serializable {
  Bitmap applyMaskTo(Bitmap var1);
}
