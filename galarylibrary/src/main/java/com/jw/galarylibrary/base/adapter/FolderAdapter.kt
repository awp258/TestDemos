package com.jw.galarylibrary.base.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.jw.galarylibrary.R
import com.jw.galarylibrary.base.bean.Folder
import com.jw.galarylibrary.base.util.Utils
import com.jw.library.loader.GlideImageLoader
import com.jw.library.model.ImageItem
import com.jw.library.model.VideoItem
import java.util.*

/**
 * 创建时间：
 * 更新时间
 * 版本：
 * 作者：Mr.jin
 * 描述：相册文件夹recyclerView的adapter
 */
class FolderAdapter<Data>(private val mActivity: Activity, mFolders: MutableList<Folder<Data>>?) :
    BaseAdapter() {
    private val mInflater: LayoutInflater
    private val mSize: Int
    private var mFolders: MutableList<Folder<Data>>? = null
    var selectIndex = 0
        set(i) {
            if (this.selectIndex != i) {
                field = i
                this.notifyDataSetChanged()
            }
        }

    init {
        if (mFolders != null && mFolders.size > 0) {
            this.mFolders = mFolders
        } else {
            this.mFolders = ArrayList()
        }

        this.mSize = Utils.getImageItemWidth(this.mActivity)
        this.mInflater =
            mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    fun refreshData(folders: MutableList<Folder<Data>>?) {
        if (folders != null && folders.size > 0) {
            this.mFolders = folders
        } else {
            this.mFolders!!.clear()
        }

        this.notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return this.mFolders!!.size
    }

    override fun getItem(position: Int): Folder<Data> {
        return this.mFolders!![position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val holder: ViewHolder
        if (convertView == null) {
            convertView = this.mInflater.inflate(R.layout.adapter_folder_list_item, parent, false)
            holder = ViewHolder(convertView!!)
        } else {
            holder = convertView.tag as ViewHolder
        }

        val folder = this.getItem(position)
        holder.folderName.text = folder.name
        var path: String? = null
        if (folder.cover is ImageItem) {
            holder.imageCount.text =
                this.mActivity.getString(R.string.ip_folder_image_count, folder.items!!.size)
            path = (folder.cover as ImageItem).path!!
        } else if (folder.cover is VideoItem) {
            holder.imageCount.text =
                this.mActivity.getString(R.string.ip_folder_video_count, folder.items!!.size)
            path = (folder.cover as VideoItem).thumbPath
        }
        GlideImageLoader.displayImage(this.mActivity, path!!, holder.cover)
        if (this.selectIndex == position) {
            holder.folderCheck.visibility = View.VISIBLE
        } else {
            holder.folderCheck.visibility = View.INVISIBLE
        }

        return convertView
    }

    private class ViewHolder internal constructor(view: View) {
        internal var cover: ImageView = view.findViewById(R.id.iv_cover)
        internal var folderName: TextView = view.findViewById(R.id.tv_folder_name)
        internal var imageCount: TextView = view.findViewById(R.id.tv_image_count)
        internal var folderCheck: ImageView = view.findViewById(R.id.iv_folder_check)

        init {
            view.tag = this
        }
    }
}