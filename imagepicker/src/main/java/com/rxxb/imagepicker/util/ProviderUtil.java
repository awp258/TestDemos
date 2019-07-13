

package com.rxxb.imagepicker.util;

import android.content.Context;

public class ProviderUtil {
 public ProviderUtil() {
 }

 public static String getFileProviderName(Context context) {
  return context.getPackageName() + ".provider";
 }
}
