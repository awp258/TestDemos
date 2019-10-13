package com.jw.shotRecord

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.widget.FrameLayout
import com.jw.galary.img.ImagePicker
import com.jw.shotRecord.JCameraView.shotModel
import com.jw.shotRecord.listener.CaptureListener
import com.jw.shotRecord.listener.ClickListener
import com.jw.shotRecord.listener.ReturnListener
import com.jw.shotRecord.listener.TypeListener
import com.jw.uploaddemo.R
import com.jw.uploaddemo.databinding.LayoutCaptureBinding
import kotlinx.android.synthetic.main.layout_capture.view.*


/**
 * =====================================
 * 作    者: 陈嘉桐 445263848@qq.com
 * 版    本：1.0.4
 * 创建日期：2017/4/26
 * 描    述：集成各个控件的布局
 * =====================================
 */

class CaptureLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    var mBinding: LayoutCaptureBinding =
        DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_capture, this, true)
    private var captureLisenter: CaptureListener? = null    //拍照按钮监听
    private var typeLisenter: TypeListener? = null          //拍照或录制后接结果按钮监听
    private var returnListener: ReturnListener? = null      //退出按钮监听
    private var leftClickListener: ClickListener? = null    //左边按钮监听
    private var rightClickListener: ClickListener? = null   //右边按钮监听

    private var currentTakeType = 1               //视频模式按钮

    private var iconLeft = 0
    private var iconRight = 0

    private var isFirst = true

    init {
        if (shotModel == 1) {
            mBinding.apply {
                tvCapture.visibility = View.VISIBLE
                tvVideo.visibility = View.VISIBLE
            }
        } else {
            mBinding.txtTip.visibility = View.VISIBLE
        }
        mBinding.apply {
            ivCustomRight.visibility = View.GONE
            btnCancel.visibility = View.GONE
            btnConfirm.visibility = View.GONE
            btnEdit.visibility = View.GONE
            btnPause.visibility = View.GONE
            clickListener = OnClickListener {
                when (it.id) {
                    R.id.btn_cancel -> {
                        if (typeLisenter != null) {
                            typeLisenter!!.cancel()
                        }
                        startAlphaAnimation()
                        resetCaptureLayout()
                    }
                    R.id.btn_edit -> {
                        if (typeLisenter != null) {
                            typeLisenter!!.edit()
                        }
                        //startAlphaAnimation();
                        //resetCaptureLayout();
                    }
                    R.id.btn_confirm -> {
                        if (ImagePicker.cutType == 2) {
                            typeLisenter!!.confirm()
                            startAlphaAnimation()
                            resetCaptureLayout()
                        } else {
                            typeLisenter!!.edit()
                        }
                    }
                    R.id.btn_pause -> {
                        mBinding.btnCapture.recordEnd()
                        mBinding.btnPause.visibility = View.GONE
                    }
                    R.id.btn_return -> {
                        if (leftClickListener != null) {
                            leftClickListener!!.onClick()
                        }
                    }
                    R.id.iv_custom_left -> {
                        if (leftClickListener != null) {
                            leftClickListener!!.onClick()
                        }
                    }
                    R.id.iv_custom_right -> {
                        if (leftClickListener != null) {
                            leftClickListener!!.onClick()
                        }
                    }
                    R.id.tv_capture -> {
                        setTakeType(JCameraView.BUTTON_STATE_ONLY_CAPTURE)
                        if (typeLisenter != null) {
                            typeLisenter!!.cancel()
                        }
                        startAlphaAnimation()
                        resetCaptureLayout()
                    }
                    R.id.tv_video -> {
                        setTakeType(JCameraView.BUTTON_STATE_ONLY_RECORDER)
                        if (typeLisenter != null) {
                            typeLisenter!!.cancel()
                        }
                        startAlphaAnimation()
                        resetCaptureLayout()
                    }
                }
            }
        }
        mBinding.btnCapture.setCaptureLisenter(object : CaptureListener {
            override fun takePictures() {
                if (captureLisenter != null) {
                    captureLisenter!!.takePictures()
                }
            }

            override fun recordShort(time: Long) {
                if (captureLisenter != null) {
                    captureLisenter!!.recordShort(time)
                }
                startAlphaAnimation()
            }

            override fun recordStart() {
                if (captureLisenter != null) {
                    captureLisenter!!.recordStart()
                }
                mBinding.ivCustomLeft.visibility = View.GONE
                startAlphaAnimation()
                if (shotModel == 1) {
                    mBinding.apply {
                        btnCapture.visibility = View.GONE
                        btnPause.visibility = View.VISIBLE
                        tvCapture.isClickable = false
                    }
                }
            }

            override fun recordEnd(time: Long) {
                if (captureLisenter != null) {
                    captureLisenter!!.recordEnd(time)
                }
                mBinding.ivCustomLeft.visibility = View.VISIBLE
                startAlphaAnimation()
                startTypeBtnAnimator()
                if (shotModel == 1) {
                    mBinding.apply {
                        btnPause.visibility = View.GONE
                        tvCapture.isClickable = true
                    }
                }
            }

            override fun recordZoom(zoom: Float) {
                if (captureLisenter != null) {
                    captureLisenter!!.recordZoom(zoom)
                }
            }

            override fun recordError() {
                if (captureLisenter != null) {
                    captureLisenter!!.recordError()
                }
            }

            override fun takeTypeChange(takeType: Int) {

            }
        })
    }

    fun startTypeBtnAnimator() {
        //拍照录制结果后的动画
        if (this.iconLeft != 0)
            mBinding.ivCustomLeft.visibility = View.GONE
        else
            mBinding.btnReturn.visibility = View.GONE
        if (this.iconRight != 0)
            mBinding.ivCustomRight.visibility = View.GONE
        mBinding.apply {
            btnCapture.visibility = View.GONE
            btnCancel.visibility = View.VISIBLE
            btnConfirm.visibility = View.VISIBLE
            btnEdit.visibility = View.VISIBLE
            btnCancel.isClickable = false
            btnConfirm.isClickable = false
            btnEdit.isClickable = false
        }

        //ObjectAnimator animator_cancel = ObjectAnimator.ofFloat(btn_cancel, "translationX", layout_width / 4, 0);
        //ObjectAnimator animator_confirm = ObjectAnimator.ofFloat(btn_confirm, "translationX", -layout_width / 4, 0);

        val set = AnimatorSet()
        //set.playTogether(animator_cancel, animator_confirm);
        set.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                mBinding.apply {
                    btnCancel.isClickable = true
                    btnConfirm.isClickable = true
                    btnEdit.isClickable = true
                }

            }
        })
        set.duration = 200
        set.start()
    }


    /**************************************************
     * 对外提供的API                      *
     */
    fun resetCaptureLayout() {
        mBinding.apply {
            btnCapture.resetState()
            btnCancel.visibility = View.GONE
            btnEdit.visibility = View.GONE
            btnConfirm.visibility = View.GONE
            btnCapture.visibility = View.VISIBLE
        }

        if (this.iconLeft != 0)
            mBinding.ivCustomLeft.visibility = View.VISIBLE
        else
            mBinding.btnReturn.visibility = View.VISIBLE
        if (this.iconRight != 0)
            mBinding.ivCustomRight.visibility = View.VISIBLE
    }


    fun startAlphaAnimation() {
        if (isFirst) {
            val animator_txt_tip = ObjectAnimator.ofFloat(txt_tip, "alpha", 1f, 0f)
            animator_txt_tip.duration = 500
            animator_txt_tip.start()
            isFirst = false
        }
    }

    fun setTextWithAnimation(tip: String) {
        mBinding.txtTip.text = tip
        val animator_txt_tip = ObjectAnimator.ofFloat(txt_tip, "alpha", 0f, 1f, 1f, 0f)
        animator_txt_tip.duration = 2500
        animator_txt_tip.start()
    }

    fun setDuration(duration: Int) {
        mBinding.btnCapture.setDuration(duration)
    }

    fun setButtonFeatures(state: Int) {
        mBinding.btnCapture.setButtonFeatures(state)
        if (shotModel == 1) {
            when (state) {
                JCameraView.BUTTON_STATE_ONLY_CAPTURE -> {
                    mBinding.apply {
                        tvVideo.isEnabled = false
                        tvVideo.visibility = View.GONE
                        tvCapture.visibility = View.GONE
                    }
                    setTakeType(JCameraView.BUTTON_STATE_ONLY_CAPTURE)
                }
                JCameraView.BUTTON_STATE_ONLY_RECORDER -> {
                    mBinding.apply {
                        tvCapture.isEnabled = false
                        tvVideo.visibility = View.GONE
                        tvCapture.visibility = View.GONE
                    }
                    setTakeType(JCameraView.BUTTON_STATE_ONLY_RECORDER)
                }
                JCameraView.BUTTON_STATE_BOTH -> setTakeType(JCameraView.BUTTON_STATE_ONLY_CAPTURE)
            }
        }
    }

    private fun setTakeType(takeType: Int) {
        this.currentTakeType = takeType
        when (takeType) {
            JCameraView.BUTTON_STATE_ONLY_CAPTURE -> {
                mBinding.apply {
                    tvCapture.setTextColor(Color.RED)
                    tvVideo.setTextColor(Color.WHITE)
                }
            }
            JCameraView.BUTTON_STATE_ONLY_RECORDER -> {
                mBinding.apply {
                    tvCapture.setTextColor(Color.WHITE)
                    tvVideo.setTextColor(Color.RED)
                }

            }
        }
        captureLisenter!!.takeTypeChange(takeType)
        mBinding.btnCapture.setCurrentTakeType(takeType)
    }

    fun setTip(tip: String) {
        mBinding.txtTip.text = tip
    }

    fun showTip() {
        mBinding.txtTip.visibility = View.VISIBLE
    }

    fun setIconSrc(iconLeft: Int, iconRight: Int) {
        this.iconLeft = iconLeft
        this.iconRight = iconRight
        if (this.iconLeft != 0) {
            mBinding.apply {
                ivCustomLeft.setImageResource(iconLeft)
                ivCustomLeft.visibility = View.VISIBLE
                btnReturn.visibility = View.GONE
            }

        } else {
            mBinding.apply {
                ivCustomLeft.visibility = View.GONE
                btnReturn.visibility = View.VISIBLE
            }
        }
        if (this.iconRight != 0) {
            mBinding.apply {
                ivCustomRight.setImageResource(iconRight)
                ivCustomRight.visibility = View.VISIBLE
            }
        } else {
            mBinding.ivCustomRight.visibility = View.GONE
        }
    }

    fun setTypeLisenter(typeLisenter: TypeListener) {
        this.typeLisenter = typeLisenter
    }

    fun setCaptureLisenter(captureLisenter: CaptureListener) {
        this.captureLisenter = captureLisenter
    }

    fun setReturnLisenter(returnListener: ReturnListener) {
        this.returnListener = returnListener
    }

    fun setLeftClickListener(leftClickListener: ClickListener) {
        this.leftClickListener = leftClickListener
    }

    fun setRightClickListener(rightClickListener: ClickListener) {
        this.rightClickListener = rightClickListener
    }
}