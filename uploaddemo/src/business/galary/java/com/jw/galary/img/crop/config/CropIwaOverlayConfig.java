

package com.jw.galary.img.crop.config;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import com.jw.galary.img.ImagePicker;
import com.jw.galary.img.crop.AspectRatio;
import com.jw.galary.img.crop.shape.CropIwaOvalShape;
import com.jw.galary.img.crop.shape.CropIwaRectShape;
import com.jw.galary.img.crop.shape.CropIwaShape;
import com.jw.galary.img.crop.util.ResUtil;
import com.jw.uploaddemo.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CropIwaOverlayConfig {
    private static final float DEFAULT_CROP_SCALE = 1.0F;
    private int overlayColor;
    private int borderColor;
    private int cornerColor;
    private int gridColor;
    private int borderStrokeWidth;
    private int cornerStrokeWidth;
    private int gridStrokeWidth;
    private int minHeight;
    private int minWidth;
    private AspectRatio aspectRatio;
    private float cropScale;
    private boolean isDynamicCrop;
    private boolean shouldDrawGrid;
    private CropIwaShape cropShape;
    private List<ConfigChangeListener> listeners = new ArrayList();
    private List<ConfigChangeListener> iterationList = new ArrayList();

    public static CropIwaOverlayConfig createDefault(Context context) {
        ResUtil r = new ResUtil(context);
        CropIwaOverlayConfig config = (new CropIwaOverlayConfig()).setBorderColor(r.color(R.color.cropiwa_default_border_color)).setCornerColor(r.color(R.color.cropiwa_default_corner_color)).setGridColor(r.color(R.color.cropiwa_default_grid_color)).setOverlayColor(r.color(R.color.cropiwa_default_overlay_color)).setBorderStrokeWidth(r.dimen(R.dimen.cropiwa_default_border_stroke_width)).setCornerStrokeWidth(r.dimen(R.dimen.cropiwa_default_corner_stroke_width)).setCropScale(1.0F).setGridStrokeWidth(r.dimen(R.dimen.cropiwa_default_grid_stroke_width)).setMinWidth(r.dimen(R.dimen.cropiwa_default_min_width)).setMinHeight(r.dimen(R.dimen.cropiwa_default_min_height)).setAspectRatio(new AspectRatio(2, 1)).setShouldDrawGrid(ImagePicker.getInstance().getCutType() == 2).setDynamicCrop(true);
        CropIwaShape shape = new CropIwaRectShape(config);
        config.setCropShape(shape);
        return config;
    }

    public static CropIwaOverlayConfig createFromAttributes(Context context, AttributeSet attrs) {
        CropIwaOverlayConfig c = createDefault(context);
        if (attrs == null) {
            return c;
        } else {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CropIwaView);

            try {
                c.setMinWidth(ta.getDimensionPixelSize(R.styleable.CropIwaView_ci_min_crop_width, c.getMinWidth()));
                c.setMinHeight(ta.getDimensionPixelSize(R.styleable.CropIwaView_ci_min_crop_height, c.getMinHeight()));
                c.setAspectRatio(new AspectRatio(ta.getInteger(R.styleable.CropIwaView_ci_aspect_ratio_w, 1), ta.getInteger(R.styleable.CropIwaView_ci_aspect_ratio_h, 1)));
                c.setCropScale(ta.getFloat(R.styleable.CropIwaView_ci_crop_scale, c.getCropScale()));
                c.setBorderColor(ta.getColor(R.styleable.CropIwaView_ci_border_color, c.getBorderColor()));
                c.setBorderStrokeWidth(ta.getDimensionPixelSize(R.styleable.CropIwaView_ci_border_width, c.getBorderStrokeWidth()));
                c.setCornerColor(ta.getColor(R.styleable.CropIwaView_ci_corner_color, c.getCornerColor()));
                c.setCornerStrokeWidth(ta.getDimensionPixelSize(R.styleable.CropIwaView_ci_corner_width, c.getCornerStrokeWidth()));
                c.setGridColor(ta.getColor(R.styleable.CropIwaView_ci_grid_color, c.getGridColor()));
                c.setGridStrokeWidth(ta.getDimensionPixelSize(R.styleable.CropIwaView_ci_grid_width, c.getGridStrokeWidth()));
                c.setShouldDrawGrid(ta.getBoolean(R.styleable.CropIwaView_ci_draw_grid, c.shouldDrawGrid()));
                c.setOverlayColor(ta.getColor(R.styleable.CropIwaView_ci_overlay_color, c.getOverlayColor()));
                c.setCropShape((CropIwaShape) (ta.getInt(R.styleable.CropIwaView_ci_crop_shape, 0) == 0 ? new CropIwaRectShape(c) : new CropIwaOvalShape(c)));
                c.setDynamicCrop(ta.getBoolean(R.styleable.CropIwaView_ci_dynamic_aspect_ratio, c.isDynamicCrop()));
            } finally {
                ta.recycle();
            }

            return c;
        }
    }

    public CropIwaOverlayConfig() {
    }

    public int getOverlayColor() {
        return this.overlayColor;
    }

    public int getBorderColor() {
        return this.borderColor;
    }

    public int getCornerColor() {
        return this.cornerColor;
    }

    public int getBorderStrokeWidth() {
        return this.borderStrokeWidth;
    }

    public int getCornerStrokeWidth() {
        return this.cornerStrokeWidth;
    }

    public int getMinHeight() {
        return this.minHeight;
    }

    public int getMinWidth() {
        return this.minWidth;
    }

    public int getGridColor() {
        return this.gridColor;
    }

    public int getGridStrokeWidth() {
        return this.gridStrokeWidth;
    }

    public boolean shouldDrawGrid() {
        return this.shouldDrawGrid;
    }

    public CropIwaShape getCropShape() {
        return this.cropShape;
    }

    public boolean isDynamicCrop() {
        return this.isDynamicCrop;
    }

    public float getCropScale() {
        return this.cropScale;
    }

    public AspectRatio getAspectRatio() {
        return this.aspectRatio;
    }

    public CropIwaOverlayConfig setOverlayColor(int overlayColor) {
        this.overlayColor = overlayColor;
        return this;
    }

    public CropIwaOverlayConfig setBorderColor(int borderColor) {
        this.borderColor = borderColor;
        return this;
    }

    public CropIwaOverlayConfig setCornerColor(int cornerColor) {
        this.cornerColor = cornerColor;
        return this;
    }

    public CropIwaOverlayConfig setGridColor(int gridColor) {
        this.gridColor = gridColor;
        return this;
    }

    public CropIwaOverlayConfig setBorderStrokeWidth(int borderStrokeWidth) {
        this.borderStrokeWidth = borderStrokeWidth;
        return this;
    }

    public CropIwaOverlayConfig setCornerStrokeWidth(int cornerStrokeWidth) {
        this.cornerStrokeWidth = cornerStrokeWidth;
        return this;
    }

    public CropIwaOverlayConfig setGridStrokeWidth(int gridStrokeWidth) {
        this.gridStrokeWidth = gridStrokeWidth;
        return this;
    }

    public CropIwaOverlayConfig setCropScale(@FloatRange(from = 0.01D, to = 1.0D) float cropScale) {
        this.cropScale = cropScale;
        return this;
    }

    public CropIwaOverlayConfig setMinHeight(int minHeight) {
        this.minHeight = minHeight;
        return this;
    }

    public CropIwaOverlayConfig setMinWidth(int minWidth) {
        this.minWidth = minWidth;
        return this;
    }

    public CropIwaOverlayConfig setAspectRatio(AspectRatio ratio) {
        this.aspectRatio = ratio;
        return this;
    }

    public CropIwaOverlayConfig setShouldDrawGrid(boolean shouldDrawGrid) {
        this.shouldDrawGrid = shouldDrawGrid;
        return this;
    }

    public CropIwaOverlayConfig setCropShape(@NonNull CropIwaShape cropShape) {
        if (this.cropShape != null) {
            this.removeConfigChangeListener(this.cropShape);
        }

        this.cropShape = cropShape;
        return this;
    }

    public CropIwaOverlayConfig setDynamicCrop(boolean enabled) {
        this.isDynamicCrop = enabled;
        return this;
    }

    public void addConfigChangeListener(ConfigChangeListener listener) {
        if (listener != null) {
            this.listeners.add(listener);
        }

    }

    public void removeConfigChangeListener(ConfigChangeListener listener) {
        this.listeners.remove(listener);
    }

    public void apply() {
        this.iterationList.addAll(this.listeners);
        Iterator var1 = this.iterationList.iterator();

        while (var1.hasNext()) {
            ConfigChangeListener listener = (ConfigChangeListener) var1.next();
            listener.onConfigChanged();
        }

        this.iterationList.clear();
    }
}
