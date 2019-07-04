//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.rxxb.imagepicker.view;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.AttributeSet;

public class SuperCheckBox extends AppCompatCheckBox {
    public SuperCheckBox(Context context) {
        super(context);
    }

    public SuperCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SuperCheckBox(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public boolean performClick() {
        boolean handled = super.performClick();
        if (!handled) {
            this.playSoundEffect(0);
        }

        return handled;
    }
}
