package com.jw.galarylibrary.base.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.View
import android.widget.CompoundButton
import com.jw.croplibrary.CropLibrary
import com.jw.croplibrary.img.CropActivity
import com.jw.croplibrary.video.VideoTrimmerActivity
import com.jw.galarylibrary.R
import com.jw.galarylibrary.base.BasePicker
import com.jw.galarylibrary.base.I.IGrid
import com.jw.galarylibrary.base.adapter.FolderAdapter
import com.jw.galarylibrary.base.adapter.GridAdapter
import com.jw.galarylibrary.base.bean.Folder
import com.jw.galarylibrary.databinding.ActivityGridBinding
import com.jw.galarylibrary.img.ImagePicker
import com.jw.galarylibrary.img.view.FolderPopUpWindow
import com.jw.galarylibrary.img.view.GridSpacingItemDecoration
import com.jw.library.model.BaseItem
import com.jw.library.model.ImageItem
import com.jw.library.model.VideoItem
import com.jw.library.ui.BaseBindingActivity
import com.jw.library.utils.ThemeUtils
import kotlinx.android.synthetic.main.activity_grid.*
import kotlinx.android.synthetic.main.activity_grid.view.*
import java.io.File

/**
 * 创建时间：
 * 更新时间
 * 版本：
 * 作者：Mr.jin
 * 描述：选择BaseActivity
 */
