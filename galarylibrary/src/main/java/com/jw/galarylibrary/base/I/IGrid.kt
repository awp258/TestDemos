package com.jw.galarylibrary.base.I

/**
 * 创建时间：
 * 更新时间
 * 版本：
 * 作者：Mr.jin
 * 描述：选择页面功能接口
 */
interface IGrid<ITEM> {
    /**
     * 弹出相册目录
     */
    fun onFolderPop()

    /**
     * 切换相册目录
     */
    fun onSwitchFolder()

    /**
     * 编辑
     * @param item ITEM
     */
    fun onEdit(item: ITEM)

    /**
     * 预览
     * @param position Int?
     */
    fun onPreview(position: Int?)

    /**
     * 带数据退出
     */
    fun onBack()
}