

package com.rxxb.imagepicker.crop.image;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;

public class CropIwaResultReceiver extends BroadcastReceiver {
    private static final String ACTION_CROP_COMPLETED = "cropIwa_action_crop_completed";
    private static final String EXTRA_ERROR = "extra_error";
    private static final String EXTRA_URI = "extra_uri";
    private CropIwaResultReceiver.Listener listener;

    public CropIwaResultReceiver() {
    }

    public static void onCropCompleted(Context context, Uri croppedImageUri) {
        Intent intent = new Intent("cropIwa_action_crop_completed");
        intent.putExtra("extra_uri", croppedImageUri);
        context.sendBroadcast(intent);
    }

    public static void onCropFailed(Context context, Throwable e) {
        Intent intent = new Intent("cropIwa_action_crop_completed");
        intent.putExtra("extra_error", e);
        context.sendBroadcast(intent);
    }

    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        if (this.listener != null) {
            if (extras.containsKey("extra_error")) {
                this.listener.onCropFailed((Throwable)extras.getSerializable("extra_error"));
            } else if (extras.containsKey("extra_uri")) {
                this.listener.onCropSuccess((Uri)extras.getParcelable("extra_uri"));
            }
        }

    }

    public void register(Context context) {
        IntentFilter filter = new IntentFilter("cropIwa_action_crop_completed");
        context.registerReceiver(this, filter);
    }

    public void unregister(Context context) {
        context.unregisterReceiver(this);
    }

    public void setListener(CropIwaResultReceiver.Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        void onCropSuccess(Uri var1);

        void onCropFailed(Throwable var1);
    }
}
