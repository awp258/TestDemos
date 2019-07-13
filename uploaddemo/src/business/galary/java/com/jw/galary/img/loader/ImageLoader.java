

package com.jw.galary.img.loader;

import android.app.Activity;
import android.widget.ImageView;
import java.io.Serializable;

public interface ImageLoader extends Serializable {
  void displayImage(Activity var1, String var2, ImageView var3, int var4, int var5);

  void displayImagePreview(Activity var1, String var2, ImageView var3, int var4, int var5);

  void clearMemoryCache();
}
