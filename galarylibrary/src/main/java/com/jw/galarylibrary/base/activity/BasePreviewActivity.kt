package com.jw.galarylibrary.base.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.jw.croplibrary.CropLibrary
import com.jw.croplibrary.img.CropActivity
import com.jw.croplibrary.video.VideoTrimmerActivity
import com.jw.galarylibrary.R
import com.jw.galarylibrary.base.BasePicker
import com.jw.galarylibrary.base.I.IPreview
import com.jw.galarylibrary.base.adapter.BasePageAdapter
import com.jw.galarylibrary.base.adapter.ThumbPreviewAdapter
import com.jw.galarylibrary.base.util.SpaceItemDecoration
import com.jw.galarylibrary.databinding.ActivityPreviewBinding
import com.jw.galarylibrary.img.adapter.ImagePageAdapter
import com.jw.library.ColorCofig
import com.jw.library.model.BaseItem
import com.jw.library.model.ImageItem
import com.jw.library.model.VideoItem
import com.jw.library.ui.BaseBindingActivity
import com.jw.library.utils.ThemeUtils
import kotlinx.android.synthetic.main.activity_preview.*
import kotlinx.android.synthetic.main.activity_preview.view.*
import java.util.*

/**
 * 创建时间：
 * 更新时间
 * 版本：
 * 作者：Mr.jin
 * 描述：预览BaseActivity
 */
