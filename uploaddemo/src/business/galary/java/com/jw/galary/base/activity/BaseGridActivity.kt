package com.jw.galary.base.activity

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.View
import android.widget.CompoundButton
import com.jw.galary.base.BasePicker
import com.jw.galary.base.I.IGrid
import com.jw.galary.base.adapter.FolderAdapter
import com.jw.galary.base.adapter.GridAdapter
import com.jw.galary.base.bean.BaseItem
import com.jw.galary.base.bean.Folder
import com.jw.galary.img.ImagePicker
import com.jw.galary.img.ui.ImageGridActivity
import com.jw.galary.img.util.Utils
import com.jw.galary.img.view.FolderPopUpWindow
import com.jw.galary.img.view.GridSpacingItemDecoration
import com.jw.uploaddemo.ColorCofig
import com.jw.uploaddemo.R
import com.jw.uploaddemo.databinding.ActivityGridBinding
import com.jw.uploaddemo.uploadPlugin.UploadPluginBindingActivity
import kotlinx.android.synthetic.main.activity_grid.*
import kotlinx.android.synthetic.main.activity_grid.view.*


abstract class BaseGridActivity<ITEM : BaseItem>(picker: BasePicker<ITEM>) :
    UploadPluginBindingActivity<ActivityGridBinding>(),
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
                setTextColor(Color.parseColor(ColorCofig.toolbarTitleColorNormal))
            }
            top_bar.setBackgroundColor(Color.parseColor(ColorCofig.naviBgColor))
            topBar.tvDes.setTextColor(Color.parseColor(ColorCofig.naviTitleColor))
            footerBar.setBackgroundColor(Color.parseColor(ColorCofig.toolbarBgColor))
            footerBar.cb_origin.setTextColor(Color.parseColor(ColorCofig.toolbarTitleColorDisabled))
            footerBar.tv_dir.setTextColor(Color.parseColor(ColorCofig.toolbarTitleColorNormal))
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
            layoutManager = GridLayoutManager(this@BaseGridActivity, ImageGridActivity.SPAN_COUNT)
            addItemDecoration(
                GridSpacingItemDecoration(
                    ImageGridActivity.SPAN_COUNT,
                    Utils.dp2px(this@BaseGridActivity, 2.0f),
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
     * @param imageItem ImageItem
     * @param position Int
     */
    override fun onItemClick(view: View, imageItem: ITEM, position: Int) {
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
                onEdit(imageItem)
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
                    setTextColor(Color.parseColor(ColorCofig.toolbarTitleColorNormal))
                }
                btnPreview.apply {
                    isEnabled = true
                    text = getString(R.string.ip_preview_count, mPicker.selectItemCount)
                    setTextColor(Color.parseColor(ColorCofig.toolbarTitleColorNormal))
                }

            }

        } else {
            mBinding.apply {
                topBar.btnOk.apply {
                    isEnabled = false
                    text = getString(R.string.ip_complete)
                    setTextColor(Color.parseColor(ColorCofig.toolbarTitleColorDisabled))
                }
                btnPreview.apply {
                    isEnabled = false
                    text = getString(R.string.ip_preview)
                    setTextColor(Color.parseColor(ColorCofig.toolbarTitleColorDisabled))
                }
            }
        }

        if (mPicker.isMultiMode) {
            for (i in 0 until mRecyclerAdapter.itemCount) {
                if (mRecyclerAdapter.getItem(i).path != null && mRecyclerAdapter.getItem(i).path == item!!.path) {
                    mRecyclerAdapter.refreshCheckedData(i)
                    return
                }
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            mPicker.REQUEST_CODE_ITEM_CROP -> { //从裁剪页面带数据返回
                val resultUri = data!!.getParcelableExtra<Uri>(mPicker.EXTRA_CROP_ITEM_OUT_URI)
                if (resultUri != null) {
                    val imageItem = BaseItem()
                    imageItem.path = resultUri.path
                    mPicker.clearSelectedItems()
                    mPicker.addSelectedItem(0, imageItem as ITEM, true)
                    onBack()
                }
            }
            mPicker.RESULT_CODE_ITEM_BACK -> {   //从其他页面反击不带数据
                mBinding.cbOrigin.isChecked = mPicker.isOrigin
            }
            mPicker.RESULT_CODE_ITEMS -> {    //带结果返回
                onBack()
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
        setResult(mPicker.RESULT_CODE_ITEMS, intent)
        super.finish()
    }

    override fun onDestroy() {
        mPicker.removeOnItemSelectedListener(this)
        super.onDestroy()
    }
}