package com.jw.uploaddemo.model

/**
 * 创建时间：2019/5/1014:28
 * 更新时间 2019/5/1014:28
 * 版本：
 * 作者：Mr.jin
 * 描述：
 */
class D {
    var files:ArrayList<FileParam> = ArrayList()
    var orgId:Long?=0

    class FileParam{
        var name:String?=null
        var type:Int?=0
    }
}