abstract class BasePreviewActivity<ITEM : BaseItem>(picker: BasePicker<ITEM>) :
    BaseBindingActivity<ActivityPreviewBinding>(),
    ImagePageAdapter.PhotoViewClickListener,
    ViewPager.OnPageChangeListener,
    ThumbPreviewAdapter.OnThumbItemClickListener<ITEM>,
    BasePicker.OnItemSelectedListener<ITEM>,
    IPreview<ITEM> {

    var mPicker = picker
    lateinit var mItems: ArrayList<ITEM>
    var mCurrentPosition = 0
    lateinit var mRvAdapter: BasePageAdapter<ITEM>
    lateinit var mThumbAdapter: ThumbPreviewAdapter<ITEM>
    var isFromItems = false

    override fun getLayoutId() = R.layout.activity_preview

    abstract fun initView()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //得到当前界面的装饰视图
        if (Build.VERSION.SDK_INT >= 21) {
            val decorView = window.decorView
            //设置让应用主题内容占据状态栏和导航栏
            val option =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            decorView.systemUiVisibility = option
            //设置状态栏和导航栏颜色为透明
            window.statusBarColor = Color.TRANSPARENT
        }
        mCurrentPosition =
            intent.getIntExtra(EXTRA_SELECTED_ITEM_POSITION, 0)
        isFromItems = intent.getBooleanExtra(EXTRA_FROM_ITEMS, false)
        mItems = if (isFromItems) {
            intent.getSerializableExtra(mPicker.EXTRA_ITEMS) as ArrayList<ITEM>
        } else {
            mPicker.data[mPicker.DH_CURRENT_ITEM_FOLDER_ITEMS] as ArrayList<ITEM>
        }
        mThumbAdapter = ThumbPreviewAdapter(this, mPicker.selectedItems)
        mThumbAdapter.setOnThumbItemClickListener(this)
        initView()
        mPicker.addOnItemSelectedListener(this)
        mBinding.apply {
            topBar.apply {
                topBar.btnOk.visibility = View.VISIBLE
                topBar.tvDes.text = getString(
                    R.string.ip_preview_image_count,
                    mCurrentPosition + 1,
                    mItems.size
                )
            }
            bottomBar.apply {
                bottomBar.visibility = View.VISIBLE
                cbCheck.isChecked = isSelected
            }
            clickListener = View.OnClickListener {
                when (it.id) {
                    R.id.btn_ok -> onBack()
                    R.id.btn_back -> finish()
                    R.id.tv_preview_edit -> onEdit(mItems[mCurrentPosition])
                    R.id.cb_check -> onChecked()
                }
            }
            top_bar.setPadding(0, ThemeUtils.getStatusBarHeight(this@BasePreviewActivity), 0, 0)
            top_bar.setBackgroundColor(Color.parseColor(ColorCofig.naviBgColor))
            bottom_bar.setBackgroundColor(Color.parseColor(ColorCofig.toolbarBgColor))
            topBar.tvDes.setTextColor(Color.parseColor(ColorCofig.naviTitleColor))
            bottomBar.tv_preview_edit.setTextColor(Color.parseColor(ColorCofig.toolbarTitleColorNormal))
            bottomBar.cb_check.setTextColor(Color.parseColor(ColorCofig.toolbarTitleColorNormal))

            viewpager.apply {
                adapter = mRvAdapter
                setCurrentItem(mCurrentPosition, false)
                addOnPageChangeListener(this@BasePreviewActivity)
            }
            rvPreview.apply {
                layoutManager = LinearLayoutManager(applicationContext, 0, false)
                addItemDecoration(
                    SpaceItemDecoration(
                        ThemeUtils.dip2px(
                            this@BasePreviewActivity,
                            6.0f
                        )
                    )
                )
                adapter = mThumbAdapter
            }
        }
        setConfirmButtonBg(mBinding.topBar.btnOk)
        onItemSelected(0, null, false)

    }

    override fun OnPhotoTapListener(view: View, x: Float, y: Float) {
        when (top_bar.visibility) {
            View.VISIBLE -> {
                mBinding.apply {
                    top_bar.animation =
                        AnimationUtils.loadAnimation(this@BasePreviewActivity, R.anim.top_out)
                    bottomBar.animation =
                        AnimationUtils.loadAnimation(this@BasePreviewActivity, R.anim.fade_out)
                    top_bar.visibility = View.GONE
                    bottomBar.visibility = View.GONE
                    window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                }
            }
            View.GONE -> {
                mBinding.apply {
                    top_bar.animation =
                        AnimationUtils.loadAnimation(this@BasePreviewActivity, R.anim.top_in)
                    bottomBar.animation =
                        AnimationUtils.loadAnimation(this@BasePreviewActivity, R.anim.fade_in)
                    top_bar.visibility = View.VISIBLE
                    bottomBar.visibility = View.VISIBLE
                    top_bar.setPadding(
                        0,
                        ThemeUtils.getStatusBarHeight(this@BasePreviewActivity),
                        0,
                        0
                    )
                    window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                    ThemeUtils.changeStatusBar(
                        this@BasePreviewActivity,
                        Color.parseColor(ColorCofig.toolbarBgColor)
                    )
                }
            }
        }
    }

    override fun onThumbItemClick(item: ITEM) {
        val position = mItems.indexOf(item)
        if (position != -1 && mCurrentPosition != position) {
            mCurrentPosition = position
            mBinding.viewpager!!.setCurrentItem(
                mCurrentPosition,
                false
            )
        }
    }

    override fun onItemSelected(position: Int, item: ITEM?, isAdd: Boolean) {
        if (mPicker.selectItemCount > 0) {
            mBinding.topBar.btnOk!!.text = getString(
                R.string.ip_select_complete,
                mPicker.selectItemCount,
                mPicker.selectLimit
            )
        } else {
            mBinding.topBar.btnOk!!.text = getString(R.string.ip_complete)
        }

        if (isAdd) {
            mThumbAdapter.setSelected(item)
        }

    }

    override fun onPageSelected(position: Int) {
        mCurrentPosition = position
        val item = mItems[mCurrentPosition]
        mBinding.bottomBar.cb_check.isChecked = mPicker.isSelect(item)
        mBinding.topBar.tvDes.text = getString(
            R.string.ip_preview_image_count,
            mCurrentPosition + 1,
            mItems.size
        )
        mThumbAdapter.setSelected(item)
    }

    override fun onChecked() {
        val imageItem =
            mItems[mCurrentPosition]
        val selectLimit = mPicker.selectLimit
        if (mBinding.bottomBar.cb_check.isChecked && mPicker.selectedItems.size >= selectLimit) {
            Toast.makeText(
                this@BasePreviewActivity,
                getString(R.string.ip_select_limit, selectLimit),
                Toast.LENGTH_SHORT
            ).show()
            mBinding.bottomBar.cb_check.isChecked = false
        } else {
            var changPosition = mPicker.selectItemCount
            if (!mBinding.bottomBar.cb_check.isChecked) {
                changPosition =
                    mPicker.selectedItems.indexOf(imageItem)
                mThumbAdapter.notifyItemRemoved(changPosition)
            } else {
                mThumbAdapter.notifyItemInserted(changPosition)
            }

            mPicker.addSelectedItem(
                mCurrentPosition,
                imageItem,
                mBinding.bottomBar.cb_check.isChecked
            )
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CropActivity.REQUEST_CODE_ITEM_CROP -> {
                    val resultUri =
                        data!!.getParcelableExtra<Uri>(CropLibrary.EXTRA_CROP_ITEM_OUT_URI)
                    if (resultUri != null) {
                        var fromSelectedPosition = -1

                        for (i in 0 until mPicker.selectedItems.size) {
                            if (mPicker.selectedItems[i].path == mItems[mCurrentPosition].path) {
                                fromSelectedPosition = i
                                break
                            }
                        }
                        val item = ImageItem(resultUri.path!!)
                        if (fromSelectedPosition != -1) {
                            mPicker.addSelectedItem(
                                fromSelectedPosition,
                                mPicker.selectedItems[fromSelectedPosition],
                                false
                            )
                            mPicker.addSelectedItem(fromSelectedPosition, item as ITEM, true)
                        }

                        if (isFromItems) {
                            mItems.removeAt(mCurrentPosition)
                        }

                        mItems.add(mCurrentPosition, item as ITEM)
                        mRvAdapter.notifyDataSetChanged()
                    }


                }
                VideoTrimmerActivity.REQUEST_CODE_ITEM_CROP -> {
                    val resultUri =
                        data!!.getParcelableExtra<Uri>(CropLibrary.EXTRA_CROP_ITEM_OUT_URI)
                    if (resultUri != null) {
                        var fromSelectedPosition = -1
                        for (i in 0 until mPicker.selectedItems.size) {
                            if (mPicker.selectedItems[i].path == mItems[mCurrentPosition].path) {
                                fromSelectedPosition = i
                                break
                            }
                        }
                        val path = resultUri.path
                        val thumbPath = data.getStringExtra("thumbPath")
                        val duration = data.getLongExtra("duration", 0)
                        val item = VideoItem(path!!, thumbPath, duration)
                        if (fromSelectedPosition != -1) {
                            mPicker.addSelectedItem(
                                fromSelectedPosition,
                                mPicker.selectedItems[fromSelectedPosition],
                                false
                            )
                            mPicker.addSelectedItem(fromSelectedPosition, item as ITEM, true)
                        }

                        if (isFromItems) {
                            mItems.removeAt(mCurrentPosition)
                        }

                        mItems.add(mCurrentPosition, item as ITEM)
                        mRvAdapter.notifyDataSetChanged()
                    }

                }

            }
        }
    }

    override fun onBack() {
        if (mPicker.selectedItems.size == 0) {
            mBinding.bottomBar.cb_check.isChecked = true
            val imageItem = mItems[mCurrentPosition]
            mPicker.addSelectedItem(
                mCurrentPosition,
                imageItem,
                mBinding.bottomBar.cb_check.isChecked
            )
        }
        val intent = Intent()
        intent.putExtra(
            mPicker.EXTRA_ITEMS,
            mPicker.selectedItems
        )
        setResult(Activity.RESULT_OK, intent)
        super.finish()
    }

    override fun onDestroy() {
        mPicker.removeOnItemSelectedListener(this)
        super.onDestroy()
    }

    override fun onPageScrollStateChanged(p0: Int) {
    }

    override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
    }

    companion object {
        const val REQUEST_CODE_ITEM_PREVIEW = 1003
        const val EXTRA_SELECTED_ITEM_POSITION = "selected_item_position"
        const val EXTRA_ITEMS = "extra_items"
        const val EXTRA_FROM_ITEMS = "extra_from_items"
    }

}