

package com.jw.galary.img.view;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import com.jw.uploaddemo.R;

public class FolderPopUpWindow extends PopupWindow implements OnClickListener {
    private ListView listView;
    private FolderPopUpWindow.OnItemClickListener onItemClickListener;
    private final View masker;
    private final View marginView;
    private int marginPx;

    public FolderPopUpWindow(Context context, BaseAdapter adapter) {
        super(context);
        final View view = View.inflate(context, R.layout.pop_folder, (ViewGroup)null);
        this.masker = view.findViewById(R.id.masker);
        this.masker.setOnClickListener(this);
        this.marginView = view.findViewById(R.id.margin);
        this.marginView.setOnClickListener(this);
        this.listView = (ListView)view.findViewById(R.id.listView);
        this.listView.setAdapter(adapter);
        this.setContentView(view);
        this.setWidth(-1);
        this.setHeight(-1);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.setBackgroundDrawable(new ColorDrawable(0));
        this.setAnimationStyle(0);
        view.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                int maxHeight = view.getHeight() * 5 / 8;
                int realHeight = FolderPopUpWindow.this.listView.getHeight();
                LayoutParams listParams = FolderPopUpWindow.this.listView.getLayoutParams();
                listParams.height = realHeight > maxHeight ? maxHeight : realHeight;
                FolderPopUpWindow.this.listView.setLayoutParams(listParams);
                android.widget.LinearLayout.LayoutParams marginParams = (android.widget.LinearLayout.LayoutParams)FolderPopUpWindow.this.marginView.getLayoutParams();
                marginParams.height = FolderPopUpWindow.this.marginPx;
                FolderPopUpWindow.this.marginView.setLayoutParams(marginParams);
                FolderPopUpWindow.this.enterAnimator();
            }
        });
        this.listView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (FolderPopUpWindow.this.onItemClickListener != null) {
                    FolderPopUpWindow.this.onItemClickListener.onItemClick(adapterView, view, position, l);
                }

            }
        });
    }

    private void enterAnimator() {
        ObjectAnimator alpha = ObjectAnimator.ofFloat(this.masker, "alpha", new float[]{0.0F, 1.0F});
        ObjectAnimator translationY = ObjectAnimator.ofFloat(this.listView, "translationY", new float[]{(float)this.listView.getHeight(), 0.0F});
        AnimatorSet set = new AnimatorSet();
        set.setDuration(400L);
        set.playTogether(new Animator[]{alpha, translationY});
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.start();
    }

    public void dismiss() {
        this.exitAnimator();
    }

    private void exitAnimator() {
        ObjectAnimator alpha = ObjectAnimator.ofFloat(this.masker, "alpha", new float[]{1.0F, 0.0F});
        ObjectAnimator translationY = ObjectAnimator.ofFloat(this.listView, "translationY", new float[]{0.0F, (float)this.listView.getHeight()});
        AnimatorSet set = new AnimatorSet();
        set.setDuration(300L);
        set.playTogether(new Animator[]{alpha, translationY});
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.addListener(new AnimatorListener() {
            public void onAnimationStart(Animator animation) {
                FolderPopUpWindow.this.listView.setVisibility(0);
            }

            public void onAnimationEnd(Animator animation) {
                FolderPopUpWindow.super.dismiss();
            }

            public void onAnimationCancel(Animator animation) {
            }

            public void onAnimationRepeat(Animator animation) {
            }
        });
        set.start();
    }

    public void setOnItemClickListener(FolderPopUpWindow.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setSelection(int selection) {
        this.listView.setSelection(selection);
    }

    public void setMargin(int marginPx) {
        this.marginPx = marginPx;
    }

    public void onClick(View v) {
        this.dismiss();
    }

    public interface OnItemClickListener {
        void onItemClick(AdapterView<?> var1, View var2, int var3, long var4);
    }
}
