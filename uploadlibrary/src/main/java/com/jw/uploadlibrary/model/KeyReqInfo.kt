package com.jw.uploadlibrary.model

/**
 * 创建时间：2019/5/1014:28
 * 更新时间 2019/5/1014:28
 * 版本：
 * 作者：Mr.jin
 * 描述：
 */
class KeyReqInfo {
    var files:ArrayList<FileInfo> = ArrayList()
    var orgId:Long?=0

    class FileInfo{
        var name:String?=null
        var type:Int?=0
    }
}