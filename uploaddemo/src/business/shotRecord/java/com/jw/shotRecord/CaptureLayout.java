package com.jw.shotRecord;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.jw.galary.img.ImagePicker;
import com.jw.shotRecord.listener.CaptureListener;
import com.jw.shotRecord.listener.ClickListener;
import com.jw.shotRecord.listener.ReturnListener;
import com.jw.shotRecord.listener.TypeListener;
import com.jw.uploaddemo.R;
import com.jw.uploaddemo.UploadConfig;
import com.jw.uploaddemo.base.utils.DateUtils;


/**
 * =====================================
 * 作    者: 陈嘉桐 445263848@qq.com
 * 版    本：1.0.4
 * 创建日期：2017/4/26
 * 描    述：集成各个控件的布局
 * =====================================
 */

public class CaptureLayout extends FrameLayout {

    private CaptureListener captureLisenter;    //拍照按钮监听
    private TypeListener typeLisenter;          //拍照或录制后接结果按钮监听
    private ReturnListener returnListener;      //退出按钮监听
    private ClickListener leftClickListener;    //左边按钮监听
    private ClickListener rightClickListener;   //右边按钮监听

    public void setTypeLisenter(TypeListener typeLisenter) {
        this.typeLisenter = typeLisenter;
    }

    public void setCaptureLisenter(CaptureListener captureLisenter) {
        this.captureLisenter = captureLisenter;
    }

    public void setReturnLisenter(ReturnListener returnListener) {
        this.returnListener = returnListener;
    }

    private CaptureButton btn_capture;      //拍照按钮
    private ImageView btn_confirm;         //确认按钮
    private ImageView btn_edit;         //确认按钮
    private ImageView btn_cancel;          //取消按钮
    private ReturnButton btn_return;        //返回按钮
    private ImageView iv_custom_left;            //左边自定义按钮
    private ImageView iv_custom_right;            //右边自定义按钮
    private TextView txt_tip;               //提示文本
    private ImageView btn_pause;         //录制停止按钮
    private TextView tv_capture;               //图片模式按钮
    private TextView tv_video;               //视频模式按钮
    private TextView tv_time;               //视频模式按钮
    private int currentState = 1;               //视频模式按钮

    private int iconLeft = 0;
    private int iconRight = 0;

    private boolean isFirst = true;

    public CaptureLayout(Context context) {
        this(context, null);
    }

