package com.jw.galarylibrary.base.I

/**
 * 创建时间：
 * 更新时间
 * 版本：
 * 作者：Mr.jin
 * 描述：预览页面功能接口
 */
interface IPreview<ITEM> {
    /**
     * 编辑
     * @param item ITEM
     */
    fun onEdit(item: ITEM)

    /**
     * 带数据退出
     */
    fun onBack()

    /**
     * 选择
     */
    fun onChecked()
}