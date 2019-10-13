

package com.jw.galary.base.util;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.support.v7.widget.RecyclerView.State;
import android.view.View;

public class SpaceItemDecoration extends ItemDecoration {
    int mSpace;

    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.right = this.mSpace;
        outRect.bottom = this.mSpace;
        outRect.top = this.mSpace;
        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.left = this.mSpace;
        }

    }

    public SpaceItemDecoration(int space) {
        this.mSpace = space;
    }
}