    public CaptureLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CaptureLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
        initEvent();
    }

    public void initEvent() {
        //默认Typebutton为隐藏
        iv_custom_right.setVisibility(GONE);
        btn_cancel.setVisibility(GONE);
        btn_confirm.setVisibility(GONE);
        btn_edit.setVisibility(GONE);
        btn_pause.setVisibility(GONE);
    }

    public void startTypeBtnAnimator() {
        //拍照录制结果后的动画
        if (this.iconLeft != 0)
            iv_custom_left.setVisibility(GONE);
        else
            btn_return.setVisibility(GONE);
        if (this.iconRight != 0)
            iv_custom_right.setVisibility(GONE);
        btn_capture.setVisibility(GONE);
        btn_cancel.setVisibility(VISIBLE);
        btn_confirm.setVisibility(VISIBLE);
        btn_edit.setVisibility(VISIBLE);
        btn_cancel.setClickable(false);
        btn_confirm.setClickable(false);
        btn_edit.setClickable(false);
        //ObjectAnimator animator_cancel = ObjectAnimator.ofFloat(btn_cancel, "translationX", layout_width / 4, 0);
        //ObjectAnimator animator_confirm = ObjectAnimator.ofFloat(btn_confirm, "translationX", -layout_width / 4, 0);

        AnimatorSet set = new AnimatorSet();
        //set.playTogether(animator_cancel, animator_confirm);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                btn_cancel.setClickable(true);
                btn_confirm.setClickable(true);
                btn_edit.setClickable(true);
            }
        });
        set.setDuration(200);
        set.start();
    }


    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_capture, this);

        //拍照按钮
        btn_capture = view.findViewById(R.id.btn_capture);
        btn_capture.setCaptureLisenter(new CaptureListener() {
            @Override
            public void takePictures() {
                if (captureLisenter != null) {
                    captureLisenter.takePictures();
                }
            }

            @Override
            public void recordShort(long time) {
                if (captureLisenter != null) {
                    captureLisenter.recordShort(time);
                }
                startAlphaAnimation();
            }

            @Override
            public void recordStart() {
                if (captureLisenter != null) {
                    captureLisenter.recordStart();
                }
                iv_custom_left.setVisibility(View.GONE);
                startAlphaAnimation();

                btn_capture.setVisibility(View.GONE);
                btn_pause.setVisibility(View.VISIBLE);
                //post(timeRunnable);
            }

            @Override
            public void recordEnd(long time) {
                if (captureLisenter != null) {
                    captureLisenter.recordEnd(time);
                }
                iv_custom_left.setVisibility(View.VISIBLE);
                startAlphaAnimation();
                startTypeBtnAnimator();

                btn_pause.setVisibility(View.GONE);
            }

            @Override
            public void recordZoom(float zoom) {
                if (captureLisenter != null) {
                    captureLisenter.recordZoom(zoom);
                }
            }

            @Override
            public void recordError() {
                if (captureLisenter != null) {
                    captureLisenter.recordError();
                }
            }
        });

        //取消按钮
        btn_cancel = view.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(v -> {
            if (typeLisenter != null) {
                typeLisenter.cancel();
            }
            startAlphaAnimation();
            resetCaptureLayout();

        });

        //编辑按钮
        btn_edit = view.findViewById(R.id.btn_edit);
        btn_edit.setOnClickListener(v -> {
            if (typeLisenter != null) {
                typeLisenter.edit();
            }
            //startAlphaAnimation();
            //resetCaptureLayout();
        });

        //确认按钮
        btn_confirm = view.findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(v -> {
            if (ImagePicker.getInstance().getCutType() == 2) {
                typeLisenter.confirm();
                startAlphaAnimation();
                resetCaptureLayout();
            } else {
                typeLisenter.edit();
            }
        });


        //录制按钮
        btn_pause = view.findViewById(R.id.btn_pause);
        btn_pause.setOnClickListener(v -> {
            btn_capture.recordEnd();
        });

        //返回按钮
        btn_return = view.findViewById(R.id.btn_return);
        btn_return.setOnClickListener(v -> {
            if (leftClickListener != null) {
                leftClickListener.onClick();
            }
        });

        //左边自定义按钮
        iv_custom_left = view.findViewById(R.id.iv_custom_left);
        iv_custom_left.setOnClickListener(v -> {
            if (leftClickListener != null) {
                leftClickListener.onClick();
            }
        });

        //左边自定义按钮
        iv_custom_right = view.findViewById(R.id.iv_custom_right);
        iv_custom_right.setOnClickListener(v -> {
            if (leftClickListener != null) {
                leftClickListener.onClick();
            }
        });

        tv_capture = view.findViewById(R.id.tv_capture);
        tv_capture.setOnClickListener(v -> {
            btn_capture.currentState = 1;
            tv_capture.setTextColor(Color.RED);
            tv_video.setTextColor(Color.WHITE);
            if (typeLisenter != null) {
                typeLisenter.cancel();
            }
            startAlphaAnimation();
            resetCaptureLayout();
        });

        tv_video = view.findViewById(R.id.tv_video);
        tv_video.setOnClickListener(v -> {
            btn_capture.currentState = 2;
            tv_capture.setTextColor(Color.WHITE);
            tv_video.setTextColor(Color.RED);
            if (typeLisenter != null) {
                typeLisenter.cancel();
            }
            startAlphaAnimation();
            resetCaptureLayout();
        });

        txt_tip = view.findViewById(R.id.txt_tip);
        tv_time = view.findViewById(R.id.tv_time);
        switch (UploadConfig.INSTANCE.getSHOT_TYPE()) {
            case 4:
                currentState = 1;
                tv_capture.setTextColor(Color.RED);
                tv_video.setTextColor(Color.WHITE);
                break;
            case 5:
                currentState = 1;
                tv_capture.setTextColor(Color.RED);
                tv_video.setTextColor(Color.WHITE);
                tv_video.setClickable(false);
                break;
            case 6:
                currentState = 2;
                tv_capture.setTextColor(Color.WHITE);
                tv_video.setTextColor(Color.RED);
                tv_capture.setClickable(false);
                break;
        }
    }

    /**************************************************
     * 对外提供的API                      *
     **************************************************/
    public void resetCaptureLayout() {
        btn_capture.resetState();
        btn_cancel.setVisibility(GONE);
        btn_edit.setVisibility(GONE);
        btn_confirm.setVisibility(GONE);
        btn_capture.setVisibility(VISIBLE);
        if (this.iconLeft != 0)
            iv_custom_left.setVisibility(VISIBLE);
        else
            btn_return.setVisibility(VISIBLE);
        if (this.iconRight != 0)
            iv_custom_right.setVisibility(VISIBLE);
    }


    public void startAlphaAnimation() {
        if (isFirst) {
            ObjectAnimator animator_txt_tip = ObjectAnimator.ofFloat(txt_tip, "alpha", 1f, 0f);
            animator_txt_tip.setDuration(500);
            animator_txt_tip.start();
            isFirst = false;
        }
    }

    public void setTextWithAnimation(String tip) {
        txt_tip.setText(tip);
        ObjectAnimator animator_txt_tip = ObjectAnimator.ofFloat(txt_tip, "alpha", 0f, 1f, 1f, 0f);
        animator_txt_tip.setDuration(2500);
        animator_txt_tip.start();
    }

    public void setDuration(int duration) {
        btn_capture.setDuration(duration);
    }

    public void setButtonFeatures(int state) {
        btn_capture.setButtonFeatures(state);
    }

    public void setTip(String tip) {
        txt_tip.setText(tip);
    }

    public void showTip() {
        txt_tip.setVisibility(VISIBLE);
    }

    public void setIconSrc(int iconLeft, int iconRight) {
        this.iconLeft = iconLeft;
        this.iconRight = iconRight;
        if (this.iconLeft != 0) {
            iv_custom_left.setImageResource(R.drawable.bg_back_record);
            iv_custom_left.setVisibility(VISIBLE);
            btn_return.setVisibility(GONE);
        } else {
            iv_custom_left.setVisibility(GONE);
            btn_return.setVisibility(VISIBLE);
        }
        if (this.iconRight != 0) {
            iv_custom_right.setImageResource(iconRight);
            iv_custom_right.setVisibility(VISIBLE);
        } else {
            iv_custom_right.setVisibility(GONE);
        }
    }

    public void setLeftClickListener(ClickListener leftClickListener) {
        this.leftClickListener = leftClickListener;
    }

    public void setRightClickListener(ClickListener rightClickListener) {
        this.rightClickListener = rightClickListener;
    }

    long mStartingTimeMillis;
    private Runnable timeRunnable = new Runnable() {
        @Override
        public void run() {
            mStartingTimeMillis = System.currentTimeMillis();
            while (true) {
                int length = (int) (System.currentTimeMillis() - mStartingTimeMillis);
                tv_time.setText(DateUtils.getDuration(length, "mm:ss"));
            }
        }
    };
}
