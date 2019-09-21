package com.jw.shotRecord;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
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


/**
 * =====================================
 * 作    者: 陈嘉桐 445263848@qq.com
 * 版    本：1.0.4
 * 创建日期：2017/4/26
 * 描    述：集成各个控件的布局
 * =====================================
 */

public class CaptureLayout extends FrameLayout implements View.OnClickListener {

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
    private int currentTakeType = 1;               //视频模式按钮

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
        initView();
        initEvent();
    }

    public void initEvent() {
        //默认Typebutton为隐藏
        iv_custom_right.setVisibility(GONE);
        btn_cancel.setVisibility(GONE);
        btn_confirm.setVisibility(GONE);
        btn_edit.setVisibility(GONE);
        btn_pause.setVisibility(GONE);
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
                tv_capture.setClickable(false);
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
                tv_capture.setClickable(true);
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

            @Override
            public void takeTypeChange(int takeType) {

            }
        });
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


    private void initView() {
        View view = View.inflate(getContext(),R.layout.layout_capture,this);
        btn_capture = view.findViewById(R.id.btn_capture);
        btn_cancel = view.findViewById(R.id.btn_cancel);
        btn_edit = view.findViewById(R.id.btn_edit);
        btn_confirm = view.findViewById(R.id.btn_confirm);
        btn_pause = view.findViewById(R.id.btn_pause);
        btn_return = view.findViewById(R.id.btn_return);
        iv_custom_left = view.findViewById(R.id.iv_custom_left);
        iv_custom_right = view.findViewById(R.id.iv_custom_right);
        tv_capture = view.findViewById(R.id.tv_capture);
        tv_video = view.findViewById(R.id.tv_video);
        txt_tip = view.findViewById(R.id.txt_tip);
        btn_capture.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        btn_edit.setOnClickListener(this);
        btn_confirm.setOnClickListener(this);
        btn_pause.setOnClickListener(this);
        btn_return.setOnClickListener(this);
        iv_custom_left.setOnClickListener(this);
        iv_custom_right.setOnClickListener(this);
        tv_capture.setOnClickListener(this);
        tv_video.setOnClickListener(this);
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
        switch (state){
            case JCameraView.BUTTON_STATE_ONLY_CAPTURE:
                tv_video.setEnabled(false);
                tv_video.setVisibility(View.GONE);
                tv_capture.setVisibility(View.GONE);
                setTakeType(JCameraView.TYPE_TAKE_CAPTURE);
                break;
            case JCameraView.BUTTON_STATE_ONLY_RECORDER:
                tv_capture.setEnabled(false);
                tv_video.setVisibility(View.GONE);
                tv_capture.setVisibility(View.GONE);
                setTakeType(JCameraView.TYPE_TAKE_RECORD);
                break;
            case JCameraView.BUTTON_STATE_BOTH:
                setTakeType(JCameraView.TYPE_TAKE_CAPTURE);
                break;
        }
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
            iv_custom_left.setImageResource(iconLeft);
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

    public void setTakeType(int takeType){
        this.currentTakeType = takeType;
        switch (takeType) {
            case 1:
                tv_capture.setTextColor(Color.RED);
                tv_video.setTextColor(Color.WHITE);
                break;
            case 2:
                tv_capture.setTextColor(Color.WHITE);
                tv_video.setTextColor(Color.RED);
                break;
        }
        captureLisenter.takeTypeChange(takeType);
        btn_capture.setCurrentTakeType(takeType);
    }
    
    @Override
    public void onClick(View v) {
        int i = v.getId();//取消按钮
//编辑按钮
//确认按钮
//录制按钮
//返回按钮
//左边自定义按钮
//右边自定义按钮
        if (i == R.id.btn_cancel) {
            if (typeLisenter != null) {
                typeLisenter.cancel();
            }
            startAlphaAnimation();
            resetCaptureLayout();
        } else if (i == R.id.btn_edit) {
            if (typeLisenter != null) {
                typeLisenter.edit();
            }
            //startAlphaAnimation();
            //resetCaptureLayout();
        } else if (i == R.id.btn_confirm) {
            if (ImagePicker.getInstance().getCutType() == 2) {
                typeLisenter.confirm();
                startAlphaAnimation();
                resetCaptureLayout();
            } else {
                typeLisenter.edit();
            }
/*            if(ImagePicker.getInstance().getAspectRatio().getRatio()==1.0){
                typeLisenter.confirm();
                startAlphaAnimation();
                resetCaptureLayout();
            }else {
                typeLisenter.edit();
            }*/
        } else if (i == R.id.btn_pause) {
            btn_capture.recordEnd();
            btn_pause.setVisibility(View.GONE);
        } else if (i == R.id.btn_return) {
            if (leftClickListener != null) {
                leftClickListener.onClick();
            }
        } else if (i == R.id.iv_custom_left) {
            if (leftClickListener != null) {
                leftClickListener.onClick();
            }
        } else if (i == R.id.iv_custom_right) {
            if (leftClickListener != null) {
                leftClickListener.onClick();
            }
        } else if (i == R.id.tv_capture) {
            setTakeType(JCameraView.TYPE_TAKE_CAPTURE);
            if (typeLisenter != null) {
                typeLisenter.cancel();
            }
            startAlphaAnimation();
            resetCaptureLayout();
        } else if (i == R.id.tv_video) {
            setTakeType(JCameraView.TYPE_TAKE_RECORD);
            if (typeLisenter != null) {
                typeLisenter.cancel();
            }
            startAlphaAnimation();
            resetCaptureLayout();
        }
    }
}