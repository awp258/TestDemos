

package com.jw.galarylibrary.img.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.RoundRectShape;

public class TextDrawable extends ShapeDrawable {
    private final Paint textPaint;
    private final Paint borderPaint;
    private static final float SHADE_FACTOR = 0.9F;
    private final String text;
    private final int color;
    private final RectShape shape;
    private final int height;
    private final int width;
    private final int fontSize;
    private final float radius;
    private final int borderThickness;

    private TextDrawable(TextDrawable.Builder builder) {
        super(builder.shape);
        this.shape = builder.shape;
        this.height = builder.height;
        this.width = builder.width;
        this.radius = builder.radius;
        this.text = builder.toUpperCase ? builder.text.toUpperCase() : builder.text;
        this.color = builder.color;
        this.fontSize = builder.fontSize;
        this.textPaint = new Paint();
        this.textPaint.setColor(builder.textColor);
        this.textPaint.setAntiAlias(true);
        this.textPaint.setFakeBoldText(builder.isBold);
        this.textPaint.setStyle(Style.FILL);
        this.textPaint.setTypeface(builder.font);
        this.textPaint.setTextAlign(Align.CENTER);
        this.textPaint.setStrokeWidth((float)builder.borderThickness);
        this.borderThickness = builder.borderThickness;
        this.borderPaint = new Paint();
        this.borderPaint.setColor(this.getDarkerShade(this.color));
        this.borderPaint.setStyle(Style.STROKE);
        this.borderPaint.setStrokeWidth((float)this.borderThickness);
        Paint paint = this.getPaint();
        paint.setColor(this.color);
    }

    private int getDarkerShade(int color) {
        return Color.rgb((int)(0.9F * (float)Color.red(color)), (int)(0.9F * (float)Color.green(color)), (int)(0.9F * (float)Color.blue(color)));
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
        Rect r = this.getBounds();
        if (this.borderThickness > 0) {
            this.drawBorder(canvas);
        }

        int count = canvas.save();
        canvas.translate((float)r.left, (float)r.top);
        int width = this.width < 0 ? r.width() : this.width;
        int height = this.height < 0 ? r.height() : this.height;
        int fontSize = this.fontSize < 0 ? Math.min(width, height) / 2 : this.fontSize;
        this.textPaint.setTextSize((float)fontSize);
        canvas.drawText(this.text, (float)(width / 2), (float)(height / 2) - (this.textPaint.descent() + this.textPaint.ascent()) / 2.0F, this.textPaint);
        canvas.restoreToCount(count);
    }

    private void drawBorder(Canvas canvas) {
        RectF rect = new RectF(this.getBounds());
        rect.inset((float)(this.borderThickness / 2), (float)(this.borderThickness / 2));
        if (this.shape instanceof OvalShape) {
            canvas.drawOval(rect, this.borderPaint);
        } else if (this.shape instanceof RoundRectShape) {
            canvas.drawRoundRect(rect, this.radius, this.radius, this.borderPaint);
        } else {
            canvas.drawRect(rect, this.borderPaint);
        }

    }

    public void setAlpha(int alpha) {
        this.textPaint.setAlpha(alpha);
    }

    public void setColorFilter(ColorFilter cf) {
        this.textPaint.setColorFilter(cf);
    }

    public int getOpacity() {
        return -3;
    }

    public int getIntrinsicWidth() {
        return this.width;
    }

    public int getIntrinsicHeight() {
        return this.height;
    }

    public static TextDrawable.IShapeBuilder builder() {
        return new TextDrawable.Builder();
    }

    public interface IShapeBuilder {
        TextDrawable.IConfigBuilder beginConfig();

        TextDrawable.IBuilder rect();

        TextDrawable.IBuilder round();

        TextDrawable.IBuilder roundRect(int var1);

        TextDrawable buildRect(String var1, int var2);

        TextDrawable buildRoundRect(String var1, int var2, int var3);

        TextDrawable buildRound(String var1, int var2);
    }

    public interface IBuilder {
        TextDrawable build(String var1, int var2);
    }

    public interface IConfigBuilder {
        TextDrawable.IConfigBuilder width(int var1);

        TextDrawable.IConfigBuilder height(int var1);

        TextDrawable.IConfigBuilder textColor(int var1);

        TextDrawable.IConfigBuilder withBorder(int var1);

        TextDrawable.IConfigBuilder useFont(Typeface var1);

        TextDrawable.IConfigBuilder fontSize(int var1);

        TextDrawable.IConfigBuilder bold();

        TextDrawable.IConfigBuilder toUpperCase();

        TextDrawable.IShapeBuilder endConfig();
    }

    public static class Builder implements TextDrawable.IConfigBuilder, TextDrawable.IShapeBuilder, TextDrawable.IBuilder {
        private String text;
        private int color;
        private int borderThickness;
        private int width;
        private int height;
        private Typeface font;
        private RectShape shape;
        public int textColor;
        private int fontSize;
        private boolean isBold;
        private boolean toUpperCase;
        public float radius;

        private Builder() {
            this.text = "";
            this.color = -7829368;
            this.textColor = -1;
            this.borderThickness = 0;
            this.width = -1;
            this.height = -1;
            this.shape = new RectShape();
            this.font = Typeface.create("sans-serif-light", 0);
            this.fontSize = -1;
            this.isBold = false;
            this.toUpperCase = false;
        }

        public TextDrawable.IConfigBuilder width(int width) {
            this.width = width;
            return this;
        }

        public TextDrawable.IConfigBuilder height(int height) {
            this.height = height;
            return this;
        }

        public TextDrawable.IConfigBuilder textColor(int color) {
            this.textColor = color;
            return this;
        }

        public TextDrawable.IConfigBuilder withBorder(int thickness) {
            this.borderThickness = thickness;
            return this;
        }

        public TextDrawable.IConfigBuilder useFont(Typeface font) {
            this.font = font;
            return this;
        }

        public TextDrawable.IConfigBuilder fontSize(int size) {
            this.fontSize = size;
            return this;
        }

        public TextDrawable.IConfigBuilder bold() {
            this.isBold = true;
            return this;
        }

        public TextDrawable.IConfigBuilder toUpperCase() {
            this.toUpperCase = true;
            return this;
        }

        public TextDrawable.IConfigBuilder beginConfig() {
            return this;
        }

        public TextDrawable.IShapeBuilder endConfig() {
            return this;
        }

        public TextDrawable.IBuilder rect() {
            this.shape = new RectShape();
            return this;
        }

        public TextDrawable.IBuilder round() {
            this.shape = new OvalShape();
            return this;
        }

        public TextDrawable.IBuilder roundRect(int radius) {
            this.radius = (float)radius;
            float[] radii = new float[]{(float)radius, (float)radius, (float)radius, (float)radius, (float)radius, (float)radius, (float)radius, (float)radius};
            this.shape = new RoundRectShape(radii, (RectF)null, (float[])null);
            return this;
        }

        public TextDrawable buildRect(String text, int color) {
            this.rect();
            return this.build(text, color);
        }

        public TextDrawable buildRoundRect(String text, int color, int radius) {
            this.roundRect(radius);
            return this.build(text, color);
        }

        public TextDrawable buildRound(String text, int color) {
            this.round();
            return this.build(text, color);
        }

        public TextDrawable build(String text, int color) {
            this.color = color;
            this.text = text;
            return new TextDrawable(this);
        }
    }
}
