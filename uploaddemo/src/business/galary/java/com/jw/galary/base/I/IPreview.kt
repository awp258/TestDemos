package com.jw.galary.base.I

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