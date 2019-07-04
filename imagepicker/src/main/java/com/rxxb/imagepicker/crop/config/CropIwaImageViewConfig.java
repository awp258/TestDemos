//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.rxxb.imagepicker.crop.config;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.FloatRange;
import android.util.AttributeSet;
import com.rxxb.imagepicker.R.styleable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CropIwaImageViewConfig {
    private static final float DEFAULT_MIN_SCALE = 0.2F;
    private static final float DEFAULT_MAX_SCALE = 3.0F;
    public static final int SCALE_UNSPECIFIED = -1;
    private float maxScale;
    private float minScale;
    private boolean isScaleEnabled;
    private boolean isTranslationEnabled;
    private float scale;
    private InitialPosition initialPosition;
    private List<ConfigChangeListener> configChangeListeners = new ArrayList();

    public static CropIwaImageViewConfig createDefault() {
        return (new CropIwaImageViewConfig()).setMaxScale(3.0F).setMinScale(0.2F).setImageTranslationEnabled(true).setImageScaleEnabled(true).setScale(-1.0F);
    }

    public static CropIwaImageViewConfig createFromAttributes(Context c, AttributeSet attrs) {
        CropIwaImageViewConfig config = createDefault();
        if (attrs == null) {
            return config;
        } else {
            TypedArray ta = c.obtainStyledAttributes(attrs, styleable.CropIwaView);

            try {
                config.setMaxScale(ta.getFloat(styleable.CropIwaView_ci_max_scale, config.getMaxScale()));
                config.setImageTranslationEnabled(ta.getBoolean(styleable.CropIwaView_ci_translation_enabled, config.isImageTranslationEnabled()));
                config.setImageScaleEnabled(ta.getBoolean(styleable.CropIwaView_ci_scale_enabled, config.isImageScaleEnabled()));
                config.setImageInitialPosition(InitialPosition.values()[ta.getInt(styleable.CropIwaView_ci_initial_position, 0)]);
            } finally {
                ta.recycle();
            }

            return config;
        }
    }

    public CropIwaImageViewConfig() {
    }

    public float getMaxScale() {
        return this.maxScale;
    }

    public float getMinScale() {
        return this.minScale;
    }

    public boolean isImageScaleEnabled() {
        return this.isScaleEnabled;
    }

    public boolean isImageTranslationEnabled() {
        return this.isTranslationEnabled;
    }

    public InitialPosition getImageInitialPosition() {
        return this.initialPosition;
    }

    public float getScale() {
        return this.scale;
    }

    public CropIwaImageViewConfig setMinScale(@FloatRange(from = 0.001D) float minScale) {
        this.minScale = minScale;
        return this;
    }

    public CropIwaImageViewConfig setMaxScale(@FloatRange(from = 0.001D) float maxScale) {
        this.maxScale = maxScale;
        return this;
    }

    public CropIwaImageViewConfig setImageScaleEnabled(boolean scaleEnabled) {
        this.isScaleEnabled = scaleEnabled;
        return this;
    }

    public CropIwaImageViewConfig setImageTranslationEnabled(boolean imageTranslationEnabled) {
        this.isTranslationEnabled = imageTranslationEnabled;
        return this;
    }

    public CropIwaImageViewConfig setImageInitialPosition(InitialPosition initialPosition) {
        this.initialPosition = initialPosition;
        return this;
    }

    public CropIwaImageViewConfig setScale(@FloatRange(from = 0.01D,to = 1.0D) float scale) {
        this.scale = scale;
        return this;
    }

    public void addConfigChangeListener(ConfigChangeListener configChangeListener) {
        if (configChangeListener != null) {
            this.configChangeListeners.add(configChangeListener);
        }

    }

    public void removeConfigChangeListener(ConfigChangeListener configChangeListener) {
        this.configChangeListeners.remove(configChangeListener);
    }

    public void apply() {
        Iterator var1 = this.configChangeListeners.iterator();

        while(var1.hasNext()) {
            ConfigChangeListener listener = (ConfigChangeListener)var1.next();
            listener.onConfigChanged();
        }

    }
}