abstract class BaseGridActivity<ITEM : BaseItem>(picker: BasePicker<ITEM>) :
    BaseBindingActivity<ActivityGridBinding>(),
    GridAdapter.OnItemsLoadedListener<ITEM>,
    GridAdapter.OnItemClickListener<ITEM>,
    BasePicker.OnItemSelectedListener<ITEM>,
    CompoundButton.OnCheckedChangeListener,
    IGrid<ITEM> {

    lateinit var mFolderAdapter: FolderAdapter<ITEM>
    lateinit var mFolderPopupWindow: FolderPopUpWindow
    lateinit var mRecyclerAdapter: GridAdapter<ITEM>
    var mPicker = picker

    override fun getLayoutId() = R.layout.activity_grid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPicker.clear()
        mPicker.addOnItemSelectedListener(this)
        mBinding.apply {
            clickListener = View.OnClickListener {
                when (it.id) {
                    R.id.btn_preview -> onPreview(null)
                    R.id.ll_dir -> onFolderPop()
                    R.id.btn_back -> finish()
                    R.id.btn_ok -> onBack()
                }
            }
            cbOrigin.apply {
                setOnCheckedChangeListener(this@BaseGridActivity)
                isChecked = mPicker.isOrigin
                setTextColor(Color.parseColor(com.jw.library.ColorCofig.toolbarTitleColorNormal))
            }
            top_bar.setBackgroundColor(Color.parseColor(com.jw.library.ColorCofig.naviBgColor))
            topBar.tvDes.setTextColor(Color.parseColor(com.jw.library.ColorCofig.naviTitleColor))
            footerBar.setBackgroundColor(Color.parseColor(com.jw.library.ColorCofig.toolbarBgColor))
            footerBar.cb_origin.setTextColor(Color.parseColor(com.jw.library.ColorCofig.toolbarTitleColorDisabled))
            footerBar.tv_dir.setTextColor(Color.parseColor(com.jw.library.ColorCofig.toolbarTitleColorNormal))
            if (mPicker.isMultiMode) {
                topBar.btnOk.visibility = View.VISIBLE
                btnPreview.visibility = View.VISIBLE
            } else {
                topBar.btnOk.visibility = View.GONE
                btnPreview.visibility = View.GONE
            }
            setConfirmButtonBg(topBar.btnOk)
        }
        mFolderAdapter = FolderAdapter(this, null)
        mRecyclerAdapter = GridAdapter(this, null, mPicker)
    }

    override fun onFolderPop() {
        onSwitchFolder()
        mFolderAdapter.refreshData(mPicker.itemFolders)
        if (mFolderPopupWindow.isShowing) {
            mFolderPopupWindow.dismiss()
        } else {
            mFolderPopupWindow.showAtLocation(mBinding.footerBar, 0, 0, 0)
            var index = mFolderAdapter.selectIndex
            index = if (index == 0) index else index - 1
            mFolderPopupWindow.setSelection(index)
        }
    }

    override fun onSwitchFolder() {
        mFolderPopupWindow = FolderPopUpWindow(this, mFolderAdapter)
        mFolderPopupWindow.setOnItemClickListener { adapterView, view, position, l ->
            mFolderAdapter.selectIndex = position
            mPicker.currentItemFolderPosition = position
            mFolderPopupWindow.dismiss()
            val imageFolder = adapterView.adapter.getItem(position) as Folder<ITEM>
            mRecyclerAdapter.refreshData(imageFolder.items)
            mBinding.tvDir.text = imageFolder.name

        }
        mFolderPopupWindow.setMargin(mBinding.footerBar.height)
    }

    /**
     * 图片读取完毕
     * @param folders MutableList<Folder<ImageItem>>
     */
    override fun onItemsLoaded(folders: MutableList<Folder<ITEM>>) {
        mPicker.itemFolders = folders
        mRecyclerAdapter.refreshData(folders[0].items)
        mRecyclerAdapter.setOnItemClickListener(this)
        mBinding.recycler.apply {
            layoutManager = GridLayoutManager(this@BaseGridActivity, SPAN_COUNT)
            addItemDecoration(
                GridSpacingItemDecoration(
                    SPAN_COUNT,
                    ThemeUtils.dip2px(this@BaseGridActivity, 2.0f),
                    false
                )
            )
            adapter = mRecyclerAdapter
        }
        mFolderAdapter.refreshData(folders)
    }

    /**
     * 图片点击
     * @param view View
     * @param item ImageItem
     * @param position Int
     */
    override fun onItemClick(view: View, item: ITEM, position: Int) {
        if (mPicker.isMultiMode)
            onPreview(position)
        else {
            mPicker.clearSelectedItems()
            mPicker.addSelectedItem(
                position,
                mPicker.currentItemFolderItems[position],
                true
            )
            if (mPicker.isCrop)
                onEdit(item)
            else
                onBack()
        }
    }

    /**
     * 图片选中
     * @param position Int
     * @param item ImageItem
     * @param isAdd Boolean
     */
    override fun onItemSelected(position: Int, item: ITEM?, isAdd: Boolean) {
        if (mPicker.selectItemCount > 0) {
            mBinding.apply {
                topBar.btnOk.apply {
                    isEnabled = true
                    text = getString(
                        R.string.ip_select_complete,
                        mPicker.selectItemCount,
                        mPicker.selectLimit
                    )
                    setTextColor(Color.parseColor(com.jw.library.ColorCofig.toolbarTitleColorNormal))
                }
                btnPreview.apply {
                    isEnabled = true
                    text = getString(R.string.ip_preview_count, mPicker.selectItemCount)
                    setTextColor(Color.parseColor(com.jw.library.ColorCofig.toolbarTitleColorNormal))
                }

            }

        } else {
            mBinding.apply {
                topBar.btnOk.apply {
                    isEnabled = false
                    text = getString(R.string.ip_complete)
                    setTextColor(Color.parseColor(com.jw.library.ColorCofig.toolbarTitleColorDisabled))
                }
                btnPreview.apply {
                    isEnabled = false
                    text = getString(R.string.ip_preview)
                    setTextColor(Color.parseColor(com.jw.library.ColorCofig.toolbarTitleColorDisabled))
                }
            }
        }

        if (mPicker.isMultiMode) {
            for (i in 0 until mRecyclerAdapter.itemCount) {
                if (mRecyclerAdapter.getItem(i)?.path != null && mRecyclerAdapter.getItem(i)?.path == item!!.path) {
                    mRecyclerAdapter.refreshCheckedData(i)
                    return
                }
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CropActivity.REQUEST_CODE_ITEM_CROP -> {
                    val resultUri =
                        data!!.getParcelableExtra<Uri>(CropLibrary.EXTRA_CROP_ITEM_OUT_URI)
                    val item = ImageItem(resultUri.path!!)
                    mPicker.clearSelectedItems()
                    mPicker.addSelectedItem(0, item as ITEM, true)
                    onBack()
                }
                VideoTrimmerActivity.REQUEST_CODE_ITEM_CROP -> {
                    val resultUri =
                        data!!.getParcelableExtra<Uri>(CropLibrary.EXTRA_CROP_ITEM_OUT_URI)
                    val path = resultUri.path
                    val thumbPath = data.getStringExtra("thumbPath")
                    val duration = data.getLongExtra("duration", 0)
                    val item = VideoItem(path!!, thumbPath, duration)
                    mPicker.clearSelectedItems()
                    mPicker.addSelectedItem(0, item as ITEM, true)
                    onBack()
                }
                BasePreviewActivity.REQUEST_CODE_ITEM_PREVIEW -> {
                    mBinding.cbOrigin.isChecked = mPicker.isOrigin
                    onBack()
                }
                REQUEST_CODE_ITEM_TAKE -> {
                    mPicker.galleryAddPic(this, mPicker.takeFile!!)
                    val path = mPicker.takeFile!!.absolutePath
                    val item = BaseItem()
                    item.path = path
                    mPicker.clearSelectedItems()
                    mPicker.addSelectedItem(0, item as ITEM, true)
                    if (mPicker.isCrop) {
                        CropActivity.start(this, Uri.fromFile(File(item.path)))
                    }
                }
            }
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        val id = buttonView?.id
        if (id == R.id.cb_origin) {
            mPicker.isOrigin = isChecked
        }
    }

    override fun onBack() {
        val intent = Intent()
        intent.putExtra(mPicker.EXTRA_ITEMS, mPicker.selectedItems)
        if (mPicker is ImagePicker)
            intent.putExtra("isImage", true)
        else
            intent.putExtra("isImage", false)
        setResult(Activity.RESULT_OK, intent)
        super.finish()
    }

    override fun onDestroy() {
        mPicker.removeOnItemSelectedListener(this)
        super.onDestroy()
    }

    companion object {
        const val REQUEST_CODE_ITEM_TAKE = 1001
        const val SPAN_COUNT = 4
    }
}