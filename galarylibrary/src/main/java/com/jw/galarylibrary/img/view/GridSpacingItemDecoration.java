

package com.jw.galarylibrary.img.view;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.support.v7.widget.RecyclerView.State;
import android.view.View;

public class GridSpacingItemDecoration extends ItemDecoration {
    private int spanCount;
    private int spacing;
    private boolean includeEdge;

    public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
        this.spanCount = spanCount;
        this.spacing = spacing;
        this.includeEdge = includeEdge;
    }

    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
        int position = parent.getChildAdapterPosition(view);
        int column = position % this.spanCount;
        if (this.includeEdge) {
            outRect.left = this.spacing - column * this.spacing / this.spanCount;
            outRect.right = (column + 1) * this.spacing / this.spanCount;
            if (position < this.spanCount) {
                outRect.top = this.spacing;
            }

            outRect.bottom = this.spacing;
        } else {
            outRect.left = column * this.spacing / this.spanCount;
            outRect.right = this.spacing - (column + 1) * this.spacing / this.spanCount;
            if (position >= this.spanCount) {
                outRect.top = this.spacing;
            }
        }

    }
}
